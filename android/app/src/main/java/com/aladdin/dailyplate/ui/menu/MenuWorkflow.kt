package com.aladdin.dailyplate.ui.menu

import android.content.Context
import com.aladdin.dailyplate.data.local.*
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.planning.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.*
import java.util.UUID

@Serializable
internal data class PendingMenu(
    val request: PlanGenerationRequest, val planId: String,
    val future: FuturePlanCommand? = null,
)

internal data class MenuData(
    val profile: UserProfile?, val plans: List<MealPlan>, val pending: PendingMenu?,
    val generated: GenerationEnvelope?, val future: FuturePlanRecord?, val inputsAvailable: Boolean,
    val recipes: Map<VersionRef, Recipe> = emptyMap(),
)

/** Worker-thread orchestration. Saves exact retry commands before computation or acceptance. */
// Synchronous commit and its Boolean result are required before computation.
// KTX edit(commit = true) discards that result, hiding failed durable writes.
@android.annotation.SuppressLint("ApplySharedPref", "UseKtx")
internal class MenuWorkflow(context: Context) {
    private val context = context.applicationContext
    private val preferences = this.context.getSharedPreferences("menu-workflow", 0)
    private val owner = "local-guest"
    private fun pending() = preferences.getString("pending", null)?.let { Json.decodeFromString<PendingMenu>(it) }
    fun load(): MenuData = UserRepositories.open(context).use { r ->
        val profile = r.profiles.latest(owner, OnboardingStore(context).load().id)
        val p = pending()
        val plans = r.plans.current(owner).sortedByDescending { it.week.iso }
        val recipes = CatalogRepository.open(context).use { catalog ->
            plans.flatMap { it.items }.map { it.recipe }.distinct().mapNotNull { ref ->
                catalog.recipe(ref)?.let { ref to it }
            }.toMap()
        }
        MenuData(profile, plans, p,
            p?.let { r.planGeneration.receipt(owner, it.request.requestId) },
            p?.future?.let { r.futurePlans.record(owner, it.operationId) },
            r.planGeneration.currentInputs(owner)?.policy != null, recipes)
    }
    fun start(plan: MealPlan?) {
        UserRepositories.open(context).use { r ->
            val profile = requireNotNull(r.profiles.latest(owner, OnboardingStore(context).load().id))
            val inputs = requireNotNull(r.planGeneration.currentInputs(owner))
            val zone = plan?.timezone ?: ZoneId.systemDefault().id
            val today = LocalDate.now(ZoneId.of(zone))
            val id = UUID.randomUUID().toString()
            val request = PlanGenerationRequest(CandidateRequest(profile.ref, requireNotNull(inputs.policy).ref,
                inputs.data.catalog.ref, RecordedAt(Instant.now().toString(), zone)),
                plan?.week ?: CalendarDate(maxOf(today, r.plans.current(owner).maxOfOrNull {
                    LocalDate.parse(it.week.iso).plusDays(7)
                } ?: today).toString()), zone, profile.goal, profile.mealsPerDay,
                UUID.randomUUID().mostSignificantBits, id, WeeklySearchRules(VersionRef("ui-search-v1", 1)))
            val command = plan?.let { FuturePlanCommand(id, it.ref, id,
                CalendarDate(maxOf(today.toString(), it.week.iso))) }
            val p = PendingMenu(request, plan?.ref?.id ?: "menu-$id", command)
            check(preferences.edit().putString("pending", Json.encodeToString(PendingMenu.serializer(), p)).commit())
        }
        resume()
    }
    fun resume() {
        val p = requireNotNull(pending())
        UserRepositories.open(context).use { r ->
            val result = r.planGeneration.generate(owner, p.request)
            if (p.future != null && result.status == GenerationStatus.SUCCESS) r.futurePlans.preview(owner, p.future)
        }
    }
    fun confirm() {
        val p = requireNotNull(pending())
        UserRepositories.open(context).use { r ->
            if (p.future == null) r.planAcceptance.accept(owner, p.request.requestId, p.planId)
            else r.futurePlans.confirm(requireNotNull(r.futurePlans.record(owner, p.future.operationId)))
        }
        discard()
    }
    fun discard() { check(preferences.edit().remove("pending").commit()) }
}
