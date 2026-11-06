package com.aladdin.dailyplate.ui

import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.ui.preview.*

/** Approved stable states. Prototype-only 'next' placeholders become real navigation, tested separately. */
internal object TabletRenderCases {
    val groups = linkedMapOf(
        "welcome" to listOf("default"), "goal" to listOf("empty", "selected"),
        "body" to listOf("empty", "filled", "unspecified", "invalid", "empty-step"),
        "activity" to listOf("empty", "inactive", "low_active", "active", "very_active", "uncertain", "help"),
        "health-1" to listOf("empty", "answered", "risk", "error", "skip"),
        "health-2" to listOf("empty", "answered", "risk", "error", "skip"),
        "health-warning" to listOf("warning"),
        "allergy" to listOf("empty", "no", "yes", "selected", "details", "error", "nuts-error", "other-error", "warning"),
        "meal-preferences" to listOf("empty", "three", "four", "five", "error", "partial-error"),
        "incomplete" to listOf("empty", "partial", "allergy-detail", "complete", "optional"),
        "account" to listOf("offer", "provider", "privacy"),
        "ready" to listOf("three", "four", "five", "diary", "risk", "empty"),
        "today" to listOf("three", "four", "five", "diary", "risk", "add", "menu", "progress", "profile"))
    fun complete() = mapOf("goal" to "maintain", "age" to "28", "height" to "172", "weight" to "78,4", "sex" to "male",
        "activity" to "low_active", "allergy" to "no", "time" to "30", "budget" to "economical", "meals" to "3") + PreviewState.healthKeys.associateWith { "no" }
    fun fixture(group: String, variant: String): PreviewState {
        val full = group in listOf("account", "ready", "today") || group == "incomplete" && variant in listOf("complete", "optional", "allergy-detail")
        return PreviewState(if (full) complete() else emptyMap()).apply {
            when (group) {
                "welcome" -> page = PreviewPage.Welcome
                "goal" -> { page = PreviewPage.Goal; if (variant == "selected") set("goal", "maintain") }
                "body" -> {
                    page = PreviewPage.Body
                    if (variant in listOf("filled", "unspecified")) { set("age", "28"); set("height", "172"); set("weight", "78,4"); set("sex", if (variant == "filled") "male" else "unspecified") }
                    if (variant == "invalid") { set("age", "abc"); error = R.string.dp_body_error }
                    if (variant == "empty-step") error = R.string.dp_number_first
                }
                "activity" -> { page = PreviewPage.Activity; if (variant == "help") dialog = PreviewDialog.ActivityHelp else if (variant != "empty") set("activity", variant) }
                "health-1", "health-2" -> {
                    page = if (group == "health-1") PreviewPage.Health1 else PreviewPage.Health2
                    val keys = if (group == "health-1") PreviewState.healthKeys.take(5) else PreviewState.healthKeys.drop(5)
                    if (variant in listOf("answered", "risk")) keys.forEach { set(it, "no") }
                    if (variant == "risk") set(keys.first(), "yes")
                    if (variant == "error") error = R.string.dp_health_note
                    if (variant == "skip") dialog = PreviewDialog.HealthSkip
                }
                "health-warning" -> page = PreviewPage.Warning
                "allergy" -> {
                    page = PreviewPage.Allergy
                    if (variant == "no") allergy("no")
                    if (variant in listOf("yes", "selected", "details", "error", "nuts-error", "other-error")) allergy("yes")
                    if (variant == "selected") { toggle("allergens", "milk"); toggle("allergens", "egg") }
                    if (variant in listOf("details", "nuts-error")) toggle("allergens", "nuts")
                    if (variant in listOf("details", "other-error")) toggle("allergens", "other")
                    if (variant == "details") { set("nuts", "badam"); set("other", "küncüt"); set("gluten", "yes") }
                    if (variant == "error") error = R.string.dp_allergy_error
                    if (variant == "nuts-error") error = R.string.dp_nuts_detail
                    if (variant == "other-error") error = R.string.dp_other_detail
                    if (variant == "warning") dialog = PreviewDialog.AllergyMissing
                }
                "meal-preferences" -> {
                    page = PreviewPage.Preferences
                    if (variant in listOf("three", "four", "five")) { set("time", "30"); set("budget", "economical"); set("meals", mealCount(variant)) }
                    if (variant == "partial-error") set("time", "30")
                    if (variant.endsWith("error")) error = R.string.dp_preferences_error
                }
                "incomplete" -> {
                    page = PreviewPage.Review
                    if (variant == "partial") { set("goal", "maintain"); set("age", "28") }
                    if (variant == "allergy-detail") { allergy("yes"); toggle("allergens", "nuts") }
                    if (variant == "optional") { set("sex", "unspecified"); set("activity", "uncertain") }
                }
                "account" -> { page = PreviewPage.Account; if (variant == "provider") dialog = PreviewDialog.Provider; if (variant == "privacy") dialog = PreviewDialog.Privacy }
                "ready", "today" -> {
                    page = if (group == "ready") PreviewPage.Ready else PreviewPage.Today
                    if (variant in listOf("three", "four", "five")) set("meals", mealCount(variant))
                    if (variant == "diary") forcedDiary = true
                    if (variant == "risk") set("pregnancy", "yes")
                    if (variant == "empty") { set("time", ""); set("budget", ""); set("meals", "") }
                    val destination = mapOf("add" to R.string.dp_add, "menu" to R.string.dp_menu, "progress" to R.string.dp_progress, "profile" to R.string.dp_profile)[variant]
                    if (destination != null) future(destination)
                }
            }
        }
    }
    private fun mealCount(variant: String) = when (variant) { "four" -> "4"; "five" -> "5"; else -> "3" }
}
