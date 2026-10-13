package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ControlLayoutType
import com.example.ui.theme.*
import com.example.viewmodel.DbzTagTeamViewModel

@Composable
fun ControlsScreen(
    viewModel: DbzTagTeamViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentLayout by viewModel.controlLayout.collectAsState()
    val autoMenuFight by viewModel.autoMenuFight.collectAsState()
    val opacity by viewModel.controlOpacity.collectAsState()
    val scale by viewModel.controlScale.collectAsState()
    val hapticEnabled by viewModel.hapticFeedback.collectAsState()

    var showLayoutEditorDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "CONTROLS",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            color = Color.White,
            letterSpacing = 1.sp
        )
        Text(
            text = "The on-screen controller: when it shows, its look, its layout and macros",
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 2.dp, bottom = 18.dp)
        )

        // Controller Diagram Canvas Preview (matching Screenshot 7)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, CardBorderDark, RoundedCornerShape(20.dp)),
            color = SurfaceDark
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val strokeLine = Stroke(width = 2.5f)
                    val outlineColor = Color(0xFF64748B).copy(alpha = 0.6f)

                    // Left Stick Outer Ring
                    drawCircle(
                        color = outlineColor,
                        radius = 42.dp.toPx(),
                        center = Offset(w * 0.18f, h * 0.62f),
                        style = strokeLine
                    )
                    // D-Pad Cross
                    drawRoundRect(
                        color = outlineColor,
                        topLeft = Offset(w * 0.12f, h * 0.22f),
                        size = Size(28.dp.toPx(), 28.dp.toPx()),
                        style = strokeLine
                    )

                    // L1 / L2
                    drawRoundRect(
                        color = outlineColor,
                        topLeft = Offset(w * 0.08f, h * 0.08f),
                        size = Size(46.dp.toPx(), 24.dp.toPx()),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f),
                        style = strokeLine
                    )

                    // R1 / R2
                    drawRoundRect(
                        color = outlineColor,
                        topLeft = Offset(w * 0.72f, h * 0.08f),
                        size = Size(50.dp.toPx(), 24.dp.toPx()),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f),
                        style = strokeLine
                    )

                    // Face buttons (Triangle, Circle, Cross, Square)
                    val faceCenter = Offset(w * 0.82f, h * 0.58f)
                    // Triangle (top)
                    val triTop = Offset(faceCenter.x, faceCenter.y - 38.dp.toPx())
                    val triPath = Path().apply {
                        moveTo(triTop.x, triTop.y - 12f)
                        lineTo(triTop.x - 14f, triTop.y + 12f)
                        lineTo(triTop.x + 14f, triTop.y + 12f)
                        close()
                    }
                    drawPath(triPath, Color(0xFF34D399), style = strokeLine)

                    // Square (left)
                    drawRect(
                        color = Color(0xFFF472B6),
                        topLeft = Offset(faceCenter.x - 42.dp.toPx(), faceCenter.y - 12f),
                        size = Size(24f, 24f),
                        style = strokeLine
                    )

                    // Circle (right)
                    drawCircle(
                        color = Color(0xFFF87171),
                        radius = 12f,
                        center = Offset(faceCenter.x + 38.dp.toPx(), faceCenter.y),
                        style = strokeLine
                    )

                    // Cross (bottom)
                    val crossCenter = Offset(faceCenter.x, faceCenter.y + 38.dp.toPx())
                    drawLine(
                        color = Color(0xFF60A5FA),
                        start = Offset(crossCenter.x - 10f, crossCenter.y - 10f),
                        end = Offset(crossCenter.x + 10f, crossCenter.y + 10f),
                        strokeWidth = 3f
                    )
                    drawLine(
                        color = Color(0xFF60A5FA),
                        start = Offset(crossCenter.x + 10f, crossCenter.y - 10f),
                        end = Offset(crossCenter.x - 10f, crossCenter.y + 10f),
                        strokeWidth = 3f
                    )

                    // Select / Start
                    drawRoundRect(
                        color = outlineColor,
                        topLeft = Offset(w * 0.38f, h * 0.82f),
                        size = Size(38.dp.toPx(), 16.dp.toPx()),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f),
                        style = strokeLine
                    )
                    drawRoundRect(
                        color = outlineColor,
                        topLeft = Offset(w * 0.52f, h * 0.82f),
                        size = Size(38.dp.toPx(), 16.dp.toPx()),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f),
                        style = strokeLine
                    )
                }

                // Labels over preview
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("L1 / L2", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("R1 / R2", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Edit Layout Button (Blue button matching screenshot)
        Button(
            onClick = {
                showLayoutEditorDialog = true
                viewModel.audioHaptics.playHaptic(30, 150)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("edit_layout_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2563EB),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit layout",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Edit layout",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // On-screen controls Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, CardBorderDark, RoundedCornerShape(20.dp)),
            color = SurfaceDark
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E283F)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Gamepad,
                            contentDescription = "Controls",
                            tint = DragonAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "On-screen controls",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Layout",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Layout Selector Buttons (Game-specific vs Classic)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LayoutOptionButton(
                        text = "Game-specific",
                        isSelected = currentLayout == ControlLayoutType.GAME_SPECIFIC,
                        onClick = { viewModel.setControlLayout(ControlLayoutType.GAME_SPECIFIC) },
                        modifier = Modifier.weight(1f)
                    )
                    LayoutOptionButton(
                        text = "Classic",
                        isSelected = currentLayout == ControlLayoutType.CLASSIC,
                        onClick = { viewModel.setControlLayout(ControlLayoutType.CLASSIC) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Game-specific: buttons for the game's own actions (target, Ki, melee, guard, dash...) in a thumb-friendly cluster, with a menu mode. Classic: the plain console pad (sticks, d-pad, four face buttons, shoulders). Each layout keeps its own placements.",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Automatic menu / fight switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Automatic menu / fight",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Menu buttons in the menus and fight buttons in a fight, switched by itself. The MENU / FIGHT button on the screen still changes it",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = TextSecondary
                        )
                    }

                    Switch(
                        checked = autoMenuFight,
                        onCheckedChange = { viewModel.setAutoMenuFight(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = DragonAmber,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = SurfaceVariantDark
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Opacity slider
                Text(
                    text = "Button Opacity (${(opacity * 100).toInt()}%)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Slider(
                    value = opacity,
                    onValueChange = { viewModel.setControlOpacity(it) },
                    valueRange = 0.2f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = DragonAmber,
                        activeTrackColor = DragonAmber,
                        inactiveTrackColor = CardBorderDark
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Haptic feedback toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Haptic feedback (Vibration)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "Vibrates on melee hits, Ki blasts and button taps",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Switch(
                        checked = hapticEnabled,
                        onCheckedChange = { viewModel.setHapticFeedback(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = DragonAmber,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = SurfaceVariantDark
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    // Interactive Layout Editor Modal
    if (showLayoutEditorDialog) {
        AlertDialog(
            onDismissRequest = { showLayoutEditorDialog = false },
            title = {
                Text(
                    text = "On-Screen Controller Test Arena",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Touch and test controls layout. Haptics and sound response active:",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Interactive touch test buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ControllerTestButton("L1 (Charge Ki)") {
                            viewModel.audioHaptics.playKiChargeSound()
                            viewModel.audioHaptics.playHaptic(40, 180)
                        }
                        ControllerTestButton("R1 (Lock Target)") {
                            viewModel.audioHaptics.playHaptic(25, 120)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ControllerTestButton("□ Melee") {
                            viewModel.audioHaptics.playMeleeHitSound()
                            viewModel.audioHaptics.playHaptic(35, 180)
                        }
                        ControllerTestButton("△ Ki Blast") {
                            viewModel.audioHaptics.playKiBlastSound()
                            viewModel.audioHaptics.playHaptic(30, 160)
                        }
                        ControllerTestButton("○ Guard") {
                            viewModel.audioHaptics.playGuardSound()
                            viewModel.audioHaptics.playHaptic(20, 100)
                        }
                        ControllerTestButton("✕ Dash") {
                            viewModel.audioHaptics.playHaptic(30, 140)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ControllerTestButton("TAG SWITCH (Select)") {
                        viewModel.audioHaptics.playTagSwitchSound()
                        viewModel.audioHaptics.playHaptic(60, 220)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLayoutEditorDialog = false
                        Toast.makeText(context, "Layout calibrated successfully!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DragonAmber)
                ) {
                    Text("Save & Exit", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun LayoutOptionButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF2563EB) else SurfaceVariantDark,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(22.dp)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun ControllerTestButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceVariantDark,
            contentColor = DragonAmber
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderDark),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
