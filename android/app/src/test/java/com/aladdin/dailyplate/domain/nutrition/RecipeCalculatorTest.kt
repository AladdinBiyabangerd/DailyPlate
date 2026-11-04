package com.aladdin.dailyplate.domain.nutrition

import com.aladdin.dailyplate.domain.*
import org.junit.Assert.*
import org.junit.Test

class RecipeCalculatorTest {
    private val ref = VersionRef("fixture", 1)
    private fun snapshot(vararg values: Pair<String, Double?>) = NutrientSnapshot(values.associate {
        it.first to NutrientAmount(it.second, if (it.first == "energy_kcal") NutrientUnit.KCAL else NutrientUnit.GRAM)
    })
    private fun component(grams: Double, nutrients: NutrientSnapshot, state: ComponentState = ComponentState.RAW,
        adjustment: CookingAdjustment? = null, food: VersionRef = ref) =
        NutrientComponent(food, Mass(grams, MassUnit.GRAM), state, nutrients, adjustment)
    private fun energy(result: NutrientSnapshot) = result.values.getValue("energy_kcal").value!!
    private fun yield(grams: Double) = CookedYield(Mass(grams, MassUnit.GRAM), YieldBasis.MEASURED, "independent fixture scale")

    @Test fun independentCookedYieldAndConsumedPortionFixture() {
        val result = RecipeCalculator.calculate(listOf(component(1000.0, snapshot("energy_kcal" to 240.0))),
            4.0, cookedYield = yield(1600.0))
        assertEquals(2400.0, energy(result.total), 1e-10)
        assertEquals(150.0, energy(result.per100gCooked!!), 1e-10)
        assertEquals(420.0, energy(result.consumedCookedMass(Mass(280.0, MassUnit.GRAM))!!), 1e-10)
        assertEquals(600.0, energy(result.consumedServings(1.0)), 1e-10)
    }

    @Test fun scalingBatchDoesNotScalePersonalServing() {
        val result = RecipeCalculator.calculate(listOf(component(100.0, snapshot("energy_kcal" to 123.45))), 1.0, 4.0)
        assertEquals(493.8, energy(result.total), 1e-10)
        assertEquals(123.45, energy(result.consumedServings(1.0)), 1e-10)
        assertEquals(61.725, energy(result.consumedServings(0.5)), 1e-10)
        assertNull(result.per100gCooked)
        assertNull(result.consumedCookedMass(Mass(20.0, MassUnit.GRAM)))
    }

    @Test fun nullAbsentAndAllAbsentValuesPropagateWithIngredientProvenance() {
        val other = VersionRef("other", 2)
        val result = RecipeCalculator.calculate(listOf(
            component(100.0, snapshot("energy_kcal" to 10.0, "fiber_g" to null)),
            component(100.0, snapshot("energy_kcal" to 20.0), food = other)), 2.0)
        assertEquals(30.0, energy(result.total), 0.0)
        assertNull(result.total.values.getValue("fiber_g").value)
        assertNull(result.perDeclaredServing.values.getValue("protein_g").value)
        assertEquals(setOf(ref, other), result.missingIngredients.getValue("fiber_g"))
        assertTrue(result.missingFields.contains("protein_g"))
        assertNull(result.consumedServings(1.0).values.getValue("fiber_g").value)
        assertFalse(result.total.values.containsKey("free_sugars_g"))
    }

    @Test fun oilLossAndNutrientRetentionUseExplicitDocumentedFactors() {
        val oil = component(20.0, snapshot("energy_kcal" to 900.0, "fat_g" to 100.0),
            adjustment = CookingAdjustment("measured drained oil", "fixture measurement", 0.75, emptyMap()))
        val vegetable = component(100.0, snapshot("energy_kcal" to 40.0, "fat_g" to 0.0, "vitamin_g" to 10.0),
            adjustment = CookingAdjustment("fixture retention", "fixture source", 1.0, mapOf("vitamin_g" to 0.5)))
        val result = RecipeCalculator.calculate(listOf(oil, vegetable), 1.0)
        assertEquals(175.0, energy(result.total), 1e-10)
        assertEquals(15.0, result.total.values.getValue("fat_g").value!!, 1e-10)
        val retained = RecipeCalculator.calculate(listOf(vegetable), 1.0)
        assertEquals(5.0, retained.total.values.getValue("vitamin_g").value!!, 1e-10)
    }

    @Test fun cookedComponentIsUsedAsSuppliedAndCannotBeCorrectedTwice() {
        val c = component(200.0, snapshot("energy_kcal" to 100.0), ComponentState.ALREADY_COOKED)
        assertEquals(200.0, energy(RecipeCalculator.calculate(listOf(c), 1.0).total), 0.0)
        assertThrows(IllegalArgumentException::class.java) {
            c.copy(adjustment = CookingAdjustment("cooking", "source", 0.8, emptyMap()))
        }
        assertThrows(IllegalArgumentException::class.java) {
            c.copy(state = ComponentState.AS_DESCRIBED,
                adjustment = CookingAdjustment("cooking", "source", 0.8, emptyMap()))
        }
    }

    @Test fun gramsKilogramsAndFoodSpecificMeasuresRemainVersionBound() {
        assertEquals(1250.0, Mass(1.25, MassUnit.KILOGRAM).grams, 0.0)
        val measure = FoodMeasure(ref, "tbsp", 12.5, "documented food portion")
        assertEquals(25.0, measure.grams(ref, 2.0), 0.0)
        assertThrows(IllegalArgumentException::class.java) { measure.grams(ref.copy(version = 2), 1.0) }
        val result = RecipeCalculator.calculate(listOf(NutrientComponent(ref, Mass(0.25, MassUnit.KILOGRAM),
            ComponentState.RAW, snapshot("energy_kcal" to 200.0))), 1.0)
        assertEquals(500.0, energy(result.total), 0.0)
    }

    @Test fun nutrientMassUnitsNormalizeAndEnergyIsNeverDerivedFromMacros() {
        val sodium = NutrientSnapshot(mapOf("sodium" to NutrientAmount(500.0, NutrientUnit.MILLIGRAM)))
        val result = RecipeCalculator.calculate(listOf(component(100.0, sodium)), 1.0,
            requiredUnits = mapOf("sodium" to NutrientUnit.GRAM, "energy_kcal" to NutrientUnit.KCAL))
        assertEquals(0.5, result.total.values.getValue("sodium").value!!, 1e-10)
        assertNull(result.total.values.getValue("energy_kcal").value)
        assertEquals(1000.0, NutrientUnits.convert(NutrientAmount(1.0, NutrientUnit.MILLIGRAM), NutrientUnit.MICROGRAM).value!!, 1e-10)
        assertThrows(IllegalArgumentException::class.java) {
            NutrientUnits.convert(NutrientAmount(null, NutrientUnit.KCAL), NutrientUnit.GRAM)
        }
    }

    @Test fun roundingOccursOnlyAtDisplayBoundary() {
        val result = RecipeCalculator.calculate(listOf(component(100.0, snapshot("energy_kcal" to 1.0))), 3.0)
        assertEquals(1.0 / 3, energy(result.perDeclaredServing), 0.0)
        assertEquals("0.33", result.perDeclaredServing.displayValues(2).getValue("energy_kcal").toString())
        assertEquals(1.0, energy(result.consumedServings(3.0)), 0.0)
        val tiny = component(100.0, snapshot("energy_kcal" to 0.004))
        val sum = RecipeCalculator.calculate(listOf(tiny, tiny, tiny), 1.0)
        assertEquals("0.01", sum.total.displayValues(2).getValue("energy_kcal").toString())
    }

    @Test fun invalidQuantitiesOverflowAndPortionBoundsFailExplicitly() {
        for (bad in listOf(0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY)) {
            assertThrows(IllegalArgumentException::class.java) { Mass(bad, MassUnit.GRAM) }
        }
        assertThrows(IllegalArgumentException::class.java) { RecipeCalculator.calculate(emptyList(), 1.0) }
        val components = listOf(component(100.0, snapshot("energy_kcal" to 100.0)))
        assertThrows(IllegalArgumentException::class.java) { RecipeCalculator.calculate(components, 0.0) }
        assertThrows(IllegalArgumentException::class.java) { CookingAdjustment("x", "y", 1.1, emptyMap()) }
        assertThrows(IllegalArgumentException::class.java) {
            CookingAdjustment("x", "y", 1.0, mapOf("energy_kcal" to Double.NaN))
        }
        assertThrows(IllegalArgumentException::class.java) {
            RecipeCalculator.calculate(listOf(component(Double.MAX_VALUE, snapshot("energy_kcal" to Double.MAX_VALUE))), 1.0)
        }
        val result = RecipeCalculator.calculate(components, 4.0, cookedYield = yield(100.0))
        assertThrows(IllegalArgumentException::class.java) { result.consumedServings(5.0) }
        assertThrows(IllegalArgumentException::class.java) { result.consumedCookedMass(Mass(101.0, MassUnit.GRAM)) }
    }

    @Test fun estimatedYieldRetainsItsEvidenceAndNeverChangesTotalEnergy() {
        val estimate = CookedYield(Mass(200.0, MassUnit.GRAM), YieldBasis.DOCUMENTED_ESTIMATE, "matching method fixture")
        val result = RecipeCalculator.calculate(listOf(component(100.0, snapshot("energy_kcal" to 300.0))), 1.0, cookedYield = estimate)
        assertEquals(estimate, result.cookedYield)
        assertEquals(300.0, energy(result.total), 0.0)
        assertEquals(150.0, energy(result.per100gCooked!!), 0.0)
    }
}
