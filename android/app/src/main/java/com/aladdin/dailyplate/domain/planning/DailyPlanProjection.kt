package com.aladdin.dailyplate.domain.planning

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.diary.diaryDay
import java.time.LocalDate
import java.time.ZoneId

/** Projection is not actual intake, a completeness declaration or permission to eat. */
data class DailyPlanProjection(
    val actual: NutrientSnapshot?, val remainingPlan: NutrientSnapshot?,
    val projected: NutrientSnapshot?, val diaryComplete: Boolean,
) {
    fun remainingEnergy(targetKcal: Double?): Double? {
        if (!diaryComplete || targetKcal == null || !targetKcal.isFinite() || targetKcal <= 0) return null
        val actualEnergy = actual?.let { WeeklyNutrition.value(it, "energy_kcal", NutrientUnit.KCAL) } ?: return null
        return targetKcal - actualEnergy
    }
}

/** Uses current log revisions, including edits/deletions. Consumed portions never reappear as plan. */
fun dailyPlanProjection(plan: MealPlan, logs: List<FoodLog>, date: LocalDate,
                        diaryComplete: Boolean = false): DailyPlanProjection {
    val owned = logs.filter { it.userId == plan.userId }.groupBy { it.ref.id }.values
        .map { revisions -> revisions.maxBy { it.ref.version } }
    val actual = diaryDay(owned, date, ZoneId.of(plan.timezone)).map { it.nutrientSnapshot }
    val remaining = plan.items.filter { it.date.iso == date.toString() &&
        it.status in setOf(PlanItemStatus.PLANNED, PlanItemStatus.PARTIALLY_CONSUMED) && it.remainingPlannedServings > 0 }
        .map { WeeklyNutrition.scale(it.nutrientSnapshot, it.remainingPlannedServings / it.plannedServings) }
    fun total(values: List<NutrientSnapshot>) = values.takeIf { it.isNotEmpty() }?.let(WeeklyNutrition::sum)
    return DailyPlanProjection(total(actual), total(remaining), total(actual + remaining), diaryComplete)
}
