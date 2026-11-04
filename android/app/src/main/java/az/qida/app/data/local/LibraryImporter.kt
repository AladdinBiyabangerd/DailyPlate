package az.qida.app.data.local

import android.database.sqlite.SQLiteDatabase
import az.qida.app.domain.*
import az.qida.app.domain.nutrition.*
import java.io.File
import kotlinx.serialization.json.*

/** The original package rows are retained alongside typed snapshots for provenance/audit.
 * This adapter only accepts the current draft format; publication needs a separate reviewed adapter.
 */
class LibraryPackage private constructor(
    internal val manifest: JsonObject,
    internal val foods: List<JsonObject>,
    internal val recipes: List<JsonObject>,
) {
    companion object {
        fun json(manifest: String, foods: String, recipes: String): LibraryPackage = LibraryPackage(
            Json.parseToJsonElement(manifest).jsonObject,
            Json.parseToJsonElement(foods).jsonArray.map { it.jsonObject },
            Json.parseToJsonElement(recipes).jsonArray.map { it.jsonObject },
        )

        fun sqlite(file: File, manifest: String): LibraryPackage {
            val db = SQLiteDatabase.openDatabase(file.absolutePath, null, SQLiteDatabase.OPEN_READONLY)
            try {
                val integrity = db.rawQuery("PRAGMA integrity_check", null)
                try { require(integrity.moveToFirst() && integrity.getString(0) == "ok") }
                finally { integrity.close() }
                val foreignKeys = db.rawQuery("PRAGMA foreign_key_check", null)
                try { require(!foreignKeys.moveToFirst()) } finally { foreignKeys.close() }
                fun rows(table: String): String = db.rawQuery("SELECT data_json FROM $table ORDER BY id", null).use { c ->
                    buildList { while (c.moveToNext()) add(c.getString(0)) }.joinToString(",", "[", "]")
                }
                val metadata = db.rawQuery("SELECT value FROM library_meta WHERE key = 'version'", null)
                try {
                    require(metadata.moveToFirst())
                    require(metadata.getString(0) == Json.parseToJsonElement(manifest).jsonObject.text("version"))
                } finally { metadata.close() }
                return json(manifest, rows("foods"), rows("recipes"))
            } finally { db.close() }
        }
    }
}

private fun JsonObject.text(key: String) = getValue(key).jsonPrimitive.content
private fun JsonObject.obj(key: String) = getValue(key).jsonObject
private fun JsonObject.number(key: String) = getValue(key).jsonPrimitive.double
private fun JsonObject.names() = obj("names").let { LocalizedText(it.text("az"), it.text("en"), it.text("ru")) }
private fun nutrients(value: JsonObject) = NutrientSnapshot(value.mapValues { (key, amount) ->
    val unit = when {
        key.endsWith("_kcal") -> NutrientUnit.KCAL
        key.endsWith("_mg") -> NutrientUnit.MILLIGRAM
        key.endsWith("_g") -> NutrientUnit.GRAM
        else -> error("Unknown nutrient unit: $key")
    }
    NutrientAmount(if (amount is JsonNull) null else amount.jsonPrimitive.double, unit)
})

/** Worker-thread import boundary, separate from personal repositories. No personal DB is accepted. */
internal class LibraryImporter(private val database: LibraryDatabase) {
    fun importPackage(pack: LibraryPackage): Long {
        val versionText = pack.manifest.text("version")
        val parts = versionText.split('.').map { it.toLong() }
        require(parts.size == 3 && parts.all { it in 0..999 })
        val version = parts[0] * 1_000_000 + parts[1] * 1_000 + parts[2] + 1
        require(pack.foods.size == pack.manifest.getValue("curated_food_count").jsonPrimitive.int)
        require(pack.recipes.size == pack.manifest.getValue("recipe_draft_count").jsonPrimitive.int)
        require(pack.foods.map { it.text("id") }.distinct().size == pack.foods.size)
        require(pack.recipes.map { it.text("id") }.distinct().size == pack.recipes.size)
        val foodRows = pack.foods.associateBy { it.text("id") }
        require(pack.foods.map { it.text("fdc_id") }.distinct().size == pack.foods.size)
        val rows = mutableListOf<LibrarySnapshotEntity>()
        fun add(kind: String, id: String, payload: String) {
            rows.add(LibrarySnapshotEntity(kind, id, version, payload))
        }
        for (raw in pack.foods) {
            require(raw.text("status") == "source_linked_draft" && !raw.getValue("publishable").jsonPrimitive.boolean)
            val source = raw.obj("source")
            require(source.text("data_type") == "SR Legacy" && source.text("dataset_release") == "2018-04")
            require(raw.number("fdc_id") > 0)
            require(raw.obj("nutrient_lineage").obj("energy_kcal").getValue("nutrient_id").jsonPrimitive.int in setOf(1008, 2048, 2047))
            require(raw.obj("nutrient_lineage").obj("energy_kcal").text("unit") == "KCAL")
            val n = nutrients(raw.obj("nutrients_per_100g"))
            for (key in listOf("energy_kcal", "protein_g", "fat_g", "carbohydrate_g")) require(n.values.getValue(key).value != null)
            val food = Food(VersionRef(raw.text("id"), version), raw.names(), emptyList(), raw.text("fdc_id"),
                source.text("data_type"), raw.text("preparation_state"), n, n.missingFields, null,
                source.text("dataset_release"), null, ContentStatus.DRAFT)
            add("food", food.ref.id, snapshotJson.encodeToString(Food.serializer(), food))
            add("source_food", food.ref.id, raw.toString())
        }
        for (raw in pack.recipes) {
            require(raw.text("version") == versionText)
            require(raw.text("status") == "formulated_source_linked_draft")
            require(!raw.getValue("publishable").jsonPrimitive.boolean)
            require(!raw.getValue("eligible_for_live_personalized_planning").jsonPrimitive.boolean)
            val ingredients = raw.getValue("ingredients").jsonArray.map { element ->
                val i = element.jsonObject
                val food = foodRows.getValue(i.text("food_id"))
                require(food.text("fdc_id") == i.text("fdc_id"))
                require(food.text("preparation_state") == i.text("input_state"))
                Ingredient(VersionRef(i.text("food_id"), version), i.number("edible_grams"))
            }
            require(ingredients.map { it.food.id }.distinct().size == ingredients.size)
            require(raw.number("declared_servings") > 0)
            val estimate = raw.obj("nutrient_estimate")
            require(estimate.text("basis") == "ingredient_sum_no_cooking_retention_or_loss_adjustment")
            require(estimate["per_100g_cooked"] is JsonNull && raw["measured_final_yield_g"] is JsonNull)
            // Verify stored ingredient sums, without introducing cooked-yield/retention calculation.
            val totals = nutrients(estimate.obj("total_recipe"))
            val perServing = nutrients(estimate.obj("per_declared_serving"))
            require(totals.values.keys == foodRows.values.first().obj("nutrients_per_100g").keys)
            val calculated = RecipeCalculator.calculate(ingredients.map { ingredient ->
                NutrientComponent(ingredient.food, Mass(ingredient.edibleGrams, MassUnit.GRAM),
                    ComponentState.AS_DESCRIBED,
                    nutrients(foodRows.getValue(ingredient.food.id).obj("nutrients_per_100g")))
            }, raw.number("declared_servings"))
            for ((key, total) in totals.values) {
                val expected = calculated.total.values.getValue(key).value
                require(if (expected == null) total.value == null else
                    total.value != null && kotlin.math.abs(total.value - expected) < 0.00001)
                val serving = perServing.values.getValue(key).value
                val expectedServing = calculated.perDeclaredServing.values.getValue(key).value
                require(if (expectedServing == null) serving == null else serving != null &&
                    kotlin.math.abs(serving - expectedServing) < 0.00001)
            }
            val recipe = Recipe(VersionRef(raw.text("id"), version), raw.names(), ingredients,
                estimate.text("basis"), raw.obj("steps").getValue("az").jsonArray.map { it.jsonPrimitive.content },
                null, null, totals, null, null, emptyMap(), null, emptySet(),
                PreparationTime(raw.number("estimated_minutes").toInt(), null, null), null, null, null,
                null, null, null, ContentStatus.DRAFT)
            add("recipe", recipe.ref.id, snapshotJson.encodeToString(Recipe.serializer(), recipe))
            add("source_recipe", recipe.ref.id, raw.toString())
        }
        add("package", "manifest", pack.manifest.toString())
        database.runInTransaction {
            val dao = database.snapshots()
            val newest = dao.all("package").firstOrNull()?.version ?: 0
            require(version >= newest) { "Package downgrade rejected" }
            for (row in rows) {
                val old = dao.version(row.kind, row.id, row.version)
                require(old == null || old == row) { "Immutable package revision conflict" }
                if (old == null) dao.insert(row)
            }
        }
        return version
    }
}
