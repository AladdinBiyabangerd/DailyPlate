package com.aladdin.dailyplate.domain.diary

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.entry.*
import java.time.*

/** A day is evaluated in the current user zone, using half-open midnight boundaries. */
fun diaryDay(logs: List<FoodLog>, date: LocalDate, zone: ZoneId): List<FoodLog> {
    val start = date.atStartOfDay(zone).toInstant()
    val end = date.plusDays(1).atStartOfDay(zone).toInstant()
    return logs.filter { !it.deleted && Instant.parse(it.consumedAt.instant).let { at -> at >= start && at < end } }
}

/** Null means incomplete, including absent keys and incompatible units. */
fun diaryTotal(logs: List<FoodLog>, key: String, unit: NutrientUnit): Double? {
    if (logs.isEmpty()) return null
    val amounts = logs.map { it.nutrientSnapshot.values[key] }
    if (amounts.any { it?.value == null || it.unit != unit }) return null
    return amounts.sumOf { requireNotNull(it?.value) }.takeIf { it.isFinite() }
}

/** Edit the saved snapshot, never today's catalog or a future menu. */
fun FoodLog.resizePortion(multiplier: Double): FoodLog {
    require(!deleted)
    val scaled = repeatDraft(displayName ?: ref.id, consumedAt).copy(quantity = multiplier)
        .confirm(userId, clientEventId)
    return copy(ref = ref.copy(version = Math.addExact(ref.version, 1)),
        confirmedPortionGrams = scaled.confirmedPortionGrams, nutrientSnapshot = scaled.nutrientSnapshot,
        actualRecipeSnapshot = scaled.actualRecipeSnapshot, enteredQuantity = scaled.confirmedPortionGrams ?: multiplier,
        enteredUnit = if (scaled.confirmedPortionGrams != null) EntryUnit.GRAM.name else EntryUnit.PREVIOUS_PORTION.name,
        syncStatus = SyncStatus.LOCAL)
}
