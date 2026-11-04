package com.aladdin.dailyplate.data.local

import android.content.Context
import androidx.room.*

@Entity(tableName = "editorial_history", primaryKeys = ["kind", "id", "revision"])
internal data class EditorialEntity(val kind: String, val id: String, val revision: Long, val payload: String)

@Dao
internal interface EditorialDao {
    @Query("SELECT * FROM editorial_history WHERE kind = :kind AND id = :id ORDER BY revision DESC LIMIT 1")
    fun latest(kind: String, id: String): EditorialEntity?

    @Query("SELECT * FROM editorial_history WHERE kind = :kind AND id = :id ORDER BY revision")
    fun history(kind: String, id: String): List<EditorialEntity>

    @Insert fun insert(entity: EditorialEntity)
}

/** Separate authoring database. It cannot mutate library or personal history. */
@Database(entities = [EditorialEntity::class], version = 1, exportSchema = true)
internal abstract class EditorialDatabase : RoomDatabase() {
    abstract fun editorial(): EditorialDao

    companion object {
        fun openTestEditor(context: Context): EditorialDatabase = Room.databaseBuilder(
            context.applicationContext, EditorialDatabase::class.java, "dailyplate-test-editor.db",
        ).build()
    }
}
