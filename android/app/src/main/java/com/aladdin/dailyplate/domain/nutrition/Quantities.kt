package com.aladdin.dailyplate.domain.nutrition

import com.aladdin.dailyplate.domain.NutrientAmount
import com.aladdin.dailyplate.domain.NutrientSnapshot
import com.aladdin.dailyplate.domain.NutrientUnit
import com.aladdin.dailyplate.domain.VersionRef
import java.math.BigDecimal
import java.math.RoundingMode

internal fun positive(value: Double): Double {
    require(value.isFinite() && value > 0) { "Quantity must be finite and positive" }
    return value
}

enum class MassUnit { GRAM, KILOGRAM }

data class Mass(val amount: Double, val unit: MassUnit) {
    init { positive(amount); positive(amount * if (unit == MassUnit.KILOGRAM) 1000 else 1) }
    val grams: Double get() = positive(amount * if (unit == MassUnit.KILOGRAM) 1000 else 1)
}

/** A spoon/item conversion belongs to one exact food version and a documented source. */
data class FoodMeasure(val food: VersionRef, val measureId: String, val gramsPerUnit: Double, val source: String) {
    init { positive(gramsPerUnit); require(measureId.isNotBlank() && source.isNotBlank()) }
    fun grams(food: VersionRef, units: Double): Double {
        require(food == this.food) { "Measure belongs to another food version" }
        return positive(positive(units) * gramsPerUnit)
    }
}

object NutrientUnits {
    fun convert(amount: NutrientAmount, target: NutrientUnit): NutrientAmount {
        if (amount.unit == target) return amount
        require(amount.unit != NutrientUnit.KCAL && target != NutrientUnit.KCAL) { "Energy and mass are incompatible" }
        fun grams(unit: NutrientUnit): Double = when (unit) {
            NutrientUnit.GRAM -> 1.0
            NutrientUnit.MILLIGRAM -> 0.001
            NutrientUnit.MICROGRAM -> 0.000001
            NutrientUnit.KCAL -> error("Energy is not mass")
        }
        return NutrientAmount(amount.value?.let { it * grams(amount.unit) / grams(target) }, target)
    }
}

/** Display boundary only. Calculation results are never rounded or fed back from this map. */
fun NutrientSnapshot.displayValues(decimals: Int): Map<String, BigDecimal?> {
    require(decimals in 0..12)
    return values.mapValues { (_, amount) -> amount.value?.let {
        BigDecimal.valueOf(it).setScale(decimals, RoundingMode.HALF_UP)
    } }
}
