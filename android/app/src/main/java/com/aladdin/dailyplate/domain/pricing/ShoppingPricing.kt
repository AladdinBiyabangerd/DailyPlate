@file:kotlinx.serialization.UseSerializers(DecimalStringSerializer::class, DurationStringSerializer::class)

package com.aladdin.dailyplate.domain.pricing

import kotlinx.serialization.Serializable
import com.aladdin.dailyplate.domain.IngredientPrice
import com.aladdin.dailyplate.domain.Money
import com.aladdin.dailyplate.domain.RecordedAt
import com.aladdin.dailyplate.domain.VersionRef
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.time.Duration
import java.time.Instant

/** Edible grams in this exact state. Count/volume and cooked/raw conversions belong
 * at a documented conversion boundary; this engine never infers a yield factor. */
@Serializable
data class ShoppingProduct(val foodId: String, val preparationState: String) {
    init { require(foodId.isNotBlank() && preparationState.isNotBlank()) }
}
@Serializable
data class ShoppingNeed(val product: ShoppingProduct, val edibleGrams: BigDecimal) {
    init { require(edibleGrams.signum() >= 0) }
}
@Serializable
data class PriceObservation(val price: IngredientPrice, val testOnly: Boolean)

/** No default freshness threshold: supply an explicitly versioned policy. */
@Serializable
data class PriceFreshness(val ref: VersionRef, val maximumAge: Duration) {
    init { require(!maximumAge.isNegative) }
}
@Serializable
enum class PriceIssue {
    REGION_MISSING, PRICE_MISSING, STATE_MISMATCH, PRICE_STALE, PRICE_FROM_FUTURE,
    FRESHNESS_POLICY_MISSING, FIXTURE_PRICE, G4_REQUIRED,
}
@Serializable
data class ShoppingCostLine(
    val product: ShoppingProduct, val requiredEdibleGrams: BigDecimal,
    val price: VersionRef?, val packages: BigDecimal?,
    val usageCost: Money?, val purchaseCost: Money?, val issues: Set<PriceIssue>,
)
@Serializable
data class ShoppingCost(
    val lines: List<ShoppingCostLine>, val totalCost: Money?,
    val issues: Set<PriceIssue>, val testOnly: Boolean, val freshnessPolicy: VersionRef?,
) {
    // G4 and full budget coverage are separate product acceptances, still closed.
    val budgetComplianceVerified: Boolean get() = false
}

/** Prices full weekly needs. Optional home-availability marks are not quantity or cost inputs. */
object ShoppingPricing {
    fun calculate(
        needs: List<ShoppingNeed>, prices: List<PriceObservation>,
        region: String?, currency: String, at: RecordedAt, freshness: PriceFreshness?,
    ): ShoppingCost = estimate(needs, prices, region, currency, at, freshness, false)

    /** Fixture arithmetic is explicitly isolated from production budget claims. */
    fun calculateTest(
        needs: List<ShoppingNeed>, prices: List<PriceObservation>,
        region: String?, currency: String, at: RecordedAt, freshness: PriceFreshness?,
    ): ShoppingCost = estimate(needs, prices, region, currency, at, freshness, true)

    private fun estimate(
        needs: List<ShoppingNeed>, prices: List<PriceObservation>,
        region: String?, currency: String, at: RecordedAt, freshness: PriceFreshness?, testOnly: Boolean,
    ): ShoppingCost {
        Money("0", currency)
        val lines = needs.groupBy { it.product }.entries
            .sortedWith(compareBy({ it.key.foodId }, { it.key.preparationState })).map { (product, rows) ->
                val required = rows.sumOf { it.edibleGrams }
                val issues = mutableSetOf<PriceIssue>()
                if (region.isNullOrBlank()) issues += PriceIssue.REGION_MISSING
                val regional = prices.filter { it.price.foodId == product.foodId &&
                    it.price.region == region && it.price.packagePrice.currency == currency }
                val matching = regional.filter { it.price.preparationState == product.preparationState }
                // Stable selection independent of input order; never silently choose cheapest/stale offer.
                val observation = matching.sortedWith(compareByDescending<PriceObservation> {
                    Instant.parse(it.price.observedAt.instant)
                }.thenBy { it.price.ref.id }.thenByDescending { it.price.ref.version }).firstOrNull()
                if (observation == null) issues += if (regional.isEmpty()) PriceIssue.PRICE_MISSING else PriceIssue.STATE_MISMATCH
                val price = observation?.price
                if (price != null) {
                    if (observation.testOnly && !testOnly) issues += PriceIssue.FIXTURE_PRICE
                    val age = Duration.between(Instant.parse(price.observedAt.instant), Instant.parse(at.instant))
                    if (age.isNegative) issues += PriceIssue.PRICE_FROM_FUTURE
                    if (freshness == null) issues += PriceIssue.FRESHNESS_POLICY_MISSING
                    else if (age > freshness.maximumAge) issues += PriceIssue.PRICE_STALE
                }
                var packages: BigDecimal? = null
                var usage: Money? = null
                var purchase: Money? = null
                if (price != null && issues.isEmpty()) {
                    val ediblePackage = BigDecimal.valueOf(price.packageGrams) * BigDecimal.valueOf(price.edibleFraction)
                    packages = required.divide(ediblePackage, 0, RoundingMode.CEILING)
                    val amount = price.packagePrice.amount.toBigDecimal()
                    usage = Money(required.multiply(amount).divide(ediblePackage, MathContext.DECIMAL128).toPlainString(), currency)
                    purchase = Money((packages * amount).toPlainString(), currency)
                }
                ShoppingCostLine(product, required, price?.ref, packages, usage, purchase, issues.toSet())
            }
        val issues = lines.flatMap { it.issues }.toMutableSet()
        if (!testOnly) issues += PriceIssue.G4_REQUIRED
        val total = if (issues.isEmpty()) Money(lines.sumOf { it.purchaseCost!!.amount.toBigDecimal() }.toPlainString(), currency) else null
        return ShoppingCost(lines, total, issues.toSet(), testOnly, freshness?.ref)
    }
}
