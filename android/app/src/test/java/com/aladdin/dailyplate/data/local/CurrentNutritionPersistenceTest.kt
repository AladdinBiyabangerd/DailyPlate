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
    @Test fun suspensionIsAtomicPersistentIdempotentAndProtectsPastAssessments() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "np16-suspension.db"
        context.deleteDatabase(name)
        fun open() = Room.databaseBuilder(context, UserDatabase::class.java, name).allowMainThreadQueries().build()
        val p = readyOnboarding().profile("az", recordedAt, date)
        val stop = com.aladdin.dailyplate.domain.profile.NutritionReview.suspension(p,
            NutritionSuspension.NEW_SYMPTOM, recordedAt)
        try {
            open().useDatabase { db ->
                val r = UserRepositories(db)
                r.profiles.save(p, 0)
                val old = NutritionAssessmentRecord(p.ref, p.userId, NutritionPolicyEngine.assessCurrent(p, recordedAt))
                r.nutritionAssessments.save(old, 0)
                db.openHelper.writableDatabase.execSQL("""CREATE TRIGGER np16_fail BEFORE INSERT ON user_snapshots
                    WHEN NEW.kind = 'profile_change' BEGIN SELECT RAISE(ABORT, 'disk failure'); END""")
                assertThrows(Exception::class.java) { r.profileEdits.confirm(stop) }
                assertEquals(p, r.profiles.latest(p.userId, p.ref.id))
                assertNull(r.nutritionAssessments.version(p.userId, stop.after.ref))
                db.openHelper.writableDatabase.execSQL("DROP TRIGGER np16_fail")
                assertEquals(stop.after, r.profileEdits.confirm(stop))
                assertEquals(stop.after, r.profileEdits.confirm(stop))
                assertEquals(old, r.nutritionAssessments.version(p.userId, p.ref))
                val w = WeightEntry(VersionRef("weight", 1), p.userId, recordedAt, 70.0)
                r.weights.save(w, 0); r.weights.save(w.copy(ref = w.ref.copy(version = 2), weightKg = 71.0), 1)
                r.weights.save(w.copy(userId = "other"), 0)
                assertEquals(listOf(71.0), r.weights.current(p.userId).map { it.weightKg })
            }
            open().useDatabase { db ->
                val r = UserRepositories(db)
                assertEquals(NutritionSuspension.NEW_SYMPTOM, r.profiles.latest(p.userId, p.ref.id)!!.nutritionSuspension)
                assertNull(r.nutritionAssessments.latest(p.userId, p.ref.id)!!.assessment.diaryTarget())
                assertEquals(2, r.profiles.history(p.userId, p.ref.id).size)
            }
        } finally { context.deleteDatabase(name) }
    }

    private fun fixture(name: String) = requireNotNull(javaClass.classLoader!!.getResourceAsStream("nutrition/$name.json"))
        .bufferedReader().use { it.readText().trim() }

    @Test fun blockedAndCancelledPreviewsNeverWriteRevisions() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, UserDatabase::class.java).allowMainThreadQueries().build()
        db.useDatabase {
            val r = UserRepositories(db)
            val rawProfile = fixture("legacy-profile")
            val rawAssessment = fixture("legacy-assessment")
            val base = snapshotJson.decodeFromString(UserProfile.serializer(), rawProfile)
            db.snapshots().insert(UserSnapshotEntity("profile", base.userId, base.ref.id, 1, rawProfile))
            db.snapshots().insert(UserSnapshotEntity("nutrition_assessment", base.userId, base.ref.id, 1, rawAssessment))
            val frozenLog = log(base.userId)
            r.foodLogs.record(frozenLog)
            var ready = ProfileEdit.draft(base).change("activity", "inactive").change("measurements_confirmed", "yes")
            for (q in NutritionHealthQuestion.entries) ready = ready.change(q.name.lowercase(), "no")
            val blocked = listOf(
                ready.change("age", "18").change("measurements_confirmed", "yes"),
                ready.change("age", "65").change("measurements_confirmed", "yes"),
                ready.change("sex", ""), ready.change("activity", "uncertain"),
                ready.copy(answers = ready.answers - "activity_model"),
                ready.change("goal", "LOSE"),
                ready.change("weight", "71"),
            ) + NutritionHealthQuestion.entries.flatMap { q ->
                listOf(ready.change(q.name.lowercase(), ""), ready.change(q.name.lowercase(), "yes"))
            }
            for ((index, draft) in (blocked + ready).withIndex()) {
                val preview = ProfileEdit.preview(base, draft, "", true, "Acceptance preview $index", recordedAt, date)
                if (index < blocked.size) {
                    assertNull("case $index", preview.assessment.dailyKcal)
                    assertNull("case $index", preview.assessment.currentCalculation?.targetKcal)
                    assertNull("case $index", preview.assessment.currentCalculation?.macros)
                } else assertNotNull(preview.assessment.dailyKcal)
                // Preview/cancel must leave both frozen bytes and revision counts untouched.
                assertEquals(1, r.profiles.history(base.userId, base.ref.id).size)
                for (kind in listOf("profile", "nutrition_assessment", "profile_change"))
                    assertNull(db.snapshots().version(kind, base.userId, base.ref.id, 2))
                assertEquals(rawProfile, db.snapshots().version("profile", base.userId, base.ref.id, 1)!!.payload)
                assertEquals(rawAssessment, db.snapshots().version("nutrition_assessment", base.userId, base.ref.id, 1)!!.payload)
                assertEquals(frozenLog, r.foodLogs.version(frozenLog.userId, frozenLog.ref))
            }
        }
    }

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
