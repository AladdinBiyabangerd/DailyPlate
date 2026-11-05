package com.aladdin.dailyplate.domain.policy

import com.aladdin.dailyplate.domain.*
import java.time.Instant
import java.time.ZoneId

/** Pure deterministic K08 core. No production activation provider exists until G2 is resolved. */
object NutritionPolicyEngine {
    /** Current onboarding/profile calculation; no nullable external policy selection. */
    fun assessCurrent(profile: UserProfile, at: RecordedAt): NutritionAssessment =
        CurrentNutritionAssessment.assess(profile, at)

    val MIFFLIN_ST_JEOR_V1 = EnergyFormula.MIFFLIN_V1

    fun assess(profile: UserProfile, policy: NutritionPolicy?, at: RecordedAt): NutritionAssessment =
        evaluate(profile, policy, at, testOnly = false)

    /** Test fixtures cannot confer production approval, even with ACTIVE payload status. */
    internal fun assessTest(profile: UserProfile, policy: NutritionPolicy?, at: RecordedAt): NutritionAssessment =
        evaluate(profile, policy, at, testOnly = true)

    private fun evaluate(profile: UserProfile, policy: NutritionPolicy?, at: RecordedAt,
                         testOnly: Boolean): NutritionAssessment {
        val date = CalendarDate(Instant.parse(at.instant).atZone(ZoneId.of(at.timezone)).toLocalDate().toString())
        val rules = policy?.execution
        val eligibility = ProfileEligibility.assess(profile, date, rules)
        val issues = eligibility.issues.toMutableList()
        fun issue(reason: PolicyReason, field: String? = null) { issues += PolicyIssue(reason, field) }
        if (policy == null) issue(PolicyReason.POLICY_MISSING)
        else {
            if (policy.status != PolicyStatus.ACTIVE) issue(PolicyReason.POLICY_NOT_ACTIVE)
            if (rules == null) issue(PolicyReason.EXECUTION_RULES_MISSING)
            if (policy.formula != MIFFLIN_ST_JEOR_V1) issue(PolicyReason.FORMULA_UNSUPPORTED)
            profile.activityCode?.let { if (it !in policy.activityMapping) issue(PolicyReason.ACTIVITY_UNMAPPED, it) }
        }
        if (!testOnly) issue(PolicyReason.PRODUCTION_APPROVAL_REQUIRED)
        fun fallback(): NutritionAssessment = NutritionAssessment(profile.ref, at, eligibility,
            if (profile.specialistTargetKcal != null) TargetPath.SPECIALIST else TargetPath.GENERAL,
            profile.specialistTargetKcal, null, issues.toList())
        if (profile.specialistTargetKcal != null || issues.isNotEmpty()) return fallback()
        check(policy != null && rules != null)
        val age = requireNotNull(profile.ageYears)
        val sex = requireNotNull(profile.calculationSex)
        val weight = requireNotNull(profile.weightKg)
        val height = requireNotNull(profile.heightCm)
        val activity = requireNotNull(profile.activityCode)
        val factor = policy.activityMapping.getValue(activity)
        val estimate = EnergyFormula.estimate(policy.formula, age, sex, height, weight,
            EnergyActivity.LegacyFactor(factor))
        if (estimate !is EnergyFormulaResult.Estimate) {
            issue(PolicyReason.NON_FINITE_RESULT); return fallback()
        }
        val resting = requireNotNull(estimate.restingKcal)
        val maintenance = estimate.maintenanceKcal
        val daily = maintenance + rules.goalAdjustmentKcal.getValue(profile.goal)
        if (listOf(resting, maintenance, daily).any { !it.isFinite() || it <= 0 }) {
            issue(PolicyReason.NON_FINITE_RESULT); return fallback()
        }
        if (daily < rules.dailyBounds.minimumKcal || daily > rules.dailyBounds.maximumKcal) {
            // Do not silently clamp a target to a limit or invent a fallback energy value.
            issue(PolicyReason.ENERGY_OUT_OF_BOUNDS); return fallback()
        }
        val macros = rules.macros.mapValues { (_, rule) -> when (rule.basis) {
            MacroBasis.GRAMS_PER_KG -> weight * rule.factor
            MacroBasis.ENERGY_FRACTION -> daily * rule.factor / requireNotNull(rule.kcalPerGram)
        } }
        if (macros.values.any { !it.isFinite() || it <= 0 }) {
            issue(PolicyReason.NON_FINITE_RESULT); return fallback()
        }
        val references = NutrientReferenceSelector.select(rules.nutrientReferenceMapping, age, sex, LifeStage.ADULT)
        issues += references.issues
        val calculation = PolicyCalculation(policy.formula, policy.ref, profile.ref, at,
            CalculationInputs(age, requireNotNull(profile.ageAsOfDate), sex, height, weight, activity,
                factor, profile.goal, profile.pregnant, profile.breastfeeding, profile.eligibilityAnswers.toMap()),
            resting, maintenance, daily, macros, references.rows, testOnly = true)
        return NutritionAssessment(profile.ref, at, eligibility, TargetPath.AUTOMATIC_TEST, daily, calculation, issues)
    }
}
