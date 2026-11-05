package com.aladdin.dailyplate.domain.policy

import com.aladdin.dailyplate.domain.CalculationSex
import com.aladdin.dailyplate.domain.Goal

/** Pure research screening. No repositories, specialist targets, production entry point or writes. */
internal object ResearchEnergyEligibilityV2 {
    fun preview(inputs: EerResearchInputsV2,
                contract: ResearchEnergyContractV2 = ResearchEnergyContractV2()): ResearchEnergyPreviewV2 {
        val rules = mutableListOf<ResearchRuleResult>()
        fun check(rule: ResearchEnergyRule, status: ResearchRuleStatus,
                  reason: ResearchEnergyReason? = null, question: ResearchHealthQuestion? = null) {
            rules += ResearchRuleResult(rule, status, reason, question)
        }
        fun condition(rule: ResearchEnergyRule, pass: Boolean, reason: ResearchEnergyReason) =
            check(rule, if (pass) ResearchRuleStatus.PASS else ResearchRuleStatus.FAIL,
                if (pass) null else reason)
        val age = inputs.ageYears
        val height = inputs.heightCm
        val weight = inputs.weightKg
        val missing = age == null || height == null || weight == null
        val valid = !missing && age!! > 0 && height!!.isFinite() && height > 0 &&
            weight!!.isFinite() && weight > 0
        check(ResearchEnergyRule.INPUT_VALIDITY, when {
            missing -> ResearchRuleStatus.UNKNOWN
            valid -> ResearchRuleStatus.PASS
            else -> ResearchRuleStatus.FAIL
        }, if (missing) ResearchEnergyReason.INPUT_MISSING else if (!valid) ResearchEnergyReason.INVALID_INPUT else null)
        condition(ResearchEnergyRule.MEASUREMENT_CONFIRMATION, inputs.measurementsConfirmed,
            ResearchEnergyReason.CONFIRMATION_REQUIRED)
        if (age == null) {
            check(ResearchEnergyRule.MODEL_AGE_SCOPE, ResearchRuleStatus.UNKNOWN, ResearchEnergyReason.INPUT_MISSING)
            check(ResearchEnergyRule.PRODUCT_AGE_SCOPE, ResearchRuleStatus.UNKNOWN, ResearchEnergyReason.INPUT_MISSING)
        } else {
            // The adult equation starts at 19; 64 is a product boundary, not its scientific upper limit.
            condition(ResearchEnergyRule.MODEL_AGE_SCOPE, age >= 19, ResearchEnergyReason.MODEL_AGE_OUT_OF_SCOPE)
            condition(ResearchEnergyRule.PRODUCT_AGE_SCOPE, age in 19..64, ResearchEnergyReason.PRODUCT_AGE_OUT_OF_SCOPE)
        }
        check(ResearchEnergyRule.SEX_REQUIRED,
            if (inputs.sex == null) ResearchRuleStatus.UNKNOWN else ResearchRuleStatus.PASS,
            if (inputs.sex == null) ResearchEnergyReason.SEX_MISSING else null)
        condition(ResearchEnergyRule.ACTIVITY_CONFIRMATION, inputs.activity != null && inputs.activityConfirmed,
            ResearchEnergyReason.ACTIVITY_UNCERTAIN)
        ResearchHealthQuestion.entries.forEach { question ->
            val answer = inputs.healthAnswers[question]
            check(ResearchEnergyRule.HEALTH_SCREENING, when (answer) {
                null -> ResearchRuleStatus.UNKNOWN
                true -> ResearchRuleStatus.FAIL
                false -> ResearchRuleStatus.PASS
            }, when (answer) {
                null -> ResearchEnergyReason.HEALTH_ANSWER_MISSING
                true -> ResearchEnergyReason.HEALTH_EXCLUSION
                false -> null
            }, question)
        }
        val bmi = if (valid) (weight!! / (height!! / 100.0) / (height / 100.0))
            .takeIf { it.isFinite() && it > 0 } else null
        if (bmi == null) check(ResearchEnergyRule.BMI_SCOPE, ResearchRuleStatus.UNKNOWN, ResearchEnergyReason.INVALID_INPUT)
        else condition(ResearchEnergyRule.BMI_SCOPE, bmi >= 18.5 && bmi < 30.0, ResearchEnergyReason.BMI_OUT_OF_SCOPE)
        val goal = inputs.goal
        if (bmi == null || goal == null)
            check(ResearchEnergyRule.GOAL_SCOPE, ResearchRuleStatus.UNKNOWN, ResearchEnergyReason.INPUT_MISSING)
        else condition(ResearchEnergyRule.GOAL_SCOPE, when (goal) {
            Goal.LOSE -> bmi >= 25 && bmi < 30
            Goal.GAIN -> bmi >= 18.5 && bmi < 25
            Goal.MAINTAIN, Goal.HEALTHY -> bmi >= 18.5 && bmi < 30
        }, ResearchEnergyReason.GOAL_OUT_OF_SCOPE)
        fun result(maintenance: Double? = null, target: Double? = null) =
            ResearchEnergyPreviewV2(contract, inputs, rules.toList(), bmi, maintenance, target)
        if (rules.any { it.status != ResearchRuleStatus.PASS }) return result()
        val estimate = EnergyFormula.estimate(contract.formula, requireNotNull(age), requireNotNull(inputs.sex),
            requireNotNull(height), requireNotNull(weight), EnergyActivity.ConfirmedEer(requireNotNull(inputs.activity)))
        if (estimate !is EnergyFormulaResult.Estimate) {
            check(ResearchEnergyRule.ENERGY_FLOOR, ResearchRuleStatus.FAIL, ResearchEnergyReason.INVALID_FORMULA_RESULT)
            return result()
        }
        val target = estimate.maintenanceKcal + when (requireNotNull(goal)) {
            Goal.LOSE -> -500.0
            Goal.GAIN -> 300.0
            Goal.MAINTAIN, Goal.HEALTHY -> 0.0
        }
        val floor = if (inputs.sex == CalculationSex.FEMALE) 1500.0 else 1800.0
        condition(ResearchEnergyRule.ENERGY_FLOOR, target.isFinite() && target >= floor,
            ResearchEnergyReason.ENERGY_BELOW_PRODUCT_FLOOR)
        return result(estimate.maintenanceKcal, target.takeIf { rules.all { r -> r.status == ResearchRuleStatus.PASS } })
    }
}
