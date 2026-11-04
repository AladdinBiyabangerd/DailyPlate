package com.aladdin.dailyplate.domain

import com.aladdin.dailyplate.data.local.*
import org.junit.Assert.*
import org.junit.Test

class ContractValidationTest {
    @Test fun invalidQuantitiesAndDatesAreRejectedWithoutInventingClinicalThresholds() {
        listOf(-1.0, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY).forEach { invalid ->
            assertThrows(IllegalArgumentException::class.java) { NutrientAmount(invalid, NutrientUnit.GRAM) }
        }
        assertThrows(IllegalArgumentException::class.java) { Ingredient(foodRef, 0.0) }
        assertThrows(IllegalArgumentException::class.java) { VersionRef("", 1) }
        assertThrows(IllegalArgumentException::class.java) { VersionRef("id", 0) }
        assertThrows(java.time.DateTimeException::class.java) { CalendarDate("2026-02-30") }
        assertThrows(java.time.DateTimeException::class.java) { RecordedAt("2026-11-04T08:00:00Z", "not-a-zone") }
        assertThrows(IllegalArgumentException::class.java) { profile().copy(mealsPerDay = 2) }
        assertThrows(IllegalArgumentException::class.java) { log().copy(portionConfirmed = false) }
        assertThrows(IllegalArgumentException::class.java) { log().copy(recipeChangedAnswer = true) }
        assertEquals(setOf("protein"), nutrients.missingFields)
    }

    @Test fun unapprovedPolicyAndUnverifiedEntitlementCannotBeActivated() {
        val draft = NutritionPolicy(VersionRef("policy", 1), "not approved", emptyList(), VersionRef("formula", 1),
            emptySet(), emptyMap(), emptyMap(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(),
            null, null, PolicyStatus.DRAFT)
        assertThrows(IllegalArgumentException::class.java) { draft.copy(status = PolicyStatus.ACTIVE) }
        assertThrows(IllegalArgumentException::class.java) {
            SubscriptionEntitlement(VersionRef("product", 1), "guest-1", "product", "store",
                SubscriptionStatus.ACTIVE, null, null)
        }
        assertNotEquals(profile().allergies, profile().copy(allergies = emptySet()).allergies)
    }
}
