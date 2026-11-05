package com.aladdin.dailyplate.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.policy.*
import java.util.concurrent.Executors
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE)
class NutritionAssessmentPersistenceTest {
    @Test fun onboardingConfirmationAndScreeningAreAtomicAndRetryIsIdempotent() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val executor = Executors.newSingleThreadExecutor()
        try {
            executor.submit {
                val draft = com.aladdin.dailyplate.domain.readyOnboarding().copy(id = "k08-atomic-test")
                UserDatabase.open(context).useDatabase { db ->
                    db.openHelper.writableDatabase.execSQL("""CREATE TRIGGER k08_fail BEFORE INSERT ON user_snapshots
                        WHEN NEW.kind = 'nutrition_assessment' BEGIN SELECT RAISE(ABORT, 'test disk write failure'); END""")
                }
                try {
                    assertThrows(Exception::class.java) { OnboardingStore(context).confirm(draft, "az") }
                    assertNull(OnboardingStore(context).confirmed(draft.id))
                } finally {
                    UserDatabase.open(context).useDatabase { it.openHelper.writableDatabase.execSQL("DROP TRIGGER k08_fail") }
                }
                val saved = OnboardingStore(context).confirm(draft, "az")
                assertEquals("incomplete", saved.eligibilityStatus)
                assertEquals(saved, OnboardingStore(context).confirm(draft, "az"))
                UserRepositories.open(context).use { r ->
                    val assessment = r.nutritionAssessments.version(saved.userId, saved.ref)!!.assessment
                    assertEquals(TargetPath.GENERAL, assessment.path)
                    assertNull(assessment.dailyKcal)
                    assertTrue(PolicyIssue(PolicyReason.POLICY_MISSING) in assessment.issues)
                }
            }.get()
        } finally { executor.shutdownNow() }
    }

    @Test fun frozenCalculationSurvivesDiskReopenAndNewProfilePolicyDoNotRewriteHistory() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "k08-assessment.db"
        context.deleteDatabase(name)
        fun open() = UserRepositories(Room.databaseBuilder(context, UserDatabase::class.java, name)
            .allowMainThreadQueries().build())
        val p = eligibleProfile()
        val original = NutritionAssessmentRecord(p.ref, p.userId,
            NutritionPolicyEngine.assessTest(p, policyFixture(), policyTime))
        val savedLog = log(p.userId)
        try {
            open().use { r ->
                r.profiles.save(p, 0)
                r.foodLogs.record(savedLog)
                r.nutritionAssessments.save(original, 0)
                assertEquals(original, r.nutritionAssessments.save(original, 0))
            }
            open().use { r ->
                assertEquals(original, r.nutritionAssessments.version(p.userId, p.ref))
                assertNull(r.nutritionAssessments.version("other-user", p.ref))
                val next = p.copy(ref = p.ref.copy(version = 2), ageYears = 40)
                val changedPolicy = policyFixture().copy(ref = VersionRef("TEST-policy", 2))
                val changed = NutritionAssessmentRecord(next.ref, next.userId,
                    NutritionPolicyEngine.assessTest(next, changedPolicy, policyTime))
                r.profiles.save(next, 1)
                r.nutritionAssessments.save(changed, 1)
                assertEquals(original, r.nutritionAssessments.version(p.userId, p.ref))
                assertEquals(changed, r.nutritionAssessments.latest(p.userId, p.ref.id))
                assertEquals(savedLog, r.foodLogs.version(savedLog.userId, savedLog.ref))
                assertThrows(RevisionConflict::class.java) {
                    r.nutritionAssessments.save(original.copy(assessment = original.assessment.copy(
                        issues = listOf(PolicyIssue(PolicyReason.NUTRIENT_REFERENCE_MISSING, "TEST")))), 0)
                }
            }
        } finally { context.deleteDatabase(name) }
    }

    @Test fun legacyPolicyPayloadWithoutExecutionRemainsReadableButCannotCalculate() {
        val policy = policyFixture().copy(execution = null)
        val raw = snapshotJson.encodeToString(NutritionPolicy.serializer(), policy)
            .replace(",\"execution\":null", "")
        val restored = snapshotJson.decodeFromString(NutritionPolicy.serializer(), raw)
        assertNull(restored.execution)
        assertNull(NutritionPolicyEngine.assessTest(eligibleProfile(), restored, policyTime).calculation)
    }
}
