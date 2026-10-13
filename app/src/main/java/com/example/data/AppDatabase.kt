package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.SaveStateDao
import com.example.data.model.SaveStateEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [SaveStateEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun saveStateDao(): SaveStateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dbz_tag_team_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateInitialSaveStates(database.saveStateDao())
                    }
                }
            }
        }

        private suspend fun populateInitialSaveStates(dao: SaveStateDao) {
            // Seed sample initial quick saves for default ISO
            val now = System.currentTimeMillis()
            dao.insertSaveState(
                SaveStateEntity(
                    slotNumber = 1,
                    romFileName = "dbz_tenkaichi_tag_team_usa.iso",
                    gameTitle = "Dragon Ball Z: Tenkaichi Tag Team",
                    title = "Quick Save: Goku SSJ & Vegeta vs Frieza",
                    timestamp = now - 3600_000L,
                    statePath = "/storage/emulated/0/PSP/PPSSPP_STATE/ULUS10537_1.ppst",
                    p1FighterName = "Goku (Super Saiyan)",
                    p1Hp = 920f,
                    p1Ki = 75f,
                    cpuFighterName = "Frieza (Final Form)",
                    cpuHp = 640f,
                    battleArenaStage = "Rocky Wasteland",
                    isAutoSave = false,
                    notes = "Sparking meter 80% charged"
                )
            )
            dao.insertSaveState(
                SaveStateEntity(
                    slotNumber = 2,
                    romFileName = "dbz_tenkaichi_tag_team_usa.iso",
                    gameTitle = "Dragon Ball Z: Tenkaichi Tag Team",
                    title = "AutoSave: Cell Games Arena Entry",
                    timestamp = now - 1800_000L,
                    statePath = "/storage/emulated/0/PSP/PPSSPP_STATE/ULUS10537_2.ppst",
                    p1FighterName = "Vegeta (Super Saiyan)",
                    p1Hp = 1000f,
                    p1Ki = 50f,
                    cpuFighterName = "Cell (Perfect Form)",
                    cpuHp = 1000f,
                    battleArenaStage = "Cell Games Arena",
                    isAutoSave = true,
                    notes = "Checkpoint before Super Final Flash"
                )
            )
        }
    }
}
