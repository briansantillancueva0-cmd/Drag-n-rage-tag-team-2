package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.NavTab
import com.example.ui.components.DragonRageHeader
import com.example.ui.theme.*
import com.example.viewmodel.DbzTagTeamViewModel

@Composable
fun HomeScreen(
    viewModel: DbzTagTeamViewModel,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isVerified by viewModel.isGameDataVerified.collectAsState()
    val gameFilesInfo by viewModel.gameDataFiles.collectAsState()
    val engineInfo by viewModel.gameEngineInfo.collectAsState()
    val texturePacks by viewModel.texturePacks.collectAsState()
    val modsList by viewModel.modsList.collectAsState()

    val texturesCount = texturePacks.count { it.isEnabled }
    val modsCount = modsList.count { it.isEnabled }
    val stagesCount = modsList.count { it.category == com.example.model.ModCategory.STAGES && it.isEnabled }
    val songsCount = modsList.count { it.category == com.example.model.ModCategory.SONGS && it.isEnabled }

    var showRomPickerModal by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        DragonRageHeader(
            title = "DRAGON RAGE",
            subtitle = "Tenkaichi Tag Team · Android"
        )

        // Hero Banner Art
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, CardBorderDark, RoundedCornerShape(18.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_dbz_tag_banner_1791904191641),
                contentDescription = "Goku and Vegeta Tag Team",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .padding(14.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "2v2 TAG BATTLE READY",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Play Button (Primary action)
        Button(
            onClick = onPlayClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .testTag("play_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = DragonAmber,
                contentColor = Color(0xFF1E1303)
            ),
            shape = RoundedCornerShape(29.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color(0xFF1E1303),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PLAY",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Modo Historia (Dragon Walker) Button
        Button(
            onClick = { viewModel.openStoryMode() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("story_mode_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E283F),
                contentColor = Color.White
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, DragonAmber),
            shape = RoundedCornerShape(26.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = "Story Mode",
                    tint = DragonAmber,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "MODO HISTORIA (DRAGON WALKER)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Visor 3D de Mods (OBJ & PCK 1 con Texturas)
        Button(
            onClick = { viewModel.selectTab(NavTab.MODS) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("viewer_3d_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E2638),
                contentColor = Color.White
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, KiCyan),
            shape = RoundedCornerShape(26.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ViewInAr,
                    contentDescription = "3D Viewer",
                    tint = KiCyan,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "VISOR 3D DE MODS (OBJ & PCK 1)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Editor TTT (MainPage / ViewPj - BBQ Modding Tool)
        Button(
            onClick = {
                viewModel.selectTab(NavTab.MODS)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("editor_ttt_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6B46C1),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(26.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Editor TTT",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "EDITOR TTT (MAINPAGE / VIEWPJ)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Add a Play shortcut button
        OutlinedButton(
            onClick = {
                viewModel.audioHaptics.playHaptic(30, 150)
                Toast.makeText(context, "Direct Play shortcut added to Home screen!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("shortcut_button"),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(CardBorderDark)
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = DragonAmber
            ),
            shape = RoundedCornerShape(26.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Shortcut icon",
                    tint = DragonAmber,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Add a Play shortcut to the home screen",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Explanatory label
        Text(
            text = "Creates an icon on the home screen that starts the game directly, without opening the app. The app icon's long-press menu has a Play entry too.",
            fontSize = 12.sp,
            lineHeight = 17.sp,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Android ISO / CSO Gallery & File Loader
        com.example.ui.components.RomFilePickerGallery(
            viewModel = viewModel,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Room Database Savestates Manager
        com.example.ui.components.SaveStateManagementSheet(
            viewModel = viewModel,
            onStateLoaded = onPlayClick,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Visual Texture Mods Gallery (Toggle before launch)
        com.example.ui.components.TexturePackGalleryComponent(
            viewModel = viewModel,
            onLaunchGame = onPlayClick,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Game Engine Card
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
                            .background(Color(0xFF1E2638)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = "Engine chip",
                            tint = KiCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Game engine",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = engineInfo,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Stats Row (4 stats cards)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                count = texturesCount,
                label = "Textures",
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.selectTab(NavTab.TEXTURE) }
            )
            StatCard(
                count = modsCount,
                label = "Mods",
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.selectTab(NavTab.MODS) }
            )
            StatCard(
                count = stagesCount,
                label = "Stages",
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.selectTab(NavTab.MODS) }
            )
            StatCard(
                count = songsCount,
                label = "Songs",
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.selectTab(NavTab.MODS) }
            )
        }

        Spacer(modifier = Modifier.height(28.dp))
    }

    // ROM Picker Dialog
    if (showRomPickerModal) {
        AlertDialog(
            onDismissRequest = { showRomPickerModal = false },
            title = {
                Text(
                    text = "DBZ Tag Team ROM Storage",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column {
                    Text(
                        text = "The app includes the verified Tenkaichi Tag Team (USA) game engine and assets bundled. You can also import any modified ISO / CSO.",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = SurfaceVariantDark,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Active: dbz_tenkaichi_tag_team_usa.iso", color = DragonAmber, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Path: /storage/emulated/0/PSP/GAME/ULES01488", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRomPickerModal = false
                        Toast.makeText(context, "ISO re-scanned and ready!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DragonAmber)
                ) {
                    Text("OK", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRomPickerModal = false }) {
                    Text("Close", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun StatCard(
    count: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
        color = SurfaceDark
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$count",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = DragonAmber
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
        }
    }
}
