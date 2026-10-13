package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savestates")
data class SaveStateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val slotNumber: Int,
    val romFileName: String,
    val gameTitle: String,
    val title: String,
    val timestamp: Long = System.currentTimeMillis(),
    val stateFileSizeBytes: Long = 4_194_304L, // ~4 MB PPSSPP state
    val statePath: String,
    val screenshotPreviewUrl: String? = null,
    val p1FighterName: String = "Goku (Super Saiyan)",
    val p1Hp: Float = 1000f,
    val p1Ki: Float = 60f,
    val cpuFighterName: String = "Frieza (Final Form)",
    val cpuHp: Float = 850f,
    val battleArenaStage: String = "Rocky Wasteland",
    val isAutoSave: Boolean = false,
    val notes: String = "Slot saved in 60 FPS battle tick"
)
