package com.aladdin.dailyplate.domain.profile

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.policy.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/** Descriptive observed change only; never a calorie titration or a clinical threshold. */
data class NutritionReviewState(
    val due: Boolean, val measurementCount: Int, val observedChangeKg: Double?,
    val latestWeightKg: Double?,
)

object NutritionReview {
    fun evaluate(profile: UserProfile, assessment: NutritionAssessment?, entries: List<WeightEntry>,
                 at: RecordedAt): NutritionReviewState {
        val now = Instant.parse(at.instant)
        val zone = ZoneId.of(at.timezone)
        val today = now.atZone(zone).toLocalDate()
        val points = entries.filter { it.userId == profile.userId }
            .groupBy { it.ref.id }.values.map { versions -> versions.maxBy { it.ref.version } }
            .filter { !Instant.parse(it.measuredAt.instant).isAfter(now) }
            .filter { date(it, zone) in today.minusDays(13)..today }
            .distinctBy { Instant.parse(it.measuredAt.instant) }
            .sortedBy { Instant.parse(it.measuredAt.instant) }
        val ready = points.size >= 3
        return NutritionReviewState(assessment?.reviewDate()?.let { !today.isBefore(it) } == true,
            points.size, if (ready) points.last().weightKg - points.first().weightKg else null,
            points.lastOrNull()?.weightKg)
    }

    /** A report stops the active target immediately; it is not a pending editor preview. */
    fun suspension(profile: UserProfile, reason: NutritionSuspension, at: RecordedAt): ProfileEditPreview {
        require(!Instant.parse(at.instant).isBefore(Instant.parse(profile.updatedAt.instant)))
        val next = profile.copy(ref = profile.ref.copy(version = Math.addExact(profile.ref.version, 1)),
            updatedAt = at, nutritionSuspension = reason, eligibilityStatus = "excluded")
        return ProfileEditPreview(profile, next, NutritionPolicyEngine.assessCurrent(next, at),
            FuturePlanRecalculation(profile.ref, next.ref, setOf("nutrition_suspension"), reason.name))
    }

    private fun date(entry: WeightEntry, zone: ZoneId): LocalDate =
        Instant.parse(entry.measuredAt.instant).atZone(zone).toLocalDate()
}
