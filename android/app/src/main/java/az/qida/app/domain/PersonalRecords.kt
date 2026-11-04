package az.qida.app.domain

import kotlinx.serialization.Serializable

@Serializable
data class WeightEntry(val ref: VersionRef, val userId: String, val measuredAt: RecordedAt, val weightKg: Double) {
    init { positive(weightKg) }
}

/** start > end is intentionally allowed: quiet hours may cross midnight. */
@Serializable
data class QuietHours(val start: WallTime, val end: WallTime)
@Serializable
enum class WeightFrequency { DAILY, WEEKLY }
@Serializable
enum class PermissionState { NOT_REQUESTED, GRANTED, DENIED }

@Serializable
data class ReminderSettings(
    val ref: VersionRef, val userId: String, val mealSlot: String?, val enabled: Boolean,
    val localTime: WallTime, val timezone: String, val quietHours: QuietHours?,
    val weightFrequency: WeightFrequency?, val permissionState: PermissionState,
) { init { java.time.ZoneId.of(timezone) } }

@Serializable
data class RecipeBookmark(val ref: VersionRef, val userId: String, val recipe: VersionRef)

@Serializable
data class ShoppingItem(
    val foodId: String, val preparationState: String, val quantity: Double, val unit: String,
    val homeQuantity: Double, val purchasedQuantity: Double, val planVersion: Long,
) {
    init { nonNegative(quantity); nonNegative(homeQuantity); nonNegative(purchasedQuantity); require(planVersion > 0) }
}

@Serializable
enum class SubscriptionStatus { ACTIVE, EXPIRED, CANCELLATION_SCHEDULED, PENDING, FAILED, RESTORED }

/** Store references and verification status only; never payment credentials. */
@Serializable
data class SubscriptionEntitlement(
    val ref: VersionRef, val userId: String, val productId: String, val store: String,
    val status: SubscriptionStatus, val validUntil: RecordedAt?, val verifiedAt: RecordedAt?,
) {
    init {
        if (status == SubscriptionStatus.ACTIVE || status == SubscriptionStatus.RESTORED ||
            status == SubscriptionStatus.CANCELLATION_SCHEDULED) require(verifiedAt != null && validUntil != null)
    }
}

@Serializable
enum class EditContext { PLAN_INGREDIENTS, PREPARED_MEAL }

@Serializable
data class EditDraft(
    val ref: VersionRef, val userId: String, val context: EditContext,
    val baseRecipe: VersionRef, val basePlan: VersionRef?, val customization: VersionRef?,
    val changes: List<IngredientChange>, val savedAt: RecordedAt,
)

@Serializable
data class IngredientPrice(
    val ref: VersionRef, val foodId: String, val region: String, val retailer: String,
    val observedAt: RecordedAt, val packageGrams: Double, val edibleFraction: Double,
    val packagePrice: Money, val preparationState: String, val source: String,
) {
    init { positive(packageGrams); positive(edibleFraction); require(edibleFraction <= 1) }
}
