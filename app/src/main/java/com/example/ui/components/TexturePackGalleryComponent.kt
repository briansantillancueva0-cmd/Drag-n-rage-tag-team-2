package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TexturePack
import com.example.ui.theme.*
import com.example.viewmodel.DbzTagTeamViewModel

@Composable
fun TexturePackGalleryComponent(
    viewModel: DbzTagTeamViewModel,
    onLaunchGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val texturePacks by viewModel.texturePacks.collectAsState()
    val activeFilter by viewModel.textureCategoryFilter.collectAsState()

    val categories = listOf("All", "Characters", "Auras & Effects", "UI & HUD", "Stages")
    val filteredPacks = if (activeFilter == "All") texturePacks else texturePacks.filter { it.category == activeFilter }
    val enabledCount = texturePacks.count { it.isEnabled }

    var inspectedPack by remember { mutableStateOf<TexturePack?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(SurfaceDark)
            .border(1.dp, CardBorderDark, RoundedCornerShape(22.dp))
            .padding(18.dp)
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF282420)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Collections,
                            contentDescription = "Texture Gallery",
                            tint = DragonAmber,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Visual Mods Gallery",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$enabledCount of ${texturePacks.size} packs active · Drag slider to preview",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            // Quick Play Button with active textures
            Button(
                onClick = onLaunchGame,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DragonAmber,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .height(38.dp)
                    .testTag("launch_with_textures_button"),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("PLAY", fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { cat ->
                val isSelected = activeFilter == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setTextureCategoryFilter(cat) },
                    label = { Text(cat, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DragonAmber,
                        selectedLabelColor = Color.Black,
                        containerColor = SurfaceVariantDark,
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

        Spacer(modifier = Modifier.height(16.dp))

        // Horizontal Gallery Carousel of Texture Cards
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(end = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filteredPacks, key = { it.id }) { pack ->
                TexturePackGalleryCard(
                    pack = pack,
                    onToggle = { viewModel.toggleTexturePack(pack.id) },
                    onInspect = { inspectedPack = pack }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Global Batch Action Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { viewModel.setAllTexturePacks(true) }
            ) {
                Icon(Icons.Default.DoneAll, contentDescription = null, tint = DragonAmber, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Enable All", color = DragonAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            TextButton(
                onClick = { viewModel.setAllTexturePacks(false) }
            ) {
                Icon(Icons.Default.ClearAll, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Vanilla Reset", color = TextSecondary, fontSize = 12.sp)
            }
        }
    }

    // Inspect Texture Pack Comparison Dialog
    if (inspectedPack != null) {
        val pack = inspectedPack!!
        AlertDialog(
            onDismissRequest = { inspectedPack = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pack.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Surface(
                        color = Color(pack.accentColorHex).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = pack.resolution,
                            color = Color(pack.accentColorHex),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            },
            text = {
                Column {
                    // Large Comparison Canvas with interactive slider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, CardBorderDark, RoundedCornerShape(14.dp))
                    ) {
                        TextureComparisonCanvas(
                            pack = pack,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = pack.description,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Features list
                    Text(
                        text = "FEATURES:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DragonAmber,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    pack.features.forEach { feat ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(feat, fontSize = 12.sp, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Target Folder: PSP/TEXTURES/ULES01488 (${pack.sizeMb} MB)",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.toggleTexturePack(pack.id)
                        inspectedPack = inspectedPack?.copy(isEnabled = !pack.isEnabled)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (pack.isEnabled) DangerRed else DragonAmber
                    )
                ) {
                    Text(
                        text = if (pack.isEnabled) "Disable Mod" else "Enable Mod",
                        color = if (pack.isEnabled) Color.White else Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { inspectedPack = null }) {
                    Text("Close", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun TexturePackGalleryCard(
    pack: TexturePack,
    onToggle: () -> Unit,
    onInspect: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (pack.isEnabled) DragonAmber else CardBorderDark,
        label = "border"
    )

    Surface(
        modifier = Modifier
            .width(260.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(18.dp)),
        color = SurfaceVariantDark
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Interactive Before / After Comparison Canvas Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(125.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onInspect() }
            ) {
                TextureComparisonCanvas(
                    pack = pack,
                    modifier = Modifier.fillMaxSize()
                )

                // Resolution Badge
                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                ) {
                    Text(
                        text = pack.resolution,
                        color = Color(pack.accentColorHex),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Rating & Downloads
                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                ) {
                    Text(
                        text = pack.rating,
                        color = DragonAmber,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Card Title & Author
            Text(
                text = pack.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${pack.category} · ${pack.sizeMb} MB · ${pack.author}",
                fontSize = 11.sp,
                color = TextSecondary,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Toggle Bar at bottom of card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onInspect() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomIn,
                        contentDescription = "Inspect",
                        tint = DragonAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Compare",
                        fontSize = 11.sp,
                        color = DragonAmber,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Switch(
                    checked = pack.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = DragonAmber,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = Color(0xFF263352)
                    ),
                    modifier = Modifier.testTag("toggle_pack_${pack.id}")
                )
            }
        }
    }
}

/**
 * Interactive Canvas that renders a simulated Before (Vanilla PSP) and After (HD / Cel-Shaded)
 * split view with a draggable divider line to showcase texture enhancements!
 */
@Composable
fun TextureComparisonCanvas(
    pack: TexturePack,
    modifier: Modifier = Modifier
) {
    var splitFraction by remember { mutableStateOf(0.5f) }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    val newFraction = (change.position.x / size.width.toFloat()).coerceIn(0.1f, 0.9f)
                    splitFraction = newFraction
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val splitX = w * splitFraction

            // Left Side: Before (Vanilla 272p low-poly)
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF1E293B), Color(0xFF0F172A)),
                    startY = 0f,
                    endY = h
                ),
                topLeft = Offset.Zero,
                size = Size(splitX, h)
            )

            // Simulate pixelated low-res texture grid on left
            val pixelSize = 14f
            for (px in 0 until (splitX / pixelSize).toInt()) {
                for (py in 0 until (h / pixelSize).toInt()) {
                    val shade = (px + py) % 3
                    val c = if (shade == 0) Color(0xFF334155) else if (shade == 1) Color(0xFF475569) else Color(0xFF1E293B)
                    drawRect(
                        color = c.copy(alpha = 0.35f),
                        topLeft = Offset(px * pixelSize, py * pixelSize),
                        size = Size(pixelSize - 1f, pixelSize - 1f)
                    )
                }
            }

            // Left Goku silhouette (flat/untextured)
            val gokuLeftCenter = Offset(w * 0.25f, h * 0.5f)
            drawCircle(
                color = Color(0xFFD97706).copy(alpha = 0.7f),
                radius = 20.dp.toPx(),
                center = gokuLeftCenter
            )

            // Right Side: After (HD Remastered / Cel-Shaded vivid render)
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(Color(pack.accentColorHex).copy(alpha = 0.35f), Color(0xFF0F172A)),
                    startY = 0f,
                    endY = h
                ),
                topLeft = Offset(splitX, 0f),
                size = Size(w - splitX, h)
            )

            // Right Goku silhouette (Vibrant glowing HD with aura particles)
            val gokuRightCenter = Offset(w * 0.75f, h * 0.5f)
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(pack.accentColorHex).copy(alpha = 0.85f), Color.Transparent),
                    center = gokuRightCenter,
                    radius = 32.dp.toPx()
                ),
                center = gokuRightCenter,
                radius = 32.dp.toPx()
            )
            drawCircle(
                color = Color(0xFFFBBF24),
                radius = 22.dp.toPx(),
                center = gokuRightCenter
            )
            // Crisp ink outline
            drawCircle(
                color = Color.White,
                radius = 22.dp.toPx(),
                center = gokuRightCenter,
                style = Stroke(width = 3.5f)
            )

            // Draggable Divider Bar Line
            drawLine(
                color = Color.White,
                start = Offset(splitX, 0f),
                end = Offset(splitX, h),
                strokeWidth = 3f
            )

            // Center Handle Pill on Divider
            drawCircle(
                color = DragonAmber,
                radius = 12.dp.toPx(),
                center = Offset(splitX, h * 0.5f)
            )
            drawCircle(
                color = Color.White,
                radius = 5.dp.toPx(),
                center = Offset(splitX, h * 0.5f)
            )
        }

        // Before Label (Top Left)
        Text(
            text = "ORIGINAL",
            color = TextSecondary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 8.dp, bottom = 6.dp)
        )

        // After Label (Bottom Right)
        Text(
            text = "HD MOD",
            color = DragonAmber,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 8.dp, bottom = 6.dp)
        )
    }
}
