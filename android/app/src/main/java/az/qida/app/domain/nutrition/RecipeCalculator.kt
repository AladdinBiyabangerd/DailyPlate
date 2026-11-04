package az.qida.app.domain.nutrition

import az.qida.app.domain.NutrientAmount
import az.qida.app.domain.NutrientSnapshot
import az.qida.app.domain.NutrientUnit
import az.qida.app.domain.VersionRef

enum class ComponentState { RAW, ALREADY_COOKED, AS_DESCRIBED }

/** Caller supplies documented factors; the engine invents no cooking/clinical coefficients.
 * retainedFraction models discarded ingredient (e.g. measured oil loss), not water evaporation.
 * Unlisted nutrient factors explicitly remain 1 under the named method.
 */
data class CookingAdjustment(
    val method: String, val source: String, val retainedFraction: Double,
    val nutrientRetention: Map<String, Double>,
) {
    init {
        require(method.isNotBlank() && source.isNotBlank())
        (nutrientRetention.values + retainedFraction).forEach { require(it.isFinite() && it in 0.0..1.0) }
    }
}

data class NutrientComponent(
    val food: VersionRef, val edibleMass: Mass, val state: ComponentState,
    val per100g: NutrientSnapshot, val adjustment: CookingAdjustment? = null,
) {
    init {
        require(state == ComponentState.RAW || adjustment == null) { "Cooking correction requires raw input" }
    }
}

enum class YieldBasis { MEASURED, DOCUMENTED_ESTIMATE }

data class CookedYield(val mass: Mass, val basis: YieldBasis, val source: String) {
    init { require(source.isNotBlank()) }
}

@ConsistentCopyVisibility
data class RecipeNutrition internal constructor(
    val total: NutrientSnapshot,
    val perDeclaredServing: NutrientSnapshot,
    val per100gCooked: NutrientSnapshot?,
    val baseServings: Double,
    val servingsPrepared: Double,
    val cookedYield: CookedYield?,
    val missingIngredients: Map<String, Set<VersionRef>>,
    val components: List<NutrientComponent>,
) {
    val missingFields: Set<String> get() = total.missingFields

    /** Increasing the batch and consuming a serving are independent operations. */
    fun consumedServings(servings: Double): NutrientSnapshot {
        positive(servings)
        require(servings <= servingsPrepared)
        return total.scaled(servings / servingsPrepared)
    }

    fun consumedCookedMass(mass: Mass): NutrientSnapshot? {
        val yield = cookedYield ?: return null
        require(mass.grams <= yield.mass.grams) { "Consumed mass exceeds batch yield" }
        return total.scaled(mass.grams / yield.mass.grams)
    }
}

private fun NutrientSnapshot.scaled(factor: Double) = NutrientSnapshot(values.mapValues { (_, amount) ->
    NutrientAmount(amount.value?.times(factor), amount.unit)
})

object RecipeCalculator {
    /** Required nutrients remain null even if absent from every ingredient. Additional keys
     * are unioned across components; absent in any component propagates unknown, never zero.
     */
    fun calculate(
        components: List<NutrientComponent>,
        baseServings: Double,
        servingsPrepared: Double = baseServings,
        cookedYield: CookedYield? = null,
        requiredUnits: Map<String, NutrientUnit> = mapOf(
            "energy_kcal" to NutrientUnit.KCAL, "protein_g" to NutrientUnit.GRAM,
            "fat_g" to NutrientUnit.GRAM, "carbohydrate_g" to NutrientUnit.GRAM,
        ),
    ): RecipeNutrition {
        require(components.isNotEmpty()) { "Empty recipe cannot be calculated" }
        positive(baseServings); positive(servingsPrepared)
        val scale = positive(servingsPrepared / baseServings)
        val units = requiredUnits.toMutableMap()
        components.forEach { component -> component.per100g.values.forEach { (key, amount) ->
            units.putIfAbsent(key, amount.unit)
        } }
        require(units.isNotEmpty())
        val missing = mutableMapOf<String, Set<VersionRef>>()
        val totals = units.mapValues { (key, unit) ->
            val unknown = mutableSetOf<VersionRef>()
            var sum = 0.0
            for (component in components) {
                val amount = component.per100g.values[key]?.let { NutrientUnits.convert(it, unit) }?.value
                if (amount == null) unknown.add(component.food)
                else {
                    val adjustment = component.adjustment
                    sum += amount * (component.edibleMass.grams / 100) * scale *
                        (adjustment?.retainedFraction ?: 1.0) * (adjustment?.nutrientRetention?.get(key) ?: 1.0)
                }
            }
            if (unknown.isNotEmpty()) missing[key] = unknown.toSet()
            NutrientAmount(if (unknown.isEmpty()) sum else null, unit)
        }
        val total = NutrientSnapshot(totals)
        // Yield always describes the prepared batch, never inferred from raw ingredient weight.
        return RecipeNutrition(total, total.scaled(1 / servingsPrepared),
            cookedYield?.let { total.scaled(100 / it.mass.grams) }, baseServings, servingsPrepared, cookedYield,
            missing.toMap(), components.toList())
    }
}
