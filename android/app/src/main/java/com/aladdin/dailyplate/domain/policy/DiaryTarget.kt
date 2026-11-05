package com.aladdin.dailyplate.domain.policy

import com.aladdin.dailyplate.domain.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/** Historical summaries select the frozen revision effective on that date, never recalculate. */
fun profileForDiaryDate(history: List<UserProfile>, date: LocalDate, zone: ZoneId): UserProfile? =
    history.filter { !Instant.parse(it.updatedAt.instant).atZone(zone).toLocalDate().isAfter(date) }
        .maxByOrNull { it.ref.version }

fun NutritionAssessment.diaryTarget(): Double? = when (path) {
    TargetPath.AUTOMATIC, TargetPath.SPECIALIST -> dailyKcal
    else -> null
}
