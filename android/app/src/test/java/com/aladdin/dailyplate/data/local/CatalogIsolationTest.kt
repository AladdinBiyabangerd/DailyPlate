package com.aladdin.dailyplate.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aladdin.dailyplate.domain.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE)
class CatalogIsolationTest {
    @Test fun libraryVersionsAndNullsPersistIndependentlyOfPersonalData() {
        Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), LibraryDatabase::class.java)
            .allowMainThreadQueries().build().useDatabase { library ->
                val food = Food(foodRef, LocalizedText("Mərci", "Lentil", "Чечевица"), listOf("mərcimək"),
                    "source-id", "source-type", "dry", nutrients, setOf("protein"), null,
                    "source-v1", null, ContentStatus.DRAFT)
                val recipe = Recipe(recipeRef, LocalizedText("Resept"), listOf(Ingredient(foodRef, 100.0)),
                    "boil", listOf("Hazırla"), null, null, nutrients, null, null, emptyMap(), null,
                    setOf("pot"), PreparationTime(null, null, null), null, null, null, null, null, null, ContentStatus.DRAFT)
                val policy = NutritionPolicy(VersionRef("policy", 1), "unapproved", emptyList(), VersionRef("formula", 1),
                    setOf("age", "sex"), emptyMap(), emptyMap(), emptyList(), emptyList(), emptyList(), emptyList(),
                    emptyList(), null, null, PolicyStatus.DRAFT)
                val evidence = EvidenceCard(VersionRef("card", 1), "claim", LocalizedText("İzah"),
                    "https://example.org/source", "section", "population", LocalizedText("Məhdudiyyət"), null, ContentStatus.DRAFT)
                val price = IngredientPrice(VersionRef("price", 1), foodRef.id, "test-region", "test-retailer", recordedAt,
                    500.0, 1.0, Money("3.50", "AZN"), "dry", "test-source")
                val dao = library.snapshots()
                dao.insert(LibrarySnapshotEntity("food", food.ref.id, food.ref.version, snapshotJson.encodeToString(Food.serializer(), food)))
                dao.insert(LibrarySnapshotEntity("recipe", recipe.ref.id, recipe.ref.version, snapshotJson.encodeToString(Recipe.serializer(), recipe)))
                dao.insert(LibrarySnapshotEntity("policy", policy.ref.id, policy.ref.version, snapshotJson.encodeToString(NutritionPolicy.serializer(), policy)))
                dao.insert(LibrarySnapshotEntity("evidence", evidence.ref.id, evidence.ref.version, snapshotJson.encodeToString(EvidenceCard.serializer(), evidence)))
                dao.insert(LibrarySnapshotEntity("price", price.ref.id, price.ref.version, snapshotJson.encodeToString(IngredientPrice.serializer(), price)))
                val catalog = CatalogRepository(library)
                assertEquals(food, catalog.food(food.ref))
                assertEquals(recipe, catalog.recipe(recipe.ref))
                assertEquals(policy, catalog.policy(policy.ref))
                assertEquals(evidence, catalog.evidence(evidence.ref))
                assertEquals(price, catalog.price(price.ref))
                assertNull(catalog.food(food.ref.copy(version = 4)))
                Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), UserDatabase::class.java)
                    .allowMainThreadQueries().build().useDatabase { personal ->
                        val repo = UserRepositories(personal)
                        repo.profiles.save(profile(), 0)
                        repo.foodLogs.record(log())
                        assertEquals(food, catalog.food(food.ref))
                        assertEquals(recipe, catalog.recipe(recipe.ref))
                        library.openHelper.readableDatabase.query("SELECT name FROM sqlite_master WHERE type='table'").use { cursor ->
                            val tables = mutableSetOf<String>()
                            while (cursor.moveToNext()) tables.add(cursor.getString(0))
                            assertFalse(tables.contains("user_snapshots"))
                            assertFalse(tables.contains("food_events"))
                        }
                    }
            }
    }
}
