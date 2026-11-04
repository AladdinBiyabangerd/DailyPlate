package az.qida.app.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import az.qida.app.domain.EvidenceCard
import az.qida.app.domain.Food
import az.qida.app.domain.IngredientPrice
import az.qida.app.domain.NutritionPolicy
import az.qida.app.domain.Recipe
import az.qida.app.domain.VersionRef
import kotlinx.serialization.KSerializer

@Entity(tableName = "library_snapshots", primaryKeys = ["kind", "id", "version"])
internal data class LibrarySnapshotEntity(val kind: String, val id: String, val version: Long, val payload: String)

@Dao
internal interface LibrarySnapshotDao {
    @Query("SELECT * FROM library_snapshots WHERE kind = :kind AND id = :id AND version = :version")
    fun version(kind: String, id: String, version: Long): LibrarySnapshotEntity?

    @Query("SELECT * FROM library_snapshots WHERE kind = :kind ORDER BY version DESC, id")
    fun all(kind: String): List<LibrarySnapshotEntity>

    // Import boundary only. Personal repositories cannot access this DAO.
    @Insert fun insert(snapshot: LibrarySnapshotEntity)
}

@Database(entities = [LibrarySnapshotEntity::class], version = 1, exportSchema = true)
internal abstract class LibraryDatabase : RoomDatabase() {
    abstract fun snapshots(): LibrarySnapshotDao

    companion object {
        fun open(context: Context): LibraryDatabase = Room.databaseBuilder(
            context.applicationContext, LibraryDatabase::class.java, "qida-library.db",
        ).build()
    }
}

/** Read-only application-facing library access. K03 owns the content import gate. */
class CatalogRepository internal constructor(private val database: LibraryDatabase) : AutoCloseable {
    /** Import is atomic and never writes user data. Call on a worker thread. */
    fun importPackage(pack: LibraryPackage): Long = LibraryImporter(database).importPackage(pack)

    /** G3 is closed: current package has no reviewed production content. */
    fun productionRecipes(): List<Recipe> = emptyList()
    fun productionFoods(): List<Food> = emptyList()

    fun food(ref: VersionRef): Food? = read("food", ref, Food.serializer())
    fun recipe(ref: VersionRef): Recipe? = read("recipe", ref, Recipe.serializer())
    fun policy(ref: VersionRef): NutritionPolicy? = read("policy", ref, NutritionPolicy.serializer())
    fun evidence(ref: VersionRef): EvidenceCard? = read("evidence", ref, EvidenceCard.serializer())
    fun price(ref: VersionRef): IngredientPrice? = read("price", ref, IngredientPrice.serializer())

    private fun <T> read(kind: String, ref: VersionRef, serializer: KSerializer<T>): T? =
        database.snapshots().version(kind, ref.id, ref.version)?.let {
            snapshotJson.decodeFromString(serializer, it.payload)
        }

    override fun close() = database.close()

    companion object {
        fun open(context: Context): CatalogRepository = CatalogRepository(LibraryDatabase.open(context))
    }
}
