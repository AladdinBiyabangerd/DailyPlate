package com.aladdin.dailyplate.ui.today

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.data.local.CatalogRepository
import com.aladdin.dailyplate.domain.FoodLog
import kotlinx.coroutines.*

/** Names may be localized; amounts always come from the immutable actual batch. */
@Composable
fun RecordedIngredients(log: FoodLog) {
    val actual = log.actualRecipeSnapshot ?: return
    val context = LocalContext.current
    val language = LocalConfiguration.current.locales[0].language
    var names by remember(log.ref, language) { mutableStateOf<Map<String, String>>(emptyMap()) }
    LaunchedEffect(log.ref, language) {
        try {
            names = withContext(Dispatchers.IO) {
                CatalogRepository.open(context).use { catalog ->
                    actual.ingredients.mapNotNull { ingredient ->
                        catalog.food(ingredient.food)?.names?.let { labels ->
                            ingredient.food.id to (when (language) { "en" -> labels.en; "ru" -> labels.ru; else -> labels.az } ?: labels.az)
                        }
                    }.toMap()
                }
            }
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (_: Exception) { /* Snapshot quantities remain available offline. */ }
    }
    actual.ingredients.forEach { ingredient ->
        Text("${names[ingredient.food.id] ?: ingredient.food.id}: ${ingredient.edibleGrams} ${stringResource(R.string.fe_grams)}")
    }
}
