package com.aladdin.dailyplate.domain.policy

import com.aladdin.dailyplate.domain.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Locale

/** One current profile adapter. Legacy snapshots are decoded, never recalculated on read. */
internal object CurrentNutritionAssessment {
    const val ACTIVITY_MODEL = "nasem-2023-four-category"

    fun assess(profile: UserProfile, at: RecordedAt): NutritionAssessment {
        if (profile.nutritionSuspension != null) {
            val issue = PolicyIssue(PolicyReason.HEALTH_EXCLUSION, profile.nutritionSuspension.name)
            return NutritionAssessment(profile.ref, at,
                EligibilityAssessment(EligibilityStatus.EXCLUDED, listOf(issue)),
                TargetPath.GENERAL, null, null, listOf(issue))
        }
        val today = Instant.parse(at.instant).atZone(ZoneId.of(at.timezone)).toLocalDate()
        val confirmedDate = profile.ageAsOfDate?.let { LocalDate.parse(it.iso) }
        val fresh = confirmedDate != null && !confirmedDate.isAfter(today) &&
            ChronoUnit.DAYS.between(confirmedDate, today) <= 365
        val input = NutritionInputs(profile.ageYears, profile.calculationSex, profile.heightCm,
            profile.weightKg, profile.measurementsConfirmed && fresh,
            EerActivity.entries.firstOrNull { it.code == profile.activityCode },
            profile.activityModel == ACTIVITY_MODEL, profile.goal,
            NutritionHealthQuestion.entries.associateWith { question -> when (question) {
                NutritionHealthQuestion.PREGNANT -> profile.pregnant
                NutritionHealthQuestion.BREASTFEEDING -> profile.breastfeeding
                else -> profile.eligibilityAnswers[question.name.lowercase(Locale.ROOT)]
            } })
        val preview = NutritionPlanning.preview(input, profile.mealsPerDay)
        val failed = preview.energy.rules.filter { it.status != NutritionRuleStatus.PASS }
        val issues = failed.map { rule -> PolicyIssue(when (rule.reason) {
            NutritionEnergyReason.SEX_MISSING -> PolicyReason.SEX_MISSING
            NutritionEnergyReason.ACTIVITY_UNCERTAIN -> PolicyReason.ACTIVITY_UNMAPPED
            NutritionEnergyReason.HEALTH_ANSWER_MISSING -> PolicyReason.HEALTH_ANSWER_MISSING
            NutritionEnergyReason.HEALTH_EXCLUSION -> PolicyReason.HEALTH_EXCLUSION
            NutritionEnergyReason.MODEL_AGE_OUT_OF_SCOPE,
            NutritionEnergyReason.PRODUCT_AGE_OUT_OF_SCOPE -> PolicyReason.MODEL_AGE_OUT_OF_SCOPE
            NutritionEnergyReason.CONFIRMATION_REQUIRED -> PolicyReason.MEASUREMENT_CONFIRMATION_REQUIRED
            NutritionEnergyReason.BMI_OUT_OF_SCOPE -> PolicyReason.BMI_OUT_OF_SCOPE
            NutritionEnergyReason.GOAL_OUT_OF_SCOPE -> PolicyReason.GOAL_OUT_OF_SCOPE
            NutritionEnergyReason.ENERGY_BELOW_PRODUCT_FLOOR -> PolicyReason.ENERGY_OUT_OF_BOUNDS
            else -> PolicyReason.NON_FINITE_RESULT
        }, rule.healthQuestion?.name?.lowercase(Locale.ROOT)) }.toMutableList()
        if (!fresh) issues += PolicyIssue(PolicyReason.AGE_CONFIRMATION_REQUIRED)
        if (preview.failure != null && preview.failure != NutritionPlanningFailure.INPUT_BLOCKED)
            issues += PolicyIssue(PolicyReason.PLANNING_INFEASIBLE, preview.failure.name)
        val eligibility = EligibilityAssessment(when {
            failed.any { it.status == NutritionRuleStatus.FAIL && it.rule !in setOf(
                NutritionEnergyRule.MEASUREMENT_CONFIRMATION, NutritionEnergyRule.ACTIVITY_CONFIRMATION) } -> EligibilityStatus.EXCLUDED
            issues.isNotEmpty() -> EligibilityStatus.INCOMPLETE
            else -> EligibilityStatus.ELIGIBLE
        }, issues.toList())
        // User-entered specialist targets remain separate and never receive automatic macros.
        val specialist = profile.specialistTargetKcal
        val path = when {
            specialist != null -> TargetPath.SPECIALIST
            preview.targetKcal != null -> TargetPath.AUTOMATIC
            else -> TargetPath.GENERAL
        }
        return NutritionAssessment(profile.ref, at, eligibility, path,
            specialist ?: preview.targetKcal, null, issues.toList(),
            currentCalculation = if (specialist == null) preview else null)
    }
}
