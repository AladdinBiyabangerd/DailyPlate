package com.aladdin.dailyplate.domain.policy

import com.aladdin.dailyplate.domain.*
import kotlinx.serialization.Serializable

@Serializable
data class AgeScope(val minimumYears: Int, val maximumYears: Int) {
    init { require(minimumYears >= 18 && maximumYears >= minimumYears) }
    operator fun contains(age: Int) = age in minimumYears..maximumYears
}

@Serializable
enum class FormulaInput { AGE, SEX, HEIGHT_CM, WEIGHT_KG, ACTIVITY }

@Serializable
enum class LifeStage { ADULT, PREGNANT, BREASTFEEDING }

@Serializable
data class DailyEnergyBounds(val minimumKcal: Double, val maximumKcal: Double) {
    init { positive(minimumKcal); positive(maximumKcal); require(minimumKcal <= maximumKcal) }
}

@Serializable
enum class MacroBasis { GRAMS_PER_KG, ENERGY_FRACTION }

/** Factors and weight basis are policy decisions, never built-in clinical defaults. */
@Serializable
data class MacroRule(val basis: MacroBasis, val factor: Double, val kcalPerGram: Double? = null) {
    init {
        positive(factor)
        when (basis) {
            MacroBasis.GRAMS_PER_KG -> require(kcalPerGram == null)
            MacroBasis.ENERGY_FRACTION -> { require(factor <= 1.0); positive(requireNotNull(kcalPerGram)) }
        }
    }
}

/** One nutrient's own source table row; no shared male/female multiplier. */
@Serializable
data class NutrientReferenceRow(
    val nutrient: String, val ages: AgeScope, val sex: CalculationSex?, val stage: LifeStage,
    val amount: NutrientAmount, val source: VersionRef, val sourceSection: String,
) {
    init {
        require(nutrient.isNotBlank() && sourceSection.isNotBlank())
        require(amount.value != null && amount.unit != NutrientUnit.KCAL)
    }
}

/** Complete executable contract. All numeric policy choices must be supplied by a reviewer.
 * Legacy PolicyRule lists remain editorial descriptions and are not executed as code.
 * K08 supports actual body weight only; another weight basis needs a new contract version.
 */
@Serializable
data class PolicyExecutionRules(
    val contractVersion: Int,
    val modelAgeScope: AgeScope,
    val formulaRequiredInputs: Set<FormulaInput>,
    val excludedAnswerKeys: Set<String>,
    val goalAdjustmentKcal: Map<Goal, Double>,
    val dailyBounds: DailyEnergyBounds,
    val macros: Map<String, MacroRule>,
    val nutrientReferenceMapping: List<NutrientReferenceRow>,
    val ageConfirmationMaxDays: Long,
    val evidenceRefs: List<String>,
) {
    init {
        require(contractVersion == 1)
        require(formulaRequiredInputs.containsAll(FormulaInput.entries))
        require(excludedAnswerKeys.containsAll(HEALTH_ANSWER_KEYS))
        require(excludedAnswerKeys.all { it.isNotBlank() })
        require(goalAdjustmentKcal.keys == Goal.entries.toSet())
        goalAdjustmentKcal.values.forEach { require(it.isFinite()) }
        require(goalAdjustmentKcal.getValue(Goal.LOSE) <= 0)
        require(goalAdjustmentKcal.getValue(Goal.GAIN) >= 0)
        require(goalAdjustmentKcal.getValue(Goal.MAINTAIN) == 0.0)
        require(goalAdjustmentKcal.getValue(Goal.HEALTHY) == 0.0)
        require(macros.keys == setOf("protein", "fat", "carbohydrate"))
        require(ageConfirmationMaxDays > 0)
        require(evidenceRefs.isNotEmpty() && evidenceRefs.all { it.isNotBlank() })
    }

    companion object {
        val HEALTH_ANSWER_KEYS = setOf("eating_risk", "medical_diet", "kidney", "treatment")
    }
}
