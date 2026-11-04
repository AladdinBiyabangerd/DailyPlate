package com.aladdin.dailyplate.domain

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import kotlinx.serialization.Serializable

@Serializable
data class VersionRef(val id: String, val version: Long) {
    init { require(id.isNotBlank()); require(version > 0) }
}

/** UTC instant plus the user's IANA zone, retained independently of device settings. */
@Serializable
data class RecordedAt(val instant: String, val timezone: String) {
    init { Instant.parse(instant); ZoneId.of(timezone) }
}

@Serializable
data class CalendarDate(val iso: String) {
    init { LocalDate.parse(iso) }
}

@Serializable
data class WallTime(val iso: String) {
    init { LocalTime.parse(iso) }
}

@Serializable
data class LocalizedText(val az: String, val en: String? = null, val ru: String? = null)

@Serializable
enum class NutrientUnit { KCAL, GRAM, MILLIGRAM, MICROGRAM }

@Serializable
data class NutrientAmount(val value: Double?, val unit: NutrientUnit) {
    init { value?.let { require(it.isFinite() && it >= 0) } }
}

/** An absent key or null amount means unknown. Never substitute zero on read. */
@Serializable
data class NutrientSnapshot(val values: Map<String, NutrientAmount>) {
    val missingFields: Set<String> get() = values.filterValues { it.value == null }.keys
}

@Serializable
data class Review(val reviewer: String, val reviewedAt: RecordedAt, val auditId: String) {
    init { require(reviewer.isNotBlank()); require(auditId.isNotBlank()) }
}

@Serializable
enum class ContentStatus { DRAFT, REVIEWED, PUBLISHED, WITHDRAWN }

@Serializable
data class ConstraintResult(val rule: String, val outcome: ConstraintOutcome, val reason: String)

@Serializable
enum class ConstraintOutcome { PASS, FAIL, UNKNOWN }

@Serializable
enum class SyncStatus { LOCAL, PENDING, SYNCED, FAILED }

internal fun positive(value: Double) { require(value.isFinite() && value > 0) }
internal fun nonNegative(value: Double) { require(value.isFinite() && value >= 0) }
