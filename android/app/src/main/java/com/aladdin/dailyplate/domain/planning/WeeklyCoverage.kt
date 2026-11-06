package com.aladdin.dailyplate.domain.planning

import com.aladdin.dailyplate.domain.*

/** Completes K08's deliberately unevaluated wider-coverage rule only from an exact-policy,
 * reviewed reference set and exact-recipe protein-quality evidence. Missing data stays hard. */
internal object WeeklyCoverage {
    fun checks(meals: List<GeneratedMeal>, data: WeeklyPlanningData, policy: VersionRef,
               scope: String, window: PlanningWindow): List<CandidateRuleResult> {
        val coverage = data.coverage
        fun missing(id: String, status: RuleStatus, reason: String) = CandidateRuleResult(id, policy, scope,
            "reviewed exact-version coverage", null, null, null, status, emptyList(),
            if (status == RuleStatus.UNKNOWN) listOf(id) else emptyList(), reason)
        if (coverage == null) return listOf(missing("coverage_contract", RuleStatus.UNKNOWN, "Supplemental reference set is missing"))
        if (coverage.policy != policy) return listOf(missing("coverage_contract", RuleStatus.FAIL, "Supplemental policy version differs"))
        val results = mutableListOf<CandidateRuleResult>()
        val totals = WeeklyNutrition.sum(meals.map { it.nutrients })
        coverage.rules.filter { it.window == window }.forEach { rule ->
            val v = WeeklyNutrition.value(totals, rule.nutrient, rule.threshold.unit)
            val threshold = requireNotNull(rule.threshold.value)
            val pass = v != null && when (rule.operator) {
                PlanningOperator.LESS_THAN -> v < threshold
                PlanningOperator.AT_MOST -> v <= threshold
                PlanningOperator.AT_LEAST -> v >= threshold
                PlanningOperator.GREATER_THAN -> v > threshold
            }
            results += CandidateRuleResult(rule.id, policy, scope, rule.operator.name, threshold.toString(), v?.toString(),
                rule.threshold.unit.name, if (v == null) RuleStatus.UNKNOWN else if (pass) RuleStatus.PASS else RuleStatus.FAIL,
                listOf(rule.source, coverage.ref.id, coverage.review.auditId), if (v == null) listOf(rule.nutrient) else emptyList(),
                "Selected source reference; ${window.name} total")
        }
        val quality = meals.map { data.evidence[it.recipe]?.proteinQualityReviewed }
        results += missing("protein_quality", when {
            quality.any { it == false } -> RuleStatus.FAIL
            quality.any { it == null } -> RuleStatus.UNKNOWN
            else -> RuleStatus.PASS
        }, "Exact-recipe protein-quality review; not inferred from protein grams").copy(
            evidenceRefs = meals.flatMap { data.evidence[it.recipe]?.sources.orEmpty() }.distinct() + coverage.review.auditId)
        return results
    }
}
