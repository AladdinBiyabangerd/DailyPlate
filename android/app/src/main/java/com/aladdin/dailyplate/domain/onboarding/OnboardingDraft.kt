package com.aladdin.dailyplate.domain.onboarding

import com.aladdin.dailyplate.domain.*
import kotlinx.serialization.Serializable

/** Raw answers remain editable and are never treated as confirmed profile data. */
@Serializable
data class OnboardingDraft(
    val id: String, val step: Int = 0, val answers: Map<String, String> = emptyMap(),
    // Retained only to decode drafts saved before home inventory was removed from onboarding.
    val storedHomeFoods: List<HomeFood> = emptyList(),
) {
    fun answer(key: String) = answers[key].orEmpty()
    val asksReproductiveHealth: Boolean get() = answer("sex") != "MALE"
    fun change(key: String, value: String): OnboardingDraft {
        val updated = answers + (key to value)
        return copy(answers = if (key == "sex" && value == "MALE")
            updated - setOf("pregnant", "breastfeeding") else updated)
    }
    fun number(key: String): Double? = answer(key).replace(',', '.').toDoubleOrNull()
        ?.takeIf { it.isFinite() && it > 0 }
    fun items(key: String) = answer(key).split(';').map(String::trim).filter(String::isNotEmpty).toSet()
    fun toggleAllergen(code: String): OnboardingDraft {
        if (code == "none") return change("allergies", if (answer("allergies") == "none") "" else "none").change("extra_allergies", "")
        val values = items("allergies") - setOf("none", "unknown")
        return change("allergies", (if (code in values) values - code else values + code).joinToString(";"))
    }
    fun errors(step: Int): Set<String> = buildSet {
        fun required(key: String) { if (answer(key).isBlank()) add(key) }
        fun numeric(key: String, optional: Boolean = false, integer: Boolean = false) {
            if (optional && answer(key).isBlank()) return
            val n = number(key)
            if (n == null || (integer && (n % 1.0 != 0.0 || n > Int.MAX_VALUE))) add(key)
        }
        when (step) {
            0 -> if (answer("goal") !in Goal.entries.map { it.name }) add("goal")
            1 -> {
                numeric("age", integer = true); numeric("height"); numeric("weight")
                if (answer("sex") !in setOf("", "MALE", "FEMALE")) add("sex")
            }
            2 -> if (answer("activity") !in setOf("sedentary", "some", "active")) add("activity")
            3 -> {
                if (answer("allergies").isBlank() && items("extra_allergies").isEmpty()) add("allergies")
                if ("tree_nuts" in items("allergies") && items("extra_allergies").isEmpty()) add("extra_allergies")
            }
            4 -> {
                if (answer("time") !in setOf("15", "30", "60")) add("time")
                if (answer("meals") !in setOf("3", "4", "5")) add("meals")
                if (answer("tier") !in BudgetTier.entries.map { it.name }) add("tier")
                required("region"); required("currency")
                if (!answer("currency").matches(Regex("[A-Z]{3}"))) add("currency")
                numeric("amount", optional = true); numeric("people", integer = true)
            }
        }
    }
    fun profile(language: String, at: RecordedAt, date: CalendarDate): UserProfile {
        require((0..4).all { errors(it).isEmpty() })
        fun yesNo(key: String) = when (answer(key)) { "yes" -> true; "no" -> false; else -> null }
        return UserProfile(
            ref = VersionRef(id, 1), userId = "local-guest", updatedAt = at,
            goal = Goal.valueOf(answer("goal")), ageYears = number("age")!!.toInt(),
            calculationSex = answer("sex").takeIf { it.isNotBlank() }?.let(CalculationSex::valueOf),
            heightCm = number("height"), weightKg = number("weight"), activityCode = answer("activity"),
            pregnant = if (asksReproductiveHealth) yesNo("pregnant") else null,
            breastfeeding = if (asksReproductiveHealth) yesNo("breastfeeding") else null,
            eligibilityAnswers = listOf("eating_risk", "medical_diet", "kidney", "treatment").associateWith(::yesNo),
            allergies = if (answer("allergies") == "unknown") null else if (answer("allergies") == "none" && items("extra_allergies").isEmpty()) emptySet()
                else (items("allergies") - setOf("none", "unknown")) + items("extra_allergies").map { "custom:$it" },
            dietaryRestrictions = items("restrictions"), dislikedFoodIds = items("disliked_foods").map { "user:$it" }.toSet(),
            dislikedRecipeIds = items("disliked_recipes").map { "user:$it" }.toSet(),
            maxPreparationMinutes = answer("time").toInt(), equipment = emptySet(),
            budget = Budget(BudgetTier.valueOf(answer("tier")), answer("amount").takeIf(String::isNotBlank)
                ?.let { Money(it.replace(',', '.'), answer("currency")) }, answer("region"), number("people")!!.toInt(), emptySet()),
            homeFoods = emptyList(),
            mealsPerDay = answer("meals").toInt(), specialistTargetKcal = null, language = language,
            ageAsOfDate = date,
        )
    }
}
