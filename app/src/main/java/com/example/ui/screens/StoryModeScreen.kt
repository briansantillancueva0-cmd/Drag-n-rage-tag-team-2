package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.StoryMission
import com.example.model.StorySaga
import com.example.ui.components.StoryNodeEditorDialog
import com.example.ui.theme.*
import com.example.viewmodel.DbzTagTeamViewModel

@Composable
fun StoryModeScreen(
    viewModel: DbzTagTeamViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler { onBack() }

    val sagas by viewModel.storySagas.collectAsState()
    val musicTracks by viewModel.installedMusicTracks.collectAsState()
    val currentMusic = musicTracks.firstOrNull { it.isPlaying } ?: musicTracks.firstOrNull { it.isCurrentBattleBgm } ?: musicTracks.firstOrNull()

    var selectedSagaId by remember { mutableStateOf("saga_broly") }
    val currentSaga = sagas.firstOrNull { it.id == selectedSagaId } ?: sagas.first()

    var selectedMissionId by remember { mutableStateOf(currentSaga.missions.firstOrNull()?.id ?: "") }
    val currentMission = currentSaga.missions.firstOrNull { it.id == selectedMissionId } ?: currentSaga.missions.firstOrNull()

    var showNodeEditor by remember { mutableStateOf(false) }
    var showMusicSelector by remember { mutableStateOf(false) }

    // When current saga changes, select first mission
    LaunchedEffect(currentSaga.id) {
        if (currentSaga.missions.isNotEmpty() && currentSaga.missions.none { it.id == selectedMissionId }) {
            selectedMissionId = currentSaga.missions.first().id
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(SurfaceDark)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "MODO HISTORIA",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                fontStyle = FontStyle.Italic,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Dragon Walker · Sagas DBZ, Broly y DBS",
                                fontSize = 11.sp,
                                color = DragonAmber
                            )
                        }
                    }

                    // Button to open Node Editor
                    Button(
                        onClick = { showNodeEditor = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DragonAmber,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.AccountTree, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Editor Nodos", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // ==========================================
            // REPRODUCTOR DE MÚSICA DE FONDO (CAMBIAR MÚSICA)
            // ==========================================
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, CardBorderDark, RoundedCornerShape(14.dp)),
                    color = Color(0xFF111827)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = "Música BGM",
                                tint = DragonAmber,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = currentMusic?.title ?: "Cha-La Head-Cha-La",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                                Text(
                                    text = if (currentMusic?.isPlaying == true) "▶ Reproduciendo en vivo" else "OST Modo Historia",
                                    fontSize = 9.sp,
                                    color = if (currentMusic?.isPlaying == true) SuccessGreen else TextSecondary
                                )
                            }
                        }

                        // Play/Pause, Next, Change
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    currentMusic?.let { viewModel.playMusicTrack(it.id) }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (currentMusic?.isPlaying == true) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pausa",
                                    tint = DragonAmber,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(
                                onClick = { viewModel.nextMusicTrack() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipNext,
                                    contentDescription = "Siguiente canción",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            OutlinedButton(
                                onClick = { showMusicSelector = true },
                                modifier = Modifier.height(28.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Cambiar", fontSize = 10.sp, color = KiCyan)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // ==========================================
            // CASILLA DESTACADA DE LA SAGA BROLY
            // ==========================================
            item {
                val brolySaga = sagas.firstOrNull { it.id == "saga_broly" }
                if (brolySaga != null) {
                    val isBrolySelected = selectedSagaId == "saga_broly"
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .border(
                                2.dp,
                                if (isBrolySelected) Color(0xFF10B981) else Color(0xFF059669).copy(alpha = 0.5f),
                                RoundedCornerShape(18.dp)
                            )
                            .clickable {
                                selectedSagaId = "saga_broly"
                                selectedMissionId = brolySaga.missions.first().id
                            }
                            .testTag("casilla_saga_broly"),
                        color = if (isBrolySelected) Color(0xFF06331C) else Color(0xFF042013)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = Color(0xFF10B981),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "CASILLA SAGA BROLY",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = Color(0xFFEF4444),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "SUPER SAIYAJIN LEGENDARIO",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                if (isBrolySelected) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Activa", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "SAGA DE BROLY: EL GUERRERO LEGENDARIO",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                fontStyle = FontStyle.Italic,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "Nuevo Planeta Vegeta & Tierra Ártica DBS · 3 Capítulos con Batallas de Jefe LSSJ",
                                fontSize = 11.sp,
                                color = Color(0xFF6EE7B7)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Enfréntate al incontrolable poder del Super Saiyajin Legendario con Goku, Vegeta, Piccolo y la Fusión Definitiva Gogeta Blue.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 15.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    brolySaga.missions.forEach { mission ->
                                        Surface(
                                            color = Color(0xFF0F4426),
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
                                        ) {
                                            Text(
                                                text = "Cap ${mission.chapterNumber}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }

                                Button(
                                    onClick = {
                                        selectedSagaId = "saga_broly"
                                        selectedMissionId = brolySaga.missions.first().id
                                        Toast.makeText(context, "¡Casilla de Saga Broly activada!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF10B981),
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text(
                                        text = if (isBrolySelected) "Explorar Capítulos" else "Seleccionar Casilla",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                }
            }

            // ==========================================
            // SELECTOR DE TODAS LAS CASILLAS DE SAGAS
            // ==========================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TODAS LAS CASILLAS DE SAGAS:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )

                    Text(
                        text = "${sagas.size} Sagas Disponibles",
                        fontSize = 10.sp,
                        color = DragonAmber
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(sagas, key = { it.id }) { saga ->
                        val isSelected = saga.id == selectedSagaId
                        val isBroly = saga.id == "saga_broly"
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) (if (isBroly) Color(0xFF10B981) else Color(saga.colorHex)) else CardBorderDark,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    selectedSagaId = saga.id
                                    if (saga.missions.isNotEmpty()) {
                                        selectedMissionId = saga.missions.first().id
                                    }
                                },
                            color = if (isSelected) (if (isBroly) Color(0xFF0A331D) else Color(0xFF1E283F)) else SurfaceDark
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(saga.colorHex))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isBroly) "★ ${saga.title}" else saga.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Saga Description Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                    color = SurfaceDark
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = null,
                                    tint = Color(currentSaga.colorHex),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = currentSaga.period,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = DragonAmber
                                )
                            }

                            // Node Editor Shortcut
                            OutlinedButton(
                                onClick = { showNodeEditor = true },
                                modifier = Modifier.height(26.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.AccountTree, contentDescription = null, tint = DragonAmber, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Nodos", fontSize = 10.sp, color = DragonAmber)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentSaga.description,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ==========================================
            // CAPÍTULOS Y MISIONES DE LA SAGA ACTIVA
            // ==========================================
            item {
                Text(
                    text = "CAPÍTULOS Y MISIONES DE ${currentSaga.title.uppercase()}:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(currentSaga.missions, key = { it.id }) { mission ->
                val isSelected = mission.id == currentMission?.id
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            1.dp,
                            if (isSelected) (if (currentSaga.id == "saga_broly") Color(0xFF10B981) else DragonAmber) else CardBorderDark,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { selectedMissionId = mission.id },
                    color = if (isSelected) (if (currentSaga.id == "saga_broly") Color(0xFF092918) else Color(0xFF1B253D)) else SurfaceDark
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Chapter Number Badge
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) {
                                        if (currentSaga.id == "saga_broly") Color(0xFF10B981) else DragonAmber
                                    } else SurfaceVariantDark
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${mission.chapterNumber}",
                                color = if (isSelected) Color.Black else (if (currentSaga.id == "saga_broly") Color(0xFF10B981) else DragonAmber),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = mission.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                if (mission.isBossBattle) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = Color(0xFFEF4444),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "JEFE",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "${mission.p1Fighter.name} vs ${mission.cpu1Fighter.name} · ${mission.stageName}",
                                fontSize = 11.sp,
                                color = if (currentSaga.id == "saga_broly") Color(0xFF6EE7B7) else DragonAmber
                            )
                        }

                        // Rank Badge
                        Surface(
                            color = Color(0xFF23304E),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Rango ${mission.rankGrade}",
                                color = SuccessGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ==========================================
            // DETALLES DEL COMBATE Y BOTÓN PARA JUGAR
            // ==========================================
            if (currentMission != null) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .border(
                                1.dp,
                                if (currentSaga.id == "saga_broly") Color(0xFF10B981) else DragonAmber,
                                RoundedCornerShape(18.dp)
                            ),
                        color = Color(0xFF131C30)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "DETALLES DEL COMBATE",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (currentSaga.id == "saga_broly") Color(0xFF10B981) else DragonAmber,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = currentMission.stageName,
                                    fontSize = 11.sp,
                                    color = KiCyan
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 2v2 Matchup Visualization
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Player Team
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text(
                                        text = "EQUIPO JUGADOR",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "1P: ${currentMission.p1Fighter.name}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    if (currentMission.p2Fighter != null) {
                                        Text(
                                            text = "TAG: ${currentMission.p2Fighter?.name}",
                                            fontSize = 11.sp,
                                            color = DragonAmber
                                        )
                                    } else {
                                        Text(text = "En solitario", fontSize = 11.sp, color = TextSecondary)
                                    }
                                }

                                Text(
                                    text = "VS",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFEF4444)
                                )

                                // CPU Team
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "OPONENTES CPU",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "CPU 1: ${currentMission.cpu1Fighter.name}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    if (currentMission.cpu2Fighter != null) {
                                        Text(
                                            text = "CPU 2: ${currentMission.cpu2Fighter?.name}",
                                            fontSize = 11.sp,
                                            color = DragonAmber
                                        )
                                    } else {
                                        Text(text = "En solitario", fontSize = 11.sp, color = TextSecondary)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Objective
                            Surface(
                                color = SurfaceDark,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Condición de Victoria:",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = currentMission.objectiveDescription,
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        lineHeight = 15.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Cutscene Dialogue Box
                            Text(
                                text = "DIÁLOGO DE LA CINEMÁTICA:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = DragonAmber,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            currentMission.dialogLines.forEach { line ->
                                Surface(
                                    color = SurfaceVariantDark,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${line.speaker}:",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = DragonAmber,
                                            modifier = Modifier.width(65.dp)
                                        )
                                        Text(
                                            text = "\"${line.text}\"",
                                            fontSize = 11.sp,
                                            color = Color.White,
                                            fontStyle = FontStyle.Italic
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Reward Tag
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Stars, contentDescription = null, tint = DragonAmber, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Recompensa: ${currentMission.rewardRewardStr}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Launch Battle Button
                            Button(
                                onClick = {
                                    viewModel.startStoryMissionInBattle(currentMission)
                                    Toast.makeText(context, "¡Iniciando combate: ${currentMission.title}!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("launch_story_mission_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentSaga.id == "saga_broly") Color(0xFF10B981) else DragonAmber,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(25.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "JUGAR MISIÓN EN TAG TEAM",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal to change music
    if (showMusicSelector) {
        AlertDialog(
            onDismissRequest = { showMusicSelector = false },
            title = {
                Text(
                    text = "CAMBIAR MÚSICA (BGM / OST)",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = DragonAmber
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Selecciona la banda sonora para el combate y la historia:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    musicTracks.forEach { track ->
                        val isSelected = track.id == currentMusic?.id
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFF1E283F) else SurfaceDark,
                            border = BorderStroke(1.dp, if (isSelected) DragonAmber else CardBorderDark),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.playMusicTrack(track.id)
                                    viewModel.setAsBattleBgm(track.id)
                                    showMusicSelector = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = track.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isSelected) Color.White else TextSecondary
                                    )
                                    Text(
                                        text = "${track.singerAnime} · ${track.durationStr}",
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                }
                                Icon(
                                    imageVector = if (track.isPlaying) Icons.Default.VolumeUp else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = if (isSelected) DragonAmber else TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMusicSelector = false }) {
                    Text("Cerrar", color = DragonAmber)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Node Editor Dialog
    if (showNodeEditor) {
        StoryNodeEditorDialog(
            saga = currentSaga,
            viewModel = viewModel,
            onDismiss = { showNodeEditor = false }
        )
    }
}
