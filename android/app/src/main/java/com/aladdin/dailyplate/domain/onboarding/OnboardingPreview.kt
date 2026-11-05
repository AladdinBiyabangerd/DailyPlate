package com.aladdin.dailyplate.domain.onboarding

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.policy.*

/** Immutable result reviewed before the first durable profile write. */
data class OnboardingPreview(val profile: UserProfile, val assessment: NutritionAssessment) {
    init {
        require(profile.ref.version == 1L && assessment.profile == profile.ref)
        require(assessment == NutritionPolicyEngine.assessCurrent(profile, profile.updatedAt))
    }
    companion object {
        fun create(draft: OnboardingDraft, language: String, at: RecordedAt, date: CalendarDate): OnboardingPreview {
            val candidate = draft.profile(language, at, date)
            val result = NutritionPolicyEngine.assessCurrent(candidate, at)
            return OnboardingPreview(candidate.copy(eligibilityStatus = result.eligibility.status.name.lowercase()), result)
        }
    }
}
