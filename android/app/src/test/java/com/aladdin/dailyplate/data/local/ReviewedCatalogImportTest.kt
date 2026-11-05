package com.aladdin.dailyplate.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.editorial.*
import java.security.MessageDigest
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE)
class ReviewedCatalogImportTest {
    private val units = mapOf("energy_kcal" to NutrientUnit.KCAL, "protein_g" to NutrientUnit.GRAM,
        "fat_g" to NutrientUnit.GRAM, "carbohydrate_g" to NutrientUnit.GRAM, "trans_fat_g" to NutrientUnit.GRAM)
    private val nutrients = NutrientSnapshot(units.mapValues { (k, u) -> NutrientAmount(
        when (k) { "energy_kcal" -> 100.0; "trans_fat_g" -> null; else -> 10.0 }, u) })
    private val food = Food(VersionRef("food", 1), testText, emptyList(), "TEST source", "TEST dataset",
        "raw_as_described", nutrients, setOf("trans_fat_g"), null, "TEST release", null, ContentStatus.DRAFT)

    private fun database() = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), LibraryDatabase::class.java)
        .allowMainThreadQueries().build()
    private fun seed(db: LibraryDatabase, value: Food = food) = db.snapshots().insert(
        LibrarySnapshotEntity("food", value.ref.id, value.ref.version, snapshotJson.encodeToString(Food.serializer(), value)))
    private fun payload() = ReviewedRecipePayload(editorialRecipe().recipe.copy(
        nutrientsTotal = nutrients, retentionMethod = "TEST no loss",
        status = ContentStatus.PUBLISHED, review = Review("TEST reviewer", editorTime, "TEST audit")),
        completeMetadata, ReviewedRecipeCalculation("TEST no loss", listOf(
            ReviewedComponentCalculation(food.ref, ReviewedComponentState.RAW, "TEST method evidence", 1.0, emptyMap()))))
    private fun record(value: ReviewedRecipePayload = payload()): ReviewedRecipeContent {
        val wire = Json.encodeToString(ReviewedRecipePayload.serializer(), value)
        val digest = MessageDigest.getInstance("SHA-256").digest(wire.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it.toInt() and 0xff) }
        return ReviewedRecipeContent(1, value.recipe.ref, wire, digest, "TEST publication", "TEST publisher",
            editorTime, ReviewedContentState.PUBLISHED)
    }
    private fun pack(version: Long, records: List<ReviewedRecipeContent> = listOf(record())) =
        ReviewedCatalogPackage(1, VersionRef("reviewed-catalog", version), records)
    private fun wire(pack: ReviewedCatalogPackage) = snapshotJson.encodeToString(ReviewedCatalogPackage.serializer(), pack)
    private fun import(repo: CatalogRepository, pack: ReviewedCatalogPackage) = repo.importReviewedCatalogJson(wire(pack), editorTime)

    @Test fun recomputedSelectionPersistsOnDiskWithoutOpeningProductionGates() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val name = "reviewed-${System.nanoTime()}.db"
        fun open() = Room.databaseBuilder(context, LibraryDatabase::class.java, name).allowMainThreadQueries().build()
        try {
            open().useDatabase { db ->
                seed(db)
                val repo = CatalogRepository(db)
                assertEquals(pack(1).ref, import(repo, pack(1)))
                assertEquals(pack(1), repo.currentReviewedCatalog())
                assertTrue(repo.productionRecipes().isEmpty())
                assertTrue(repo.productionFoods().isEmpty())
                assertEquals(ContentStatus.DRAFT, repo.food(food.ref)!!.status)
            }
            open().useDatabase { db -> assertEquals(pack(1), CatalogRepository(db).currentReviewedCatalog()) }
        } finally { context.deleteDatabase(name) }
    }

    @Test fun forgedTotalsMissingVersionsUnitsUnknownsAndCookingDoubleCorrectionRejectAtomically() {
        database().useDatabase { db ->
            seed(db)
            val repo = CatalogRepository(db)
            import(repo, pack(1))
            val p = payload()
            val changedTotals = nutrients.copy(values = nutrients.values + ("energy_kcal" to NutrientAmount(999.0, NutrientUnit.KCAL)))
            val zeroUnknown = nutrients.copy(values = nutrients.values + ("trans_fat_g" to NutrientAmount(0.0, NutrientUnit.GRAM)))
            val wrongUnit = nutrients.copy(values = nutrients.values + ("protein_g" to NutrientAmount(10.0, NutrientUnit.MILLIGRAM)))
            val invalid = listOf(
                p.copy(recipe = p.recipe.copy(nutrientsTotal = changedTotals)),
                p.copy(recipe = p.recipe.copy(nutrientsTotal = zeroUnknown)),
                p.copy(recipe = p.recipe.copy(nutrientsTotal = wrongUnit)),
                p.copy(recipe = p.recipe.copy(ingredients = listOf(Ingredient(food.ref.copy(version = 2), 100.0))),
                    calculation = p.calculation!!.copy(components = listOf(p.calculation.components.single().copy(food = food.ref.copy(version = 2))))),
                p.copy(calculation = null),
                p.copy(calculation = p.calculation!!.copy(components = listOf(p.calculation.components.single().copy(
                    state = ReviewedComponentState.AS_DESCRIBED, retainedFraction = 0.5)))),
            )
            invalid.forEach { bad ->
                // New recipe version ensures rejection is from validation/recompute, not immutability.
                val next = record(bad.copy(recipe = bad.recipe.copy(ref = VersionRef("recipe-new", 1))))
                assertThrows(IllegalArgumentException::class.java) { import(repo, pack(2, listOf(record(), next))) }
                assertEquals(pack(1), repo.currentReviewedCatalog())
                assertEquals(1, db.snapshots().all("reviewed_catalog").size)
            }
            val invalidHash = record().copy(payloadSha256 = "0".repeat(64))
            assertThrows(IllegalArgumentException::class.java) { import(repo, pack(2, listOf(invalidHash))) }
        }
    }

    @Test fun recomputeUsesDbNotPayloadAndVerifiesCookedYieldAndRetention() {
        database().useDatabase { db ->
            seed(db)
            val repo = CatalogRepository(db)
            val p = payload()
            val half = NutrientSnapshot(nutrients.values.mapValues { (_, n) -> n.copy(value = n.value?.times(0.5)) })
            val cooked = p.copy(recipe = p.recipe.copy(yieldCookedGrams = 200.0, nutrientsPer100g = half),
                metadata = p.metadata.copy(yieldEvidence = "TEST measured yield"),
                calculation = p.calculation!!.copy(yieldBasis = ReviewedYieldBasis.MEASURED))
            import(repo, pack(1, listOf(record(cooked))))
            val bad = cooked.copy(recipe = cooked.recipe.copy(ref = VersionRef("bad-yield", 1), nutrientsPer100g = nutrients))
            assertThrows(IllegalArgumentException::class.java) { import(repo, pack(2, listOf(record(bad)))) }
            val retained = cooked.copy(recipe = cooked.recipe.copy(ref = VersionRef("retained", 1),
                nutrientsTotal = half, nutrientsPer100g = half.copy(values = half.values.mapValues { (_, n) -> n.copy(value = n.value?.times(0.5)) })),
                calculation = cooked.calculation!!.copy(components = listOf(cooked.calculation.components.single().copy(retainedFraction = 0.5))))
            import(repo, pack(2, listOf(record(retained))))
        }
        database().useDatabase { db ->
            seed(db, food.copy(nutrientsPer100g = nutrients.copy(values = nutrients.values +
                ("energy_kcal" to NutrientAmount(200.0, NutrientUnit.KCAL)))))
            assertThrows(IllegalArgumentException::class.java) { import(CatalogRepository(db), pack(1)) }
        }
        database().useDatabase { db ->
            seed(db, food.copy(preparationState = "cooked_as_described"))
            assertThrows(IllegalArgumentException::class.java) { import(CatalogRepository(db), pack(1)) }
        }
    }

    @Test fun replayConflictDowngradeWithdrawalAndEmptyReplacementNeverRestoreOldSelection() {
        database().useDatabase { db ->
            seed(db)
            val repo = CatalogRepository(db)
            import(repo, pack(1))
            val withdrawn = record().copy(state = ReviewedContentState.WITHDRAWN, withdrawnAt = editorTime,
                withdrawalReason = "TEST recall")
            val recall = pack(2, listOf(withdrawn))
            import(repo, recall)
            import(repo, pack(1))
            assertEquals(recall, repo.currentReviewedCatalog())
            assertThrows(IllegalArgumentException::class.java) { import(repo, pack(2, emptyList())) }
            assertThrows(IllegalArgumentException::class.java) { import(repo, pack(3)) }
            import(repo, pack(3, emptyList()))
            assertTrue(repo.currentReviewedCatalog()!!.recipes.isEmpty())
            assertThrows(IllegalArgumentException::class.java) { import(repo, pack(4)) }
            assertThrows(IllegalArgumentException::class.java) { import(repo, pack(2, emptyList())) }
        }
    }

    @Test fun schemaAndSyntheticEditorialWireCannotEnterCatalogImport() {
        database().useDatabase { db ->
            seed(db)
            val repo = CatalogRepository(db)
            val synthetic = EditorialWorkflow(testRoles).create(editorialRecipe(), completeMetadata, "author", editorTime, "TEST")
            assertThrows(IllegalArgumentException::class.java) { repo.importReviewedCatalogJson(
                Json.encodeToString(EditorialSnapshot.serializer(), synthetic), editorTime) }
            assertThrows(IllegalArgumentException::class.java) {
                repo.importReviewedCatalogJson(wire(pack(1)).replace("\"schemaVersion\":1", "\"schemaVersion\":99"), editorTime)
            }
            assertNull(repo.currentReviewedCatalog())
        }
    }
}
