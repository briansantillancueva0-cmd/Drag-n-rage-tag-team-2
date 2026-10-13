package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ControlLayoutType
import com.example.ui.theme.*
import com.example.viewmodel.DbzTagTeamViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BattleArenaScreen(
    viewModel: DbzTagTeamViewModel,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val battleState by viewModel.battleState.collectAsState()
    val showFps by viewModel.showFps.collectAsState()
    val controlLayout by viewModel.controlLayout.collectAsState()
    val opacity by viewModel.controlOpacity.collectAsState()
    val miniMapConfig by viewModel.miniMapConfig.collectAsState()

    val p1Active = battleState.p1Team[battleState.p1ActiveIndex]
    val p1Partner = battleState.p1Team[if (battleState.p1ActiveIndex == 0) 1 else 0]
    val cpuActive = battleState.cpuTeam[battleState.cpuActiveIndex]
    val cpuPartner = battleState.cpuTeam[if (battleState.cpuActiveIndex == 0) 1 else 0]

    // Infinite animation for aura & energy waves
    val infiniteTransition = rememberInfiniteTransition(label = "aura")
    val auraPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
    ) {
        // ==========================================
        // 1. FIGHTING ARENA CANVAS
        // ==========================================
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Wasteland sky gradient
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B),
                        Color(0xFF334155),
                        Color(0xFF475569)
                    ),
                    startY = 0f,
                    endY = h * 0.65f
                ),
                topLeft = Offset.Zero,
                size = Size(w, h * 0.65f)
            )

            // Rocky Ground Arena
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF854D0E),
                        Color(0xFF713F12),
                        Color(0xFF451A03),
                        Color(0xFF1C1917)
                    ),
                    startY = h * 0.65f,
                    endY = h
                ),
                topLeft = Offset(0f, h * 0.65f),
                size = Size(w, h * 0.35f)
            )

            // Arena grid lines (3D depth)
            for (i in 1..8) {
                val lineY = h * 0.65f + (h * 0.35f * (i / 8f))
                drawLine(
                    color = Color(0xFFA16207).copy(alpha = 0.35f),
                    start = Offset(0f, lineY),
                    end = Offset(w, lineY),
                    strokeWidth = 2f
                )
            }

            // P1 Fighter (Goku / Player on left)
            val p1Pos = Offset(w * 0.28f, h * 0.58f)
            val p1Color = Color(p1Active.auraColorHex)

            // Aura effect around P1
            val auraRadius = (44.dp.toPx()) * (if (battleState.isChargingKi) auraPulse * 1.35f else auraPulse)
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        p1Color.copy(alpha = if (battleState.isChargingKi) 0.85f else 0.45f),
                        p1Color.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = p1Pos,
                    radius = auraRadius
                ),
                center = p1Pos,
                radius = auraRadius
            )

            // Sparking electric lightning
            if (battleState.sparkingModeActive) {
                for (angle in listOf(30.0, 110.0, 220.0, 310.0)) {
                    val rad = Math.toRadians(angle)
                    val endX = p1Pos.x + (cos(rad) * auraRadius * 0.9).toFloat()
                    val endY = p1Pos.y + (sin(rad) * auraRadius * 0.9).toFloat()
                    drawLine(
                        color = Color(0xFF67E8F9),
                        start = p1Pos,
                        end = Offset(endX, endY),
                        strokeWidth = 4f
                    )
                }
            }

            // P1 Body silhouette representation
            drawCircle(
                color = Color(0xFFEA580C),
                radius = 24.dp.toPx(),
                center = p1Pos
            )
            // Golden Hair / Form
            drawCircle(
                color = p1Color,
                radius = 16.dp.toPx(),
                center = Offset(p1Pos.x, p1Pos.y - 18.dp.toPx())
            )

            // CPU Opponent (Frieza / Cell on right)
            val cpuPos = Offset(w * 0.72f, h * 0.58f)
            val cpuColor = Color(cpuActive.auraColorHex)

            // CPU Aura
            val cpuAuraRadius = 40.dp.toPx() * auraPulse
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        cpuColor.copy(alpha = 0.5f),
                        Color.Transparent
                    ),
                    center = cpuPos,
                    radius = cpuAuraRadius
                ),
                center = cpuPos,
                radius = cpuAuraRadius
            )

            // CPU Body silhouette
            drawCircle(
                color = cpuColor,
                radius = 24.dp.toPx(),
                center = cpuPos
            )

            // Guarding Shield Sphere
            if (battleState.isGuarding) {
                drawCircle(
                    color = Color(0xFF38BDF8).copy(alpha = 0.5f),
                    radius = 36.dp.toPx(),
                    center = p1Pos,
                    style = Stroke(width = 6f)
                )
            }

            // Super Attack Beam Animation (Kamehameha blast firing across screen)
            if (battleState.isSuperAttackExecuting) {
                val beamProgress = battleState.superAttackProgress
                val startX = p1Pos.x + 24.dp.toPx()
                val targetX = cpuPos.x
                val currentBeamX = startX + (targetX - startX) * beamProgress

                // Energy Beam
                drawLine(
                    color = Color(0xFF67E8F9),
                    start = Offset(startX, p1Pos.y),
                    end = Offset(currentBeamX, cpuPos.y),
                    strokeWidth = 28f * auraPulse
                )
                drawLine(
                    color = Color.White,
                    start = Offset(startX, p1Pos.y),
                    end = Offset(currentBeamX, cpuPos.y),
                    strokeWidth = 12f
                )

                // Blast Impact Sphere
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color.White, Color(0xFF38BDF8), Color.Transparent),
                        center = Offset(currentBeamX, cpuPos.y),
                        radius = 40.dp.toPx() * auraPulse
                    ),
                    center = Offset(currentBeamX, cpuPos.y),
                    radius = 40.dp.toPx() * auraPulse
                )
            }
        }

        // ==========================================
        // 2. TOP HUD: 2v2 HEALTH & KI BARS
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 24.dp)
        ) {
            // Top Bar: Pause, FPS, Timer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.pauseBattle() },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SurfaceDark)
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Pause",
                        tint = DragonAmber,
                        modifier = Modifier.size(22.dp)
                    )
                }

                if (showFps) {
                    Text(
                        text = "60.0 FPS · 1.25x (FSR)",
                        color = SuccessGreen,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (battleState.sparkingModeActive) {
                    Surface(
                        color = DangerRed,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "SPARKING! ${battleState.sparkingTimerSeconds.toInt()}s",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2v2 Fighters HUD Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Player 1 Team HUD (Left)
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Active P1 portrait
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(DragonAmber),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = p1Active.name.take(2).uppercase(),
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Column {
                            Text(
                                text = "${p1Active.name} (${p1Active.title})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            // Partner Sub-Tag slot
                            Text(
                                text = "Tag: ${p1Partner.name} (Ready)",
                                fontSize = 11.sp,
                                color = if (battleState.tagCooldownSeconds <= 0f) SuccessGreen else TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // P1 HP Bar
                    LinearProgressIndicator(
                        progress = { (p1Active.currentHp / p1Active.maxHp).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = if (p1Active.currentHp > 400f) SuccessGreen else DangerRed,
                        trackColor = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // P1 Ki Gauge
                    LinearProgressIndicator(
                        progress = { (p1Active.currentKi / p1Active.maxKi).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = DragonAmber,
                        trackColor = Color(0xFF1E293B)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // CPU Team HUD (Right)
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${cpuActive.name} (${cpuActive.title})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Tag: ${cpuPartner.name}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(cpuActive.auraColorHex)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cpuActive.name.take(2).uppercase(),
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // CPU HP Bar
                    LinearProgressIndicator(
                        progress = { (cpuActive.currentHp / cpuActive.maxHp).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = if (cpuActive.currentHp > 350f) DangerRed else Color(0xFF991B1B),
                        trackColor = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // CPU Ki Gauge
                    LinearProgressIndicator(
                        progress = { (cpuActive.currentKi / cpuActive.maxKi).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = KiCyan,
                        trackColor = Color(0xFF1E293B)
                    )
                }
            }

            // Combat Announcement Log
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = battleState.lastCombatLog,
                    color = Color(0xFFFDE047),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                )
            }
        }

        // ==========================================
        // 3. ON-SCREEN CONTROLLER TOUCH OVERLAY
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Shoulders Row: L1 (Charge Ki) and R1 (Lock target)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // L1 / L2 Shoulder (Hold to charge Ki)
                    Surface(
                        modifier = Modifier
                            .width(88.dp)
                            .height(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.5.dp, DragonAmber.copy(alpha = opacity), RoundedCornerShape(14.dp))
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        viewModel.setChargeKi(true)
                                        tryAwaitRelease()
                                        viewModel.setChargeKi(false)
                                    }
                                )
                            },
                        color = SurfaceDark.copy(alpha = opacity)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "L1 / Ki Aura",
                                color = DragonAmber,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // R1 Shoulder (Target Lock / Ascend)
                    Surface(
                        modifier = Modifier
                            .width(88.dp)
                            .height(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.5.dp, Color(0xFF60A5FA).copy(alpha = opacity), RoundedCornerShape(14.dp))
                            .clickable {
                                viewModel.audioHaptics.playHaptic(25, 120)
                            },
                        color = SurfaceDark.copy(alpha = opacity)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "R1 / Lock",
                                color = Color(0xFF60A5FA),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Main Controls Row: Left D-Pad/Stick, Center TAG SWITCH, Right Action Face Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Left: Analog / D-Pad cluster
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .border(2.dp, CardBorderDark.copy(alpha = opacity), CircleShape)
                            .background(SurfaceDark.copy(alpha = opacity * 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Directional buttons inside
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxSize().padding(8.dp)
                        ) {
                            IconButton(
                                onClick = { viewModel.audioHaptics.playHaptic(20, 100) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = Color.White)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(
                                    onClick = { viewModel.audioHaptics.playHaptic(20, 100) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Left", tint = Color.White)
                                }
                                // Center Analog thumb
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(DragonAmber.copy(alpha = opacity)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("PAD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                                IconButton(
                                    onClick = { viewModel.audioHaptics.playHaptic(20, 100) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Right", tint = Color.White)
                                }
                            }
                            IconButton(
                                onClick = { viewModel.audioHaptics.playHaptic(20, 100) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = Color.White)
                            }
                        }
                    }

                    // Center: TAG SWITCH & Super Button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Tag Switch Button
                        Button(
                            onClick = { viewModel.executeTagSwitch() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (battleState.tagCooldownSeconds <= 0f) SuccessGreen else SurfaceVariantDark,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("tag_switch_button"),
                            contentPadding = PaddingValues(horizontal = 14.dp)
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = "Tag", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (battleState.tagCooldownSeconds <= 0f) "TAG SWITCH" else "${battleState.tagCooldownSeconds.toInt()}s",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        // ULTIMATE / SUPER ATTACK BUTTON
                        Button(
                            onClick = { viewModel.executeSuperAttack() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DragonAmber,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(42.dp)
                                .testTag("super_attack_button"),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text(
                                text = "SUPER KAMEHAMEHA",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Right: Face Action Buttons (Square = Melee, Triangle = Ki Blast, Circle = Guard, Cross = Dash)
                    Box(
                        modifier = Modifier
                            .size(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Triangle: Ki Blast (Top)
                        ActionButton(
                            label = "△ Ki",
                            color = Color(0xFF34D399),
                            modifier = Modifier.align(Alignment.TopCenter),
                            onClick = { viewModel.executeKiBlast() }
                        )

                        // Square: Melee Combo (Left)
                        ActionButton(
                            label = "□ Melee",
                            color = Color(0xFFF472B6),
                            modifier = Modifier.align(Alignment.CenterStart),
                            onClick = { viewModel.executeMeleeAttack() }
                        )

                        // Circle: Guard (Right)
                        ActionButton(
                            label = "○ Guard",
                            color = Color(0xFFF87171),
                            modifier = Modifier.align(Alignment.CenterEnd),
                            onPress = { isPressed -> viewModel.setGuarding(isPressed) }
                        )

                        // Cross: Dash (Bottom)
                        ActionButton(
                            label = "✕ Dash",
                            color = Color(0xFF60A5FA),
                            modifier = Modifier.align(Alignment.BottomCenter),
                            onClick = {
                                viewModel.audioHaptics.playHaptic(30, 160)
                            }
                        )
                    }
                }
            }
        }

        // ==========================================
        // 4. PAUSE MENU DIALOG
        // ==========================================
        if (battleState.isPaused) {
            AlertDialog(
                onDismissRequest = { viewModel.resumeBattle() },
                title = {
                    Text(
                        text = "PAUSE - TENKAICHI TAG TEAM",
                        fontWeight = FontWeight.Black,
                        color = DragonAmber,
                        fontStyle = FontStyle.Italic
                    )
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { viewModel.resumeBattle() },
                            colors = ButtonDefaults.buttonColors(containerColor = DragonAmber),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Resume Fight", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = {
                                viewModel.createSaveState(1, "In-Game Quick Save")
                                viewModel.resumeBattle()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DragonAmber)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = DragonAmber, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Quick Save to Room (Slot 1)", color = DragonAmber, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = {
                                viewModel.setUnlockEverything(true)
                                viewModel.resumeBattle()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderDark)
                        ) {
                            Text("Apply In-Game CWCheats (God Mode)", color = DragonGold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = { viewModel.startBattle() },
                            modifier = Modifier.fillMaxWidth(),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderDark)
                        ) {
                            Text("Restart Match", color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = {
                                viewModel.exitBattle()
                                onExit()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed)
                        ) {
                            Text("Exit to Launcher", color = DangerRed)
                        }
                    }
                },
                confirmButton = {},
                containerColor = SurfaceDark
            )
        }
    }
}

@Composable
fun ActionButton(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onPress: ((Boolean) -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .size(46.dp)
            .clip(CircleShape)
            .border(2.dp, color, CircleShape)
            .then(
                if (onPress != null) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                onPress(true)
                                tryAwaitRelease()
                                onPress(false)
                            }
                        )
                    }
                } else {
                    Modifier.clickable { onClick?.invoke() }
                }
            ),
        color = SurfaceDark.copy(alpha = 0.85f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
