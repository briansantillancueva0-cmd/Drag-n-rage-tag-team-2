package com.example.model

enum class NavTab(val title: String) {
    HOME("Home"),
    TEXTURE("Texture"),
    MODS("Mods"),
    CONTROLS("Controls"),
    CHEATS("Cheats"),
    SETTINGS("Settings")
}

enum class ControlLayoutType(val label: String) {
    GAME_SPECIFIC("Game-specific"),
    CLASSIC("Classic")
}

enum class ScreenFilterType(val label: String) {
    BILINEAR("Bilinear"),
    SHARP_BILINEAR("Sharp bilinear"),
    FXAA("FXAA"),
    FSR("FSR")
}

enum class AspectRatioType(val label: String) {
    SCREEN("Screen"),
    RATIO_4_3("4:3"),
    RATIO_16_10("16:10"),
    RATIO_16_9("16:9"),
    RATIO_21_9("21:9")
}

enum class ModCategory(val label: String) {
    FILES("Files"),
    STAGES("Stages"),
    SONGS("Songs")
}

enum class RomFormat(val label: String) {
    ISO("ISO Image"),
    CSO("CSO Compressed")
}

data class RomGameFile(
    val id: String,
    val title: String,
    val fileName: String,
    val fileSizeMb: Long,
    val filePath: String,
    val format: RomFormat,
    val region: String = "USA",
    val gameId: String = "ULUS10537",
    val isVerified: Boolean = true,
    val isSelected: Boolean = false,
    val lastPlayed: String = "Ready",
    val isExpanded: Boolean = false,
    val expandedCapacityGb: Double = 1.28, // 1.28 GB original, 2.0 GB o 4.0 GB expandida
    val allocatedModSlots: Int = 12, // Casillas de mods instalados
    val maxModSlots: Int = 64, // Capacidad máxima de casillas de mods
    val installedModNames: List<String> = listOf("pzs3us1.pck (Roster)", "Latin Voces Mario Castañeda", "Cha-La BGM", "Cel-Shaded HD")
)

data class TexturePack(
    val id: String,
    val name: String,
    val resolution: String,
    val sizeMb: Int,
    val author: String,
    val description: String,
    val isEnabled: Boolean = false,
    val isInstalled: Boolean = true,
    val category: String = "Characters",
    val beforePreviewLabel: String = "Vanilla PSP (272p)",
    val afterPreviewLabel: String = "HD Remaster (2160p)",
    val rating: String = "4.9 ★",
    val downloadsCount: String = "38.5k",
    val accentColorHex: Long = 0xFFF59E0B,
    val features: List<String> = listOf("60 FPS Smooth Textures", "MIP Mapping Fixed", "HDR Ready")
)

data class GameMod(
    val id: String,
    val title: String,
    val category: ModCategory,
    val version: String,
    val sizeMb: Int,
    val author: String,
    val description: String,
    val isEnabled: Boolean = false,
    val isInstalled: Boolean = true
)

data class ColorKiMod(
    val id: String,
    val name: String,
    val colorHex: Long,
    val auraDescription: String,
    val colorPaletteSlot: Int = 1, // Color 1, 2, 3, 4
    val sfxHdName: String = "SFX POHD Resonancia Estéreo 96kHz",
    val sfxType: String = "SFX POHD", // SFX POHD, SFX Clásico Z, SFX Sparking ZERO
    val sfxStartFreq: Double = 440.0,
    val sfxEndFreq: Double = 880.0,
    val sfxDurationMs: Int = 300,
    val isSfxEnabled: Boolean = true,
    val isChecked: Boolean = true,
    val isInstalled: Boolean = true
)

data class AudioVoiceMod(
    val id: String,
    val name: String,
    val actors: String,
    val sampleText: String,
    val audioFormat: String = "POHD 96kHz / 24-bit",
    val sampleFreq: Double = 440.0,
    val isChecked: Boolean = true,
    val isInstalled: Boolean = true
)

data class PckCharacterMod(
    val id: String,
    val name: String,
    val replacesOriginal: String,
    val specialMove: String,
    val pckSlot: String = "pzs3us1 (PCK 1)",
    val colorPalette: String = "Color 1 (Normal)",
    val assignedAudio: String = "SFX POHD + Voces Latino",
    val isChecked: Boolean = true,
    val isInstalled: Boolean = true
)

data class StageMapMod(
    val id: String,
    val name: String,
    val atmosphere: String,
    val originalReplaced: String = "Llanura Rocosa / Wasteland",
    val isChecked: Boolean = true,
    val isInstalled: Boolean = true
)

data class MenuUiMod(
    val id: String,
    val name: String,
    val themeStyle: String,
    val hudStyle: String = "Barras HP/Ki Sparking ZERO",
    val isChecked: Boolean = false,
    val isInstalled: Boolean = true
)

data class InstalledMusicTrack(
    val id: String,
    val title: String,
    val singerAnime: String,
    val durationStr: String,
    val isPlaying: Boolean = false,
    val isCurrentBattleBgm: Boolean = false,
    val notesFrequencies: List<Double> = listOf(440.0, 523.25, 587.33, 659.25, 783.99)
)

data class ExtractedGameFile(
    val path: String,
    val name: String,
    val sizeMb: Double,
    val fileType: String, // PCK, AFS, ADX, DDS, BIN, SFO, PMF, DIR
    val description: String,
    val isModded: Boolean = false,
    val isDirectory: Boolean = false
)

data class GameExtractionProgress(
    val isExtracting: Boolean = false,
    val progress: Float = 0f,
    val currentFileName: String = "",
    val isCompleted: Boolean = true,
    val extractedSizeStr: String = "4.0 GB",
    val extractedFilesCount: Int = 13,
    val extractedFiles: List<ExtractedGameFile> = emptyList()
)

data class CwCheat(
    val id: String,
    val name: String,
    val description: String,
    val codeHex: String,
    val isEnabled: Boolean = false,
    val isShenronWish: Boolean = false
)

data class Fighter(
    val id: String,
    val name: String,
    val title: String,
    val maxHp: Float = 1000f,
    var currentHp: Float = 1000f,
    val maxKi: Float = 100f,
    var currentKi: Float = 50f,
    val superAttackName: String,
    val superAttackKiCost: Float = 60f,
    val auraColorHex: Long = 0xFFF59E0B,
    val tagPartnerId: String? = null
)

data class BattleGameState(
    val isPlaying: Boolean = false,
    val isPaused: Boolean = false,
    val p1ActiveIndex: Int = 0,
    val p1Team: List<Fighter> = listOf(
        Fighter("goku_ssj", "Goku", "Super Saiyan", 1000f, 1000f, 100f, 50f, "Super Kamehameha", 60f, 0xFFFBBF24),
        Fighter("vegeta_ssj", "Vegeta", "Super Saiyan", 950f, 950f, 100f, 40f, "Final Flash", 60f, 0xFF38BDF8)
    ),
    val cpuActiveIndex: Int = 0,
    val cpuTeam: List<Fighter> = listOf(
        Fighter("frieza_final", "Frieza", "Final Form", 980f, 980f, 100f, 40f, "Death Ball", 60f, 0xFFA855F7),
        Fighter("cell_perfect", "Cell", "Perfect Form", 1050f, 1050f, 100f, 50f, "Solar Kamehameha", 60f, 0xFF22C55E)
    ),
    val tagCooldownSeconds: Float = 0f,
    val isChargingKi: Boolean = false,
    val isGuarding: Boolean = false,
    val isSuperAttackExecuting: Boolean = false,
    val superAttackProgress: Float = 0f,
    val lastCombatLog: String = "Match started! 2v2 Tenkaichi Tag Team Battle!",
    val p1HitsCombo: Int = 0,
    val sparkingModeActive: Boolean = false,
    val sparkingTimerSeconds: Float = 0f,
    val fps: Int = 60
)

// ==========================================
// 3D MODEL & PCK 1 VIEWER MODELS
// ==========================================
data class Vertex3D(val x: Float, val y: Float, val z: Float, val u: Float = 0f, val v: Float = 0f)
data class Triangle3D(val v1: Int, val v2: Int, val v3: Int, val colorTint: Long = 0xFFE2E8F0)

enum class TextureRenderMode(val label: String) {
    CEL_SHADED_HD("Textura Cel-Shaded HD"),
    ORIGINAL_PSP("Textura Original PSP"),
    MANGA_INK("Textura Manga Ink"),
    WIREFRAME_3D("Malla 3D Wireframe")
}

data class Model3DMesh(
    val id: String,
    val name: String,
    val pckSlot: String,
    val characterOrigin: String,
    val polyCount: Int,
    val textureResolution: String,
    val defaultColorHex: Long,
    val auraColorHex: Long,
    val costumeColorSlots: List<Long> = listOf(
        0xFFF59E0B, // 1: Naranja / Original
        0xFF3B82F6, // 2: Azul Gi / Alt
        0xFFEF4444, // 3: Rojo Torneo / Manga
        0xFF1E293B, // 4: Negro / Shadow
        0xFF10B981, // 5: Verde Esmeralda / Berserk
        0xFF8B5CF6, // 6: Morado Hakai / Ultra Ego
        0xFFEC4899, // 7: Rosa Carmesí / Rosé
        0xFFE2E8F0  // 8: Plateado / Ultra Instinct
    ),
    val vertices: List<Vertex3D>,
    val triangles: List<Triangle3D>,
    val description: String = "Malla 3D extraída de pzs3us1.pck con mapeo UV y texturas DDS",
    val isCustomObj: Boolean = false
)

// ==========================================
// DRAGON WALKER (MODO HISTORIA) MODELS
// ==========================================
data class StoryDialogLine(
    val speaker: String,
    val text: String,
    val portrait: String = "goku"
)

data class StoryMission(
    val id: String,
    val title: String,
    val sagaEra: String,
    val chapterNumber: Int,
    val p1Fighter: Fighter,
    val p2Fighter: Fighter?,
    val cpu1Fighter: Fighter,
    val cpu2Fighter: Fighter?,
    val stageName: String,
    val objectiveDescription: String,
    val dialogLines: List<StoryDialogLine>,
    val isCompleted: Boolean = false,
    val rankGrade: String = "Z",
    val rewardRewardStr: String = "10,000 Zeni + Cápsula Potara",
    val isBossBattle: Boolean = false,
    val nodeX: Float = 0.5f,
    val nodeY: Float = 0.5f,
    val nextNodeIds: List<String> = emptyList(),
    val nodeType: String = "COMBATE" // COMBATE, JEFE, CINEMATICA, WHAT_IF, RECOMPENSA
)

data class StorySaga(
    val id: String,
    val title: String,
    val period: String,
    val description: String,
    val colorHex: Long,
    val missions: List<StoryMission>,
    val isCustom: Boolean = false,
    val isBrolySaga: Boolean = false
)

// ==========================================
// MINI MAPA (RADAR HUD) MODELS
// ==========================================
enum class MiniMapStyle(val label: String, val description: String, val colorHex: Long) {
    DRAGON_RADAR("Radar Dragón Clásico Z", "Radar circular verde esmeralda con cuadrícula concéntrica y esfera de 4 estrellas", 0xFF10B981),
    SCOUTER_BLUE("Scouter Saiyajin Digital", "HUD táctico cian con lecturas numéricas de Ki y detección militar", 0xFF06B6D4),
    POWER_TOURNAMENT("Torneo del Poder Holográfico", "Plataforma circular Kachi Katchin con marcas de arena y vacío", 0xFFA855F7),
    TACTICAL_TAG("Radar Táctico Tenkaichi 3D", "Radar 2v2 táctico con flechas direccionales de aliados y rivales", 0xFFF59E0B)
}

data class MiniMapConfig(
    val style: MiniMapStyle = MiniMapStyle.DRAGON_RADAR,
    val isEnabled: Boolean = true,
    val zoomLevel: Float = 1.0f,
    val sizeDp: Int = 125,
    val showDragonBalls: Boolean = true,
    val showNorthIndicator: Boolean = true
)

// ==========================================
// TTT BBQ & PCK CHARACTER EDITOR MODELS (MainPage / ViewPj)
// ==========================================
enum class TttEditorSubTab(val label: String) {
    MAIN_PAGE("MainPage"),
    VIEW_PJ("ViewPj"),
    COLOR_REFERENCE("Selección Color Juego")
}

data class TttBbqCharacterSlot(
    val slotNumber: Int,
    val hexId: String,
    val characterName: String,
    val colorNumber: Int = 1, // 1 Color, 2 Color, 3 Color, 4 Color
    val commentTag: String,
    val characterFamily: String = "Saibaiman", // "Saibaiman 1", "Saibaiman 2", "Saibaiman 3", "Goku SSJ3", etc.
    val colorHex: Long = 0xFF22C55E
)

data class TttBbqFileState(
    val fileName: String = "00013277-000033DD.BBQ",
    val filePath: String = "C:\\Escritorio\\00013277-000033DD.BBQ",
    val isOpen: Boolean = true,
    val currentOrderIndex: Int = 0,
    val isModified: Boolean = false
)

