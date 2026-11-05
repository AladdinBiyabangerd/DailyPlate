package com.aladdin.dailyplate.ui.menu

import android.content.Context
import com.aladdin.dailyplate.data.local.*
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.planning.PlanConsumption
import kotlinx.serialization.json.Json

/** Durable exact command is retained on failure; retry never invents an event or timestamp. */
@android.annotation.SuppressLint("ApplySharedPref", "UseKtx")
internal class RecipeWorkflow(context: Context) {
    private val context = context.applicationContext
    private val preferences = this.context.getSharedPreferences("recipe-consumption", 0)
    fun pending(): PlanConsumption? = preferences.getString("pending", null)?.let { Json.decodeFromString<PlanConsumption>(it) }
    fun load(planId: String, itemId: String): RecipeDetailData = UserRepositories.open(context).use { r ->
        val plan = requireNotNull(r.plans.latest("local-guest", planId))
        val item = plan.items.single { it.id == itemId }
        val custom = item.customization?.let { requireNotNull(r.customizations.version(plan.userId, it)) }
        CatalogRepository.open(context).use { catalog ->
            val recipe = requireNotNull(catalog.recipe(item.recipe))
            require(custom == null || custom.baseRecipe == recipe.ref)
            val refs = recipe.ingredients.map { it.food } + custom?.ingredientChanges.orEmpty().map { it.food }
            RecipeDetailData(plan, item, recipe, custom, refs.distinct().mapNotNull { ref ->
                catalog.food(ref)?.let { ref to it }
            }.toMap())
        }
    }
    fun confirm(command: PlanConsumption) {
        val old = pending()
        require(old == null || old == command)
        check(preferences.edit().putString("pending", Json.encodeToString(PlanConsumption.serializer(), command)).commit())
        resume()
    }
    fun resume() {
        val command = requireNotNull(pending())
        try {
            UserRepositories.open(context).use { it.planConsumption.confirm(command) }
        } catch (stale: RevisionConflict) {
            // Repository checks its receipt before revision: this event has not committed.
            check(preferences.edit().remove("pending").commit())
            throw stale
        }
        check(preferences.edit().remove("pending").commit())
    }
}
