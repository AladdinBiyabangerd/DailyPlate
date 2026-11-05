package com.aladdin.dailyplate.domain.policy

import com.aladdin.dailyplate.domain.CalculationSex
import com.aladdin.dailyplate.domain.Goal
import com.aladdin.dailyplate.domain.VersionRef
import kotlinx.serialization.Serializable

/** Isolated research representation; never accepted by the v1 persistence/production engine. */
@Serializable
internal data class ResearchEnergyContractV2(
    val contractVersion: Int = 2,
    val candidate: VersionRef = VersionRef("dailyplate-adult-energy-candidate", 1),
    val formula: VersionRef = VersionRef("nasem-2023-adult-eer", 1),
    val status: ResearchPolicyStatus = ResearchPolicyStatus.RESEARCH_PROPOSAL,
    val productionEnabled: Boolean = false,
    val approvedBy: String? = null,
    val approvedAt: String? = null,
    val provenance: ResearchEnergyProvenance = ResearchEnergyProvenance(),
) {
    init {
        require(contractVersion == 2)
        require(candidate == VersionRef("dailyplate-adult-energy-candidate", 1))
        require(formula == VersionRef("nasem-2023-adult-eer", 1))
        require(!productionEnabled && approvedBy == null && approvedAt == null)
        require(provenance == ResearchEnergyProvenance())
    }
}

@Serializable internal enum class ResearchPolicyStatus { RESEARCH_PROPOSAL }

@Serializable
internal data class ResearchEnergyProvenance(
    val canonicalSource: String = "planning/research-data.json#nutrition_policy_candidate",
    val designSections: String = "26–29 / NP-01–12, NP-28",
    val formulaEvidence: List<String> = listOf("S23", "S24"),
    val scopeEvidence: List<String> = listOf("S7", "S29"),
    val goalAndFloorEvidence: List<String> = listOf("S14", "S25", "S26"),
    val scopeKind: String = "PRODUCT_PILOT_SCOPE_NOT_MODEL_LIMIT_OR_DIAGNOSIS",
    val floorKind: String = "PROPOSED_PRODUCT_GUARDRAIL_NOT_PROVEN_SAFETY_THRESHOLD",
)

@Serializable
internal enum class ResearchHealthQuestion {
    PREGNANT, BREASTFEEDING, EATING_RISK, MEDICAL_DIET, KIDNEY, TREATMENT,
    DIABETES, UNINTENTIONAL_WEIGHT_LOSS, HIGH_PERFORMANCE_SPORT, BODY_COMPOSITION_AFFECTS_BMI,
}

/** No activity factor: category must be explicitly confirmed against the four-choice model. */
@Serializable
internal data class EerResearchInputsV2(
    val ageYears: Int?, val sex: CalculationSex?, val heightCm: Double?, val weightKg: Double?,
    val measurementsConfirmed: Boolean, val activity: EerActivity?, val activityConfirmed: Boolean,
    val goal: Goal?, val healthAnswers: Map<ResearchHealthQuestion, Boolean?>,
)

@Serializable internal enum class ResearchRuleStatus { PASS, FAIL, UNKNOWN }
@Serializable internal enum class ResearchEnergyRule {
    INPUT_VALIDITY, MEASUREMENT_CONFIRMATION, MODEL_AGE_SCOPE, PRODUCT_AGE_SCOPE,
    SEX_REQUIRED, ACTIVITY_CONFIRMATION, HEALTH_SCREENING, BMI_SCOPE, GOAL_SCOPE, ENERGY_FLOOR,
}
@Serializable internal enum class ResearchEnergyReason {
    INPUT_MISSING, INVALID_INPUT, CONFIRMATION_REQUIRED, MODEL_AGE_OUT_OF_SCOPE,
    PRODUCT_AGE_OUT_OF_SCOPE, SEX_MISSING, ACTIVITY_UNCERTAIN, HEALTH_ANSWER_MISSING,
    HEALTH_EXCLUSION, BMI_OUT_OF_SCOPE, GOAL_OUT_OF_SCOPE, ENERGY_BELOW_PRODUCT_FLOOR,
    INVALID_FORMULA_RESULT,
}
@Serializable
internal data class ResearchRuleResult(
    val rule: ResearchEnergyRule, val status: ResearchRuleStatus,
    val reason: ResearchEnergyReason? = null, val healthQuestion: ResearchHealthQuestion? = null,
)

/** Candidate energy preview only, not a complete macro/menu plan or approved personal target. */
@Serializable
internal data class ResearchEnergyPreviewV2(
    val contract: ResearchEnergyContractV2, val inputs: EerResearchInputsV2,
    val rules: List<ResearchRuleResult>, val bmi: Double?,
    val maintenanceKcal: Double?, val candidateTargetKcal: Double?,
) {
    init {
        require(candidateTargetKcal == null ||
            (rules.all { it.status == ResearchRuleStatus.PASS } &&
                candidateTargetKcal.isFinite() && candidateTargetKcal > 0))
    }
}
