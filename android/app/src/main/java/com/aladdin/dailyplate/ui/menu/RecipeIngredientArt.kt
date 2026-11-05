package com.aladdin.dailyplate.ui.menu

import androidx.annotation.DrawableRes
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.domain.Food

/** Decorative reference-board vocabulary only; never used for identity or nutrition. */
@DrawableRes
internal fun recipeIngredientArt(food: Food?): Int {
    val words = (food?.names?.en.orEmpty() + " " + food?.names?.az.orEmpty())
        .lowercase(java.util.Locale.ROOT).split(Regex("[^\\p{L}]+"))
    return when {
        words.any { it in setOf("chicken", "toyuq") } -> R.drawable.art_ingredient_chicken
        words.any { it in setOf("rice", "düyü") } -> R.drawable.art_ingredient_rice
        words.any { it in setOf("carrot", "carrots", "kök") } -> R.drawable.art_ingredient_carrot
        words.any { it in setOf("onion", "onions", "soğan") } -> R.drawable.art_ingredient_onion
        words.any { it in setOf("oil", "yağ", "yağı") } -> R.drawable.art_ingredient_oil
        else -> R.drawable.art_cooking
    }
}
