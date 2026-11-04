package az.qida.app.data.local

import android.content.Context
import az.qida.app.domain.EditDraft
import az.qida.app.domain.PhotoMealDraft
import az.qida.app.domain.RecipeBookmark
import az.qida.app.domain.RecipeCustomization
import az.qida.app.domain.ReminderSettings
import az.qida.app.domain.SubscriptionEntitlement
import az.qida.app.domain.UserProfile
import az.qida.app.domain.VersionRef
import az.qida.app.domain.WeightEntry
import kotlinx.serialization.KSerializer

/** Owns only qida-user.db; it has no library DB dependency or import access. */
class UserRepositories internal constructor(private val database: UserDatabase) : AutoCloseable {
    val profiles = repository("profile", UserProfile.serializer(), UserProfile::userId, UserProfile::ref)
    val customizations = repository("customization", RecipeCustomization.serializer(), RecipeCustomization::userId, RecipeCustomization::ref)
    val photoDrafts = repository("photo_draft", PhotoMealDraft.serializer(), PhotoMealDraft::userId, PhotoMealDraft::ref)
    val weights = repository("weight", WeightEntry.serializer(), WeightEntry::userId, WeightEntry::ref)
    val reminders = repository("reminder", ReminderSettings.serializer(), ReminderSettings::userId, ReminderSettings::ref)
    val bookmarks = repository("bookmark", RecipeBookmark.serializer(), RecipeBookmark::userId, RecipeBookmark::ref)
    val entitlements = repository("entitlement", SubscriptionEntitlement.serializer(), SubscriptionEntitlement::userId, SubscriptionEntitlement::ref)
    val editDrafts = repository("edit_draft", EditDraft.serializer(), EditDraft::userId, EditDraft::ref)
    val foodLogs = FoodLogRepository(database)
    val plans = MealPlanRepository(database)

    private fun <T> repository(kind: String, serializer: KSerializer<T>, userId: (T) -> String, ref: (T) -> VersionRef) =
        UserSnapshotRepository(database, kind, serializer, userId, ref)

    override fun close() = database.close()

    companion object {
        fun open(context: Context): UserRepositories = UserRepositories(UserDatabase.open(context))
    }
}
