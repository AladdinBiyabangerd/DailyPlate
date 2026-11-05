package com.aladdin.dailyplate.data.local

import android.content.Context
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.onboarding.OnboardingDraft
import com.aladdin.dailyplate.domain.policy.NutritionAssessmentRecord
import com.aladdin.dailyplate.domain.policy.NutritionPolicyEngine
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

/** All methods run on an IO dispatcher. Failed durable writes must remain visible to the caller. */
class OnboardingStore(context: Context) {
    private val context = context.applicationContext
    private val file = android.util.AtomicFile(java.io.File(this.context.filesDir, "onboarding-draft.json"))
    companion object { private val writer = java.util.concurrent.Executors.newSingleThreadExecutor() }
    fun load(): OnboardingDraft {
        writer.submit {}.get()
        return if (file.baseFile.exists() || java.io.File(file.baseFile.path + ".bak").exists()) file.openRead().bufferedReader().use {
            Json.decodeFromString(OnboardingDraft.serializer(), it.readText())
        } else OnboardingDraft(UUID.randomUUID().toString())
    }
    fun queueSave(draft: OnboardingDraft): java.util.concurrent.Future<*> = writer.submit { save(draft) }
    private fun save(draft: OnboardingDraft) {
        val stream = file.startWrite()
        try {
            stream.write(Json.encodeToString(OnboardingDraft.serializer(), draft).toByteArray(Charsets.UTF_8))
            stream.fd.sync()
            file.finishWrite(stream)
        } catch (failure: Exception) {
            file.failWrite(stream)
            throw failure
        }
    }
    fun confirmed(id: String): UserProfile? = UserRepositories.open(context).use { it.profiles.latest("local-guest", id) }
    fun confirm(draft: OnboardingDraft, language: String): UserProfile = confirm(
        com.aladdin.dailyplate.domain.onboarding.OnboardingPreview.create(draft, language,
            RecordedAt(Instant.now().toString(), ZoneId.systemDefault().id), CalendarDate(LocalDate.now().toString())))

    fun confirm(preview: com.aladdin.dailyplate.domain.onboarding.OnboardingPreview): UserProfile {
        val database = UserDatabase.open(context)
        return UserRepositories(database).use { repositories ->
            database.runInTransaction(java.util.concurrent.Callable {
                val candidate = preview.profile
                require(preview.assessment == NutritionPolicyEngine.assessCurrent(candidate, candidate.updatedAt))
                val existing = repositories.profiles.latest(candidate.userId, candidate.ref.id)
                // Retry after durable success never rewrites the first snapshot.
                if (existing != null) return@Callable existing
                repositories.profiles.save(candidate, 0)
                repositories.nutritionAssessments.save(NutritionAssessmentRecord(candidate.ref, candidate.userId,
                    preview.assessment), 0)
                candidate
            })
        }
    }
}
