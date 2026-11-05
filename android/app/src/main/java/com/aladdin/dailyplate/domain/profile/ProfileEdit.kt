package com.aladdin.dailyplate.domain.profile

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.onboarding.OnboardingDraft
import com.aladdin.dailyplate.domain.policy.*
import kotlinx.serialization.Serializable

/** A future-plan request, not evidence that any menu or portion has been recalculated. */
@Serializable
data class FuturePlanRecalculation(
    val fromProfile: VersionRef, val toProfile: VersionRef,
    val changedFields: Set<String>, val reason: String,
    val status: String = "pending_k16_preview",
) {
    init {
        require(fromProfile.id == toProfile.id && toProfile.version == fromProfile.version + 1)
        require(reason.isNotBlank() && changedFields.isNotEmpty())
        require(status == "pending_k16_preview")
    }
}

@Serializable
data class ProfileChangeRecord(
    val ref: VersionRef, val userId: String, val confirmedAt: RecordedAt,
    val request: FuturePlanRecalculation,
) { init { require(ref == request.toProfile && userId.isNotBlank()) } }

data class ProfileEditPreview(
    val before: UserProfile, val after: UserProfile, val assessment: NutritionAssessment,
    val request: FuturePlanRecalculation,
)

/** Shares K07 validation without persisting edits into the onboarding draft. */
object ProfileEdit {
    fun draft(p: UserProfile): OnboardingDraft {
        fun yesNo(value: Boolean?) = when (value) { true -> "yes"; false -> "no"; null -> "" }
        val answers = mapOf(
            "goal" to p.goal.name, "age" to p.ageYears?.toString().orEmpty(),
            "sex" to p.calculationSex?.name.orEmpty(), "height" to p.heightCm?.toString().orEmpty(),
            "weight" to p.weightKg?.toString().orEmpty(), "activity" to p.activityCode.orEmpty(),
            "pregnant" to yesNo(p.pregnant), "breastfeeding" to yesNo(p.breastfeeding),
            "allergies" to (p.allergies?.filterNot { it.startsWith("custom:") }?.joinToString(";")
                ?.ifEmpty { if (p.allergies.isEmpty()) "none" else "" } ?: "unknown"),
            "extra_allergies" to p.allergies.orEmpty().filter { it.startsWith("custom:") }.joinToString(";") { it.removePrefix("custom:") },
            "restrictions" to p.dietaryRestrictions.joinToString(";"),
            "disliked_foods" to p.dislikedFoodIds.joinToString(";") { it.removePrefix("user:") },
            "disliked_recipes" to p.dislikedRecipeIds.joinToString(";") { it.removePrefix("user:") },
            "time" to p.maxPreparationMinutes?.toString().orEmpty(), "meals" to p.mealsPerDay.toString(),
            "tier" to p.budget.tier?.name.orEmpty(), "region" to p.budget.region.orEmpty(),
            "currency" to (p.budget.currency ?: ""),
            "amount" to p.budget.weeklyAmount?.amount.orEmpty(), "people" to p.budget.people.toString(),
        ) + p.eligibilityAnswers.mapValues { yesNo(it.value) }
        return OnboardingDraft(p.ref.id, answers = answers)
    }

    fun preview(base: UserProfile, draft: OnboardingDraft, specialist: String,
                confirmAge: Boolean, reason: String, at: RecordedAt, date: CalendarDate): ProfileEditPreview {
        require(draft.id == base.ref.id && reason.isNotBlank())
        require(specialist.isBlank() || specialist.replace(',', '.').toDoubleOrNull()?.let { it.isFinite() && it > 0 } == true)
        require(draft.number("age")?.toInt() == base.ageYears || confirmAge) { "Confirm updated age" }
        val edited = draft.profile(base.language, at, date)
        val candidate = edited.copy(
            ref = base.ref.copy(version = Math.addExact(base.ref.version, 1)), userId = base.userId,
            ageAsOfDate = if (confirmAge) date else base.ageAsOfDate,
            specialistTargetKcal = specialist.takeIf { it.isNotBlank() }?.replace(',', '.')?.toDouble(),
            // Fields outside this editor are retained, including real source IDs.
            equipment = base.equipment, homeFoods = base.homeFoods,
            budget = edited.budget.copy(coveredMealSlots = base.budget.coveredMealSlots),
            dislikedFoodIds = restoreIds(draft.items("disliked_foods"), base.dislikedFoodIds),
            dislikedRecipeIds = restoreIds(draft.items("disliked_recipes"), base.dislikedRecipeIds),
        )
        val assessment = NutritionPolicyEngine.assess(candidate, null, at)
        val after = candidate.copy(eligibilityStatus = assessment.eligibility.status.name.lowercase())
        val old = ProfileEdit.draft(base).answers
        val changed = (old.keys + draft.answers.keys).filter { old[it] != draft.answers[it] }.toMutableSet()
        if (after.specialistTargetKcal != base.specialistTargetKcal) changed += "specialist_target"
        if (after.ageAsOfDate != base.ageAsOfDate) changed += "age_confirmation"
        require(changed.isNotEmpty()) { "No profile changes" }
        return ProfileEditPreview(base, after, assessment,
            FuturePlanRecalculation(base.ref, after.ref, changed, reason.trim()))
    }

    private fun restoreIds(values: Set<String>, previous: Set<String>): Set<String> = values.map { value ->
        previous.firstOrNull { it.removePrefix("user:") == value } ?: "user:$value"
    }.toSet()
}
