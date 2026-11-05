package com.aladdin.dailyplate.domain.policy

import com.aladdin.dailyplate.domain.CalculationSex
import com.aladdin.dailyplate.domain.Goal

/** Pure current energy screening. No repositories or writes. */
object NutritionEnergyEligibility {
    fun preview(inputs: NutritionInputs,
                contract: NutritionEnergyContract = NutritionEnergyContract()): NutritionEnergyPreview {
        val rules = mutableListOf<NutritionRuleResult>()
        fun check(rule: NutritionEnergyRule, status: NutritionRuleStatus,
                  reason: NutritionEnergyReason? = null, question: NutritionHealthQuestion? = null) {
            rules += NutritionRuleResult(rule, status, reason, question)
        }
        fun condition(rule: NutritionEnergyRule, pass: Boolean, reason: NutritionEnergyReason) =
            check(rule, if (pass) NutritionRuleStatus.PASS else NutritionRuleStatus.FAIL,
                if (pass) null else reason)
        val age = inputs.ageYears
        val height = inputs.heightCm
        val weight = inputs.weightKg
        val missing = age == null || height == null || weight == null
        val valid = !missing && age!! > 0 && height!!.isFinite() && height > 0 &&
            weight!!.isFinite() && weight > 0
        check(NutritionEnergyRule.INPUT_VALIDITY, when {
            missing -> NutritionRuleStatus.UNKNOWN
            valid -> NutritionRuleStatus.PASS
            else -> NutritionRuleStatus.FAIL
        }, if (missing) NutritionEnergyReason.INPUT_MISSING else if (!valid) NutritionEnergyReason.INVALID_INPUT else null)
        condition(NutritionEnergyRule.MEASUREMENT_CONFIRMATION, inputs.measurementsConfirmed,
            NutritionEnergyReason.CONFIRMATION_REQUIRED)
        if (age == null) {
            check(NutritionEnergyRule.MODEL_AGE_SCOPE, NutritionRuleStatus.UNKNOWN, NutritionEnergyReason.INPUT_MISSING)
            check(NutritionEnergyRule.PRODUCT_AGE_SCOPE, NutritionRuleStatus.UNKNOWN, NutritionEnergyReason.INPUT_MISSING)
        } else {
            // The adult equation starts at 19; 64 is a product boundary, not its scientific upper limit.
            condition(NutritionEnergyRule.MODEL_AGE_SCOPE, age >= 19, NutritionEnergyReason.MODEL_AGE_OUT_OF_SCOPE)
            condition(NutritionEnergyRule.PRODUCT_AGE_SCOPE, age in 19..64, NutritionEnergyReason.PRODUCT_AGE_OUT_OF_SCOPE)
        }
        check(NutritionEnergyRule.SEX_REQUIRED,
            if (inputs.sex == null) NutritionRuleStatus.UNKNOWN else NutritionRuleStatus.PASS,
            if (inputs.sex == null) NutritionEnergyReason.SEX_MISSING else null)
        condition(NutritionEnergyRule.ACTIVITY_CONFIRMATION, inputs.activity != null && inputs.activityConfirmed,
            NutritionEnergyReason.ACTIVITY_UNCERTAIN)
        NutritionHealthQuestion.entries.forEach { question ->
            val answer = inputs.healthAnswers[question]
            check(NutritionEnergyRule.HEALTH_SCREENING, when (answer) {
                null -> NutritionRuleStatus.UNKNOWN
                true -> NutritionRuleStatus.FAIL
                false -> NutritionRuleStatus.PASS
            }, when (answer) {
                null -> NutritionEnergyReason.HEALTH_ANSWER_MISSING
                true -> NutritionEnergyReason.HEALTH_EXCLUSION
                false -> null
            }, question)
        }
        val bmi = if (valid) (weight!! / (height!! / 100.0) / (height / 100.0))
            .takeIf { it.isFinite() && it > 0 } else null
        if (bmi == null) check(NutritionEnergyRule.BMI_SCOPE, NutritionRuleStatus.UNKNOWN, NutritionEnergyReason.INVALID_INPUT)
        else condition(NutritionEnergyRule.BMI_SCOPE, bmi >= 18.5 && bmi < 30.0, NutritionEnergyReason.BMI_OUT_OF_SCOPE)
        val goal = inputs.goal
        if (bmi == null || goal == null)
            check(NutritionEnergyRule.GOAL_SCOPE, NutritionRuleStatus.UNKNOWN, NutritionEnergyReason.INPUT_MISSING)
        else condition(NutritionEnergyRule.GOAL_SCOPE, when (goal) {
            Goal.LOSE -> bmi >= 25 && bmi < 30
            Goal.GAIN -> bmi >= 18.5 && bmi < 25
            Goal.MAINTAIN, Goal.HEALTHY -> bmi >= 18.5 && bmi < 30
        }, NutritionEnergyReason.GOAL_OUT_OF_SCOPE)
        fun result(maintenance: Double? = null, target: Double? = null) =
            NutritionEnergyPreview(contract, inputs, rules.toList(), bmi, maintenance, target)
        if (rules.any { it.status != NutritionRuleStatus.PASS }) return result()
        val estimate = EnergyFormula.estimate(contract.formula, requireNotNull(age), requireNotNull(inputs.sex),
            requireNotNull(height), requireNotNull(weight), EnergyActivity.ConfirmedEer(requireNotNull(inputs.activity)))
        if (estimate !is EnergyFormulaResult.Estimate) {
            check(NutritionEnergyRule.ENERGY_FLOOR, NutritionRuleStatus.FAIL, NutritionEnergyReason.INVALID_FORMULA_RESULT)
            return result()
        }
        val target = estimate.maintenanceKcal + when (requireNotNull(goal)) {
            Goal.LOSE -> -500.0
            Goal.GAIN -> 300.0
            Goal.MAINTAIN, Goal.HEALTHY -> 0.0
        }
        val floor = if (inputs.sex == CalculationSex.FEMALE) 1500.0 else 1800.0
        condition(NutritionEnergyRule.ENERGY_FLOOR, target.isFinite() && target >= floor,
            NutritionEnergyReason.ENERGY_BELOW_PRODUCT_FLOOR)
        return result(estimate.maintenanceKcal, target.takeIf { rules.all { r -> r.status == NutritionRuleStatus.PASS } })
    }
}
