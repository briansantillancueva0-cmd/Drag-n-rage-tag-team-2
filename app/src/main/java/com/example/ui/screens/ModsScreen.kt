package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.ObjPckModelViewer
import com.example.ui.components.TttCharacterModEditor
import com.example.ui.theme.*
import com.example.viewmodel.DbzTagTeamViewModel

enum class ModViewTab(val label: String) {
    TTT_EDITOR("Editor TTT (ViewPj)"),
    PCK1_ROSTER("PCK 1 Roster"),
    VIEWER_3D("Visor 3D OBJ & PCK 1"),
    KI_COLORS("Colores & SFX POHD"),
    AUDIO_VOICES("Audio & Voces"),
    MAPS_MENUS("Mapas & Menús"),
    MUSIC_PLAYER("Música Reproductor"),
    GAME_DUMP("Archivos Extraídos")
}

@Composable
fun ModsScreen(
    viewModel: DbzTagTeamViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(ModViewTab.PCK1_ROSTER) }

    val extractionProgress by viewModel.extractionProgress.collectAsState()
    val pckCharacters by viewModel.pckCharacters.collectAsState()
    val colorKiMods by viewModel.colorKiMods.collectAsState()
    val audioVoiceMods by viewModel.audioVoiceMods.collectAsState()
    val stageMapMods by viewModel.stageMapMods.collectAsState()
    val menuUiMods by viewModel.menuUiMods.collectAsState()
    val musicTracks by viewModel.installedMusicTracks.collectAsState()

    // Dialog state for adding mods
    var showAddPckDialog by remember { mutableStateOf(false) }
    var newPckCharName by remember { mutableStateOf("") }
    var newPckReplaces by remember { mutableStateOf("Goku SSJ3") }
    var newPckSpecialMove by remember { mutableStateOf("Kamehameha Divino") }
    var newPckSlot by remember { mutableStateOf("pzs3us1.pck (PCK 1 Slot 9)") }

    var showAddColorDialog by remember { mutableStateOf(false) }
    var newColorName by remember { mutableStateOf("") }
    var newColorAuraDesc by remember { mutableStateOf("Aura brillante con chispas de divinidad") }
    var newColorHex by remember { mutableStateOf(0xFFF59E0B) }
    var newColorSfxType by remember { mutableStateOf("SFX POHD") }

    var showAddAudioDialog by remember { mutableStateOf(false) }
    var newAudioName by remember { mutableStateOf("") }
    var newAudioActors by remember { mutableStateOf("Doblaje Latino Oficial") }
    var newAudioSample by remember { mutableStateOf("¡Kamehameha x10!") }

    var showAddMapDialog by remember { mutableStateOf(false) }
    var newMapName by remember { mutableStateOf("") }
    var newMapAtmosphere by remember { mutableStateOf("Arena cósmica con rocas flotantes y cielo estelar") }

    var showAddMenuDialog by remember { mutableStateOf(false) }
    var newMenuName by remember { mutableStateOf("") }
    var newMenuStyle by remember { mutableStateOf("Estilo Sparking ZERO con barra de vida curva") }

    var showAddSongDialog by remember { mutableStateOf(false) }
    var newSongTitle by remember { mutableStateOf("") }
    var newSongSinger by remember { mutableStateOf("Hironobu Kageyama (DBZ OST)") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
        ) {
            // Header
            item {
                Text(
                    text = "MODS",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "PCK 1, Visor 3D OBJ, Colores, Audios SFX POHD y Música",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                )
            }

            // ==========================================
            // BANNER: "SE EXTRAE TODO EL JUEGO"
            // ==========================================
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, if (extractionProgress.isExtracting) DragonAmber else CardBorderDark, RoundedCornerShape(20.dp)),
                    color = SurfaceDark
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF282420)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FolderZip,
                                        contentDescription = "Extractor",
                                        tint = DragonAmber,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Extracción del Juego (Dump)",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = if (extractionProgress.isExtracting) "Extrayendo todo el juego..." else "Juego extraído: 13 archivos · 4.0 GB",
                                        fontSize = 12.sp,
                                        color = if (extractionProgress.isExtracting) DragonAmber else SuccessGreen
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.extractAllGameData()
                                    Toast.makeText(context, "Extrayendo archivos del juego...", Toast.LENGTH_SHORT).show()
                                },
                                enabled = !extractionProgress.isExtracting,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DragonAmber,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(18.dp),
                                modifier = Modifier
                                    .height(36.dp)
                                    .testTag("extract_all_game_button"),
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Icon(Icons.Default.Unarchive, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (extractionProgress.isExtracting) "Extrayendo" else "Extraer Todo",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Progress Bar during extraction
                        if (extractionProgress.isExtracting) {
                            Spacer(modifier = Modifier.height(12.dp))
                            LinearProgressIndicator(
                                progress = { extractionProgress.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = DragonAmber,
                                trackColor = CardBorderDark
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = extractionProgress.currentFileName,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = DragonGold
                            )
                        } else {
                            // Quick ISO Capacity & Mod Slots expansion badge
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = Color(0xFF131D33),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Storage, contentDescription = null, tint = DragonAmber, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "ISO Expandida: 2.0 o 4.0 GB habilitada para casillas de mods",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                    }
                                    Surface(
                                        color = DragonAmber,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "4.0 GB DISPONIBLE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Tabs Selector
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ModViewTab.values()) { tab ->
                        val isSelected = selectedTab == tab
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTab = tab },
                            label = { Text(tab.label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DragonAmber,
                                selectedLabelColor = Color.Black,
                                containerColor = SurfaceDark,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) DragonAmber else CardBorderDark
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // ==========================================
            // PESTAÑA: EDITOR TTT (MAINPAGE / VIEWPJ)
            // ==========================================
            if (selectedTab == ModViewTab.TTT_EDITOR) {
                item {
                    TttCharacterModEditor(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ==========================================
            // PESTAÑA 1: PCK 1 (ROSTER DE PERSONAJES)
            // ==========================================
            if (selectedTab == ModViewTab.PCK1_ROSTER) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "MODS PCK 1 (pzs3us1.pck)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DragonAmber,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Casillas para activar o reemplazar personajes en PCK 1:",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Button(
                            onClick = { showAddPckDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = DragonAmber, contentColor = Color.Black),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Agregar Mod", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(pckCharacters, key = { it.id }) { char ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, if (char.isChecked) DragonAmber else CardBorderDark, RoundedCornerShape(16.dp))
                            .clickable { viewModel.togglePckCharacter(char.id) },
                        color = SurfaceDark
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = char.isChecked,
                                onCheckedChange = { viewModel.togglePckCharacter(char.id) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = DragonAmber,
                                    checkmarkColor = Color.Black
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = char.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Reemplaza: ${char.replacesOriginal} · Super: ${char.specialMove}",
                                    fontSize = 11.sp,
                                    color = DragonAmber
                                )
                                Text(
                                    text = "${char.pckSlot} · ${char.colorPalette}",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextSecondary
                                )
                            }

                            // View in 3D Button
                            IconButton(
                                onClick = {
                                    viewModel.select3DModel("model_goku_mui")
                                    selectedTab = ModViewTab.VIEWER_3D
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceVariantDark)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ViewInAr,
                                    contentDescription = "Ver en 3D",
                                    tint = KiCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // ==========================================
            // PESTAÑA 2: VISOR 3D DE MODS OBJ & PCK 1
            // ==========================================
            if (selectedTab == ModViewTab.VIEWER_3D) {
                item {
                    ObjPckModelViewer(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ==========================================
            // PESTAÑA 3: COLORES & SFX POHD
            // ==========================================
            if (selectedTab == ModViewTab.KI_COLORS) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "MODS DE COLORES & SFX POHD",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DragonAmber,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Casillas de colores de Ki y efectos sonoros POHD:",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Button(
                            onClick = { showAddColorDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = DragonAmber, contentColor = Color.Black),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Agregar Color", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(colorKiMods, key = { it.id }) { colorMod ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, if (colorMod.isChecked) Color(colorMod.colorHex) else CardBorderDark, RoundedCornerShape(16.dp))
                            .clickable { viewModel.toggleColorKiMod(colorMod.id) },
                        color = SurfaceDark
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = colorMod.isChecked,
                                onCheckedChange = { viewModel.toggleColorKiMod(colorMod.id) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(colorMod.colorHex),
                                    checkmarkColor = Color.Black
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(colorMod.colorHex))
                                    .border(1.dp, Color.White, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = colorMod.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${colorMod.sfxType}: ${colorMod.sfxHdName}",
                                    fontSize = 11.sp,
                                    color = DragonAmber
                                )
                                Text(
                                    text = colorMod.auraDescription,
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }

                            // Play SFX POHD Sound button
                            OutlinedButton(
                                onClick = {
                                    viewModel.playColorPohdSfx(colorMod.id)
                                    Toast.makeText(context, "Sonando ${colorMod.sfxHdName}", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.height(32.dp),
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = DragonAmber, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("SFX", fontSize = 10.sp, color = DragonAmber, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // ==========================================
            // PESTAÑA 4: AUDIO & VOCES
            // ==========================================
            if (selectedTab == ModViewTab.AUDIO_VOICES) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "MODS DE AUDIO Y VOCES",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DragonAmber,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Casillas para instalar audios de doblaje y SFX:",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Button(
                            onClick = { showAddAudioDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = DragonAmber, contentColor = Color.Black),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Agregar Audio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(audioVoiceMods, key = { it.id }) { audioMod ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, if (audioMod.isChecked) DragonAmber else CardBorderDark, RoundedCornerShape(16.dp))
                            .clickable { viewModel.toggleAudioVoiceMod(audioMod.id) },
                        color = SurfaceDark
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = audioMod.isChecked,
                                onCheckedChange = { viewModel.toggleAudioVoiceMod(audioMod.id) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = DragonAmber,
                                    checkmarkColor = Color.Black
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = audioMod.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Actores: ${audioMod.actors}",
                                    fontSize = 11.sp,
                                    color = DragonAmber
                                )
                                Text(
                                    text = "\"${audioMod.sampleText}\" · ${audioMod.audioFormat}",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }

                            // Preview Voice Sample button
                            OutlinedButton(
                                onClick = {
                                    viewModel.playVoiceSample(audioMod.id)
                                    Toast.makeText(context, "Muestra: ${audioMod.sampleText}", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.height(32.dp),
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = KiCyan, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Escuchar", fontSize = 10.sp, color = KiCyan, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // ==========================================
            // PESTAÑA 5: MAPAS & MENÚS
            // ==========================================
            if (selectedTab == ModViewTab.MAPS_MENUS) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MAPAS Y ESCENARIOS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DragonAmber,
                            letterSpacing = 1.sp
                        )
                        Button(
                            onClick = { showAddMapDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = DragonAmber, contentColor = Color.Black),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Agregar Mapa", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(stageMapMods, key = { it.id }) { mapMod ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, if (mapMod.isChecked) DragonAmber else CardBorderDark, RoundedCornerShape(14.dp))
                            .clickable { viewModel.toggleStageMapMod(mapMod.id) },
                        color = SurfaceDark
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = mapMod.isChecked,
                                onCheckedChange = { viewModel.toggleStageMapMod(mapMod.id) },
                                colors = CheckboxDefaults.colors(checkedColor = DragonAmber, checkmarkColor = Color.Black)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = mapMod.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(text = mapMod.atmosphere, fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MENÚS Y ESTILOS DE INTERFAZ",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = KiCyan,
                            letterSpacing = 1.sp
                        )
                        Button(
                            onClick = { showAddMenuDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = KiCyan, contentColor = Color.Black),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Agregar Menú", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(menuUiMods, key = { it.id }) { menuMod ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, if (menuMod.isChecked) KiCyan else CardBorderDark, RoundedCornerShape(14.dp))
                            .clickable { viewModel.toggleMenuUiMod(menuMod.id) },
                        color = SurfaceDark
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = menuMod.isChecked,
                                onCheckedChange = { viewModel.toggleMenuUiMod(menuMod.id) },
                                colors = CheckboxDefaults.colors(checkedColor = KiCyan, checkmarkColor = Color.Black)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = menuMod.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(text = "${menuMod.themeStyle} · ${menuMod.hudStyle}", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            // ==========================================
            // PESTAÑA 6: REPRODUCTOR DE MÚSICA INSTALADA
            // ==========================================
            if (selectedTab == ModViewTab.MUSIC_PLAYER) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "REPRODUCTOR DE MÚSICA INSTALADA",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DragonAmber,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Reproducción sonora real de temas y fijar como BGM de combate:",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Button(
                            onClick = { showAddSongDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = DragonAmber, contentColor = Color.Black),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Canción", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(musicTracks, key = { it.id }) { track ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, if (track.isPlaying) DragonAmber else CardBorderDark, RoundedCornerShape(16.dp)),
                        color = if (track.isPlaying) Color(0xFF1E283F) else SurfaceDark
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.playMusicTrack(track.id) },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (track.isPlaying) DragonAmber else SurfaceVariantDark)
                            ) {
                                Icon(
                                    imageVector = if (track.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (track.isPlaying) "Pausar" else "Reproducir",
                                    tint = if (track.isPlaying) Color.Black else DragonAmber,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = track.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    if (track.isCurrentBattleBgm) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = SuccessGreen,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "BGM",
                                                color = Color.Black,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = track.singerAnime,
                                    fontSize = 11.sp,
                                    color = DragonAmber
                                )
                                Text(
                                    text = "${track.durationStr} · Audio HD 320kbps",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }

                            TextButton(
                                onClick = {
                                    viewModel.setAsBattleBgm(track.id)
                                    Toast.makeText(context, "${track.title} fijada como BGM de combate!", Toast.LENGTH_SHORT).show()
                                },
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Text(
                                    text = if (track.isCurrentBattleBgm) "En uso" else "Fijar BGM",
                                    color = if (track.isCurrentBattleBgm) SuccessGreen else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // ==========================================
            // PESTAÑA 7: ARCHIVOS EXTRAÍDOS (EXPLORADOR)
            // ==========================================
            if (selectedTab == ModViewTab.GAME_DUMP) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "EXPLORADOR DEL JUEGO EXTRAÍDO",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DragonAmber,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Archivos del sistema PSP, paquetes PCK y audio AFS:",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.repackGameToIso()
                                Toast.makeText(context, "¡Juego re-empaquetado a ISO con mods inyectados!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Color.Black),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.Archive, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Empaquetar ISO", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(extractionProgress.extractedFiles, key = { it.path }) { file ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, if (file.isModded) DragonAmber else CardBorderDark, RoundedCornerShape(12.dp)),
                        color = SurfaceDark
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (file.fileType) {
                                    "PCK" -> Icons.Default.Inventory2
                                    "AFS", "ADX", "SND" -> Icons.Default.Audiotrack
                                    "DDS", "PNG" -> Icons.Default.Image
                                    "PMF" -> Icons.Default.Movie
                                    else -> Icons.Default.Description
                                },
                                contentDescription = null,
                                tint = if (file.isModded) DragonAmber else KiCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = file.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.White
                                    )
                                    if (file.isModded) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = DragonAmber,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "MOD",
                                                color = Color.Black,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = file.description,
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "${file.path} · ${file.sizeMb} MB",
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = DragonAmber
                                )
                            }

                            TextButton(
                                onClick = {
                                    viewModel.injectModIntoExtractedFile(file.path)
                                    Toast.makeText(context, "Mod inyectado en ${file.name}!", Toast.LENGTH_SHORT).show()
                                },
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Text("Inyectar", fontSize = 10.sp, color = KiCyan, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }

    // ==========================================
    // DIALOGS: ADD MODS WITH CUSTOM NAMES
    // ==========================================

    // 1. Add PCK Character Dialog
    if (showAddPckDialog) {
        AlertDialog(
            onDismissRequest = { showAddPckDialog = false },
            title = { Text("Instalar Mod en PCK 1 (pzs3us1.pck)", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPckCharName,
                        onValueChange = { newPckCharName = it },
                        label = { Text("Nombre del Personaje Mod") },
                        placeholder = { Text("ej. Goku Ultra Instinct") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPckReplaces,
                        onValueChange = { newPckReplaces = it },
                        label = { Text("Personaje a reemplazar") },
                        placeholder = { Text("ej. Goku SSJ3") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPckSpecialMove,
                        onValueChange = { newPckSpecialMove = it },
                        label = { Text("Super Ataque Especial") },
                        placeholder = { Text("ej. Super Kamehameha Plateado") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = newPckCharName.ifBlank { "Nuevo Personaje Mod PCK 1" }
                        viewModel.addPckCharacter(name, newPckReplaces, newPckSpecialMove, newPckSlot)
                        showAddPckDialog = false
                        newPckCharName = ""
                        Toast.makeText(context, "$name instalado en PCK 1 con éxito!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DragonAmber)
                ) {
                    Text("Instalar en PCK 1", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPckDialog = false }) { Text("Cancelar", color = TextSecondary) }
            },
            containerColor = SurfaceDark
        )
    }

    // 2. Add Color Ki Mod Dialog
    if (showAddColorDialog) {
        AlertDialog(
            onDismissRequest = { showAddColorDialog = false },
            title = { Text("Instalar Mod de Color & SFX POHD", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newColorName,
                        onValueChange = { newColorName = it },
                        label = { Text("Nombre del Color / Traje") },
                        placeholder = { Text("ej. Color 4: Ki Divino Carmesí") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newColorAuraDesc,
                        onValueChange = { newColorAuraDesc = it },
                        label = { Text("Descripción del Aura") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = newColorName.ifBlank { "Color Personalizado POHD" }
                        viewModel.addColorKiMod(name, newColorHex, newColorAuraDesc, 4, "SFX POHD 96kHz Custom", newColorSfxType)
                        showAddColorDialog = false
                        newColorName = ""
                        Toast.makeText(context, "$name registrado con SFX POHD!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DragonAmber)
                ) {
                    Text("Instalar Color", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddColorDialog = false }) { Text("Cancelar", color = TextSecondary) }
            },
            containerColor = SurfaceDark
        )
    }

    // 3. Add Audio Voice Dialog
    if (showAddAudioDialog) {
        AlertDialog(
            onDismissRequest = { showAddAudioDialog = false },
            title = { Text("Instalar Mod de Audio y Voces", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newAudioName,
                        onValueChange = { newAudioName = it },
                        label = { Text("Nombre del Audio / Doblaje") },
                        placeholder = { Text("ej. Voces Latino Mario Castañeda HD") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newAudioActors,
                        onValueChange = { newAudioActors = it },
                        label = { Text("Actores de Doblaje") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newAudioSample,
                        onValueChange = { newAudioSample = it },
                        label = { Text("Frase de Muestra") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = newAudioName.ifBlank { "Audio Mod Latino HD" }
                        viewModel.addAudioVoiceMod(name, newAudioActors, newAudioSample)
                        showAddAudioDialog = false
                        newAudioName = ""
                        Toast.makeText(context, "$name instalado!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DragonAmber)
                ) {
                    Text("Instalar Audio", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAudioDialog = false }) { Text("Cancelar", color = TextSecondary) }
            },
            containerColor = SurfaceDark
        )
    }

    // 4. Add Stage Map Dialog
    if (showAddMapDialog) {
        AlertDialog(
            onDismissRequest = { showAddMapDialog = false },
            title = { Text("Instalar Mod de Mapa / Escenario", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newMapName,
                        onValueChange = { newMapName = it },
                        label = { Text("Nombre del Mapa") },
                        placeholder = { Text("ej. Torneo del Poder (Mundo de la Nada)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newMapAtmosphere,
                        onValueChange = { newMapAtmosphere = it },
                        label = { Text("Atmósfera y Entorno") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = newMapName.ifBlank { "Mapa Custom HD" }
                        viewModel.addStageMapMod(name, newMapAtmosphere)
                        showAddMapDialog = false
                        newMapName = ""
                        Toast.makeText(context, "Mapa $name instalado!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DragonAmber)
                ) {
                    Text("Instalar Mapa", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMapDialog = false }) { Text("Cancelar", color = TextSecondary) }
            },
            containerColor = SurfaceDark
        )
    }

    // 5. Add Menu UI Dialog
    if (showAddMenuDialog) {
        AlertDialog(
            onDismissRequest = { showAddMenuDialog = false },
            title = { Text("Instalar Mod de Menú e Interfaz", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newMenuName,
                        onValueChange = { newMenuName = it },
                        label = { Text("Nombre del Menú") },
                        placeholder = { Text("ej. Menú Sparking ZERO HUD") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newMenuStyle,
                        onValueChange = { newMenuStyle = it },
                        label = { Text("Estilo Visual") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = newMenuName.ifBlank { "Menú Custom UI" }
                        viewModel.addMenuUiMod(name, newMenuStyle)
                        showAddMenuDialog = false
                        newMenuName = ""
                        Toast.makeText(context, "Menú $name instalado!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DragonAmber)
                ) {
                    Text("Instalar Menú", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMenuDialog = false }) { Text("Cancelar", color = TextSecondary) }
            },
            containerColor = SurfaceDark
        )
    }

    // 6. Add Installed Song Dialog
    if (showAddSongDialog) {
        AlertDialog(
            onDismissRequest = { showAddSongDialog = false },
            title = { Text("Instalar Canción para el Reproductor", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newSongTitle,
                        onValueChange = { newSongTitle = it },
                        label = { Text("Título de la Canción") },
                        placeholder = { Text("ej. We Gotta Power") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newSongSinger,
                        onValueChange = { newSongSinger = it },
                        label = { Text("Artista / Intérprete") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val title = newSongTitle.ifBlank { "Tema Dragon Ball Z" }
                        viewModel.addInstalledMusicTrack(title, newSongSinger)
                        showAddSongDialog = false
                        newSongTitle = ""
                        Toast.makeText(context, "¡Canción $title agregada y lista para reproducir!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DragonAmber)
                ) {
                    Text("Agregar Canción", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSongDialog = false }) { Text("Cancelar", color = TextSecondary) }
            },
            containerColor = SurfaceDark
        )
    }
}
