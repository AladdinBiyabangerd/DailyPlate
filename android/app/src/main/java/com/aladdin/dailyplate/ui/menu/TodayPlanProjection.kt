package com.aladdin.dailyplate.ui.menu

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.data.local.UserRepositories
import com.aladdin.dailyplate.domain.FoodLog
import com.aladdin.dailyplate.domain.planning.*
import com.aladdin.dailyplate.ui.QidaCard
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.ZoneId

/** Reads accepted plans only; incomplete diaries never acquire an exact remaining allowance. */
@Composable
internal fun TodayPlanProjection(logs: List<FoodLog>, refresh: Int, date: LocalDate) {
    val context = LocalContext.current
    var projection by remember { mutableStateOf<DailyPlanProjection?>(null) }
    LaunchedEffect(logs, refresh, date) {
        projection = null
        try {
            projection = withContext(Dispatchers.IO) {
                UserRepositories.open(context).use { r ->
                    val plan = r.plans.current("local-guest").filter { p ->
                        val today = LocalDate.now(ZoneId.of(p.timezone)).toString()
                        today >= p.week.iso && today <= LocalDate.parse(p.week.iso).plusDays(6).toString()
                    }.maxByOrNull { it.week.iso }
                    plan?.let { dailyPlanProjection(it, logs, LocalDate.now(ZoneId.of(it.timezone))) }
                }
            }
        } catch (e: CancellationException) { throw e }
        catch (_: Exception) { /* Do not display a stale or invented projection after a failed read. */ }
    }
    projection?.let { p ->
        QidaCard {
            val energy = p.projected?.let { WeeklyNutrition.value(it, "energy_kcal", com.aladdin.dailyplate.domain.NutrientUnit.KCAL) }
            Text(stringResource(R.string.mn_projection, energy?.let { "%.0f".format(it) } ?: "—"))
            Text(stringResource(R.string.mn_diary_incomplete))
        }
    }
}
