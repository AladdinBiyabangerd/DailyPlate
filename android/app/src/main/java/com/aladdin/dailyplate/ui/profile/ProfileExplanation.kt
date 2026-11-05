package com.aladdin.dailyplate.ui.profile

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalUriHandler
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.profile.ProfileEdit
import com.aladdin.dailyplate.domain.policy.*
import com.aladdin.dailyplate.ui.QidaCard

private val fieldLabels = mapOf(
    "goal" to R.string.ob_goal, "age" to R.string.ob_age, "sex" to R.string.ob_sex,
    "height" to R.string.ob_height, "weight" to R.string.ob_weight, "activity" to R.string.ob_activity,
    "pregnant" to R.string.ob_pregnant, "breastfeeding" to R.string.ob_breastfeeding,
    "eating_risk" to R.string.ob_eating_risk, "medical_diet" to R.string.ob_medical_diet,
    "kidney" to R.string.ob_kidney, "treatment" to R.string.ob_treatment,
    "diabetes" to R.string.ob_diabetes, "unintentional_weight_loss" to R.string.ob_unintentional_loss,
    "high_performance_sport" to R.string.ob_high_sport, "body_composition_affects_bmi" to R.string.ob_body_composition,
    "activity_model" to R.string.ob_activity_question, "measurements_confirmed" to R.string.ob_measurements_confirm,
    "allergies" to R.string.ob_allergies, "extra_allergies" to R.string.ob_extra_allergies,
    "restrictions" to R.string.ob_restrictions, "disliked_foods" to R.string.ob_disliked_foods,
    "disliked_recipes" to R.string.ob_disliked_recipes, "time" to R.string.ob_time,
    "meals" to R.string.ob_meals, "tier" to R.string.ob_tier, "region" to R.string.ob_region,
    "currency" to R.string.ob_currency, "amount" to R.string.ob_amount, "people" to R.string.ob_people,
    "specialist_target" to R.string.pf_specialist, "age_confirmation" to R.string.ob_age_date,
)
@Composable
internal fun profileFieldLabel(key: String): String = fieldLabels[key]?.let { stringResource(it) } ?: key

@Composable
internal fun profileAnswer(key: String, value: String, p: UserProfile): String {
    if (key == "specialist_target") return p.specialistTargetKcal?.toString() ?: "—"
    if (key == "age_confirmation") return p.ageAsOfDate?.iso ?: "—"
    val options = when (key) {
        "goal" -> mapOf("LOSE" to R.string.ob_lose, "GAIN" to R.string.ob_gain, "MAINTAIN" to R.string.ob_maintain, "HEALTHY" to R.string.ob_healthy)
        "sex" -> mapOf("MALE" to R.string.ob_male, "FEMALE" to R.string.ob_female)
        "activity" -> mapOf("inactive" to R.string.ob_inactive, "low_active" to R.string.ob_low_active,
            "very_active" to R.string.ob_very_active, "uncertain" to R.string.ob_activity_uncertain, "sedentary" to R.string.ob_sedentary, "some" to R.string.ob_some, "active" to if (p.activityModel == null) R.string.ob_active else R.string.ob_current_active)
        "tier" -> mapOf("ECONOMICAL" to R.string.ob_economical, "MEDIUM" to R.string.ob_medium, "FLEXIBLE" to R.string.ob_flexible)
        "allergies" -> mapOf("none" to R.string.ob_none, "unknown" to R.string.ob_skip, "milk" to R.string.ob_milk,
            "egg" to R.string.ob_egg, "tree_nuts" to R.string.ob_nuts, "fish" to R.string.ob_fish, "wheat" to R.string.ob_wheat)
        else -> mapOf("yes" to R.string.ob_yes, "no" to R.string.ob_no)
    }
    return value.split(';').map { options[it]?.let { id -> stringResource(id) } ?: it.ifBlank { "—" } }.joinToString("; ")
}

@Composable
fun TargetExplanation(profile: UserProfile, assessment: NutritionAssessment?) {
    val uri = LocalUriHandler.current
    fun number(value: Double) = java.text.DecimalFormat("0.#").format(value)
    QidaCard {
        Text(stringResource(R.string.pf_basis), style = MaterialTheme.typography.titleLarge)
        Text(stringResource(R.string.pf_version, profile.ref.version))
        Text(java.time.Instant.parse(profile.updatedAt.instant).atZone(java.time.ZoneId.of(profile.updatedAt.timezone))
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
        Text(profile.updatedAt.timezone)
        Text(stringResource(when (assessment?.path) {
            TargetPath.SPECIALIST -> R.string.pf_specialist_note
            TargetPath.AUTOMATIC_TEST -> R.string.pf_test_only
            TargetPath.AUTOMATIC -> R.string.pf_current_target
            else -> R.string.pf_general
        }))
        assessment?.dailyKcal?.let { Text(stringResource(R.string.pf_kcal, number(it))) }
        assessment?.currentCalculation?.let { current ->
            Text(stringResource(R.string.pf_current_basis))
            TextButton(onClick = { uri.openUri("https://www.canada.ca/en/health-canada/services/food-nutrition/healthy-eating/dietary-reference-intakes/tables/equations-estimate-energy-requirement.html") }) {
                Text(stringResource(R.string.pf_formula_source))
            }
            Text(stringResource(R.string.pf_maintenance, current.energy.maintenanceKcal?.let(::number) ?: "—"))
            current.macros?.let { macros ->
                Text(stringResource(R.string.pf_macros, number(macros.proteinGrams),
                    number(macros.fatGrams), number(macros.availableCarbohydrateGrams)))
            }
            current.bounds?.let { Text(stringResource(R.string.pf_energy_interval, number(it.lowerKcal), number(it.upperKcal))) }
            Text(stringResource(R.string.pf_menu_unverified))
        }
        assessment?.calculation?.let { c ->
            Text(stringResource(R.string.pf_formula) + ": ${c.formula.id} v${c.formula.version}")
            if (c.formula == NutritionPolicyEngine.MIFFLIN_ST_JEOR_V1) {
                TextButton(onClick = { uri.openUri("https://ajcn.nutrition.org/article/S0002-9165%2823%2916698-6/fulltext") }) {
                    Text(stringResource(R.string.pf_formula_source))
                }
            }
            Text(stringResource(R.string.pf_policy) + ": ${c.policy.id} v${c.policy.version}")
            Text("REE: ${c.restingKcal}; ${c.inputs.activityFactor}; ${c.maintenanceKcal}")
            c.nutrientReferences.forEach { row -> Text("${row.nutrient}: ${row.amount.value} ${row.amount.unit} · ${row.source.id} v${row.source.version} · ${row.sourceSection}") }
        }
        if (assessment?.calculation == null && assessment?.currentCalculation?.targetKcal == null)
            Text(stringResource(R.string.pf_no_formula))
        assessment?.issues?.map { reasonLabel(it.reason) }?.distinct()?.forEach { label ->
            Text(stringResource(label))
        }
        Text(stringResource(R.string.pf_inputs), style = MaterialTheme.typography.titleMedium)
        ProfileEdit.draft(profile).answers.filterKeys { it != "activity_model" }.forEach { (key, value) ->
            Text(profileFieldLabel(key) + ": " + profileAnswer(key, value, profile))
        }
        Text(stringResource(R.string.pf_age_date, profile.ageAsOfDate?.iso ?: "—"))
        Text(stringResource(R.string.pf_limit))
    }
}

private fun reasonLabel(reason: PolicyReason): Int = when (reason) {
    PolicyReason.POLICY_MISSING, PolicyReason.POLICY_NOT_ACTIVE, PolicyReason.EXECUTION_RULES_MISSING,
    PolicyReason.PRODUCTION_APPROVAL_REQUIRED -> R.string.pf_policy_closed
    PolicyReason.AGE_CONFIRMATION_REQUIRED -> R.string.pf_age_needed
    PolicyReason.MEASUREMENT_CONFIRMATION_REQUIRED -> R.string.ob_measurements_confirm
    PolicyReason.ACTIVITY_UNMAPPED -> R.string.ob_activity_uncertain
    PolicyReason.BMI_OUT_OF_SCOPE, PolicyReason.GOAL_OUT_OF_SCOPE -> R.string.pf_scope_needed
    PolicyReason.UNDER_18, PolicyReason.MODEL_AGE_OUT_OF_SCOPE -> R.string.pf_age_scope
    PolicyReason.PREGNANT, PolicyReason.BREASTFEEDING, PolicyReason.HEALTH_EXCLUSION -> R.string.pf_specialist_needed
    PolicyReason.AGE_MISSING, PolicyReason.SEX_MISSING, PolicyReason.HEIGHT_MISSING,
    PolicyReason.WEIGHT_MISSING, PolicyReason.ACTIVITY_MISSING, PolicyReason.HEALTH_ANSWER_MISSING -> R.string.pf_missing
    else -> R.string.pf_calculation_unavailable
}
