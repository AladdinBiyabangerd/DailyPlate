package com.aladdin.dailyplate.data.local

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.planning.*
import java.util.concurrent.Callable
import kotlinx.serialization.Serializable

/** One immutable acceptance per generation request. The generation envelope retains complete
 * catalog/coverage/pricing provenance; the initial plan remains readable after later edits. */
@Serializable
data class PlanAcceptance(val userId: String, val requestId: String, val plan: VersionRef)

/** Explicit acceptance only. Never accepts caller-supplied meals and never writes intake.
 * Recalculation of an existing plan is deliberately a separate preview/change operation. */
class PlanAcceptanceRepository internal constructor(private val database: UserDatabase) {
    private val generation = PlanGenerationRepository(database)
    private val plans = MealPlanRepository(database)
    private val profiles = UserSnapshotRepository(database, "profile", UserProfile.serializer(), UserProfile::userId, UserProfile::ref)

    fun receipt(userId: String, requestId: String): PlanAcceptance? =
        database.snapshots().version("plan_acceptance", userId, requestId, 1)?.let {
            require(it.payloadSchema == 1)
            snapshotJson.decodeFromString(PlanAcceptance.serializer(), it.payload)
        }

    fun accept(userId: String, requestId: String, planId: String): MealPlan = execute(userId, requestId, planId, false)
    internal fun acceptTest(userId: String, requestId: String, planId: String): MealPlan = execute(userId, requestId, planId, true)

    private fun execute(userId: String, requestId: String, planId: String, test: Boolean): MealPlan =
        database.runInTransaction(Callable {
            require(userId.isNotBlank() && requestId.isNotBlank() && planId.isNotBlank())
            val envelope = requireNotNull(generation.receipt(userId, requestId)) { "Generation receipt missing" }
            require(envelope.result.testOnly == test) { "Fixture results cannot be accepted in production" }
            receipt(userId, requestId)?.let {
                if (it.plan.id != planId) throw IdempotencyConflict("Generation already accepted under another plan ID")
                return@Callable requireNotNull(plans.version(userId, it.plan))
            }
            require(envelope.result.status == GenerationStatus.SUCCESS) { "Only successful generation can be accepted" }
            if (profiles.latest(userId, envelope.profile.ref.id) != envelope.profile ||
                generation.currentInputs(userId) != envelope.inputs) {
                throw RevisionConflict("Planning inputs changed since generation; generate a new request")
            }
            val week = requireNotNull(envelope.result.plan)
            require(week.request == envelope.request)
            val ref = VersionRef(planId, 1)
            if (plans.latest(userId, planId) != null) throw RevisionConflict("Acceptance requires a new plan ID")
            fun itemId(day: CalendarDate, slot: Int) = "${day.iso}:$slot"
            val energyInputs = week.nutritionPreview.energy.inputs
            val measuredInputs = buildMap {
                energyInputs.ageYears?.let { put("ageYears", it.toDouble()) }
                energyInputs.heightCm?.let { put("heightCm", it) }
                energyInputs.weightKg?.let { put("weightKg", it) }
            }
            val plan = MealPlan(ref, userId, envelope.profile.ref, week.request.weekStart,
                week.request.timezone, week.request.candidate.policy,
                EnergyEstimate(week.nutritionPreview.energy.contract.formula, week.request.candidate.policy,
                    envelope.profile.ref, measuredInputs, week.request.candidate.at, week.targetKcal),
                // One serving here is the generated personal portion, not the entire cooked batch.
                week.meals.map { meal -> MealPlanItem(itemId(meal.day, meal.slot), meal.day, meal.slot.toString(),
                    meal.recipe, null, meal.portionGrams, 1.0, 1.0, meal.nutrients,
                    PlanItemStatus.PLANNED, emptySet()) },
                week.weeklyNutrients, envelope.result.ruleResults.filter { it.status != RuleStatus.NOT_APPLICABLE }.map { rule -> ConstraintResult(rule.ruleId,
                    when (rule.status) {
                        RuleStatus.PASS -> ConstraintOutcome.PASS
                        RuleStatus.NOT_APPLICABLE -> error("Not applicable rules are not constraints")
                        RuleStatus.FAIL -> ConstraintOutcome.FAIL
                        RuleStatus.UNKNOWN -> ConstraintOutcome.UNKNOWN
                    }, rule.reason) },
                week.alternatives.map { MealAlternative(itemId(it.day, it.slot), it.recipe, "generated alternative") },
                week.shoppingCost.lines.map { ShoppingItem(it.product.foodId, it.product.preparationState,
                    it.requiredEdibleGrams.toDouble(), "g", 0.0, 0.0, 1) }, SyncStatus.LOCAL)
            plans.create(plan)
            val acceptance = PlanAcceptance(userId, requestId, ref)
            database.snapshots().insert(UserSnapshotEntity("plan_acceptance", userId, requestId, 1,
                snapshotJson.encodeToString(PlanAcceptance.serializer(), acceptance)))
            plan
        })
}
