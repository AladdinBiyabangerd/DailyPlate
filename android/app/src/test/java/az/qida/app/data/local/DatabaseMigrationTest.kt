package az.qida.app.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE)
class DatabaseMigrationTest {
    @Test fun exportedV1MigratesToV2AndRetainsPayloadAndEventIdentity() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "migration-v1-v2.db"
        context.deleteDatabase(name)
        val file = context.getDatabasePath(name)
        file.parentFile!!.mkdirs()
        val resource = requireNotNull(javaClass.classLoader!!.getResourceAsStream("az.qida.app.data.local.UserDatabase/1.json"))
        val schema = resource.bufferedReader().use { JSONObject(it.readText()).getJSONObject("database") }
        val original = profile()
        val payload = snapshotJson.encodeToString(az.qida.app.domain.UserProfile.serializer(), original)
        SQLiteDatabase.openOrCreateDatabase(file, null).use { old ->
            val entities = schema.getJSONArray("entities")
            for (i in 0 until entities.length()) {
                val entity = entities.getJSONObject(i)
                old.execSQL(entity.getString("createSql").replace("\${TABLE_NAME}", entity.getString("tableName")))
            }
            old.execSQL("INSERT INTO user_snapshots(kind,userId,id,revision,payload) VALUES(?,?,?,?,?)",
                arrayOf<Any>("profile", original.userId, original.ref.id, 1, payload))
            old.execSQL("INSERT INTO food_events(userId,clientEventId,logId) VALUES(?,?,?)",
                arrayOf(original.userId, "legacy-event", "legacy-log"))
            old.version = 1
        }
        try {
            Room.databaseBuilder(context, UserDatabase::class.java, name)
                .addMigrations(UserDatabase.MIGRATION_1_2).allowMainThreadQueries().build().useDatabase { migrated ->
                    val row = migrated.snapshots().latest("profile", original.userId, original.ref.id)!!
                    assertEquals(1, row.payloadSchema)
                    assertEquals(payload, row.payload)
                    assertEquals(original, UserRepositories(migrated).profiles.version(original.userId, original.ref))
                    assertEquals("legacy-log", migrated.snapshots().event(original.userId, "legacy-event")!!.logId)
                    assertEquals(2, migrated.openHelper.readableDatabase.version)
                }
        } finally { context.deleteDatabase(name) }
    }

    @Test fun unsupportedPayloadSchemaIsNeverSilentlyInterpretedAsCurrent() {
        Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), UserDatabase::class.java)
            .allowMainThreadQueries().build().useDatabase { db ->
                db.snapshots().insert(UserSnapshotEntity("profile", "guest-1", "profile", 1, "{}", 99))
                assertThrows(IllegalArgumentException::class.java) {
                    UserRepositories(db).profiles.latest("guest-1", "profile")
                }
            }
    }
}
