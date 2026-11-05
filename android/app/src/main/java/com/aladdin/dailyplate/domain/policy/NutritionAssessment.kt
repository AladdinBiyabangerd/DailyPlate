package com.aladdin.dailyplate.domain.policy

import com.aladdin.dailyplate.domain.*
import kotlinx.serialization.Serializable

@Serializable
enum class TargetPath { GENERAL, SPECIALIST, AUTOMATIC_TEST }

/** Frozen inputs, including the age confirmation date and explicit sex/health answers. */
@Serializable
data class CalculationInputs(
    val ageYears: Int, val ageAsOfDate: CalendarDate, val sex: CalculationSex,
    val heightCm: Double, val weightKg: Double, val activityCode: String,
    val activityFactor: Double, val goal: Goal,
    val pregnant: Boolean?, val breastfeeding: Boolean?,
    val healthAnswers: Map<String, Boolean?>,
)

@Serializable
data class PolicyCalculation(
    val formula: VersionRef, val policy: VersionRef, val profile: VersionRef,
    val calculatedAt: RecordedAt, val inputs: CalculationInputs,
    val restingKcal: Double, val maintenanceKcal: Double, val dailyKcal: Double,
    val macroGrams: Map<String, Double>, val nutrientReferences: List<NutrientReferenceRow>,
    val testOnly: Boolean,
) {
    init {
        positive(restingKcal); positive(maintenanceKcal); positive(dailyKcal)
        macroGrams.values.forEach(::positive)
        require(testOnly) { "K08 has no production activation provider" }
    }
}

/** Specialist values are user-entered targets, never labelled formula predictions.
 * They do not override screening or authorize clinical menus/macros.
 */
@Serializable
data class NutritionAssessment(
    val profile: VersionRef, val assessedAt: RecordedAt,
    val eligibility: EligibilityAssessment, val path: TargetPath,
    val dailyKcal: Double?, val calculation: PolicyCalculation?, val issues: List<PolicyIssue>,
    val canUseGeneralDiary: Boolean = true,
    val canEnterSpecialistTarget: Boolean = true,
) {
    init {
        dailyKcal?.let(::positive)
        require((path == TargetPath.AUTOMATIC_TEST) == (calculation != null))
        require(path != TargetPath.GENERAL || dailyKcal == null)
        require(path != TargetPath.SPECIALIST || dailyKcal != null)
        calculation?.let {
            require(it.profile == profile && it.dailyKcal == dailyKcal && it.calculatedAt == assessedAt)
            require(eligibility.status == EligibilityStatus.ELIGIBLE)
        }
    }
}

/** Append-only assessment history; future K09 confirmation chooses a new profile revision. */
@Serializable
data class NutritionAssessmentRecord(
    val ref: VersionRef, val userId: String, val assessment: NutritionAssessment,
) {
    init {
        require(userId.isNotBlank())
        require(ref == assessment.profile)
    }
}
