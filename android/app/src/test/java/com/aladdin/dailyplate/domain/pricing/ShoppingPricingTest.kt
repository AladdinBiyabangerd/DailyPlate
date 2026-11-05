package com.aladdin.dailyplate.domain.pricing

import com.aladdin.dailyplate.domain.*
import java.math.BigDecimal
import java.time.Duration
import org.junit.Assert.*
import org.junit.Test

class ShoppingPricingTest {
    private val product = ShoppingProduct("TEST-rice", "dry")
    private val at = RecordedAt("2026-11-05T00:00:00Z", "Asia/Baku")
    private val freshness = PriceFreshness(VersionRef("TEST-freshness", 1), Duration.ofDays(7))
    private val price = IngredientPrice(VersionRef("TEST-price", 1), product.foodId, "TEST-region", "TEST-store",
        at, 500.0, 1.0, Money("3.25", "AZN"), "dry", "TEST fixture, not a market observation")
    private fun need(grams: String, p: ShoppingProduct = product) = ShoppingNeed(p, grams.toBigDecimal())
    private fun run(
        needs: List<ShoppingNeed> = listOf(need("250"), need("500")),
        home: List<ShoppingNeed> = emptyList(), observations: List<PriceObservation> = listOf(PriceObservation(price, true)),
        region: String? = "TEST-region", policy: PriceFreshness? = freshness,
    ) = ShoppingPricing.calculateTest(needs, home, observations, region, "AZN", at, policy)
    private fun decimal(expected: String, actual: BigDecimal?) {
        assertNotNull(actual); assertEquals(0, expected.toBigDecimal().compareTo(actual!!))
    }
    @Test fun aggregatesWeekAndSeparatesUsageFromWholePackages() {
        val r = run()
        assertEquals(1, r.lines.size)
        decimal("750", r.lines.single().requiredEdibleGrams)
        decimal("2", r.lines.single().packages)
        decimal("4.875", r.lines.single().usageCost!!.amount.toBigDecimal())
        decimal("6.50", r.totalCost!!.amount.toBigDecimal())
        assertTrue(r.testOnly); assertFalse(r.budgetComplianceVerified)
    }
    @Test fun homeIsDeductedOnceAfterAggregationAndNeverMakesNegativePurchase() {
        decimal("1", run(home = listOf(need("100"), need("150"))).lines.single().packages)
        decimal("0", run(home = listOf(need("1000"))).lines.single().packages)
        decimal("2", run(home = listOf(need("250", product.copy(preparationState = "cooked")))).lines.single().packages)
    }
    @Test fun edibleFractionAndExactPackageBoundary() {
        val observations = listOf(PriceObservation(price.copy(edibleFraction = 0.5), true))
        decimal("3", run(observations = observations).lines.single().packages)
        decimal("4", run(needs = listOf(need("750.000001")), observations = observations).lines.single().packages)
    }
    @Test fun rawPriceIsNeverAppliedToCookedGramsOrOtherCurrency() {
        val cooked = run(needs = listOf(need("750", product.copy(preparationState = "cooked"))))
        assertNull(cooked.totalCost); assertTrue(PriceIssue.STATE_MISMATCH in cooked.issues)
        val currency = run(observations = listOf(PriceObservation(price.copy(packagePrice = Money("1", "USD")), true)))
        assertNull(currency.totalCost); assertTrue(PriceIssue.PRICE_MISSING in currency.issues)
    }
    @Test fun missingRegionPriceOrPolicyNeverProducesPartialTotal() {
        for (r in listOf(run(region = null), run(region = "other"), run(observations = emptyList()),
            run(policy = null), run(needs = listOf(need("750"), need("1", ShoppingProduct("missing", "raw")))))) {
            assertNull(r.totalCost); assertTrue(r.issues.isNotEmpty())
        }
    }
    @Test fun freshnessBoundaryFutureAndStale() {
        fun observed(instant: String) = run(observations = listOf(PriceObservation(price.copy(observedAt = at.copy(instant = instant)), true)))
        assertNotNull(observed("2026-10-29T00:00:00Z").totalCost)
        assertTrue(PriceIssue.PRICE_STALE in observed("2026-10-28T23:59:59Z").issues)
        assertTrue(PriceIssue.PRICE_FROM_FUTURE in observed("2026-11-05T00:00:01Z").issues)
    }
    @Test fun deterministicSelectionAndProductionGate() {
        val older = PriceObservation(price.copy(ref = VersionRef("older", 1), observedAt = at.copy(instant = "2026-11-04T00:00:00Z")), true)
        val current = PriceObservation(price, true)
        assertEquals(run(observations = listOf(older, current)), run(observations = listOf(current, older)))
        for (fixture in listOf(true, false)) {
            val r = ShoppingPricing.calculate(listOf(need("750")), emptyList(), listOf(PriceObservation(price, fixture)),
                "TEST-region", "AZN", at, freshness)
            assertNull(r.totalCost); assertFalse(r.budgetComplianceVerified)
            assertTrue(PriceIssue.G4_REQUIRED in r.issues)
            assertEquals(fixture, PriceIssue.FIXTURE_PRICE in r.issues)
        }
    }
    @Test(expected = IllegalArgumentException::class) fun rejectsNegativeInventory() { need("-1") }
}
