package com.aladdin.dailyplate.domain.planning

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.nutrition.NutrientUnits
import com.aladdin.dailyplate.domain.policy.*

internal object WeeklyNutrition {
    fun scale(snapshot: NutrientSnapshot, factor: Double) = NutrientSnapshot(snapshot.values.mapValues { (_, a) ->
        NutrientAmount(a.value?.times(factor)?.takeIf { it.isFinite() }, a.unit)
    })
    fun sum(snapshots: List<NutrientSnapshot>): NutrientSnapshot {
        val keys = snapshots.flatMap { it.values.keys }.toSortedSet()
        return NutrientSnapshot(keys.associateWith { key ->
            val unit = snapshots.firstNotNullOf { it.values[key]?.unit }
            val amounts = snapshots.map { it.values[key]?.let { a -> convert(a, unit) } }
            NutrientAmount(if (amounts.any { it == null }) null else amounts.sumOf { it!! }.takeIf { it.isFinite() }, unit)
        })
    }
    fun value(s: NutrientSnapshot, key: String, unit: NutrientUnit = NutrientUnit.GRAM) =
        s.values[key]?.let { convert(it, unit) }

    private fun convert(amount: NutrientAmount, unit: NutrientUnit): Double? {
        if ((amount.unit == NutrientUnit.KCAL) != (unit == NutrientUnit.KCAL)) return null
        return NutrientUnits.convert(amount, unit).value?.takeIf { it.isFinite() }
    }

    fun check(meals: List<GeneratedMeal>, recipes: Map<VersionRef, Recipe>, data: WeeklyPlanningData,
              preview: NutritionPlanningPreview, policy: VersionRef, scope: String): List<CandidateRuleResult> {
        val totals = sum(meals.map { it.nutrients })
        fun quantity(key: String, unit: NutrientUnit = NutrientUnit.GRAM): NutritionMenuQuantity {
            val v = value(totals, key, unit)
            return NutritionMenuQuantity(v, v != null)
        }
        val evidence = meals.map { data.evidence[it.recipe] }
        val basis = if (evidence.all { it?.carbohydrateBasis == NutritionCarbohydrateBasis.AVAILABLE_GLYCAEMIC })
            NutritionCarbohydrateBasis.AVAILABLE_GLYCAEMIC else NutritionCarbohydrateBasis.UNKNOWN
        val assessment = NutritionMenuChecks.assess(preview, NutritionMenuObservation(
            quantity("energy_kcal", NutrientUnit.KCAL), quantity("protein_g"), quantity("fat_g"),
            quantity("available_carbohydrate_g"), quantity("fibre_g"), basis,
            NutritionCatalogFeasibility.VERIFIED_PUBLISHED_PORTIONS))
        val supplemental = WeeklyCoverage.checks(meals, data, policy, scope, PlanningWindow.DAY)
        val results = assessment.rules.map { rule ->
            val energy = value(totals, "energy_kcal", NutrientUnit.KCAL)
            val protein = value(totals, "protein_g")
            val fat = value(totals, "fat_g")
            val carbohydrate = value(totals, "available_carbohydrate_g")
            val detail = when (rule.rule) {
                NutritionMenuRule.ENERGY_INTERVAL -> Triple("inclusive interval + " + preview.bounds?.maintenanceComparison,
                    "${preview.bounds?.lowerKcal}..${preview.bounds?.upperKcal}; maintenance=${preview.bounds?.maintenanceKcal}", energy?.toString())
                NutritionMenuRule.PROTEIN_PRI -> Triple(">=", (0.83 * requireNotNull(preview.energy.inputs.weightKg)).toString(), protein?.toString())
                NutritionMenuRule.FAT_RI -> Triple("inclusive energy fraction", "0.20..0.35", fat?.let { f -> energy?.takeIf { it > 0 }?.let { 9 * f / it } }?.toString())
                NutritionMenuRule.CARBOHYDRATE_RI -> Triple("inclusive available carbohydrate energy fraction", "0.45..0.60", carbohydrate?.let { c -> energy?.takeIf { it > 0 }?.let { 4 * c / it } }?.toString())
                NutritionMenuRule.FIBRE_AI -> Triple(">=", "25", value(totals, "fibre_g")?.toString())
                NutritionMenuRule.WHO_TOTAL_FAT_ADVISORY -> Triple("<= energy fraction", "0.30", fat?.let { f -> energy?.takeIf { it > 0 }?.let { 9 * f / it } }?.toString())
                else -> Triple("reviewed exact-version evidence", null, null)
            }
            val status = if (rule.rule == NutritionMenuRule.OTHER_NUTRIENTS_AND_PROTEIN_QUALITY) {
                when { supplemental.any { it.status == RuleStatus.FAIL } -> RuleStatus.FAIL
                    supplemental.any { it.status == RuleStatus.UNKNOWN } -> RuleStatus.UNKNOWN
                    else -> RuleStatus.PASS }
            } else when (rule.status) { NutritionRuleStatus.PASS -> RuleStatus.PASS
                NutritionRuleStatus.FAIL -> RuleStatus.FAIL; NutritionRuleStatus.UNKNOWN -> RuleStatus.UNKNOWN }
            CandidateRuleResult(rule.rule.name, policy, scope, detail.first, detail.second, detail.third, null,
                status, preview.evidenceRefs, if (status == RuleStatus.UNKNOWN) listOf(rule.rule.name) else emptyList(),
                if (rule.rule == NutritionMenuRule.OTHER_NUTRIENTS_AND_PROTEIN_QUALITY)
                    "Selected supplemental reference checks and reviewed protein quality; no universal micronutrient claim"
                else rule.reason?.name ?: "K08 menu check")
        }.toMutableList()
        results += supplemental
        fun limit(id: String, v: Double?, threshold: Double, operator: String, unit: String) {
            val status = when { v == null -> RuleStatus.UNKNOWN
                (when (operator) { "<" -> v < threshold; "<=" -> v <= threshold; ">=" -> v >= threshold; else -> false }) -> RuleStatus.PASS
                else -> RuleStatus.FAIL }
            results += CandidateRuleResult(id, policy, scope, operator, threshold.toString(), v?.toString(), unit,
                status, listOf("S3"), if (v == null) listOf(id) else emptyList(), "WHO population reference; not a clinical adequacy claim")
        }
        val energy = value(totals, "energy_kcal", NutrientUnit.KCAL)
        limit("free_sugar", value(totals, "free_sugar_g")?.let { g -> energy?.takeIf { it > 0 }?.let { 4 * g / it } }, 0.10, "<", "energy fraction")
        limit("saturated_fat", value(totals, "saturated_fat_g")?.let { g -> energy?.takeIf { it > 0 }?.let { 9 * g / it } }, 0.10, "<=", "energy fraction")
        limit("trans_fat", value(totals, "trans_fat_g")?.let { g -> energy?.takeIf { it > 0 }?.let { 9 * g / it } }, 0.01, "<=", "energy fraction")
        val salt = value(totals, "sodium_mg", NutrientUnit.MILLIGRAM)?.times(2.5 / 1000)
        limit("salt_from_sodium", salt, 5.0, "<", "g salt equivalent")
        val vegetableAmounts = meals.map { meal -> data.evidence[meal.recipe]?.fruitVegetableGrams?.let {
            it * meal.portionGrams / requireNotNull(recipes.getValue(meal.recipe).yieldCookedGrams)
        } }
        limit("fruit_vegetable", if (vegetableAmounts.any { it == null }) null else vegetableAmounts.sumOf { it!! }, 400.0, ">=", "g edible mapped fruit/vegetable")
        return results
    }

    /** Every FAIL/UNKNOWN is blocking except the separately labelled WHO advisory. */
    fun hardFailures(results: List<CandidateRuleResult>) = results.filter {
        it.ruleId != "WHO_TOTAL_FAT_ADVISORY" && it.status in setOf(RuleStatus.FAIL, RuleStatus.UNKNOWN)
    }
}
