package az.qida.app.data.local

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity(tableName = "user_snapshots", primaryKeys = ["kind", "userId", "id", "revision"])
internal data class UserSnapshotEntity(
    val kind: String, val userId: String, val id: String, val revision: Long,
    val payload: String,
    @ColumnInfo(defaultValue = "1") val payloadSchema: Int = 1,
)

@Entity(tableName = "food_events", primaryKeys = ["userId", "clientEventId"])
internal data class FoodEventEntity(val userId: String, val clientEventId: String, val logId: String)

@Dao
internal interface UserSnapshotDao {
    @Query("SELECT * FROM user_snapshots WHERE kind = :kind AND userId = :userId AND id = :id ORDER BY revision DESC LIMIT 1")
    fun latest(kind: String, userId: String, id: String): UserSnapshotEntity?

    @Query("SELECT * FROM user_snapshots WHERE kind = :kind AND userId = :userId AND id = :id AND revision = :revision")
    fun version(kind: String, userId: String, id: String, revision: Long): UserSnapshotEntity?

    @Insert fun insert(snapshot: UserSnapshotEntity)
    @Insert fun insertEvent(event: FoodEventEntity)

    @Query("SELECT * FROM food_events WHERE userId = :userId AND clientEventId = :eventId")
    fun event(userId: String, eventId: String): FoodEventEntity?
}

@Database(entities = [UserSnapshotEntity::class, FoodEventEntity::class], version = 2, exportSchema = true)
internal abstract class UserDatabase : RoomDatabase() {
    abstract fun snapshots(): UserSnapshotDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE user_snapshots ADD COLUMN payloadSchema INTEGER NOT NULL DEFAULT 1")
            }
        }

        fun open(context: Context): UserDatabase = Room.databaseBuilder(
            context.applicationContext, UserDatabase::class.java, "qida-user.db",
        ).addMigrations(MIGRATION_1_2).build()
    }
}
