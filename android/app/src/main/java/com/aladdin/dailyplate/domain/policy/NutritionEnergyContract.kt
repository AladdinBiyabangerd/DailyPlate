package com.aladdin.dailyplate.domain.policy

import com.aladdin.dailyplate.domain.CalculationSex
import com.aladdin.dailyplate.domain.Goal
import com.aladdin.dailyplate.domain.VersionRef
import kotlinx.serialization.Serializable

/** Direct-EER contract, separate from historical scalar-factor snapshots. */
@Serializable
data class NutritionEnergyContract(
    val contractVersion: Int = 2,
    val candidate: VersionRef = VersionRef("dailyplate-adult-energy-candidate", 1),
    val formula: VersionRef = VersionRef("nasem-2023-adult-eer", 1),
    val status: NutritionPolicyStatus = NutritionPolicyStatus.SPECIALIST_APPROVAL_REPORTED,
    // False default preserves historical payloads that omitted this field.
    val productionEnabled: Boolean = false,
    val approvedBy: String? = null,
    val approvedAt: String? = null,
    val provenance: NutritionEnergyProvenance = NutritionEnergyProvenance(),
) {
    init {
        require(contractVersion == 2)
        require(candidate == VersionRef("dailyplate-adult-energy-candidate", 1))
        require(formula == VersionRef("nasem-2023-adult-eer", 1))
        require(approvedBy == null && approvedAt == null)
        require(!productionEnabled || status == NutritionPolicyStatus.SPECIALIST_APPROVAL_REPORTED)
        require(provenance == NutritionEnergyProvenance())
    }
}

@Serializable enum class NutritionPolicyStatus { RESEARCH_PROPOSAL, SPECIALIST_APPROVAL_REPORTED }

@Serializable
data class NutritionEnergyProvenance(
    val canonicalSource: String = "planning/research-data.json#nutrition_policy_candidate",
    val designSections: String = "26–29 / NP-01–12, NP-28",
    val approvalBasis: String = "USER_ATTESTATION",
    val approvalScope: String = "2.1-research / design section 30",
    val approvedCanonicalSha256: String = "d008480bf08dc912094b0c73000bf071843fc3c5883afdba930a14e30b122705",
    val formulaEvidence: List<String> = listOf("S23", "S24"),
    val scopeEvidence: List<String> = listOf("S7", "S29"),
    val goalAndFloorEvidence: List<String> = listOf("S14", "S25", "S26"),
    val scopeKind: String = "PRODUCT_PILOT_SCOPE_NOT_MODEL_LIMIT_OR_DIAGNOSIS",
    val floorKind: String = "PROPOSED_PRODUCT_GUARDRAIL_NOT_PROVEN_SAFETY_THRESHOLD",
)

@Serializable
enum class NutritionHealthQuestion {
    PREGNANT, BREASTFEEDING, EATING_RISK, MEDICAL_DIET, KIDNEY, TREATMENT,
    DIABETES, UNINTENTIONAL_WEIGHT_LOSS, HIGH_PERFORMANCE_SPORT, BODY_COMPOSITION_AFFECTS_BMI,
}

/** No activity factor: category must be explicitly confirmed against the four-choice model. */
@Serializable
data class NutritionInputs(
    val ageYears: Int?, val sex: CalculationSex?, val heightCm: Double?, val weightKg: Double?,
    val measurementsConfirmed: Boolean, val activity: EerActivity?, val activityConfirmed: Boolean,
    val goal: Goal?, val healthAnswers: Map<NutritionHealthQuestion, Boolean?>,
)

@Serializable enum class NutritionRuleStatus { PASS, FAIL, UNKNOWN }
@Serializable enum class NutritionEnergyRule {
    INPUT_VALIDITY, MEASUREMENT_CONFIRMATION, MODEL_AGE_SCOPE, PRODUCT_AGE_SCOPE,
    SEX_REQUIRED, ACTIVITY_CONFIRMATION, HEALTH_SCREENING, BMI_SCOPE, GOAL_SCOPE, ENERGY_FLOOR,
}
@Serializable enum class NutritionEnergyReason {
    INPUT_MISSING, INVALID_INPUT, CONFIRMATION_REQUIRED, MODEL_AGE_OUT_OF_SCOPE,
    PRODUCT_AGE_OUT_OF_SCOPE, SEX_MISSING, ACTIVITY_UNCERTAIN, HEALTH_ANSWER_MISSING,
    HEALTH_EXCLUSION, BMI_OUT_OF_SCOPE, GOAL_OUT_OF_SCOPE, ENERGY_BELOW_PRODUCT_FLOOR,
    INVALID_FORMULA_RESULT,
}
@Serializable
data class NutritionRuleResult(
    val rule: NutritionEnergyRule, val status: NutritionRuleStatus,
    val reason: NutritionEnergyReason? = null, val healthQuestion: NutritionHealthQuestion? = null,
)

/** Energy calculation only; not evidence of a complete menu. */
@Serializable
data class NutritionEnergyPreview(
    val contract: NutritionEnergyContract, val inputs: NutritionInputs,
    val rules: List<NutritionRuleResult>, val bmi: Double?,
    val maintenanceKcal: Double?, val candidateTargetKcal: Double?,
) {
    init {
        require(candidateTargetKcal == null ||
            (rules.all { it.status == NutritionRuleStatus.PASS } &&
                candidateTargetKcal.isFinite() && candidateTargetKcal > 0))
    }
}
