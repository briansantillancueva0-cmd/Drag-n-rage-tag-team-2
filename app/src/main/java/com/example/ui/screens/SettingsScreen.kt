package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AspectRatioType
import com.example.model.ScreenFilterType
import com.example.ui.theme.*
import com.example.viewmodel.DbzTagTeamViewModel

@Composable
fun SettingsScreen(
    viewModel: DbzTagTeamViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val internalResolution by viewModel.internalResolution.collectAsState()
    val screenFilter by viewModel.screenFilter.collectAsState()
    val aspectRatio by viewModel.aspectRatio.collectAsState()
    val showFps by viewModel.showFps.collectAsState()
    val fps60Patch by viewModel.fps60Patch.collectAsState()
    val soundVolume by viewModel.soundVolume.collectAsState()
    val bgmVolume by viewModel.bgmVolume.collectAsState()
    val fastMemory by viewModel.fastMemory.collectAsState()
    val multithreading by viewModel.multithreading.collectAsState()
    val language by viewModel.language.collectAsState()

    val resText = when {
        internalResolution <= 1.05f -> "1x · 480×272"
        internalResolution <= 1.3f -> "1.25x · 640×560"
        internalResolution <= 1.6f -> "1.5x · 720×408"
        internalResolution <= 2.1f -> "2x · 960×544 (1080p)"
        internalResolution <= 3.1f -> "3x · 1440×816 (2K)"
        else -> "4x · 1920×1088 (4K Ultra)"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SETTINGS",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            color = Color.White,
            letterSpacing = 1.sp
        )
        Text(
            text = "They apply the next time the game starts",
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 2.dp, bottom = 18.dp)
        )

        // Card 1: Video (matching Screenshot 2)
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
                            .background(Color(0xFF282420)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = "Video",
                            tint = DragonAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Video",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Internal Resolution Header & Value
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Internal resolution",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = resText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DragonAmber
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Resolution Slider
                Slider(
                    value = internalResolution,
                    onValueChange = { viewModel.setInternalResolution(it) },
                    valueRange = 1.0f..4.0f,
                    steps = 5,
                    colors = SliderDefaults.colors(
                        thumbColor = DragonAmber,
                        activeTrackColor = DragonAmber,
                        inactiveTrackColor = CardBorderDark
                    )
                )

                Text(
                    text = "1x is the PSP's own resolution. On phones and handhelds, 2x or 3x is usually the sweet spot; on weaker GPUs (Mali, Adreno 6xx), try 1.25x to 1.75x.",
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Screen Filter
                Text(
                    text = "Screen filter",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Filter Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ScreenFilterType.values().forEach { filter ->
                        FilterChipButton(
                            text = filter.label,
                            isSelected = screenFilter == filter,
                            onClick = { viewModel.setScreenFilter(filter) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "FXAA smooths jagged edges. FSR and Snapdragon GSR upscale with sharper edges: best with an internal resolution below the screen's (1x to 2x), so they also help weaker GPUs.",
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Aspect Ratio
                Text(
                    text = "Aspect ratio",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AspectRatioType.values().forEach { ratio ->
                        FilterChipButton(
                            text = ratio.label,
                            isSelected = aspectRatio == ratio,
                            onClick = { viewModel.setAspectRatio(ratio) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Screen: the phone's own shape, so the fight fills it. Wide shapes show more of the scene to the sides (nothing is stretched); the menus are drawn for 4:3 and are widened.",
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Show FPS Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Show FPS",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "Frames-per-second counter while playing",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Switch(
                        checked = showFps,
                        onCheckedChange = { viewModel.setShowFps(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = DragonAmber,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = SurfaceVariantDark
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 60 FPS Patch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "60 FPS Native Patch",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "Forces the physics and render tick to smooth 60 frames",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Switch(
                        checked = fps60Patch,
                        onCheckedChange = { viewModel.setFps60Patch(it) },
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

        Spacer(modifier = Modifier.height(16.dp))

        // Card 2: Audio Settings
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
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Audio",
                            tint = KiCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Audio & Sound Effects",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Sound Effects Volume (${(soundVolume * 100).toInt()}%)",
                    fontSize = 13.sp,
                    color = Color.White
                )
                Slider(
                    value = soundVolume,
                    onValueChange = { viewModel.setSoundVolume(it) },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = KiCyan,
                        activeTrackColor = KiCyan,
                        inactiveTrackColor = CardBorderDark
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "BGM Music Volume (${(bgmVolume * 100).toInt()}%)",
                    fontSize = 13.sp,
                    color = Color.White
                )
                Slider(
                    value = bgmVolume,
                    onValueChange = { viewModel.setBgmVolume(it) },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = KiCyan,
                        activeTrackColor = KiCyan,
                        inactiveTrackColor = CardBorderDark
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card 3: System & Language
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
                            .background(Color(0xFF282420)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            tint = DragonAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "System & Localization",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Fast Memory (arm64)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("Boosts emulation and texture streaming speed", color = TextSecondary, fontSize = 12.sp)
                    }
                    Switch(
                        checked = fastMemory,
                        onCheckedChange = { viewModel.setFastMemory(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = DragonAmber
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Multithreading (Multi-Core)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("Distributes GPU & sound rendering across CPU cores", color = TextSecondary, fontSize = 12.sp)
                    }
                    Switch(
                        checked = multithreading,
                        onCheckedChange = { viewModel.setMultithreading(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = DragonAmber
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Game Language", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = language,
                        color = DragonAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun FilterChipButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(38.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF2563EB) else SurfaceVariantDark,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(19.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
    }
}
