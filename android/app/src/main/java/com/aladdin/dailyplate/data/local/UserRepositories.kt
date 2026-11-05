package com.aladdin.dailyplate.data.local

import android.content.Context
import com.aladdin.dailyplate.domain.EditDraft
import com.aladdin.dailyplate.domain.PhotoMealDraft
import com.aladdin.dailyplate.domain.RecipeBookmark
import com.aladdin.dailyplate.domain.RecipeCustomization
import com.aladdin.dailyplate.domain.ReminderSettings
import com.aladdin.dailyplate.domain.SubscriptionEntitlement
import com.aladdin.dailyplate.domain.UserProfile
import com.aladdin.dailyplate.domain.VersionRef
import com.aladdin.dailyplate.domain.WeightEntry
import com.aladdin.dailyplate.domain.policy.NutritionAssessmentRecord
import kotlinx.serialization.KSerializer

/** Owns only qida-user.db; it has no library DB dependency or import access. */
class UserRepositories internal constructor(private val database: UserDatabase) : AutoCloseable {
    val profiles = repository("profile", UserProfile.serializer(), UserProfile::userId, UserProfile::ref)
    val nutritionAssessments = repository("nutrition_assessment", NutritionAssessmentRecord.serializer(),
        NutritionAssessmentRecord::userId, NutritionAssessmentRecord::ref)
    val profileChanges = repository("profile_change", com.aladdin.dailyplate.domain.profile.ProfileChangeRecord.serializer(),
        com.aladdin.dailyplate.domain.profile.ProfileChangeRecord::userId, com.aladdin.dailyplate.domain.profile.ProfileChangeRecord::ref)
    val profileEdits = ProfileEditRepository(database, this)
    val customizations = repository("customization", RecipeCustomization.serializer(), RecipeCustomization::userId, RecipeCustomization::ref)
    val photoDrafts = repository("photo_draft", PhotoMealDraft.serializer(), PhotoMealDraft::userId, PhotoMealDraft::ref)
    val weights = repository("weight", WeightEntry.serializer(), WeightEntry::userId, WeightEntry::ref)
    val reminders = repository("reminder", ReminderSettings.serializer(), ReminderSettings::userId, ReminderSettings::ref)
    val bookmarks = repository("bookmark", RecipeBookmark.serializer(), RecipeBookmark::userId, RecipeBookmark::ref)
    val entitlements = repository("entitlement", SubscriptionEntitlement.serializer(), SubscriptionEntitlement::userId, SubscriptionEntitlement::ref)
    val editDrafts = repository("edit_draft", EditDraft.serializer(), EditDraft::userId, EditDraft::ref)
    val foodLogs = FoodLogRepository(database)
    val plans = MealPlanRepository(database)
    val planConsumption = PlanConsumptionRepository(database)
    val planningImports = PlanningInputImporter(database)
    val planGeneration = PlanGenerationRepository(database)
    val planAcceptance = PlanAcceptanceRepository(database)
    val futurePlans = FuturePlanRepository(database)

    private fun <T> repository(kind: String, serializer: KSerializer<T>, userId: (T) -> String, ref: (T) -> VersionRef) =
        UserSnapshotRepository(database, kind, serializer, userId, ref)

    override fun close() = database.close()

    companion object {
        fun open(context: Context): UserRepositories = UserRepositories(UserDatabase.open(context))
    }
}
