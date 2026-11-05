package com.aladdin.dailyplate.domain.entry

import com.aladdin.dailyplate.domain.*
import java.text.Normalizer
import java.util.Locale
import kotlinx.serialization.Serializable

/** Search across all names, preserving exact version and preparation-state identities. */
object FoodSearch {
    private fun key(text: String) = Normalizer.normalize(text.lowercase(Locale.ROOT)
        .replace('ı', 'i'), Normalizer.Form.NFD).replace(Regex("\\p{M}+"), "").trim()
    fun find(foods: List<Food>, query: String): List<Food> {
        val terms = key(query).split(Regex("\\s+")).filter { it.isNotEmpty() }
        return foods.filter { food ->
            val names = (listOfNotNull(food.names.az, food.names.en, food.names.ru) + food.aliases).map(::key)
            terms.all { term -> names.any { term in it } }
        }.distinctBy { it.ref }
    }
}

@Serializable
enum class EntryUnit { GRAM, KILOGRAM, PREVIOUS_PORTION }

/** K11 input only: no event ID, confirmation flag or repository write capability. */
@Serializable
data class PortionDraft(
    val name: String,
    val consumedAt: RecordedAt,
    val method: EntryMethod,
    val food: VersionRef? = null,
    val preparationState: String? = null,
    val source: String? = null,
    val quantity: Double,
    val unit: EntryUnit,
    val nutrientsPerBasis: NutrientSnapshot,
    val previous: FoodLog? = null,
) {
    init {
        require(name.isNotBlank() && quantity.isFinite() && quantity > 0)
        require(method in setOf(EntryMethod.SEARCH, EntryMethod.MANUAL, EntryMethod.PREVIOUS))
        require((method == EntryMethod.PREVIOUS) == (previous != null))
        require((unit == EntryUnit.PREVIOUS_PORTION) == (previous != null))
        require(unit != EntryUnit.KILOGRAM || (quantity * 1000).isFinite())
        require(nutrientsPerBasis.values.values.none { it.value != null } || !source.isNullOrBlank())
    }
    val grams: Double? get() = when (unit) {
        EntryUnit.GRAM -> quantity
        EntryUnit.KILOGRAM -> quantity * 1000
        EntryUnit.PREVIOUS_PORTION -> previous?.confirmedPortionGrams?.times(quantity)
    }
    val nutrients: NutrientSnapshot get() {
        val factor = if (unit == EntryUnit.PREVIOUS_PORTION) quantity else requireNotNull(grams) / 100
        return NutrientSnapshot(nutrientsPerBasis.values.mapValues { (_, value) ->
            NutrientAmount(value.value?.times(factor), value.unit)
        })
    }
}

fun parseEntryNumber(text: String): Double? = text.trim().replace(',', '.').toDoubleOrNull()
    ?.takeIf { it.isFinite() && it >= 0 }

fun Food.portionDraft(quantity: Double, unit: EntryUnit, at: RecordedAt) = PortionDraft(
    names.az, at, EntryMethod.SEARCH, ref, preparationState, "$sourceType / $sourceId / $sourceVersion",
    quantity, unit, nutrientsPer100g,
)

fun FoodLog.repeatDraft(name: String, at: RecordedAt) = PortionDraft(
    name, at, EntryMethod.PREVIOUS, food = food, source = "FoodLog ${ref.id} / ${ref.version}",
    quantity = 1.0, unit = EntryUnit.PREVIOUS_PORTION, nutrientsPerBasis = nutrientSnapshot, previous = this,
)
