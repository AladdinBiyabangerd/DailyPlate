package com.aladdin.dailyplate.domain.policy

import com.aladdin.dailyplate.domain.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlinx.serialization.Serializable

@Serializable
enum class EligibilityStatus { ELIGIBLE, INCOMPLETE, EXCLUDED }

@Serializable
enum class PolicyReason {
    MEASUREMENT_CONFIRMATION_REQUIRED, BMI_OUT_OF_SCOPE, GOAL_OUT_OF_SCOPE, PLANNING_INFEASIBLE,
    AGE_MISSING, UNDER_18, MODEL_AGE_OUT_OF_SCOPE, AGE_CONFIRMATION_REQUIRED,
    SEX_MISSING, HEIGHT_MISSING, WEIGHT_MISSING, ACTIVITY_MISSING, ACTIVITY_UNMAPPED,
    PREGNANT, BREASTFEEDING, HEALTH_EXCLUSION, HEALTH_ANSWER_MISSING,
    POLICY_MISSING, POLICY_NOT_ACTIVE, EXECUTION_RULES_MISSING,
    PRODUCTION_APPROVAL_REQUIRED, FORMULA_UNSUPPORTED, ENERGY_OUT_OF_BOUNDS,
    NON_FINITE_RESULT, NUTRIENT_REFERENCE_MISSING, NUTRIENT_REFERENCE_AMBIGUOUS,
}

@Serializable
data class PolicyIssue(val reason: PolicyReason, val field: String? = null)

@Serializable
data class EligibilityAssessment(val status: EligibilityStatus, val issues: List<PolicyIssue>)

/** Screening only; no diagnosis. Explicit risks take precedence over unanswered questions. */
object ProfileEligibility {
    fun assess(profile: UserProfile, date: CalendarDate,
               rules: PolicyExecutionRules? = null): EligibilityAssessment {
        val excluded = mutableListOf<PolicyIssue>()
        val missing = mutableListOf<PolicyIssue>()
        fun miss(reason: PolicyReason, field: String? = null) { missing += PolicyIssue(reason, field) }
        val age = profile.ageYears
        if (age == null) miss(PolicyReason.AGE_MISSING)
        else if (age < 18) excluded += PolicyIssue(PolicyReason.UNDER_18)
        else if (rules != null && age !in rules.modelAgeScope)
            excluded += PolicyIssue(PolicyReason.MODEL_AGE_OUT_OF_SCOPE)

        val asOf = profile.ageAsOfDate?.let { LocalDate.parse(it.iso) }
        val today = LocalDate.parse(date.iso)
        if (asOf == null || asOf.isAfter(today) ||
            (rules != null && ChronoUnit.DAYS.between(asOf, today) > rules.ageConfirmationMaxDays))
            miss(PolicyReason.AGE_CONFIRMATION_REQUIRED)
        // An integer age is not a birth date. Never guess a birthday or silently increment age.
        if (profile.calculationSex == null) miss(PolicyReason.SEX_MISSING)
        if (profile.heightCm == null) miss(PolicyReason.HEIGHT_MISSING)
        if (profile.weightKg == null) miss(PolicyReason.WEIGHT_MISSING)
        if (profile.activityCode.isNullOrBlank()) miss(PolicyReason.ACTIVITY_MISSING)

        fun health(value: Boolean?, field: String, reason: PolicyReason, required: Boolean = true) {
            if (value == true) excluded += PolicyIssue(reason, field)
            else if (value == null && required) miss(PolicyReason.HEALTH_ANSWER_MISSING, field)
        }
        // Male onboarding does not ask these questions. Explicit true still excludes any profile.
        val asksReproductiveHealth = profile.calculationSex != CalculationSex.MALE
        health(profile.pregnant, "pregnant", PolicyReason.PREGNANT, asksReproductiveHealth)
        health(profile.breastfeeding, "breastfeeding", PolicyReason.BREASTFEEDING, asksReproductiveHealth)
        (rules?.excludedAnswerKeys ?: PolicyExecutionRules.HEALTH_ANSWER_KEYS).sorted().forEach {
            health(profile.eligibilityAnswers[it], it, PolicyReason.HEALTH_EXCLUSION)
        }
        return EligibilityAssessment(when {
            excluded.isNotEmpty() -> EligibilityStatus.EXCLUDED
            missing.isNotEmpty() -> EligibilityStatus.INCOMPLETE
            else -> EligibilityStatus.ELIGIBLE
        }, excluded + missing)
    }
}
