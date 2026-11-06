package com.aladdin.dailyplate.data.local

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.editorial.*
import com.aladdin.dailyplate.domain.nutrition.*
import kotlin.math.abs

/** Only DB-resolved nutrient data enters arithmetic; payload totals are assertions to verify. */
internal object ReviewedRecipeRecompute {
    fun verify(payload: ReviewedRecipePayload, food: (VersionRef) -> Food?) {
        val recipe = payload.recipe
        val calculation = requireNotNull(payload.calculation) { "Reviewed calculation evidence required" }
        require(calculation.method.isNotBlank() && recipe.retentionMethod == calculation.method)
        require(calculation.components.map { it.food } == recipe.ingredients.map { it.food })
        val components = recipe.ingredients.zip(calculation.components).map { (ingredient, evidence) ->
            val stored = requireNotNull(food(ingredient.food)) { "Exact ingredient version missing from library DB" }
            require(stored.ref == ingredient.food && stored.status != ContentStatus.WITHDRAWN)
            require(evidence.source.isNotBlank())
            val adjustment = CookingAdjustment(calculation.method, evidence.source,
                evidence.retainedFraction, evidence.nutrientRetention)
            val state = ComponentState.valueOf(evidence.state.name)
            require(evidence.nutrientRetention.keys.all { it in stored.nutrientsPer100g.values }) {
                "Retention references a nutrient absent from the exact DB food"
            }
            require(state != ComponentState.RAW || stored.preparationState in
                setOf("raw_as_described", "dry_as_described")) {
                "Raw cooking adjustment cannot reinterpret a cooked/ambiguous DB source"
            }
            require(state != ComponentState.ALREADY_COOKED || stored.preparationState == "cooked_as_described")
            require(state == ComponentState.RAW ||
                (evidence.retainedFraction == 1.0 && evidence.nutrientRetention.isEmpty())) {
                "Already cooked/as-described inputs cannot receive cooking adjustment"
            }
            NutrientComponent(stored.ref, Mass(ingredient.edibleGrams, MassUnit.GRAM), state,
                stored.nutrientsPer100g, if (state == ComponentState.RAW) adjustment else null)
        }
        require((recipe.yieldCookedGrams != null) == (calculation.yieldBasis != null))
        val yield = recipe.yieldCookedGrams?.let {
            CookedYield(Mass(it, MassUnit.GRAM), YieldBasis.valueOf(requireNotNull(calculation.yieldBasis).name),
                requireNotNull(payload.metadata.yieldEvidence))
        }
        val recomputed = RecipeCalculator.calculate(components, requireNotNull(payload.metadata.declaredServings),
            cookedYield = yield)
        equal(recipe.nutrientsTotal, recomputed.total)
        recipe.nutrientsPer100g?.let { equal(it, requireNotNull(recomputed.per100gCooked)) }
    }

    private fun equal(asserted: NutrientSnapshot, computed: NutrientSnapshot) {
        require(asserted.values.keys == computed.values.keys) { "Nutrient coverage differs from DB recompute" }
        asserted.values.forEach { (key, actual) ->
            val expected = computed.values.getValue(key)
            require(actual.unit == expected.unit) { "Nutrient unit mismatch: $key" }
            // Numeric equality allowance only; not a nutritional/clinical tolerance.
            require(if (expected.value == null) actual.value == null else actual.value?.let {
                abs(it - expected.value) <= 1e-9 * maxOf(1.0, abs(expected.value))
            } == true) { "Nutrient differs from DB recompute: $key" }
        }
    }
}
