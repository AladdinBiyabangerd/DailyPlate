package com.aladdin.dailyplate.domain.planning

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.nutrition.NutrientUnits
import com.aladdin.dailyplate.domain.policy.*

internal object WeeklyNutrition {
    fun scale(snapshot: NutrientSnapshot, factor: Double) = NutrientSnapshot(snapshot.values.mapValues { (_, a) ->
        NutrientAmount(a.value?.times(factor), a.unit)
    })
    fun sum(snapshots: List<NutrientSnapshot>): NutrientSnapshot {
        val keys = snapshots.flatMap { it.values.keys }.toSortedSet()
        return NutrientSnapshot(keys.associateWith { key ->
            val unit = snapshots.firstNotNullOf { it.values[key]?.unit }
            val amounts = snapshots.map { it.values[key]?.let { a -> NutrientUnits.convert(a, unit) }?.value }
            NutrientAmount(if (amounts.any { it == null }) null else amounts.sumOf { it!! }, unit)
        })
    }
    fun value(s: NutrientSnapshot, key: String, unit: NutrientUnit = NutrientUnit.GRAM) =
        s.values[key]?.let { NutrientUnits.convert(it, unit) }?.value

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
        val results = assessment.rules.map { rule -> CandidateRuleResult(rule.rule.name, policy, scope,
            "K08 source operator", null, null, null,
            when (rule.status) { NutritionRuleStatus.PASS -> RuleStatus.PASS
                NutritionRuleStatus.FAIL -> RuleStatus.FAIL; NutritionRuleStatus.UNKNOWN -> RuleStatus.UNKNOWN },
            preview.evidenceRefs, if (rule.status == NutritionRuleStatus.UNKNOWN) listOf(rule.rule.name) else emptyList(),
            rule.reason?.name ?: "K08 menu check") }.toMutableList()
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

    /** K08 explicitly leaves wider micronutrient/protein-quality coverage unknown. It stays visible,
     * outside this supported healthy-adult subset; restricted diets are blocked by the generator. */
    fun hardFailures(results: List<CandidateRuleResult>) = results.filter {
        it.ruleId !in setOf("OTHER_NUTRIENTS_AND_PROTEIN_QUALITY", "WHO_TOTAL_FAT_ADVISORY") &&
            it.status in setOf(RuleStatus.FAIL, RuleStatus.UNKNOWN)
    }
}
