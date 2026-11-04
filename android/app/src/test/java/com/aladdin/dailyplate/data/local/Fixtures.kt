package com.aladdin.dailyplate.data.local

import com.aladdin.dailyplate.domain.*

internal val recordedAt = RecordedAt("2026-11-04T08:12:30.123Z", "Asia/Baku")
internal val date = CalendarDate("2026-11-04")
internal val recipeRef = VersionRef("recipe-1", 7)
internal val foodRef = VersionRef("food-1", 3)
internal val nutrients = NutrientSnapshot(mapOf(
    "energy" to NutrientAmount(220.5, NutrientUnit.KCAL),
    "protein" to NutrientAmount(null, NutrientUnit.GRAM),
    "sodium" to NutrientAmount(0.0, NutrientUnit.MILLIGRAM),
))
internal val shopping = ShoppingItem("food-1", "dry", 200.0, "g", 25.0, 50.0, 1)

internal fun profile(user: String = "guest-1") = UserProfile(
    VersionRef("profile", 1), user, recordedAt, Goal.HEALTHY, 29, null,
    172.5, 68.4, null, null, null, mapOf("eligibility" to null),
    null, setOf("gluten"), setOf("food-2"), emptySet(), 30, setOf("pan"),
    Budget(BudgetTier.ECONOMICAL, null, "AZ-Baku", 2, setOf("lunch", "dinner")),
    emptyList(), 4, null, "az",
)

internal fun item(id: String = "item-a") = MealPlanItem(
    id, date, "lunch", recipeRef, null, 180.0,
    1.0, 1.0, nutrients, PlanItemStatus.PLANNED, emptySet(),
)

internal fun plan() = MealPlan(
    VersionRef("plan-1", 1), "guest-1", VersionRef("profile", 1),
    CalendarDate("2026-11-02"), "Asia/Baku", null, null,
    listOf(item(), item("item-b").copy(date = CalendarDate("2026-11-06"), recipe = VersionRef("recipe-2", 4))),
    nutrients, emptyList(), emptyList(), listOf(shopping), SyncStatus.LOCAL,
)

internal fun swap(current: MealPlan = plan(), operationId: String = "operation-1"): Pair<PlanChange, MealPlan> {
    val (a, b) = current.items
    val changed = listOf(a.copy(recipe = b.recipe), b.copy(recipe = a.recipe))
    val next = current.copy(ref = current.ref.copy(version = current.ref.version + 1), items = changed,
        shopping = current.shopping.map { it.copy(quantity = 250.0, planVersion = current.ref.version + 1) })
    return PlanChange(operationId, current.userId, current.ref.id, PlanChangeType.SWAP,
        current.ref.version, setOf(a.id, b.id), listOf(a, b), changed, emptyList(),
        listOf(ShoppingDelta("food-1", "dry", 50.0, "g")), recordedAt, SyncStatus.PENDING) to next
}

internal fun log(user: String = "guest-1") = FoodLog(
    VersionRef("log-1", 1), "event-1", user, recordedAt, null, recipeRef,
    90.0, nutrients, EntryMethod.PLAN, "item-a", SyncStatus.PENDING,
    ActualRecipeSnapshot(recipeRef, null, listOf(Ingredient(foodRef, 100.0)), 4.0, 0.25, null, null, InstructionStatus.NEEDS_REVIEW),
    false, NutrientBasis.RECIPE_CONFIRMED, "local-photo-1", true,
)

internal inline fun <T : androidx.room.RoomDatabase, R> T.useDatabase(block: (T) -> R): R =
    try { block(this) } finally { close() }
