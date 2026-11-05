package com.aladdin.dailyplate.domain.policy

import com.aladdin.dailyplate.domain.CalculationSex

data class NutrientReferenceSelection(val rows: List<NutrientReferenceRow>, val issues: List<PolicyIssue>)

object NutrientReferenceSelector {
    fun select(table: List<NutrientReferenceRow>, age: Int?, sex: CalculationSex?,
               stage: LifeStage?): NutrientReferenceSelection {
        val selected = mutableListOf<NutrientReferenceRow>()
        val issues = mutableListOf<PolicyIssue>()
        table.groupBy { it.nutrient }.toSortedMap().forEach { (nutrient, rows) ->
            val matches = rows.filter { age != null && age in it.ages && stage == it.stage &&
                (it.sex == null || sex == it.sex) }
            when (matches.size) {
                1 -> selected += matches.single()
                0 -> issues += PolicyIssue(PolicyReason.NUTRIENT_REFERENCE_MISSING, nutrient)
                else -> issues += PolicyIssue(PolicyReason.NUTRIENT_REFERENCE_AMBIGUOUS, nutrient)
            }
        }
        return NutrientReferenceSelection(selected, issues)
    }
}
