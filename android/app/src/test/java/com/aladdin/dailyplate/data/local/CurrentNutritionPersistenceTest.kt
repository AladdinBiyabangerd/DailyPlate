package com.aladdin.dailyplate.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.policy.*
import com.aladdin.dailyplate.domain.profile.ProfileEdit
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE)
class CurrentNutritionPersistenceTest {
    private fun fixture(name: String) = requireNotNull(javaClass.classLoader!!.getResourceAsStream("nutrition/$name.json"))
        .bufferedReader().use { it.readText().trim() }

    @Test fun legacyPayloadsRemainByteIdenticalAfterReconfirmationAndReopen() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "current-nutrition-migration.db"
        context.deleteDatabase(name)
        fun open() = Room.databaseBuilder(context, UserDatabase::class.java, name).allowMainThreadQueries().build()
        val rawProfile = fixture("legacy-profile")
        val rawAssessment = fixture("legacy-assessment")
        val legacy = snapshotJson.decodeFromString(UserProfile.serializer(), rawProfile)
        val frozen = snapshotJson.decodeFromString(NutritionAssessmentRecord.serializer(), rawAssessment)
        val log = log(legacy.userId)
        try {
            open().useDatabase { db ->
                val r = UserRepositories(db)
                db.snapshots().insert(UserSnapshotEntity("profile", legacy.userId, legacy.ref.id, 1, rawProfile))
                db.snapshots().insert(UserSnapshotEntity("nutrition_assessment", legacy.userId, legacy.ref.id, 1, rawAssessment))
                r.foodLogs.record(log)
                assertNull(legacy.activityModel)
                assertFalse(legacy.measurementsConfirmed)
                assertNull(NutritionPolicyEngine.assessCurrent(legacy, recordedAt).dailyKcal)
                assertEquals(1648.75, frozen.assessment.calculation!!.restingKcal, 0.0)
                var draft = ProfileEdit.draft(legacy).change("activity", "inactive").change("measurements_confirmed", "yes")
                for (q in NutritionHealthQuestion.entries) draft = draft.change(q.name.lowercase(), "no")
                val preview = ProfileEdit.preview(legacy, draft, "", true, "Confirm current inputs", recordedAt, date)
                assertEquals(TargetPath.AUTOMATIC, preview.assessment.path)
                // Preview/cancel has no new revisions. Confirmation writes only the new revision.
                assertEquals(1, r.profiles.history(legacy.userId, legacy.ref.id).size)
                assertEquals(preview.after, r.profileEdits.confirm(preview))
                assertEquals(preview.after, r.profileEdits.confirm(preview))
                assertEquals(2, r.profiles.history(legacy.userId, legacy.ref.id).size)
            }
            open().useDatabase { db ->
                val r = UserRepositories(db)
                assertEquals(rawProfile, db.snapshots().version("profile", legacy.userId, legacy.ref.id, 1)!!.payload)
                assertEquals(rawAssessment, db.snapshots().version("nutrition_assessment", legacy.userId, legacy.ref.id, 1)!!.payload)
                assertEquals(frozen, r.nutritionAssessments.version(legacy.userId, legacy.ref))
                assertEquals(log, r.foodLogs.version(log.userId, log.ref))
                val current = r.nutritionAssessments.latest(legacy.userId, legacy.ref.id)!!.assessment
                assertEquals(2552.67, current.dailyKcal!!, 0.000001)
                assertNull(current.calculation)
                assertNotNull(current.currentCalculation!!.bounds)
            }
        } finally { context.deleteDatabase(name) }
    }
}
