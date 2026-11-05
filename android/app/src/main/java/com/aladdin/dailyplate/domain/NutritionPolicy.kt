package com.aladdin.dailyplate.domain

import kotlinx.serialization.Serializable
import com.aladdin.dailyplate.domain.policy.PolicyExecutionRules

/** Legacy editorial rule descriptions; executable K08 rules are typed separately. */
@Serializable
data class PolicyRule(val code: String, val parameters: Map<String, String>, val evidenceRefs: List<String>)

@Serializable
enum class PolicyStatus { DRAFT, APPROVED, ACTIVE, WITHDRAWN }

@Serializable
data class NutritionPolicy(
    val ref: VersionRef, val eligiblePopulation: String,
    val exclusionRules: List<PolicyRule>, val formula: VersionRef,
    val requiredInputs: Set<String>, val activityMapping: Map<String, Double>,
    val energyAdjustment: Map<Goal, PolicyRule>, val energyBounds: List<PolicyRule>,
    val macroRules: List<PolicyRule>, val nutrientChecks: List<PolicyRule>,
    val reassessmentRules: List<PolicyRule>, val evidenceRefs: List<String>,
    val review: Review?, val approvedAt: RecordedAt?, val status: PolicyStatus,
    // Absent in K02/K05 snapshots: legacy policies remain readable but cannot execute.
    val execution: PolicyExecutionRules? = null,
) {
    init {
        activityMapping.values.forEach(::positive)
        if (status == PolicyStatus.ACTIVE || status == PolicyStatus.APPROVED) {
            require(review != null && approvedAt != null && evidenceRefs.isNotEmpty())
        }
    }
}

@Serializable
data class EnergyEstimate(
    val formula: VersionRef, val policy: VersionRef, val profile: VersionRef,
    val inputs: Map<String, Double>, val calculatedAt: RecordedAt, val dailyKcal: Double,
) { init { positive(dailyKcal); inputs.values.forEach { require(it.isFinite()) } } }
