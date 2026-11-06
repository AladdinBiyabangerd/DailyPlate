package com.aladdin.dailyplate.ui.preview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.listSaver
import org.json.JSONObject

/** UI draft only: deliberately independent of profile, nutrition and remote repositories. */
enum class PreviewPage { Welcome, Goal, Body, Activity, Health1, Health2, Warning, Allergy, Preferences, Review, Account, Ready, Today }
enum class PreviewDialog { None, Language, Provider, Privacy, ActivityHelp, HealthSkip, AllergyMissing, Future }

class PreviewState(initial: Map<String, String> = emptyMap()) {
    var answers by mutableStateOf(initial)
        private set
    var page by mutableStateOf(PreviewPage.Welcome)
    var dialog by mutableStateOf(PreviewDialog.None)
    var language by mutableStateOf("az")
    var editing by mutableStateOf(false)
    var reviewAll by mutableStateOf(false)
    var forcedDiary by mutableStateOf(false)
    var error by mutableIntStateOf(0)
    var futureTitle by mutableIntStateOf(0)
    operator fun get(key: String) = answers[key].orEmpty()
    fun set(key: String, value: String) { answers = answers + (key to value); error = 0 }
    fun selected(key: String, value: String) = value in this[key].split(',')
    fun toggle(key: String, value: String) {
        val values = this[key].split(',').filter(String::isNotBlank).toMutableSet()
        if (!values.add(value)) values.remove(value)
        set(key, values.joinToString(","))
    }
    fun allergy(value: String) {
        set("allergy", value)
        if (value == "no") { set("allergens", ""); set("nuts", ""); set("other", "") }
        set("allergyUnknown", "")
    }
    fun go(target: PreviewPage) { page = target; error = 0; dialog = PreviewDialog.None }
    fun next(target: PreviewPage) {
        if (editing) { editing = false; go(PreviewPage.Review) } else go(target)
    }
    fun edit(target: PreviewPage) { editing = true; go(target) }
    fun back() {
        if (dialog != PreviewDialog.None) { dialog = PreviewDialog.None; return }
        if (editing) { editing = false; go(PreviewPage.Review); return }
        go(when (page) {
            PreviewPage.Welcome -> PreviewPage.Welcome
            PreviewPage.Goal -> PreviewPage.Welcome
            PreviewPage.Body -> PreviewPage.Goal
            PreviewPage.Activity -> PreviewPage.Body
            PreviewPage.Health1 -> PreviewPage.Activity
            PreviewPage.Health2 -> PreviewPage.Health1
            PreviewPage.Warning -> PreviewPage.Health2
            PreviewPage.Allergy -> if (healthKeys.any { this[it] == "yes" }) PreviewPage.Warning else PreviewPage.Health2
            PreviewPage.Preferences -> PreviewPage.Allergy
            PreviewPage.Review -> PreviewPage.Preferences
            PreviewPage.Account -> PreviewPage.Review
            PreviewPage.Ready -> PreviewPage.Account
            PreviewPage.Today -> PreviewPage.Ready
        })
    }
    fun future(title: Int) { futureTitle = title; dialog = PreviewDialog.Future }
    fun validNumber(key: String): Boolean {
        val raw = this[key].trim()
        val n = raw.replace(',', '.').toDoubleOrNull() ?: return false
        return raw.matches(Regex("\\d+(?:[,.]\\d+)?")) && n.isFinite() && n > 0 && (key != "age" || n % 1.0 == 0.0)
    }
    fun missing(): List<PreviewPage> = buildList {
        if (this@PreviewState["goal"] !in listOf("lose", "maintain", "gain", "healthy")) add(PreviewPage.Goal)
        if (listOf("age", "height", "weight").any { !validNumber(it) }) add(PreviewPage.Body)
        if (this@PreviewState["activity"] !in listOf("inactive", "low_active", "active", "very_active", "uncertain")) add(PreviewPage.Activity)
        if (healthKeys.take(5).any { this@PreviewState[it] !in listOf("yes", "no") }) add(PreviewPage.Health1)
        if (healthKeys.drop(5).any { this@PreviewState[it] !in listOf("yes", "no") }) add(PreviewPage.Health2)
        if (!allergyComplete()) add(PreviewPage.Allergy)
        if (this@PreviewState["time"] !in listOf("15", "30", "60") ||
            this@PreviewState["budget"] !in listOf("economical", "medium", "flexible") ||
            this@PreviewState["meals"] !in listOf("3", "4", "5")) add(PreviewPage.Preferences)
    }
    fun allergyComplete() = this["allergy"] == "no" || (this["allergy"] == "yes" && this["allergens"].isNotBlank() &&
        (!selected("allergens", "nuts") || this["nuts"].isNotBlank()) &&
        (!selected("allergens", "other") || this["other"].isNotBlank()))
    // This only controls a labelled fixture preview; it is NOT clinical eligibility logic.
    val diary: Boolean get() = forcedDiary || missing().isNotEmpty() || this["sex"] !in listOf("male", "female") ||
        this["activity"] == "uncertain" || this["healthIncomplete"] == "yes" || healthKeys.any { this[it] == "yes" } || this["allergyUnknown"] == "yes"
    companion object {
        val healthKeys = listOf("pregnancy", "breastfeeding", "kidney", "diabetes", "diet", "eating", "medication", "loss", "sport", "composition")
        val Saver = listSaver<PreviewState, Any>(save = {
            listOf(JSONObject(it.answers).toString(), it.page.name, it.dialog.name, it.language, it.editing, it.reviewAll, it.forcedDiary, it.error, it.futureTitle)
        }, restore = {
            val json = JSONObject(it[0] as String)
            PreviewState(json.keys().asSequence().associateWith { key -> json.getString(key) }).apply {
                page = PreviewPage.valueOf(it[1] as String); dialog = PreviewDialog.valueOf(it[2] as String)
                language = it[3] as String; editing = it[4] as Boolean; reviewAll = it[5] as Boolean
                forcedDiary = it[6] as Boolean; error = it[7] as Int; futureTitle = it[8] as Int
            }
        })
    }
}
