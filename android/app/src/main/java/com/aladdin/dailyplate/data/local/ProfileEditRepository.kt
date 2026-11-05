package com.aladdin.dailyplate.data.local

import com.aladdin.dailyplate.domain.UserProfile
import com.aladdin.dailyplate.domain.profile.*
import com.aladdin.dailyplate.domain.policy.*
import java.util.concurrent.Callable

/** One transaction for the new profile, frozen assessment, and K16 request. Never writes logs/plans. */
class ProfileEditRepository internal constructor(private val database: UserDatabase, private val repositories: UserRepositories) {
    fun confirm(preview: ProfileEditPreview): UserProfile = database.runInTransaction(Callable {
        val before = preview.before
        val after = preview.after
        require(after.ref == preview.request.toProfile && before.ref == preview.request.fromProfile)
        require(before.userId == after.userId && preview.assessment.profile == after.ref)
        require(preview.assessment == NutritionPolicyEngine.assess(after, null, after.updatedAt))
        val record = ProfileChangeRecord(after.ref, after.userId, after.updatedAt, preview.request)
        val existing = repositories.profileChanges.version(after.userId, after.ref)
        if (existing == record && repositories.profiles.version(after.userId, after.ref) == after)
            return@Callable after
        if (repositories.profiles.latest(before.userId, before.ref.id) != before)
            throw RevisionConflict("Profile changed; reopen the editor")
        repositories.profiles.save(after, before.ref.version)
        repositories.nutritionAssessments.save(NutritionAssessmentRecord(after.ref, after.userId, preview.assessment), before.ref.version)
        // Receipts share the profile revision; the first receipt is revision 2.
        database.snapshots().insert(UserSnapshotEntity("profile_change", after.userId, after.ref.id,
            after.ref.version, snapshotJson.encodeToString(ProfileChangeRecord.serializer(), record)))
        after
    })
}
