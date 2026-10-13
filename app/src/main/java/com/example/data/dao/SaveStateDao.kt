package com.example.data.dao

import androidx.room.*
import com.example.data.model.SaveStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SaveStateDao {

    @Query("SELECT * FROM savestates ORDER BY timestamp DESC")
    fun getAllSaveStates(): Flow<List<SaveStateEntity>>

    @Query("SELECT * FROM savestates WHERE romFileName = :romFileName ORDER BY slotNumber ASC, timestamp DESC")
    fun getSaveStatesForRom(romFileName: String): Flow<List<SaveStateEntity>>

    @Query("SELECT * FROM savestates WHERE id = :id LIMIT 1")
    suspend fun getSaveStateById(id: Long): SaveStateEntity?

    @Query("SELECT * FROM savestates WHERE romFileName = :romFileName AND slotNumber = :slot LIMIT 1")
    suspend fun getSaveStateBySlot(romFileName: String, slot: Int): SaveStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaveState(saveState: SaveStateEntity): Long

    @Update
    suspend fun updateSaveState(saveState: SaveStateEntity)

    @Delete
    suspend fun deleteSaveState(saveState: SaveStateEntity)

    @Query("DELETE FROM savestates WHERE id = :id")
    suspend fun deleteSaveStateById(id: Long)

    @Query("DELETE FROM savestates WHERE romFileName = :romFileName")
    suspend fun deleteAllSaveStatesForRom(romFileName: String)

    @Query("SELECT COUNT(*) FROM savestates WHERE romFileName = :romFileName")
    suspend fun countSaveStatesForRom(romFileName: String): Int
}
