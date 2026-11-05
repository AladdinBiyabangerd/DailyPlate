package com.aladdin.dailyplate.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.profile.*
import com.aladdin.dailyplate.domain.policy.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE)
class ProfileEditPersistenceTest {
    @Test fun futurePlanAcceptanceBindsExactVersionsAndProtectsConsumedItems() {
        val p = readyOnboarding().profile("az", recordedAt, date)
        val request = ProfileEdit.preview(p, ProfileEdit.draft(p).change("goal", "LOSE"), "", false,
            "Goal", recordedAt, date).request
        val before = plan().copy(userId = p.userId, userProfile = p.ref)
        val after = before.copy(ref = before.ref.copy(version = 2), userProfile = request.toProfile,
            shopping = before.shopping.map { it.copy(planVersion = 2) })
        val preview = FuturePlanPreview(request, before, after, date, "Future portions reviewed")
        assertEquals(preview, preview.accept(before.ref, request.toProfile).preview)
        assertThrows(IllegalArgumentException::class.java) { preview.accept(after.ref, request.toProfile) }
        val consumed = before.items.first().copy(status = PlanItemStatus.CONSUMED, consumedLogIds = setOf("old-log"))
        val protected = before.copy(items = listOf(consumed) + before.items.drop(1))
        assertThrows(IllegalArgumentException::class.java) {
            FuturePlanPreview(request, protected, after, date, "Cannot rewrite consumed item")
        }
    }

    @Test fun previewCancelConfirmRetryConflictAndReopenPreserveHistoricalSnapshots() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "k09-test.db"
        context.deleteDatabase(name)
        fun open() = Room.databaseBuilder(context, UserDatabase::class.java, name).allowMainThreadQueries().build()
        val p = readyOnboarding().profile("az", recordedAt, date)
        val original = NutritionAssessmentRecord(p.ref, p.userId, NutritionPolicyEngine.assess(p, null, recordedAt))
        val oldLog = log(p.userId)
        val oldPlan = plan().copy(userId = p.userId, userProfile = p.ref)
        val preview = ProfileEdit.preview(p, ProfileEdit.draft(p).change("goal", "LOSE"), "", false, "Goal changed", recordedAt, date)
        try {
            open().useDatabase { db ->
                val r = UserRepositories(db)
                r.profiles.save(p, 0); r.nutritionAssessments.save(original, 0)
                r.foodLogs.record(oldLog); r.plans.create(oldPlan)
                // Preview/cancel has no storage side effect.
                assertEquals(p, r.profiles.latest(p.userId, p.ref.id))
                assertTrue(r.profileChanges.history(p.userId, p.ref.id).isEmpty())
                db.openHelper.writableDatabase.execSQL("""CREATE TRIGGER k09_fail BEFORE INSERT ON user_snapshots
                    WHEN NEW.kind = 'profile_change' BEGIN SELECT RAISE(ABORT, 'disk failure'); END""")
                assertThrows(Exception::class.java) { r.profileEdits.confirm(preview) }
                assertEquals(p, r.profiles.latest(p.userId, p.ref.id))
                assertNull(r.nutritionAssessments.version(p.userId, preview.after.ref))
                db.openHelper.writableDatabase.execSQL("DROP TRIGGER k09_fail")
                assertEquals(preview.after, r.profileEdits.confirm(preview))
                assertEquals(preview.after, r.profileEdits.confirm(preview))
                val competing = ProfileEdit.preview(p, ProfileEdit.draft(p).change("goal", "GAIN"), "", false, "Other edit", recordedAt, date)
                assertThrows(RevisionConflict::class.java) { r.profileEdits.confirm(competing) }
            }
            open().useDatabase { db ->
                val r = UserRepositories(db)
                assertEquals(2, r.profiles.history(p.userId, p.ref.id).size)
                assertEquals(original, r.nutritionAssessments.version(p.userId, p.ref))
                assertEquals(preview.assessment, r.nutritionAssessments.latest(p.userId, p.ref.id)!!.assessment)
                assertEquals(1, r.profileChanges.history(p.userId, p.ref.id).size)
                assertTrue(r.profileChanges.history("other-user", p.ref.id).isEmpty())
                assertEquals(oldLog, r.foodLogs.version(p.userId, oldLog.ref))
                assertEquals(oldPlan, r.plans.latest(p.userId, oldPlan.ref.id))
            }
        } finally { context.deleteDatabase(name) }
    }
}
