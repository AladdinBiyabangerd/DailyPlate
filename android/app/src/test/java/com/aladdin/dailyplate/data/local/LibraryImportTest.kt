package com.aladdin.dailyplate.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aladdin.dailyplate.domain.VersionRef
import java.io.File
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE)
class LibraryImportTest {
    private fun resource(name: String) = javaClass.classLoader!!.getResourceAsStream(name)!!.bufferedReader().use { it.readText() }
    private fun pack(version: String = "0.3.0", foods: String = resource("foods.json")) = LibraryPackage.json(
        resource("manifest.json").replace("0.3.0", version), foods,
        resource("recipes.json").replace("0.3.0", version),
    )
    private fun database() = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), LibraryDatabase::class.java)
        .allowMainThreadQueries().build()

    @Test fun currentJsonAndSqliteAreIdenticalAndIdempotent() {
        database().useDatabase { db ->
            val catalog = CatalogRepository(db)
            val version = catalog.importPackage(pack())
            assertEquals(500, db.snapshots().all("food").size)
            assertEquals(224, db.snapshots().all("recipe").size)
            assertEquals("172217", catalog.food(VersionRef("milk_whole", version))!!.sourceId)
            assertNull(catalog.food(VersionRef("milk_whole", version))!!.nutrientsPer100g.values.getValue("trans_fat_g").value)
            catalog.importPackage(pack())
            val file = File.createTempFile("library", ".sqlite")
            try {
                javaClass.classLoader!!.getResourceAsStream("nutrition-library.sqlite")!!.use { input -> file.outputStream().use { input.copyTo(it) } }
                catalog.importPackage(LibraryPackage.sqlite(file, resource("manifest.json")))
            } finally { file.delete() }
            assertEquals(500, db.snapshots().all("food").size)
            assertTrue(catalog.productionRecipes().isEmpty())
            assertTrue(catalog.productionFoods().isEmpty())
            val states = db.snapshots().all("food").map { catalog.food(VersionRef(it.id, version))!!.preparationState }.toSet()
            assertTrue(states.contains("raw_as_described"))
            assertTrue(states.any { it.contains("cooked") })
        }
    }

    @Test fun databaseFailureRollsBackEveryInsertedRow() {
        database().useDatabase { db ->
            db.openHelper.writableDatabase.execSQL("CREATE TRIGGER reject_recipe BEFORE INSERT ON library_snapshots WHEN NEW.kind = 'recipe' BEGIN SELECT RAISE(ABORT, 'test failure'); END")
            assertThrows(android.database.sqlite.SQLiteConstraintException::class.java) {
                CatalogRepository(db).importPackage(pack())
            }
            assertTrue(db.snapshots().all("food").isEmpty())
            assertTrue(db.snapshots().all("source_food").isEmpty())
            assertTrue(db.snapshots().all("package").isEmpty())
        }
    }

    @Test fun revisionsPreserveOldSnapshotsAndRejectCorruptionAndDowngrades() {
        database().useDatabase { db ->
            val catalog = CatalogRepository(db)
            val v1 = catalog.importPackage(pack())
            val old = catalog.food(VersionRef("milk_whole", v1))
            val changed = resource("foods.json").replace("Süd, 3,25% yağlı", "Yeni ad")
            val v2 = catalog.importPackage(pack("0.3.1", changed))
            assertEquals(old, catalog.food(VersionRef("milk_whole", v1)))
            assertEquals("Yeni ad", catalog.food(VersionRef("milk_whole", v2))!!.names.az)
            val before = db.snapshots().all("food")
            for (bad in listOf(pack(), pack("0.3.1"), pack("0.3.2", changed.replace("\"fdc_id\": 172217", "\"fdc_id\": -1")),
                pack("0.3.2", changed.replace("\"publishable\": false", "\"publishable\": true")))) {
                assertThrows(IllegalArgumentException::class.java) { catalog.importPackage(bad) }
                assertEquals(before, db.snapshots().all("food"))
            }
            Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), UserDatabase::class.java)
                .allowMainThreadQueries().build().useDatabase { personal ->
                    val logs = UserRepositories(personal).foodLogs
                    val entry = log().copy(food = old!!.ref, recipe = null, actualRecipeSnapshot = null,
                        nutrientSnapshot = old.nutrientsPer100g, recipeChangedAnswer = null,
                        nutrientBasis = com.aladdin.dailyplate.domain.NutrientBasis.FOOD_SOURCE)
                    logs.record(entry)
                    catalog.importPackage(pack("0.3.2", changed))
                    assertEquals(entry, logs.version(entry.userId, entry.ref))
                }
        }
    }
}
