package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.model.SaveStateEntity
import com.example.data.repository.SaveStateRepository
import com.example.model.*
import com.example.util.GameAudioHaptics
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class DbzTagTeamViewModel(application: Application) : AndroidViewModel(application) {

    val audioHaptics = GameAudioHaptics(application.applicationContext)

    // Room Database & Savestates Repository
    private val database = AppDatabase.getDatabase(application.applicationContext, viewModelScope)
    val saveStateRepository = SaveStateRepository(database.saveStateDao())

    val allSaveStates: StateFlow<List<SaveStateEntity>> = saveStateRepository.allSaveStates
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedTab = MutableStateFlow(NavTab.HOME)
    val selectedTab: StateFlow<NavTab> = _selectedTab.asStateFlow()

    // Game data & Engine info
    private val _isGameDataVerified = MutableStateFlow(true)
    val isGameDataVerified: StateFlow<Boolean> = _isGameDataVerified.asStateFlow()

    private val _gameDataFiles = MutableStateFlow("13 files · 4.0 GB")
    val gameDataFiles: StateFlow<String> = _gameDataFiles.asStateFlow()

    private val _gameEngineInfo = MutableStateFlow("Included (arm64 · PPSSPP Core)")
    val gameEngineInfo: StateFlow<String> = _gameEngineInfo.asStateFlow()

    // Textures state
    private val _useTexturePacks = MutableStateFlow(true)
    val useTexturePacks: StateFlow<Boolean> = _useTexturePacks.asStateFlow()

    // ROMs Library (.iso and .cso)
    private val _romFilesList = MutableStateFlow<List<RomGameFile>>(
        listOf(
            RomGameFile(
                id = "rom_1",
                title = "Dragon Ball Z: Tenkaichi Tag Team",
                fileName = "dbz_tenkaichi_tag_team_usa.iso",
                fileSizeMb = 1280L,
                filePath = "/storage/emulated/0/PSP/GAME/ULUS10537/dbz_tenkaichi_tag_team_usa.iso",
                format = RomFormat.ISO,
                region = "USA",
                gameId = "ULUS10537",
                isVerified = true,
                isSelected = true,
                lastPlayed = "Active Default"
            ),
            RomGameFile(
                id = "rom_2",
                title = "DBZ Tenkaichi Tag Team (European Multi-5)",
                fileName = "dbz_tag_team_eur.cso",
                fileSizeMb = 890L,
                filePath = "/storage/emulated/0/Download/dbz_tag_team_eur.cso",
                format = RomFormat.CSO,
                region = "EUR",
                gameId = "ULES01488",
                isVerified = true,
                isSelected = false,
                lastPlayed = "2 days ago"
            )
        )
    )
    val romFilesList: StateFlow<List<RomGameFile>> = _romFilesList.asStateFlow()

    private val _textureCategoryFilter = MutableStateFlow("All")
    val textureCategoryFilter: StateFlow<String> = _textureCategoryFilter.asStateFlow()

    private val _texturePacks = MutableStateFlow<List<TexturePack>>(
        listOf(
            TexturePack(
                id = "tex_1",
                name = "DBZ TTT Ultra HD Remaster",
                resolution = "4K UHD (2160p)",
                sizeMb = 480,
                author = "TenkaichiTeam",
                description = "Complete upscaled high-resolution textures for all 70 characters, UI, HUD and battle arenas.",
                isEnabled = true,
                category = "Characters",
                beforePreviewLabel = "Vanilla PSP (272p)",
                afterPreviewLabel = "Ultra HD 4K Remaster",
                rating = "4.9 ★",
                downloadsCount = "48.2k",
                accentColorHex = 0xFFF59E0B,
                features = listOf("4x Upscaled Faces & Gi", "MIP Mapping Fixed", "Crisp Textures", "OLED HDR Ready")
            ),
            TexturePack(
                id = "tex_2",
                name = "Anime Cel-Shading & Aura HD",
                resolution = "1080p FHD",
                sizeMb = 210,
                author = "GokuModder",
                description = "High-definition hand-drawn outline cel-shading and glowing aura particle effects.",
                isEnabled = true,
                category = "Auras & Effects",
                beforePreviewLabel = "Soft 3D Lighting",
                afterPreviewLabel = "Anime Shintani Cel-Shade",
                rating = "4.8 ★",
                downloadsCount = "31.7k",
                accentColorHex = 0xFF06B6D4,
                features = listOf("Ink Outlines", "Vibrant Ki Auras", "Super Saiyan Glow", "Anime Style")
            ),
            TexturePack(
                id = "tex_3",
                name = "Sparking! ZERO UI & Modern HUD",
                resolution = "1080p FHD",
                sizeMb = 85,
                author = "DBS_Studio",
                description = "Modernizes the in-game menus, health bars, and fighter portraits to match Dragon Ball: Sparking! ZERO.",
                isEnabled = false,
                category = "UI & HUD",
                beforePreviewLabel = "Classic 2010 HUD",
                afterPreviewLabel = "Sparking! ZERO Minimal HUD",
                rating = "4.9 ★",
                downloadsCount = "52.1k",
                accentColorHex = 0xFF3B82F6,
                features = listOf("Circular Health Bars", "3D Dynamic Icons", "Futuristic Ki Meter", "Clean Layout")
            ),
            TexturePack(
                id = "tex_4",
                name = "Toriyama Manga Shading Pack",
                resolution = "2K (1440p)",
                sizeMb = 160,
                author = "AkiraTribute",
                description = "Black and white screentone manga shading with vivid colored Ki auras, mimicking the original Dragon Ball manga pages.",
                isEnabled = false,
                category = "Characters",
                beforePreviewLabel = "Colored Anime Render",
                afterPreviewLabel = "Manga Screentone & Ink",
                rating = "4.7 ★",
                downloadsCount = "19.4k",
                accentColorHex = 0xFFA855F7,
                features = listOf("Original Crosshatching", "Monochrome Outlines", "Toriyama Color Accents", "Paper Texture")
            ),
            TexturePack(
                id = "tex_5",
                name = "Tournament of Power HD Arenas",
                resolution = "4K UHD (2160p)",
                sizeMb = 310,
                author = "StageMaster",
                description = "Photorealistic textures for ground debris, arena cracks, skybox nebula, and sacred pillars.",
                isEnabled = false,
                category = "Stages",
                beforePreviewLabel = "Low-poly Stage Textures",
                afterPreviewLabel = "PBR 4K Arena Ground & Sky",
                rating = "4.8 ★",
                downloadsCount = "24.6k",
                accentColorHex = 0xFF10B981,
                features = listOf("Destructible Rock Textures", "Cosmic Skyboxes", "PBR Specular Maps", "Zero Void Glow")
            )
        )
    )
    val texturePacks: StateFlow<List<TexturePack>> = _texturePacks.asStateFlow()

    // Mods state
    private val _selectedModCategory = MutableStateFlow(ModCategory.FILES)
    val selectedModCategory: StateFlow<ModCategory> = _selectedModCategory.asStateFlow()

    private val _modsList = MutableStateFlow<List<GameMod>>(
        listOf(
            GameMod(
                id = "mod_1",
                title = "DBS Super Hero Roster (Beast Gohan & Orange Piccolo)",
                category = ModCategory.FILES,
                version = "v2.4",
                sizeMb = 145,
                author = "Z-Warriors Team",
                description = "Adds Gohan Beast, Orange Piccolo, and Gamma 1 & Gamma 2 with custom movesets and voice lines.",
                isEnabled = true
            ),
            GameMod(
                id = "mod_2",
                title = "Ultra Instinct Goku & Ultra Ego Vegeta",
                category = ModCategory.FILES,
                version = "v3.1",
                sizeMb = 120,
                author = "TenkaichiMods",
                description = "Silver hair Ultra Instinct Goku with auto-dodge mechanics and Ultra Ego Vegeta with damage-boost ki aura.",
                isEnabled = false
            ),
            GameMod(
                id = "mod_3",
                title = "Tournament of Power & Beerus Planet Stages",
                category = ModCategory.STAGES,
                version = "v1.8",
                sizeMb = 88,
                author = "StageMaster",
                description = "Replaces Glacier and Mountain with the Null Realm fighting platform and Beerus's sacred realm.",
                isEnabled = true
            ),
            GameMod(
                id = "mod_4",
                title = "Latin American & Japanese OST (Cha-La, Dan Dan, Blizzard)",
                category = ModCategory.SONGS,
                version = "v1.0",
                sizeMb = 260,
                author = "DBZAudioOrg",
                description = "Full Spanish Latin & Japanese original anime soundtrack replacing default background music.",
                isEnabled = true
            )
        )
    )
    val modsList: StateFlow<List<GameMod>> = _modsList.asStateFlow()

    // 1. Full Game Extraction State
    private val _extractionProgress = MutableStateFlow(GameExtractionProgress())
    val extractionProgress: StateFlow<GameExtractionProgress> = _extractionProgress.asStateFlow()

    // 2. PCK 1 Characters Roster (pzs3us1.pck)
    private val _pckCharacters = MutableStateFlow<List<PckCharacterMod>>(
        listOf(
            PckCharacterMod("pck_goku_ui", "Goku Ultra Instinct Dominado (MUI)", "Goku SSJ3", "Kamehameha Supremo Plateado", "pzs3us1.pck (PCK 1)", isChecked = true),
            PckCharacterMod("pck_vegeta_ue", "Vegeta Ultra Ego / Mega Instinto", "Vegeta SSJ2", "Hakai Galick Gun", "pzs3us1.pck (PCK 1)", isChecked = true),
            PckCharacterMod("pck_gohan_beast", "Gohan Beast (Super Hero)", "Ultimate Gohan", "Makankosappo Demoníaco", "pzs3us1.pck (PCK 1)", isChecked = true),
            PckCharacterMod("pck_piccolo_orange", "Orange Piccolo Gigante", "Piccolo", "Ataque Granada de Furia", "pzs3us1.pck (PCK 1)", isChecked = true),
            PckCharacterMod("pck_broly_dbs", "Broly DBS Full Power (LSSJ)", "Broly SSJ Legendario", "Gigantic Roar / Blaster Meteor", "pzs3us1.pck (PCK 1)", isChecked = true),
            PckCharacterMod("pck_gogeta_blue", "Gogeta Super Saiyan Blue", "Gogeta SSJ", "God Punisher / Stardust Breaker", "pzs3us1.pck (PCK 1)", isChecked = true),
            PckCharacterMod("pck_vegetto_blue", "Vegetto Blue", "Vegetto SSJ", "Final Kamehameha Divino", "pzs3us1.pck (PCK 1)", isChecked = true),
            PckCharacterMod("pck_black_rose", "Goku Black Super Saiyan Rosé", "Goku SSJ2", "Espada de Energía Violenta", "pzs3us1.pck (PCK 1)", isChecked = true)
        )
    )
    val pckCharacters: StateFlow<List<PckCharacterMod>> = _pckCharacters.asStateFlow()

    // 3. Ki Colors & Auras Mods (Paletas de Color 1 a 4 con SFX POHD)
    private val _colorKiMods = MutableStateFlow<List<ColorKiMod>>(
        listOf(
            ColorKiMod("col_blue", "Color 1 (Normal): Ki Azul Divino (SSJ Blue)", 0xFF3B82F6, "Aura celeste con partículas de divinidad y ondas suaves", colorPaletteSlot = 1, sfxHdName = "SFX POHD Resonancia Estéreo 96kHz", sfxType = "SFX POHD", sfxStartFreq = 520.0, sfxEndFreq = 180.0, isChecked = true),
            ColorKiMod("col_gold", "Color 2 (Alternativo): Ki Dorado Super Saiyan", 0xFFF59E0B, "Aura flameante dorada con chispas de relámpago", colorPaletteSlot = 2, sfxHdName = "SFX Sparking! ZERO Impact Bass HD", sfxType = "SFX Sparking ZERO", sfxStartFreq = 880.0, sfxEndFreq = 140.0, isChecked = true),
            ColorKiMod("col_silver", "Color 3 (Manga): Ki Plateado / Blanco (Ultra Instinct)", 0xFFE2E8F0, "Resplandor plateado fluido con calor divino y dodge sfx", colorPaletteSlot = 3, sfxHdName = "SFX POHD Ultra Instinct Silver Resonance", sfxType = "SFX POHD", sfxStartFreq = 640.0, sfxEndFreq = 220.0, isChecked = true),
            ColorKiMod("col_purple", "Color 4 (Especial): Ki Morado Hakai Destrucción", 0xFFA855F7, "Aura oscura carmesí-morada de energía de la destrucción", colorPaletteSlot = 4, sfxHdName = "SFX POHD Hakai Obliteration Sub-Bass", sfxType = "SFX POHD", sfxStartFreq = 420.0, sfxEndFreq = 90.0, isChecked = true),
            ColorKiMod("col_rose", "Color 1 (Rosé): Ki Rosa Carmesí (SSJ Rosé)", 0xFFEC4899, "Aura púrpura oscura con brillo magenta rosado y espada de ki", colorPaletteSlot = 1, sfxHdName = "SFX POHD Blade Cleave 96kHz", sfxType = "SFX POHD", sfxStartFreq = 750.0, sfxEndFreq = 300.0, isChecked = true),
            ColorKiMod("col_green", "Color 2 (Berserk): Ki Verde Legendario (Broly)", 0xFF10B981, "Aura esmeralda gigante e inestable con rugido devastador", colorPaletteSlot = 2, sfxHdName = "SFX Movie Broly POHD Roar", sfxType = "SFX POHD", sfxStartFreq = 320.0, sfxEndFreq = 80.0, isChecked = true),
            ColorKiMod("col_red", "Color 3 (Kaio-ken): Ki Rojo Kaio-ken x20 / God", 0xFFEF4444, "Capa doble de aura rojiza intensa con zumbido agudo", colorPaletteSlot = 3, sfxHdName = "SFX Clásico Z Kikuchi Vintage", sfxType = "SFX Clásico Z", sfxStartFreq = 350.0, sfxEndFreq = 700.0, isChecked = false)
        )
    )
    val colorKiMods: StateFlow<List<ColorKiMod>> = _colorKiMods.asStateFlow()

    // 4. Audio & Voice Mods
    private val _audioVoiceMods = MutableStateFlow<List<AudioVoiceMod>>(
        listOf(
            AudioVoiceMod("aud_latino", "Audio Latino Oficial (México/LATAM)", "Mario Castañeda, René García, Gerardo Reyero", "¡Kamehameha! / ¡Insecto! / ¡Muere sabandija!", isChecked = true),
            AudioVoiceMod("aud_jap", "Audio Japonés Original Anime", "Masako Nozawa, Ryo Horikawa, Ryusei Nakao", "Osu! Ora Goku! / Kakarotto!", isChecked = true),
            AudioVoiceMod("aud_sfx_sz", "Efectos SFX Sparking! ZERO & Tenkaichi 3 HD", "Efectos sonoros remasterizados a 96kHz", "Impactos pesados, explosión de Ki y teletransporte", isChecked = true),
            AudioVoiceMod("aud_sfx_classic", "Efectos Sonoros Shunsuke Kikuchi (Anime 1989)", "Sintetizadores vintage de Dragon Ball Z", "Zumbido clásico de aura y silbato de ráfaga", isChecked = false)
        )
    )
    val audioVoiceMods: StateFlow<List<AudioVoiceMod>> = _audioVoiceMods.asStateFlow()

    // 5. Maps & Stages Mods
    private val _stageMapMods = MutableStateFlow<List<StageMapMod>>(
        listOf(
            StageMapMod("map_top", "Torneo del Poder (Mundo de la Nada)", "Plataforma de Kachi Katchin destruida con cielo cósmico", isChecked = true),
            StageMapMod("map_beerus", "Planeta Sagrado de Beerus y Whis", "Árbol sagrado gigante rodeado de lago místico", isChecked = true),
            StageMapMod("map_glacier_dbs", "Glaciar Ártico DBS Broly", "Hielo agrietado y lava volcánica en erupción", isChecked = true),
            StageMapMod("map_rosat", "Habitación del Tiempo y Espacio HD", "Vacío blanco infinito con campanas de oro pulido", isChecked = true),
            StageMapMod("map_budokai", "Torneo de las Artes Marciales Clásico", "Ring tradicional de baldosas con graderías llenas", isChecked = false)
        )
    )
    val stageMapMods: StateFlow<List<StageMapMod>> = _stageMapMods.asStateFlow()

    // 6. Custom Menus & UI Mods
    private val _menuUiMods = MutableStateFlow<List<MenuUiMod>>(
        listOf(
            MenuUiMod("menu_sz", "Menú Sparking! ZERO Minimalista", "Fuentes modernas, selector circular y fondos dinámicos", isChecked = true),
            MenuUiMod("menu_bt3", "Menú Budokai Tenkaichi 3 Clásico (PS2)", "Gráficos retro 2007 con fuentes amarillas en relieve", isChecked = false),
            MenuUiMod("menu_superhero", "Menú Dragon Ball Super: Super Hero", "Estilo de cómic con viñetas y arte de Gamma 1 & 2", isChecked = false)
        )
    )
    val menuUiMods: StateFlow<List<MenuUiMod>> = _menuUiMods.asStateFlow()

    // 7. Installed Songs Music Player
    private val _installedMusicTracks = MutableStateFlow<List<InstalledMusicTrack>>(
        listOf(
            InstalledMusicTrack(
                id = "song_chala",
                title = "Cha-La Head-Cha-La",
                singerAnime = "Ricardo Silva / Hironobu Kageyama (DBZ OP 1)",
                durationStr = "3:18",
                isPlaying = false,
                isCurrentBattleBgm = true,
                notesFrequencies = listOf(261.63, 293.66, 329.63, 392.00, 440.00, 523.25, 440.00, 392.00, 329.63, 293.66, 261.63)
            ),
            InstalledMusicTrack(
                id = "song_dandan",
                title = "Dan Dan Kokoro Hikareteku",
                singerAnime = "Aarón Montalvo / FIELD OF VIEW (DBGT OP)",
                durationStr = "3:34",
                isPlaying = false,
                isCurrentBattleBgm = false,
                notesFrequencies = listOf(329.63, 349.23, 392.00, 440.00, 493.88, 523.25, 493.88, 440.00, 392.00, 349.23)
            ),
            InstalledMusicTrack(
                id = "song_limitbreak",
                title = "Limit Break x Survivor",
                singerAnime = "Adrián Barba / Kiyoshi Hikawa (DBS Torneo del Poder)",
                durationStr = "3:48",
                isPlaying = false,
                isCurrentBattleBgm = false,
                notesFrequencies = listOf(392.00, 440.00, 493.88, 587.33, 659.25, 587.33, 493.88, 440.00, 392.00)
            ),
            InstalledMusicTrack(
                id = "song_solidstate",
                title = "Solid State Scouter",
                singerAnime = "Dragon Magic Orchestra (Tema de Bardock)",
                durationStr = "4:32",
                isPlaying = false,
                isCurrentBattleBgm = false,
                notesFrequencies = listOf(220.00, 246.94, 261.63, 293.66, 329.63, 293.66, 261.63, 246.94)
            ),
            InstalledMusicTrack(
                id = "song_blizzard",
                title = "Blizzard",
                singerAnime = "Daichi Miura (Tema Oficial Película DBS Broly)",
                durationStr = "5:12",
                isPlaying = false,
                isCurrentBattleBgm = false,
                notesFrequencies = listOf(293.66, 329.63, 369.99, 440.00, 493.88, 440.00, 369.99, 329.63)
            ),
            InstalledMusicTrack(
                id = "song_angeles",
                title = "Ángeles Fuimos (Boku-tachi wa Tenshi datta)",
                singerAnime = "Adrián Barba / Hironobu Kageyama (DBZ ED 2)",
                durationStr = "3:50",
                isPlaying = false,
                isCurrentBattleBgm = false,
                notesFrequencies = listOf(261.63, 329.63, 392.00, 523.25, 659.25, 523.25, 392.00, 329.63)
            )
        )
    )
    val installedMusicTracks: StateFlow<List<InstalledMusicTrack>> = _installedMusicTracks.asStateFlow()

    // 8. 3D Model & PCK 1 Visualizer State
    private val _available3DModels = MutableStateFlow<List<Model3DMesh>>(com.example.util.DbzModel3DGenerator.getDefaultModels())
    val available3DModels: StateFlow<List<Model3DMesh>> = _available3DModels.asStateFlow()

    private val _selected3DModel = MutableStateFlow<Model3DMesh>(_available3DModels.value.first())
    val selected3DModel: StateFlow<Model3DMesh> = _selected3DModel.asStateFlow()

    private val _selectedTextureRenderMode = MutableStateFlow(TextureRenderMode.CEL_SHADED_HD)
    val selectedTextureRenderMode: StateFlow<TextureRenderMode> = _selectedTextureRenderMode.asStateFlow()

    private val _selectedCostumeSlotIndex = MutableStateFlow(0)
    val selectedCostumeSlotIndex: StateFlow<Int> = _selectedCostumeSlotIndex.asStateFlow()

    // 9. Dragon Walker (Modo Historia) State & Casillas
    private val _storySagas = MutableStateFlow<List<StorySaga>>(com.example.util.DbzStoryModeData.getAllSagas())
    val storySagas: StateFlow<List<StorySaga>> = _storySagas.asStateFlow()

    private val _isStoryModeOpen = MutableStateFlow(false)
    val isStoryModeOpen: StateFlow<Boolean> = _isStoryModeOpen.asStateFlow()

    private val _isNodeEditorOpen = MutableStateFlow(false)
    val isNodeEditorOpen: StateFlow<Boolean> = _isNodeEditorOpen.asStateFlow()

    // 9b. Mini Mapa Config State
    private val _miniMapConfig = MutableStateFlow(MiniMapConfig())
    val miniMapConfig: StateFlow<MiniMapConfig> = _miniMapConfig.asStateFlow()

    // 10. TTT Character & Color Editor (MainPage / ViewPj - BBQ Modding Tool)
    private val _bbqSlots = MutableStateFlow<List<TttBbqCharacterSlot>>(
        listOf(
            TttBbqCharacterSlot(1, "00013276", "Saibaiman 1", 1, "Saibaiman 1 (Verde Base)", "Saibaiman 1", 0xFF22C55E),
            TttBbqCharacterSlot(2, "00013277", "Saibaiman 3", 4, "Saibaiman 3 (4 Color)", "Saibaiman 3", 0xFF8B5CF6),
            TttBbqCharacterSlot(3, "00013278", "Saibaiman 3", 3, "Saibaiman 3 (3 Color)", "Saibaiman 3", 0xFFF59E0B),
            TttBbqCharacterSlot(4, "00013279", "Saibaiman 1", 1, "Saibaiman 1 (1 Color)", "Saibaiman 1", 0xFF10B981),
            TttBbqCharacterSlot(5, "00013280", "Goku SSJ3", 1, "Goku Super Saiyan 3 (Color 1 Naranja)", "Goku SSJ3", 0xFFEAB308),
            TttBbqCharacterSlot(6, "00013281", "Goku SSJ3", 2, "Goku Super Saiyan 3 (Color 2 Azul)", "Goku SSJ3", 0xFF3B82F6),
            TttBbqCharacterSlot(7, "00013282", "Goku SSJ3", 3, "Goku Super Saiyan 3 (Color 3 Rojo)", "Goku SSJ3", 0xFFEF4444),
            TttBbqCharacterSlot(8, "00013283", "Goku SSJ3", 4, "Goku Super Saiyan 3 (Color 4 Negro)", "Goku SSJ3", 0xFF374151),
            TttBbqCharacterSlot(9, "00013284", "Vegeta Majin", 1, "Vegeta Majin (Color 1 Azul Clásico)", "Vegeta Majin", 0xFF6366F1),
            TttBbqCharacterSlot(10, "00013285", "Vegeta Majin", 2, "Vegeta Majin (Color 2 Gris Anime)", "Vegeta Majin", 0xFF64748B)
        )
    )
    val bbqSlots: StateFlow<List<TttBbqCharacterSlot>> = _bbqSlots.asStateFlow()

    private val _bbqFileState = MutableStateFlow(TttBbqFileState())
    val bbqFileState: StateFlow<TttBbqFileState> = _bbqFileState.asStateFlow()

    private val _selectedTttEditorSubTab = MutableStateFlow(TttEditorSubTab.MAIN_PAGE)
    val selectedTttEditorSubTab: StateFlow<TttEditorSubTab> = _selectedTttEditorSubTab.asStateFlow()


    // Controls state
    private val _controlLayout = MutableStateFlow(ControlLayoutType.GAME_SPECIFIC)
    val controlLayout: StateFlow<ControlLayoutType> = _controlLayout.asStateFlow()

    private val _autoMenuFight = MutableStateFlow(true)
    val autoMenuFight: StateFlow<Boolean> = _autoMenuFight.asStateFlow()

    private val _controlOpacity = MutableStateFlow(0.75f)
    val controlOpacity: StateFlow<Float> = _controlOpacity.asStateFlow()

    private val _controlScale = MutableStateFlow(1.0f)
    val controlScale: StateFlow<Float> = _controlScale.asStateFlow()

    private val _hapticFeedback = MutableStateFlow(true)
    val hapticFeedback: StateFlow<Boolean> = _hapticFeedback.asStateFlow()

    // Cheats state
    private val _unlockEverything = MutableStateFlow(false)
    val unlockEverything: StateFlow<Boolean> = _unlockEverything.asStateFlow()

    private val _cheatsList = MutableStateFlow<List<CwCheat>>(
        listOf(
            CwCheat(
                id = "c1",
                name = "Infinite Health (P1 & Partner)",
                description = "Player tag team members never lose HP in battle.",
                codeHex = "_C1 Infinite Health Team 1\n_L 0x203A45C0 0x000003E8",
                isEnabled = false
            ),
            CwCheat(
                id = "c2",
                name = "Infinite Ki Energy",
                description = "Ki gauge stays permanently maxed at 100%.",
                codeHex = "_C1 Max Ki Energy\n_L 0x203A45C8 0x00000064",
                isEnabled = false
            ),
            CwCheat(
                id = "c3",
                name = "Instant Tag Cooldown",
                description = "Switch between Goku & Vegeta immediately with zero waiting delay.",
                codeHex = "_C1 Instant Tag\n_L 0x203A45E0 0x00000000",
                isEnabled = false
            ),
            CwCheat(
                id = "c4",
                name = "Permanent Sparking! Mode",
                description = "Sparking aura never depletes; unlimited Super Kamehamehas.",
                codeHex = "_C1 Perm Sparking\n_L 0x203A45EC 0x000000FF",
                isEnabled = false
            ),
            CwCheat(
                id = "c5",
                name = "One Hit K.O. Opponents",
                description = "Defeat CPU opponents in a single strike.",
                codeHex = "_C1 1-Hit Kill Enemy\n_L 0x203B2310 0x00000001",
                isEnabled = false
            ),
            CwCheat(
                id = "c6",
                name = "Max D-Points / Zeni (9,999,999)",
                description = "Purchase all Potara items and custom capsules in the shop.",
                codeHex = "_C1 Max Zeni\n_L 0x2031B900 0x0098967F",
                isEnabled = true
            )
        )
    )
    val cheatsList: StateFlow<List<CwCheat>> = _cheatsList.asStateFlow()

    // Settings state
    private val _internalResolution = MutableStateFlow(1.25f)
    val internalResolution: StateFlow<Float> = _internalResolution.asStateFlow()

    private val _screenFilter = MutableStateFlow(ScreenFilterType.BILINEAR)
    val screenFilter: StateFlow<ScreenFilterType> = _screenFilter.asStateFlow()

    private val _aspectRatio = MutableStateFlow(AspectRatioType.SCREEN)
    val aspectRatio: StateFlow<AspectRatioType> = _aspectRatio.asStateFlow()

    private val _showFps = MutableStateFlow(true)
    val showFps: StateFlow<Boolean> = _showFps.asStateFlow()

    private val _fps60Patch = MutableStateFlow(true)
    val fps60Patch: StateFlow<Boolean> = _fps60Patch.asStateFlow()

    private val _soundVolume = MutableStateFlow(0.85f)
    val soundVolume: StateFlow<Float> = _soundVolume.asStateFlow()

    private val _bgmVolume = MutableStateFlow(0.70f)
    val bgmVolume: StateFlow<Float> = _bgmVolume.asStateFlow()

    private val _fastMemory = MutableStateFlow(true)
    val fastMemory: StateFlow<Boolean> = _fastMemory.asStateFlow()

    private val _multithreading = MutableStateFlow(true)
    val multithreading: StateFlow<Boolean> = _multithreading.asStateFlow()

    private val _language = MutableStateFlow("Español Latino")
    val language: StateFlow<String> = _language.asStateFlow()

    // Battle Simulator State
    private val _battleState = MutableStateFlow(BattleGameState())
    val battleState: StateFlow<BattleGameState> = _battleState.asStateFlow()

    private var battleJob: Job? = null

    // Navigation and tab switching
    fun selectTab(tab: NavTab) {
        _selectedTab.value = tab
        audioHaptics.playHaptic(20, 100)
    }

    // Toggle Texture Packs
    fun setUseTexturePacks(value: Boolean) {
        _useTexturePacks.value = value
        audioHaptics.playHaptic(25, 120)
    }

    fun toggleTexturePack(id: String) {
        _texturePacks.update { list ->
            list.map { if (it.id == id) it.copy(isEnabled = !it.isEnabled) else it }
        }
        audioHaptics.playHaptic(25, 120)
    }

    // Texture Packs Gallery filtering & actions
    fun setTextureCategoryFilter(category: String) {
        _textureCategoryFilter.value = category
        audioHaptics.playHaptic(20, 100)
    }

    fun setAllTexturePacks(enabled: Boolean) {
        _texturePacks.update { list ->
            list.map { it.copy(isEnabled = enabled) }
        }
        audioHaptics.playHaptic(35, 160)
    }

    // ROM Management (.iso and .cso)
    fun importRomFile(fileName: String, sizeBytes: Long, filePath: String) {
        val isCso = fileName.endsWith(".cso", ignoreCase = true)
        val format = if (isCso) RomFormat.CSO else RomFormat.ISO
        val sizeMb = (sizeBytes / (1024 * 1024)).coerceAtLeast(1)

        val cleanTitle = fileName
            .removeSuffix(".iso").removeSuffix(".ISO")
            .removeSuffix(".cso").removeSuffix(".CSO")
            .replace("_", " ").replace("-", " ")
            .trim()
            .ifBlank { "DBZ Tenkaichi Tag Team Custom ROM" }

        val newRom = RomGameFile(
            id = "rom_${System.currentTimeMillis()}",
            title = cleanTitle,
            fileName = fileName,
            fileSizeMb = sizeMb,
            filePath = filePath,
            format = format,
            region = if (fileName.contains("eur", true)) "EUR" else "USA",
            gameId = if (fileName.contains("eur", true)) "ULES01488" else "ULUS10537",
            isVerified = true,
            isSelected = true,
            lastPlayed = "Imported just now"
        )

        // Mark previously selected as unselected
        _romFilesList.update { list ->
            val updated = list.map { it.copy(isSelected = false) }
            listOf(newRom) + updated
        }
        _gameDataFiles.value = "Custom: $fileName (${sizeMb} MB)"
        audioHaptics.playSuperBeamSound()
        audioHaptics.playHaptic(60, 220)
    }

    fun selectActiveRom(id: String) {
        _romFilesList.update { list ->
            list.map {
                if (it.id == id) {
                    _gameDataFiles.value = "${it.fileName} (${it.fileSizeMb} MB)"
                    it.copy(isSelected = true, lastPlayed = "Active")
                } else {
                    it.copy(isSelected = false)
                }
            }
        }
        audioHaptics.playHaptic(30, 150)
    }

    fun deleteRomFile(id: String) {
        _romFilesList.update { list ->
            val remaining = list.filter { it.id != id }
            if (remaining.isNotEmpty() && remaining.none { it.isSelected }) {
                remaining.first().let { first ->
                    _gameDataFiles.value = "${first.fileName} (${first.fileSizeMb} MB)"
                }
                remaining.mapIndexed { idx, rom -> if (idx == 0) rom.copy(isSelected = true) else rom }
            } else {
                remaining
            }
        }
        audioHaptics.playHaptic(40, 180)
    }

    // ==========================================
    // EXPANSIÓN DE ISO A 2 GB O 4 GB Y CASILLAS DE MODS
    // ==========================================
    fun expandIsoImage(romId: String, targetGb: Double) { // targetGb: 2.0 o 4.0
        val targetMb = (targetGb * 1024).toLong()
        val targetMaxSlots = if (targetGb >= 4.0) 128 else 64

        _romFilesList.update { list ->
            list.map { rom ->
                if (rom.id == romId) {
                    rom.copy(
                        isExpanded = true,
                        expandedCapacityGb = targetGb,
                        fileSizeMb = targetMb,
                        maxModSlots = targetMaxSlots
                    )
                } else rom
            }
        }
        audioHaptics.playSuperBeamSound()
        audioHaptics.playHaptic(70, 250)
    }

    fun installModIntoIsoSlot(romId: String, modName: String) {
        _romFilesList.update { list ->
            list.map { rom ->
                if (rom.id == romId) {
                    val currentMods = rom.installedModNames
                    if (!currentMods.contains(modName)) {
                        rom.copy(
                            allocatedModSlots = rom.allocatedModSlots + 1,
                            installedModNames = currentMods + modName
                        )
                    } else rom
                } else rom
            }
        }
        audioHaptics.playTagSwitchSound()
        audioHaptics.playHaptic(40, 160)
    }

    fun removeModFromIsoSlot(romId: String, modName: String) {
        _romFilesList.update { list ->
            list.map { rom ->
                if (rom.id == romId) {
                    rom.copy(
                        allocatedModSlots = (rom.allocatedModSlots - 1).coerceAtLeast(0),
                        installedModNames = rom.installedModNames.filter { it != modName }
                    )
                } else rom
            }
        }
        audioHaptics.playHaptic(20, 100)
    }

    // Save State Management (Savestates Room DB)
    fun createSaveState(slotNumber: Int, title: String) {
        val activeRom = _romFilesList.value.firstOrNull { it.isSelected } ?: _romFilesList.value.first()
        val currentBattle = _battleState.value
        val p1 = currentBattle.p1Team.getOrNull(currentBattle.p1ActiveIndex) ?: currentBattle.p1Team[0]
        val cpu = currentBattle.cpuTeam.getOrNull(currentBattle.cpuActiveIndex) ?: currentBattle.cpuTeam[0]

        val newState = SaveStateEntity(
            slotNumber = slotNumber,
            romFileName = activeRom.fileName,
            gameTitle = activeRom.title,
            title = title.ifBlank { "Slot $slotNumber: ${p1.name} vs ${cpu.name}" },
            timestamp = System.currentTimeMillis(),
            statePath = "/storage/emulated/0/PSP/PPSSPP_STATE/${activeRom.gameId}_$slotNumber.ppst",
            p1FighterName = "${p1.name} (${p1.title})",
            p1Hp = p1.currentHp,
            p1Ki = p1.currentKi,
            cpuFighterName = "${cpu.name} (${cpu.title})",
            cpuHp = cpu.currentHp,
            battleArenaStage = "Wasteland Arena",
            isAutoSave = slotNumber == 0,
            notes = "HP: ${p1.currentHp.toInt()} | Ki: ${p1.currentKi.toInt()}%"
        )

        viewModelScope.launch {
            saveStateRepository.saveState(newState)
        }
        audioHaptics.playSuperBeamSound()
        audioHaptics.playHaptic(50, 200)
    }

    fun loadSaveState(saveState: SaveStateEntity) {
        // Apply saved fighters state into battle
        _battleState.update { current ->
            val p1Team = current.p1Team.mapIndexed { idx, fighter ->
                if (idx == current.p1ActiveIndex) {
                    fighter.copy(currentHp = saveState.p1Hp, currentKi = saveState.p1Ki)
                } else fighter
            }
            val cpuTeam = current.cpuTeam.mapIndexed { idx, fighter ->
                if (idx == current.cpuActiveIndex) {
                    fighter.copy(currentHp = saveState.cpuHp)
                } else fighter
            }
            current.copy(
                isPlaying = true,
                isPaused = false,
                p1Team = p1Team,
                cpuTeam = cpuTeam,
                lastCombatLog = "LOADED STATE: ${saveState.title} (Slot ${saveState.slotNumber})"
            )
        }
        audioHaptics.playTagSwitchSound()
        audioHaptics.playHaptic(60, 220)
    }

    fun deleteSaveState(id: Long) {
        viewModelScope.launch {
            saveStateRepository.deleteSaveStateById(id)
        }
        audioHaptics.playHaptic(30, 140)
    }

    // Toggle Color Ki Mod Checkbox
    fun toggleColorKiMod(id: String) {
        _colorKiMods.update { list ->
            list.map { if (it.id == id) it.copy(isChecked = !it.isChecked) else it }
        }
        audioHaptics.playHaptic(20, 110)
    }

    // Toggle Audio Voice Mod Checkbox
    fun toggleAudioVoiceMod(id: String) {
        _audioVoiceMods.update { list ->
            list.map { if (it.id == id) it.copy(isChecked = !it.isChecked) else it }
        }
        audioHaptics.playHaptic(20, 110)
    }

    // Toggle PCK 1 Character Checkbox
    fun togglePckCharacter(id: String) {
        _pckCharacters.update { list ->
            list.map { if (it.id == id) it.copy(isChecked = !it.isChecked) else it }
        }
        audioHaptics.playHaptic(20, 110)
    }

    // Toggle Map/Stage Checkbox
    fun toggleStageMapMod(id: String) {
        _stageMapMods.update { list ->
            list.map { if (it.id == id) it.copy(isChecked = !it.isChecked) else it }
        }
        audioHaptics.playHaptic(20, 110)
    }

    // Toggle Menu UI Mod Checkbox
    fun toggleMenuUiMod(id: String) {
        _menuUiMods.update { list ->
            list.map { if (it.id == id) it.copy(isChecked = !it.isChecked) else it }
        }
        audioHaptics.playHaptic(20, 110)
    }

    // Reproducir música instalada
    fun playMusicTrack(trackId: String) {
        val track = _installedMusicTracks.value.firstOrNull { it.id == trackId } ?: return

        if (track.isPlaying) {
            stopMusicTrack()
            return
        }

        audioHaptics.playHaptic(30, 150)
        _installedMusicTracks.update { list ->
            list.map { it.copy(isPlaying = it.id == trackId) }
        }

        audioHaptics.playSongMelody(track.notesFrequencies, tempoMs = 230) {
            _installedMusicTracks.update { list ->
                list.map { if (it.id == trackId) it.copy(isPlaying = false) else it }
            }
        }
    }

    fun stopMusicTrack() {
        audioHaptics.stopSongMelody()
        _installedMusicTracks.update { list ->
            list.map { it.copy(isPlaying = false) }
        }
        audioHaptics.playHaptic(20, 100)
    }

    fun setAsBattleBgm(trackId: String) {
        _installedMusicTracks.update { list ->
            list.map { it.copy(isCurrentBattleBgm = it.id == trackId) }
        }
        audioHaptics.playTagSwitchSound()
        audioHaptics.playHaptic(40, 180)
    }

    // Extraer todo el juego (Full Dump ISO/CSO con progreso)
    fun extractAllGameData() {
        if (_extractionProgress.value.isExtracting) return
        audioHaptics.playSuperBeamSound()
        audioHaptics.playHaptic(60, 220)

        viewModelScope.launch {
            val filesToExtract = listOf(
                "ULES01488/USRDIR/pzs3us0.pck (Sistema y Menús)",
                "ULES01488/USRDIR/pzs3us1.pck (Roster de 70+ Personajes PCK 1)",
                "ULES01488/USRDIR/pzs3us2.pck (Animaciones y Super Ataques)",
                "ULES01488/USRDIR/disc.pck (Escenarios y Mapas HD)",
                "ULES01488/USRDIR/sound/audio_latino.afs (Voces Mario Castañeda / René García)",
                "ULES01488/USRDIR/sound/bgm_stream.adx (Banda Sonora Cha-La & Dan Dan)",
                "ULES01488/USRDIR/textures/cel_shading.dds (Texturas Remasterizadas)",
                "ULES01488/PARAM.SFO (Descriptor del Sistema PSP)",
                "ULES01488/ICON0.PNG & PIC1.PNG (Arte Oficial de Portada)"
            )

            _extractionProgress.value = GameExtractionProgress(
                isExtracting = true,
                progress = 0.05f,
                currentFileName = "Iniciando descompresión ISO/CSO...",
                isCompleted = false
            )

            for ((index, file) in filesToExtract.withIndex()) {
                delay(180)
                val currentProgress = (index + 1).toFloat() / filesToExtract.size
                _extractionProgress.value = GameExtractionProgress(
                    isExtracting = true,
                    progress = currentProgress,
                    currentFileName = "Extrayendo $file...",
                    isCompleted = false
                )
            }

            val defaultExtractedList = listOf(
                ExtractedGameFile("PSP_GAME/SYSDIR/EBOOT.BIN", "EBOOT.BIN", 18.4, "BIN", "Ejecutable nativo PSP MIPS"),
                ExtractedGameFile("PSP_GAME/USRDIR/pzs3us0.pck", "pzs3us0.pck", 180.0, "PCK", "Sistema, Fuentes y Menús XMB"),
                ExtractedGameFile("PSP_GAME/USRDIR/pzs3us1.pck", "pzs3us1.pck", 1240.0, "PCK", "PCK 1: Roster 70+ Personajes [MODS ACTIVOS]", isModded = true),
                ExtractedGameFile("PSP_GAME/USRDIR/pzs3us2.pck", "pzs3us2.pck", 850.0, "PCK", "Animaciones, Super Ataques y Ki"),
                ExtractedGameFile("PSP_GAME/USRDIR/disc.pck", "disc.pck", 620.0, "PCK", "Arenas y Escenarios 3D HD"),
                ExtractedGameFile("PSP_GAME/USRDIR/sound/audio_latino.afs", "audio_latino.afs", 450.0, "AFS", "Voces Latino Mario Castañeda / René García"),
                ExtractedGameFile("PSP_GAME/USRDIR/sound/bgm_stream.adx", "bgm_stream.adx", 380.0, "ADX", "Música Original Cha-La / Dan Dan"),
                ExtractedGameFile("PSP_GAME/USRDIR/sound/sfx_pohd.snd", "sfx_pohd.snd", 110.0, "SND", "Librería SFX POHD 96kHz"),
                ExtractedGameFile("PSP_GAME/USRDIR/textures/cel_shading_hd.dds", "cel_shading_hd.dds", 210.0, "DDS", "Texturas 4K Remasterizadas"),
                ExtractedGameFile("PSP_GAME/USRDIR/movie/op_intro.pmf", "op_intro.pmf", 145.0, "PMF", "Video Cinemática Intro 60FPS"),
                ExtractedGameFile("PSP_GAME/PARAM.SFO", "PARAM.SFO", 0.2, "SFO", "Descriptor de Región PSP ULUS10537"),
                ExtractedGameFile("PSP_GAME/ICON0.PNG", "ICON0.PNG", 0.5, "PNG", "Icono Oficial Menú XMB"),
                ExtractedGameFile("PSP_GAME/PIC1.PNG", "PIC1.PNG", 1.8, "PNG", "Fondo Oficial de Pantalla")
            )

            delay(150)
            _extractionProgress.value = GameExtractionProgress(
                isExtracting = false,
                progress = 1.0f,
                currentFileName = "¡Extracción completa exitosa! 13 archivos / 4.0 GB",
                isCompleted = true,
                extractedSizeStr = "4.0 GB",
                extractedFilesCount = 13,
                extractedFiles = defaultExtractedList
            )
            _isGameDataVerified.value = true
            _gameDataFiles.value = "Extraído verificado · 13 archivos · 4.0 GB"
            audioHaptics.playSuperBeamSound()
            audioHaptics.playHaptic(80, 240)
        }
    }

    // Repack extracted files into modded ISO/CSO
    fun repackGameToIso() {
        audioHaptics.playSuperBeamSound()
        audioHaptics.playHaptic(70, 230)
        _gameDataFiles.value = "dbz_tag_team_modded_v3.iso (4.0 GB)"
    }

    fun injectModIntoExtractedFile(path: String) {
        audioHaptics.playTagSwitchSound()
        audioHaptics.playHaptic(40, 180)
    }

    // ==========================================
    // 3D MODEL & PCK 1 VIEWER METHODS
    // ==========================================
    fun select3DModel(modelId: String) {
        val model = _available3DModels.value.firstOrNull { it.id == modelId } ?: return
        _selected3DModel.value = model
        audioHaptics.playHaptic(25, 120)
    }

    fun setTextureRenderMode(mode: TextureRenderMode) {
        _selectedTextureRenderMode.value = mode
        audioHaptics.playHaptic(20, 110)
    }

    fun setCostumeSlotIndex(index: Int) {
        _selectedCostumeSlotIndex.value = index.coerceIn(0, 3)
        audioHaptics.playHaptic(20, 110)
    }

    fun injectTextureIntoPck1(modelId: String, textureMode: TextureRenderMode) {
        audioHaptics.playSuperBeamSound()
        audioHaptics.playHaptic(60, 220)
    }

    fun loadCustomObjFile(fileName: String, content: String) {
        val parsedModel = com.example.util.DbzModel3DGenerator.parseObjFile(fileName, content)
        _available3DModels.update { listOf(parsedModel) + it }
        _selected3DModel.value = parsedModel
        audioHaptics.playSuperBeamSound()
        audioHaptics.playHaptic(50, 200)
    }

    // ==========================================
    // DRAGON WALKER (MODO HISTORIA) METHODS
    // ==========================================
    fun openStoryMode() {
        _isStoryModeOpen.value = true
        audioHaptics.playTagSwitchSound()
        audioHaptics.playHaptic(30, 150)
    }

    fun closeStoryMode() {
        _isStoryModeOpen.value = false
        audioHaptics.playHaptic(20, 100)
    }

    fun startStoryMissionInBattle(mission: StoryMission) {
        _isStoryModeOpen.value = false
        val p1Team = if (mission.p2Fighter != null) {
            listOf(mission.p1Fighter, mission.p2Fighter)
        } else {
            listOf(mission.p1Fighter, Fighter("partner_support", "Krillin", "Soporte Z", 800f, 800f, 100f, 40f, "Kienzan", 50f, 0xFFF59E0B))
        }
        val cpuTeam = if (mission.cpu2Fighter != null) {
            listOf(mission.cpu1Fighter, mission.cpu2Fighter)
        } else {
            listOf(mission.cpu1Fighter, Fighter("saibaiman", "Saibaiman", "Soporte", 600f, 600f, 100f, 30f, "Autodestrucción", 40f, 0xFF10B981))
        }

        _battleState.update {
            it.copy(
                isPlaying = true,
                isPaused = false,
                p1ActiveIndex = 0,
                p1Team = p1Team,
                cpuActiveIndex = 0,
                cpuTeam = cpuTeam,
                lastCombatLog = "HISTORIA: ${mission.title} en ${mission.stageName}!"
            )
        }
        audioHaptics.playSuperBeamSound()
        audioHaptics.playHaptic(70, 240)
    }

    // ==========================================
    // EDITOR DE NODOS DEL MODO HISTORIA & CASILLAS DE SAGAS
    // ==========================================
    fun setNodeEditorOpen(open: Boolean) {
        _isNodeEditorOpen.value = open
        audioHaptics.playTagSwitchSound()
        audioHaptics.playHaptic(25, 120)
    }

    fun updateMissionNode(sagaId: String, updatedMission: StoryMission) {
        _storySagas.update { sagas ->
            sagas.map { saga ->
                if (saga.id == sagaId) {
                    val updatedMissions = saga.missions.map {
                        if (it.id == updatedMission.id) updatedMission else it
                    }
                    saga.copy(missions = updatedMissions)
                } else saga
            }
        }
        audioHaptics.playSuperBeamSound()
        audioHaptics.playHaptic(30, 150)
    }

    fun addMissionNode(sagaId: String, mission: StoryMission) {
        _storySagas.update { sagas ->
            sagas.map { saga ->
                if (saga.id == sagaId) {
                    saga.copy(missions = saga.missions + mission)
                } else saga
            }
        }
        audioHaptics.playSuperBeamSound()
        audioHaptics.playHaptic(35, 180)
    }

    fun deleteMissionNode(sagaId: String, missionId: String) {
        _storySagas.update { sagas ->
            sagas.map { saga ->
                if (saga.id == sagaId) {
                    saga.copy(missions = saga.missions.filter { it.id != missionId })
                } else saga
            }
        }
        audioHaptics.playHaptic(30, 150)
    }

    fun connectMissionNodes(sagaId: String, sourceNodeId: String, targetNodeId: String) {
        _storySagas.update { sagas ->
            sagas.map { saga ->
                if (saga.id == sagaId) {
                    val updated = saga.missions.map { mission ->
                        if (mission.id == sourceNodeId) {
                            if (!mission.nextNodeIds.contains(targetNodeId)) {
                                mission.copy(nextNodeIds = mission.nextNodeIds + targetNodeId)
                            } else mission
                        } else mission
                    }
                    saga.copy(missions = updated)
                } else saga
            }
        }
        audioHaptics.playTagSwitchSound()
        audioHaptics.playHaptic(30, 150)
    }

    fun addStorySaga(saga: StorySaga) {
        _storySagas.update { it + saga }
        audioHaptics.playSuperBeamSound()
        audioHaptics.playHaptic(40, 190)
    }

    // ==========================================
    // MINI MAPA (RADAR HUD) CONTROLS
    // ==========================================
    fun setMiniMapStyle(style: MiniMapStyle) {
        _miniMapConfig.update { it.copy(style = style) }
        audioHaptics.playTagSwitchSound()
        audioHaptics.playHaptic(25, 120)
    }

    fun toggleMiniMap(enabled: Boolean) {
        _miniMapConfig.update { it.copy(isEnabled = enabled) }
        audioHaptics.playHaptic(20, 100)
    }

    fun setMiniMapZoom(zoom: Float) {
        _miniMapConfig.update { it.copy(zoomLevel = zoom) }
        audioHaptics.playHaptic(15, 80)
    }

    fun setMiniMapSize(sizeDp: Int) {
        _miniMapConfig.update { it.copy(sizeDp = sizeDp) }
        audioHaptics.playHaptic(15, 80)
    }

    // ==========================================
    // CAMBIO DE OBJ & PCK 1 SLOTS
    // ==========================================
    fun swapModelPckSlot(modelId: String, newSlot: String) {
        _available3DModels.update { list ->
            list.map { if (it.id == modelId) it.copy(pckSlot = newSlot) else it }
        }
        _selected3DModel.update { if (it.id == modelId) it.copy(pckSlot = newSlot) else it }
        audioHaptics.playSuperBeamSound()
        audioHaptics.playHaptic(30, 150)
    }

    // ==========================================
    // CAMBIO DE MÚSICA & REPRODUCTOR
    // ==========================================
    fun nextMusicTrack() {
        val tracks = _installedMusicTracks.value
        if (tracks.isEmpty()) return
        val currentIndex = tracks.indexOfFirst { it.isPlaying }
        val nextIndex = if (currentIndex in 0 until tracks.lastIndex) currentIndex + 1 else 0
        playMusicTrack(tracks[nextIndex].id)
    }

    fun previousMusicTrack() {
        val tracks = _installedMusicTracks.value
        if (tracks.isEmpty()) return
        val currentIndex = tracks.indexOfFirst { it.isPlaying }
        val prevIndex = if (currentIndex > 0) currentIndex - 1 else tracks.lastIndex
        playMusicTrack(tracks[prevIndex].id)
    }

    fun addCustomMusicTrack(title: String, singerAnime: String, durationStr: String) {
        val newTrack = InstalledMusicTrack(
            id = "song_${System.currentTimeMillis()}",
            title = title,
            singerAnime = singerAnime,
            durationStr = durationStr,
            isPlaying = false,
            isCurrentBattleBgm = false,
            notesFrequencies = listOf(329.63, 392.00, 440.00, 523.25, 587.33, 523.25, 440.00, 392.00)
        )
        _installedMusicTracks.update { listOf(newTrack) + it }
        audioHaptics.playSuperBeamSound()
        audioHaptics.playHaptic(40, 180)
    }

    // ==========================================
    // MODS MANAGEMENT WITH CUSTOM NAMES
    // ==========================================
    fun addPckCharacter(
        name: String,
        replacesOriginal: String,
        specialMove: String,
        pckSlot: String = "pzs3us1.pck (PCK 1)",
        colorPalette: String = "Color 1 (Normal)",
        assignedAudio: String = "SFX POHD + Voces Latino"
    ) {
        val newChar = PckCharacterMod(
            id = "pck_${System.currentTimeMillis()}",
            name = name,
            replacesOriginal = replacesOriginal,
            specialMove = specialMove,
            pckSlot = pckSlot,
            colorPalette = colorPalette,
            assignedAudio = assignedAudio,
            isChecked = true,
            isInstalled = true
        )
        _pckCharacters.update { listOf(newChar) + it }
        audioHaptics.playSuperBeamSound()
        audioHaptics.playHaptic(40, 180)
    }

    fun renamePckCharacter(id: String, newName: String) {
        _pckCharacters.update { list ->
            list.map { if (it.id == id) it.copy(name = newName) else it }
        }
        audioHaptics.playHaptic(25, 120)
    }

    fun addColorKiMod(
        name: String,
        colorHex: Long,
        auraDescription: String,
        colorPaletteSlot: Int,
        sfxHdName: String = "SFX POHD Resonancia Estéreo 96kHz",
        sfxType: String = "SFX POHD"
    ) {
        val newColor = ColorKiMod(
            id = "col_${System.currentTimeMillis()}",
            name = name,
            colorHex = colorHex,
            auraDescription = auraDescription,
            colorPaletteSlot = colorPaletteSlot,
            sfxHdName = sfxHdName,
            sfxType = sfxType,
            isChecked = true,
            isInstalled = true
        )
        _colorKiMods.update { listOf(newColor) + it }
        audioHaptics.playTagSwitchSound()
        audioHaptics.playHaptic(35, 160)
    }

    fun toggleColorSfx(colorId: String) {
        _colorKiMods.update { list ->
            list.map { if (it.id == colorId) it.copy(isSfxEnabled = !it.isSfxEnabled) else it }
        }
        audioHaptics.playHaptic(20, 110)
    }

    fun playColorPohdSfx(colorId: String) {
        val mod = _colorKiMods.value.firstOrNull { it.id == colorId } ?: _colorKiMods.value.first()
        audioHaptics.playPohdSfx(
            startFreq = mod.sfxStartFreq,
            endFreq = mod.sfxEndFreq,
            durationMs = mod.sfxDurationMs,
            sfxType = mod.sfxType
        )
    }

    fun addAudioVoiceMod(
        name: String,
        actors: String,
        sampleText: String,
        audioFormat: String = "POHD 96kHz / 24-bit",
        sampleFreq: Double = 440.0
    ) {
        val newAudio = AudioVoiceMod(
            id = "aud_${System.currentTimeMillis()}",
            name = name,
            actors = actors,
            sampleText = sampleText,
            audioFormat = audioFormat,
            sampleFreq = sampleFreq,
            isChecked = true,
            isInstalled = true
        )
        _audioVoiceMods.update { listOf(newAudio) + it }
        audioHaptics.playTagSwitchSound()
        audioHaptics.playHaptic(35, 160)
    }

    fun playVoiceSample(audioModId: String) {
        val mod = _audioVoiceMods.value.firstOrNull { it.id == audioModId }
        val freq = mod?.sampleFreq ?: 440.0
        audioHaptics.playAudioVoiceSample(sampleFreq = freq)
    }

    fun addStageMapMod(name: String, atmosphere: String, originalReplaced: String = "Llanura Rocosa / Wasteland") {
        val newMap = StageMapMod(
            id = "map_${System.currentTimeMillis()}",
            name = name,
            atmosphere = atmosphere,
            originalReplaced = originalReplaced,
            isChecked = true,
            isInstalled = true
        )
        _stageMapMods.update { listOf(newMap) + it }
        audioHaptics.playHaptic(35, 160)
    }

    fun addMenuUiMod(name: String, themeStyle: String, hudStyle: String = "Barras HP/Ki Sparking ZERO") {
        val newMenu = MenuUiMod(
            id = "menu_${System.currentTimeMillis()}",
            name = name,
            themeStyle = themeStyle,
            hudStyle = hudStyle,
            isChecked = true,
            isInstalled = true
        )
        _menuUiMods.update { listOf(newMenu) + it }
        audioHaptics.playHaptic(35, 160)
    }

    fun addInstalledMusicTrack(
        title: String,
        singerAnime: String,
        durationStr: String = "3:30",
        notesFrequencies: List<Double> = listOf(261.63, 293.66, 329.63, 392.00, 440.00, 523.25)
    ) {
        val newTrack = InstalledMusicTrack(
            id = "song_${System.currentTimeMillis()}",
            title = title,
            singerAnime = singerAnime,
            durationStr = durationStr,
            isPlaying = false,
            isCurrentBattleBgm = false,
            notesFrequencies = notesFrequencies
        )
        _installedMusicTracks.update { listOf(newTrack) + it }
        audioHaptics.playTagSwitchSound()
        audioHaptics.playHaptic(35, 160)
    }

    fun addCustomTexturePack(name: String, sizeMb: Int, category: String = "Characters") {
        val newPack = TexturePack(
            id = "tex_${System.currentTimeMillis()}",
            name = name,
            resolution = "4K UHD",
            sizeMb = sizeMb,
            author = "User Import",
            description = "Custom unpacked textures into textures/ULES01488 folder.",
            isEnabled = true,
            category = category,
            beforePreviewLabel = "Vanilla Original",
            afterPreviewLabel = "Custom Injected HD",
            accentColorHex = 0xFFF59E0B
        )
        _texturePacks.update { listOf(newPack) + it }
        audioHaptics.playHaptic(40, 180)
    }

    // ==========================================
    // TTT BBQ & PCK CHARACTER EDITOR METHODS (MainPage / ViewPj)
    // ==========================================
    fun selectTttEditorSubTab(tab: TttEditorSubTab) {
        _selectedTttEditorSubTab.value = tab
        audioHaptics.playHaptic(20, 100)
    }

    fun openBbqFile(path: String = "C:\\Escritorio\\00013277-000033DD.BBQ", fileName: String = "00013277-000033DD.BBQ") {
        _bbqFileState.update {
            it.copy(
                filePath = path,
                fileName = fileName,
                isOpen = true,
                isModified = false
            )
        }
        audioHaptics.playTagSwitchSound()
        audioHaptics.playHaptic(30, 150)
    }

    fun saveBbqFile() {
        _bbqFileState.update { it.copy(isModified = false) }
        audioHaptics.playSuperBeamSound()
        audioHaptics.playHaptic(50, 200)
    }

    fun closeBbqFile() {
        _bbqFileState.update { it.copy(isOpen = false, filePath = "Sin archivo abierto") }
        audioHaptics.playHaptic(20, 100)
    }

    fun nextBbqOrder() {
        val total = _bbqSlots.value.size
        if (total > 0) {
            _bbqFileState.update { it.copy(currentOrderIndex = (it.currentOrderIndex + 1) % total) }
            audioHaptics.playHaptic(15, 90)
        }
    }

    fun prevBbqOrder() {
        val total = _bbqSlots.value.size
        if (total > 0) {
            _bbqFileState.update { it.copy(currentOrderIndex = if (it.currentOrderIndex - 1 < 0) total - 1 else it.currentOrderIndex - 1) }
            audioHaptics.playHaptic(15, 90)
        }
    }

    fun saveCurrentBbqSlot(slotNumber: Int, hexId: String, colorNumber: Int, comment: String, characterFamily: String) {
        _bbqSlots.update { list ->
            list.map { slot ->
                if (slot.slotNumber == slotNumber) {
                    val colorHex = when (colorNumber) {
                        1 -> 0xFF22C55E
                        2 -> 0xFF3B82F6
                        3 -> 0xFFF59E0B
                        4 -> 0xFF8B5CF6
                        else -> 0xFFEAB308
                    }
                    slot.copy(
                        hexId = hexId,
                        colorNumber = colorNumber,
                        commentTag = comment,
                        characterFamily = characterFamily,
                        characterName = characterFamily,
                        colorHex = colorHex
                    )
                } else slot
            }
        }
        _bbqFileState.update { it.copy(isModified = true) }
        audioHaptics.playSuperBeamSound()
        audioHaptics.playHaptic(40, 180)
    }

    fun addBbqSlot(
        hexId: String,
        characterFamily: String,
        colorNumber: Int,
        comment: String
    ) {
        val nextNum = (_bbqSlots.value.maxOfOrNull { it.slotNumber } ?: 0) + 1
        val colorHex = when (colorNumber) {
            1 -> 0xFF22C55E
            2 -> 0xFF3B82F6
            3 -> 0xFFF59E0B
            4 -> 0xFF8B5CF6
            else -> 0xFFEAB308
        }
        val newSlot = TttBbqCharacterSlot(
            slotNumber = nextNum,
            hexId = hexId.ifBlank { "000132" + (70 + nextNum).toString(16).uppercase() },
            characterName = characterFamily,
            colorNumber = colorNumber,
            commentTag = comment.ifBlank { "$characterFamily ($colorNumber Color)" },
            characterFamily = characterFamily,
            colorHex = colorHex
        )
        _bbqSlots.update { it + newSlot }
        _bbqFileState.update { it.copy(isModified = true) }
        audioHaptics.playSuperBeamSound()
        audioHaptics.playHaptic(45, 190)
    }

    fun updateSlotColorNumber(slotNumber: Int, newColor: Int) {
        _bbqSlots.update { list ->
            list.map { slot ->
                if (slot.slotNumber == slotNumber) {
                    val colorHex = when (newColor) {
                        1 -> 0xFF22C55E
                        2 -> 0xFF3B82F6
                        3 -> 0xFFF59E0B
                        4 -> 0xFF8B5CF6
                        else -> 0xFFEAB308
                    }
                    slot.copy(colorNumber = newColor, colorHex = colorHex)
                } else slot
            }
        }
        _bbqFileState.update { it.copy(isModified = true) }
        audioHaptics.playHaptic(20, 110)
    }

    fun updateSlotCharacterFamily(slotNumber: Int, newFamily: String) {
        _bbqSlots.update { list ->
            list.map { slot ->
                if (slot.slotNumber == slotNumber) {
                    slot.copy(characterFamily = newFamily, characterName = newFamily)
                } else slot
            }
        }
        _bbqFileState.update { it.copy(isModified = true) }
        audioHaptics.playHaptic(20, 110)
    }


    // Mods methods
    fun selectModCategory(category: ModCategory) {
        _selectedModCategory.value = category
        audioHaptics.playHaptic(20, 100)
    }

    fun toggleMod(id: String) {
        _modsList.update { list ->
            list.map { if (it.id == id) it.copy(isEnabled = !it.isEnabled) else it }
        }
        audioHaptics.playHaptic(25, 120)
    }

    fun addCustomMod(title: String, category: ModCategory, sizeMb: Int) {
        val newMod = GameMod(
            id = "mod_${System.currentTimeMillis()}",
            title = title,
            category = category,
            version = "v1.0",
            sizeMb = sizeMb,
            author = "Custom Mod",
            description = "Extracted to gamedata folder with higher priority.",
            isEnabled = true
        )
        _modsList.update { listOf(newMod) + it }
        audioHaptics.playHaptic(40, 180)
    }

    // Controls methods
    fun setControlLayout(layout: ControlLayoutType) {
        _controlLayout.value = layout
        audioHaptics.playHaptic(25, 120)
    }

    fun setAutoMenuFight(value: Boolean) {
        _autoMenuFight.value = value
        audioHaptics.playHaptic(20, 100)
    }

    fun setControlOpacity(opacity: Float) {
        _controlOpacity.value = opacity
    }

    fun setControlScale(scale: Float) {
        _controlScale.value = scale
    }

    fun setHapticFeedback(enabled: Boolean) {
        _hapticFeedback.value = enabled
        audioHaptics.isHapticsEnabled = enabled
        if (enabled) audioHaptics.playHaptic(40, 200)
    }

    // Cheats methods
    fun setUnlockEverything(value: Boolean) {
        _unlockEverything.value = value
        audioHaptics.playHaptic(50, 220)
        if (value) {
            audioHaptics.playSuperBeamSound()
            _cheatsList.update { list ->
                list.map { it.copy(isEnabled = true) }
            }
        }
    }

    fun toggleCheat(id: String) {
        _cheatsList.update { list ->
            list.map { if (it.id == id) it.copy(isEnabled = !it.isEnabled) else it }
        }
        audioHaptics.playHaptic(25, 140)
    }

    fun addCustomCheat(name: String, code: String) {
        val newCheat = CwCheat(
            id = "c_${System.currentTimeMillis()}",
            name = name,
            description = "User added CWCheat code.",
            codeHex = code,
            isEnabled = true
        )
        _cheatsList.update { list -> list + newCheat }
        audioHaptics.playHaptic(40, 180)
    }

    // Settings methods
    fun setInternalResolution(res: Float) {
        _internalResolution.value = res
        audioHaptics.playHaptic(15, 80)
    }

    fun setScreenFilter(filter: ScreenFilterType) {
        _screenFilter.value = filter
        audioHaptics.playHaptic(20, 100)
    }

    fun setAspectRatio(ratio: AspectRatioType) {
        _aspectRatio.value = ratio
        audioHaptics.playHaptic(20, 100)
    }

    fun setShowFps(show: Boolean) {
        _showFps.value = show
        audioHaptics.playHaptic(20, 100)
    }

    fun setFps60Patch(patch: Boolean) {
        _fps60Patch.value = patch
        audioHaptics.playHaptic(20, 100)
    }

    fun setSoundVolume(volume: Float) {
        _soundVolume.value = volume
        audioHaptics.isSoundEnabled = volume > 0.05f
    }

    fun setBgmVolume(volume: Float) {
        _bgmVolume.value = volume
    }

    fun setFastMemory(value: Boolean) {
        _fastMemory.value = value
    }

    fun setMultithreading(value: Boolean) {
        _multithreading.value = value
    }

    fun setLanguage(lang: String) {
        _language.value = lang
    }

    // ==========================================
    // BATTLE SIMULATOR ENGINE (2v2 TAG TEAM)
    // ==========================================
    fun startBattle() {
        audioHaptics.playSuperBeamSound()
        audioHaptics.playHaptic(70, 240)

        // Reset battle state with cheats applied if enabled
        val p1Team = listOf(
            Fighter("goku_ssj", "Goku", "Super Saiyan", 1000f, 1000f, 100f, if (_unlockEverything.value) 100f else 50f, "Super Kamehameha", 60f, 0xFFFBBF24),
            Fighter("vegeta_ssj", "Vegeta", "Super Saiyan", 950f, 950f, 100f, if (_unlockEverything.value) 100f else 40f, "Final Flash", 60f, 0xFF38BDF8)
        )
        val cpuTeam = listOf(
            Fighter("frieza_final", "Frieza", "Final Form", 980f, 980f, 100f, 40f, "Death Ball", 60f, 0xFFA855F7),
            Fighter("cell_perfect", "Cell", "Perfect Form", 1050f, 1050f, 100f, 50f, "Solar Kamehameha", 60f, 0xFF22C55E)
        )

        _battleState.value = BattleGameState(
            isPlaying = true,
            isPaused = false,
            p1ActiveIndex = 0,
            p1Team = p1Team,
            cpuActiveIndex = 0,
            cpuTeam = cpuTeam,
            sparkingModeActive = _unlockEverything.value,
            lastCombatLog = "FIGHT! 2v2 Tag Team: Goku & Vegeta VS Frieza & Cell!"
        )

        startBattleLoop()
    }

    fun pauseBattle() {
        _battleState.update { it.copy(isPaused = true) }
        audioHaptics.playHaptic(30, 150)
    }

    fun resumeBattle() {
        _battleState.update { it.copy(isPaused = false) }
        audioHaptics.playHaptic(30, 150)
    }

    fun exitBattle() {
        battleJob?.cancel()
        _battleState.update { it.copy(isPlaying = false, isPaused = false) }
        audioHaptics.playHaptic(30, 150)
    }

    fun executeMeleeAttack() {
        val state = _battleState.value
        if (!state.isPlaying || state.isPaused || state.isSuperAttackExecuting) return

        audioHaptics.playMeleeHitSound()
        audioHaptics.playHaptic(35, 190)

        _battleState.update { current ->
            val activeP1 = current.p1Team[current.p1ActiveIndex]
            val activeCpu = current.cpuTeam[current.cpuActiveIndex]

            val isOneHitKill = _unlockEverything.value || _cheatsList.value.any { it.name.contains("One Hit", true) && it.isEnabled }
            val damage = if (isOneHitKill) 1200f else (if (current.sparkingModeActive) 120f else 65f)
            val newCpuHp = (activeCpu.currentHp - damage).coerceAtLeast(0f)
            val newKi = (activeP1.currentKi + 8f).coerceAtMost(activeP1.maxKi)

            val combo = current.p1HitsCombo + 1
            val log = "${activeP1.name}: Melee Rush Hit x$combo! (-${damage.toInt()} HP)"

            // Check if CPU knocked out
            var newCpuIndex = current.cpuActiveIndex
            if (newCpuHp <= 0f && current.cpuActiveIndex == 0) {
                newCpuIndex = 1
                audioHaptics.playSuperBeamSound()
            }

            activeCpu.currentHp = newCpuHp
            activeP1.currentKi = newKi

            current.copy(
                p1HitsCombo = combo,
                cpuActiveIndex = newCpuIndex,
                lastCombatLog = log
            )
        }
    }

    fun executeKiBlast() {
        val state = _battleState.value
        if (!state.isPlaying || state.isPaused || state.isSuperAttackExecuting) return

        val activeP1 = state.p1Team[state.p1ActiveIndex]
        if (activeP1.currentKi < 10f) {
            _battleState.update { it.copy(lastCombatLog = "${activeP1.name}: Need more Ki to fire Ki blast!") }
            return
        }

        audioHaptics.playKiBlastSound()
        audioHaptics.playHaptic(30, 160)

        _battleState.update { current ->
            val p1 = current.p1Team[current.p1ActiveIndex]
            val cpu = current.cpuTeam[current.cpuActiveIndex]

            val infiniteKi = _unlockEverything.value || _cheatsList.value.any { it.name.contains("Max Ki", true) && it.isEnabled }
            if (!infiniteKi) p1.currentKi = (p1.currentKi - 10f).coerceAtLeast(0f)

            val damage = if (current.sparkingModeActive) 90f else 45f
            cpu.currentHp = (cpu.currentHp - damage).coerceAtLeast(0f)

            current.copy(
                lastCombatLog = "${p1.name} fired Ki Blast Volley! (-${damage.toInt()} HP)"
            )
        }
    }

    fun setChargeKi(isCharging: Boolean) {
        val state = _battleState.value
        if (!state.isPlaying || state.isPaused) return

        _battleState.update { it.copy(isChargingKi = isCharging) }
        if (isCharging) {
            audioHaptics.playKiChargeSound()
            audioHaptics.playHaptic(50, 180)
        }
    }

    fun executeSuperAttack() {
        val state = _battleState.value
        if (!state.isPlaying || state.isPaused || state.isSuperAttackExecuting) return

        val activeP1 = state.p1Team[state.p1ActiveIndex]
        val infiniteKi = _unlockEverything.value || _cheatsList.value.any { it.name.contains("Max Ki", true) && it.isEnabled }

        if (activeP1.currentKi < activeP1.superAttackKiCost && !infiniteKi) {
            _battleState.update { it.copy(lastCombatLog = "Not enough Ki for ${activeP1.superAttackName}! Charge up first (L).") }
            return
        }

        audioHaptics.playSuperBeamSound()
        audioHaptics.playHaptic(120, 255)

        _battleState.update { current ->
            val p1 = current.p1Team[current.p1ActiveIndex]
            if (!infiniteKi) p1.currentKi = (p1.currentKi - p1.superAttackKiCost).coerceAtLeast(0f)
            current.copy(
                isSuperAttackExecuting = true,
                superAttackProgress = 0f,
                lastCombatLog = "ULTIMATE: ${p1.name} unleashes ${p1.superAttackName}!!"
            )
        }

        viewModelScope.launch {
            for (step in 1..10) {
                delay(80)
                _battleState.update { it.copy(superAttackProgress = step / 10f) }
            }
            delay(150)
            _battleState.update { current ->
                val cpu = current.cpuTeam[current.cpuActiveIndex]
                val damage = if (current.sparkingModeActive) 450f else 320f
                cpu.currentHp = (cpu.currentHp - damage).coerceAtLeast(0f)
                current.copy(
                    isSuperAttackExecuting = false,
                    superAttackProgress = 0f,
                    lastCombatLog = "DIRECT HIT! ${current.p1Team[current.p1ActiveIndex].superAttackName} deals ${damage.toInt()} damage!"
                )
            }
        }
    }

    fun executeTagSwitch() {
        val state = _battleState.value
        if (!state.isPlaying || state.isPaused || state.isSuperAttackExecuting) return

        val noCooldown = _unlockEverything.value || _cheatsList.value.any { it.name.contains("Instant Tag", true) && it.isEnabled }
        if (state.tagCooldownSeconds > 0f && !noCooldown) {
            _battleState.update { it.copy(lastCombatLog = "Tag partner ready in ${state.tagCooldownSeconds.toInt()}s!") }
            return
        }

        audioHaptics.playTagSwitchSound()
        audioHaptics.playHaptic(60, 220)

        _battleState.update { current ->
            val nextIndex = if (current.p1ActiveIndex == 0) 1 else 0
            val incoming = current.p1Team[nextIndex]
            current.copy(
                p1ActiveIndex = nextIndex,
                tagCooldownSeconds = if (noCooldown) 0f else 8f,
                lastCombatLog = "TAG SWITCH! ${incoming.name} jumps into battle with support strike!"
            )
        }
    }

    fun setGuarding(isGuarding: Boolean) {
        val state = _battleState.value
        if (!state.isPlaying || state.isPaused) return
        _battleState.update { it.copy(isGuarding = isGuarding) }
        if (isGuarding) {
            audioHaptics.playGuardSound()
            audioHaptics.playHaptic(20, 100)
        }
    }

    private fun startBattleLoop() {
        battleJob?.cancel()
        battleJob = viewModelScope.launch {
            while (isActive) {
                delay(200)
                val current = _battleState.value
                if (!current.isPlaying || current.isPaused) continue

                // 1. Tag Cooldown reduction
                val newCooldown = (current.tagCooldownSeconds - 0.2f).coerceAtLeast(0f)

                // 2. Ki charging for P1
                val p1 = current.p1Team[current.p1ActiveIndex]
                val infiniteKi = _unlockEverything.value || _cheatsList.value.any { it.name.contains("Max Ki", true) && it.isEnabled }
                if (current.isChargingKi || infiniteKi) {
                    p1.currentKi = (p1.currentKi + 3f).coerceAtMost(p1.maxKi)
                    if (p1.currentKi >= p1.maxKi && !current.sparkingModeActive) {
                        _battleState.update { it.copy(sparkingModeActive = true, sparkingTimerSeconds = 15f) }
                        audioHaptics.playSuperBeamSound()
                    }
                }

                // 3. Sparking Mode Timer
                var sparkingActive = current.sparkingModeActive
                var sparkingTimer = current.sparkingTimerSeconds
                if (sparkingActive) {
                    sparkingTimer -= 0.2f
                    if (sparkingTimer <= 0f) {
                        sparkingActive = false
                    }
                }

                // 4. Infinite Health Cheat check
                val infiniteHealth = _unlockEverything.value || _cheatsList.value.any { it.name.contains("Infinite Health", true) && it.isEnabled }
                if (infiniteHealth) {
                    p1.currentHp = p1.maxHp
                }

                // 5. CPU Opponent AI action (Attack or Charge)
                val cpu = current.cpuTeam[current.cpuActiveIndex]
                if (cpu.currentHp > 0f && Random.nextFloat() < 0.25f && !current.isSuperAttackExecuting) {
                    val cpuAction = Random.nextInt(3)
                    if (cpuAction == 0) {
                        // CPU Ki charge
                        cpu.currentKi = (cpu.currentKi + 15f).coerceAtMost(cpu.maxKi)
                    } else if (cpuAction == 1) {
                        // CPU attacks P1
                        if (!current.isGuarding) {
                            val cpuDmg = if (infiniteHealth) 0f else 35f
                            p1.currentHp = (p1.currentHp - cpuDmg).coerceAtLeast(0f)
                            if (cpuDmg > 0f) {
                                audioHaptics.playMeleeHitSound()
                                audioHaptics.playHaptic(20, 140)
                            }
                        } else {
                            audioHaptics.playGuardSound()
                        }
                    } else if (cpuAction == 2 && cpu.currentKi >= 50f) {
                        // CPU Super attack
                        if (!current.isGuarding) {
                            val cpuDmg = if (infiniteHealth) 0f else 110f
                            p1.currentHp = (p1.currentHp - cpuDmg).coerceAtLeast(0f)
                            cpu.currentKi -= 50f
                            if (cpuDmg > 0f) {
                                audioHaptics.playSuperBeamSound()
                                audioHaptics.playHaptic(40, 200)
                            }
                        }
                    }
                }

                // Check Victory / Defeat
                val cpu1Hp = current.cpuTeam[0].currentHp
                val cpu2Hp = current.cpuTeam[1].currentHp
                val p1Hp = current.p1Team[0].currentHp
                val p2Hp = current.p1Team[1].currentHp

                var battleEndLog: String? = null
                if (cpu1Hp <= 0f && cpu2Hp <= 0f) {
                    battleEndLog = "VICTORY! Dragon Ball Z Tenkaichi Tag Team Champions!"
                } else if (p1Hp <= 0f && p2Hp <= 0f) {
                    battleEndLog = "K.O.! Opponent team wins. Shenron awaits your return!"
                }

                _battleState.update { state ->
                    state.copy(
                        tagCooldownSeconds = newCooldown,
                        sparkingModeActive = sparkingActive,
                        sparkingTimerSeconds = sparkingTimer,
                        lastCombatLog = battleEndLog ?: state.lastCombatLog
                    )
                }
            }
        }
    }
}
