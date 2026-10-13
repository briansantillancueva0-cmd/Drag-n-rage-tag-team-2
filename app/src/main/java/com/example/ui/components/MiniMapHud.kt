package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MiniMapConfig
import com.example.model.MiniMapStyle
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MiniMapHud(
    config: MiniMapConfig,
    onStyleChange: (MiniMapStyle) -> Unit,
    onToggle: (Boolean) -> Unit,
    onZoomChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    p1OffsetFraction: Offset = Offset(-0.35f, 0.15f),
    cpuOffsetFraction: Offset = Offset(0.35f, -0.15f),
    showControls: Boolean = true
) {
    if (!config.isEnabled) {
        if (showControls) {
            IconButton(
                onClick = { onToggle(true) },
                modifier = modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SurfaceDark.copy(alpha = 0.85f))
                    .border(1.dp, CardBorderDark, CircleShape)
            ) {
                Icon(Icons.Default.Explore, contentDescription = "Activar Mini Mapa", tint = DragonAmber, modifier = Modifier.size(20.dp))
            }
        }
        return
    }

    var showStyleDialog by remember { mutableStateOf(false) }

    // Continuous sweep animation for radar line
    val infiniteTransition = rememberInfiniteTransition(label = "radarSweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    val sizeDp = config.sizeDp.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(sizeDp)
                .clip(CircleShape)
                .background(
                    when (config.style) {
                        MiniMapStyle.DRAGON_RADAR -> Color(0xFF042F1A).copy(alpha = 0.9f)
                        MiniMapStyle.SCOUTER_BLUE -> Color(0xFF082F49).copy(alpha = 0.9f)
                        MiniMapStyle.POWER_TOURNAMENT -> Color(0xFF2E1065).copy(alpha = 0.9f)
                        MiniMapStyle.TACTICAL_TAG -> Color(0xFF1C1917).copy(alpha = 0.9f)
                    }
                )
                .border(2.dp, Color(config.style.colorHex), CircleShape)
                .clickable { showStyleDialog = true }
                .testTag("mini_map_canvas")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.width / 2f - 4f

                val primaryColor = Color(config.style.colorHex)

                // 1. Concentric distance rings
                drawCircle(
                    color = primaryColor.copy(alpha = 0.25f),
                    radius = radius * 0.33f,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
                drawCircle(
                    color = primaryColor.copy(alpha = 0.25f),
                    radius = radius * 0.66f,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
                drawCircle(
                    color = primaryColor.copy(alpha = 0.4f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // 2. Crosshairs
                drawLine(
                    color = primaryColor.copy(alpha = 0.3f),
                    start = Offset(center.x, center.y - radius),
                    end = Offset(center.x, center.y + radius),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = primaryColor.copy(alpha = 0.3f),
                    start = Offset(center.x - radius, center.y),
                    end = Offset(center.x + radius, center.y),
                    strokeWidth = 1.dp.toPx()
                )

                // 3. Radar Sweep Line & Cone
                val rad = Math.toRadians(sweepAngle.toDouble())
                val sweepX = center.x + (radius * cos(rad)).toFloat()
                val sweepY = center.y + (radius * sin(rad)).toFloat()
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(primaryColor.copy(alpha = 0.1f), primaryColor.copy(alpha = 0.8f)),
                        start = center,
                        end = Offset(sweepX, sweepY)
                    ),
                    start = center,
                    end = Offset(sweepX, sweepY),
                    strokeWidth = 2.dp.toPx()
                )

                // 4. Style-Specific Features
                when (config.style) {
                    MiniMapStyle.DRAGON_RADAR -> {
                        // 4-star Dragon Ball in center
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(Color(0xFFFBBF24), Color(0xFFD97706)),
                                center = center,
                                radius = 7.dp.toPx()
                            ),
                            center = center,
                            radius = 5.dp.toPx()
                        )
                        // Second Dragon ball blip
                        val dbOffset = Offset(center.x + radius * 0.45f, center.y - radius * 0.3f)
                        drawCircle(
                            color = Color(0xFFF59E0B),
                            radius = 4.dp.toPx(),
                            center = dbOffset
                        )
                    }
                    MiniMapStyle.SCOUTER_BLUE -> {
                        // Scouter HUD arc corners
                        drawArc(
                            color = KiCyan,
                            startAngle = -45f,
                            sweepAngle = 90f,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
                    MiniMapStyle.POWER_TOURNAMENT -> {
                        // Kachi Katchin circular fighting platform
                        drawCircle(
                            color = Color(0xFFA855F7).copy(alpha = 0.3f),
                            radius = radius * 0.85f,
                            center = center,
                            style = Stroke(width = 2.dp.toPx())
                        )
                        // Center pillar
                        drawRect(
                            color = Color.White.copy(alpha = 0.7f),
                            topLeft = Offset(center.x - 3.dp.toPx(), center.y - 3.dp.toPx()),
                            size = Size(6.dp.toPx(), 6.dp.toPx())
                        )
                    }
                    MiniMapStyle.TACTICAL_TAG -> {
                        // Tactical zone divisions
                        drawLine(
                            color = DragonAmber.copy(alpha = 0.25f),
                            start = Offset(center.x - radius * 0.7f, center.y - radius * 0.7f),
                            end = Offset(center.x + radius * 0.7f, center.y + radius * 0.7f),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }

                // 5. Fighters Positions
                val p1Pos = Offset(
                    center.x + (radius * p1OffsetFraction.x * config.zoomLevel).coerceIn(-radius * 0.85f, radius * 0.85f),
                    center.y + (radius * p1OffsetFraction.y * config.zoomLevel).coerceIn(-radius * 0.85f, radius * 0.85f)
                )
                val cpuPos = Offset(
                    center.x + (radius * cpuOffsetFraction.x * config.zoomLevel).coerceIn(-radius * 0.85f, radius * 0.85f),
                    center.y + (radius * cpuOffsetFraction.y * config.zoomLevel).coerceIn(-radius * 0.85f, radius * 0.85f)
                )

                // P1 Blip (Green/Cyan triangle/circle)
                drawCircle(
                    color = SuccessGreen,
                    radius = 5.dp.toPx(),
                    center = p1Pos
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.dp.toPx(),
                    center = p1Pos
                )

                // P1 Tag Partner Blip (Blue)
                val partnerPos = Offset(p1Pos.x - 12.dp.toPx(), p1Pos.y + 10.dp.toPx())
                drawCircle(
                    color = Color(0xFF38BDF8),
                    radius = 3.5.dp.toPx(),
                    center = partnerPos
                )

                // CPU 1 Blip (Red)
                drawCircle(
                    color = DangerRed,
                    radius = 5.dp.toPx(),
                    center = cpuPos
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.dp.toPx(),
                    center = cpuPos
                )

                // CPU 2 Partner Blip (Purple)
                val cpu2Pos = Offset(cpuPos.x + 10.dp.toPx(), cpuPos.y - 12.dp.toPx())
                drawCircle(
                    color = Color(0xFFA855F7),
                    radius = 3.5.dp.toPx(),
                    center = cpu2Pos
                )
            }

            // North indicator
            if (config.showNorthIndicator) {
                Text(
                    text = "N",
                    color = Color(config.style.colorHex),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 2.dp)
                )
            }
        }

        // Mini controls bar beneath the map
        if (showControls) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Style Change button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceDark.copy(alpha = 0.85f),
                    modifier = Modifier.clickable { showStyleDialog = true }
                ) {
                    Text(
                        text = "Cambiar Radar",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = DragonAmber,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Zoom cycle button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceDark.copy(alpha = 0.85f),
                    modifier = Modifier.clickable {
                        val nextZoom = when (config.zoomLevel) {
                            1.0f -> 1.5f
                            1.5f -> 2.0f
                            else -> 1.0f
                        }
                        onZoomChange(nextZoom)
                    }
                ) {
                    Text(
                        text = "${config.zoomLevel}x",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }

    // Modal to choose Mini Map Style
    if (showStyleDialog) {
        AlertDialog(
            onDismissRequest = { showStyleDialog = false },
            title = {
                Text(
                    text = "CAMBIAR MINI MAPA (RADAR)",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = DragonAmber
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Selecciona el estilo de HUD y radar para el combate:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    MiniMapStyle.values().forEach { style ->
                        val isSelected = style == config.style
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFF1E283F) else SurfaceDark,
                            border = BorderStroke(1.dp, if (isSelected) Color(style.colorHex) else CardBorderDark),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onStyleChange(style)
                                    showStyleDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color(style.colorHex))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = style.label,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isSelected) Color.White else TextSecondary
                                    )
                                    Text(
                                        text = style.description,
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStyleDialog = false }) {
                    Text("Cerrar", color = DragonAmber)
                }
            },
            containerColor = SurfaceDark
        )
    }
}
