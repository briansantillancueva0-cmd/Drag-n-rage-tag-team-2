package com.example.data.repository

import com.example.data.dao.SaveStateDao
import com.example.data.model.SaveStateEntity
import kotlinx.coroutines.flow.Flow

class SaveStateRepository(private val saveStateDao: SaveStateDao) {

    val allSaveStates: Flow<List<SaveStateEntity>> = saveStateDao.getAllSaveStates()

    fun getSaveStatesForRom(romFileName: String): Flow<List<SaveStateEntity>> {
        return saveStateDao.getSaveStatesForRom(romFileName)
    }

    suspend fun getSaveStateById(id: Long): SaveStateEntity? {
        return saveStateDao.getSaveStateById(id)
    }

    suspend fun getSaveStateBySlot(romFileName: String, slot: Int): SaveStateEntity? {
        return saveStateDao.getSaveStateBySlot(romFileName, slot)
    }

    suspend fun saveState(saveState: SaveStateEntity): Long {
        return saveStateDao.insertSaveState(saveState)
    }

    suspend fun updateSaveState(saveState: SaveStateEntity) {
        saveStateDao.updateSaveState(saveState)
    }

    suspend fun deleteSaveState(saveState: SaveStateEntity) {
        saveStateDao.deleteSaveState(saveState)
    }

    suspend fun deleteSaveStateById(id: Long) {
        saveStateDao.deleteSaveStateById(id)
    }

    suspend fun deleteAllSaveStatesForRom(romFileName: String) {
        saveStateDao.deleteAllSaveStatesForRom(romFileName)
    }
}
