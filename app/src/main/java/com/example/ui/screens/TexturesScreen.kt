package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TexturePack
import com.example.ui.theme.*
import com.example.viewmodel.DbzTagTeamViewModel

@Composable
fun TexturesScreen(
    viewModel: DbzTagTeamViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val useTexturePacks by viewModel.useTexturePacks.collectAsState()
    val texturePacks by viewModel.texturePacks.collectAsState()

    var showDownloadDialog by remember { mutableStateOf(false) }
    var showAddPackDialog by remember { mutableStateOf(false) }
    var newPackName by remember { mutableStateOf("") }

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
            item {
                Text(
                    text = "TEXTURES",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "HD packs made for PPSSPP / PCSX2 work as they are",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp, bottom = 20.dp)
                )
            }

            // Gallery-Style Interactive Texture Pack Preview Component
            item {
                com.example.ui.components.TexturePackGalleryComponent(
                    viewModel = viewModel,
                    onLaunchGame = { viewModel.startBattle() },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Card 1: Use texture packs switch
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, CardBorderDark, RoundedCornerShape(20.dp)),
                    color = SurfaceDark
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2E2218)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Texture,
                                        contentDescription = "Texture icon",
                                        tint = DragonAmber,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Use texture packs",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Switch(
                                checked = useTexturePacks,
                                onCheckedChange = { viewModel.setUseTexturePacks(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = DragonAmber,
                                    uncheckedThumbColor = TextSecondary,
                                    uncheckedTrackColor = SurfaceVariantDark
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Turns every pack on or off in the game without deleting anything.",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Card 2: Download
            item {
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
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "Download icon",
                                    tint = DragonAmber,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Download",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Download a texture pack from the internet and install it from here. Packs from the list, or any direct link to a .zip.",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showDownloadDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DragonAmber,
                                contentColor = Color(0xFF1E1303)
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("download_packs_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Download packs",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Packs list header
            item {
                Text(
                    text = "INSTALLED PACKS (${texturePacks.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Texture pack items
            if (texturePacks.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Texture,
                            contentDescription = null,
                            tint = DragonAmber.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "No pack installed",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap 'Add pack' and pick the .zip. The app unpacks the textures (.dds or .png) into the right folder and the game uses them the next time it starts.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                items(texturePacks, key = { it.id }) { pack ->
                    TexturePackItem(
                        pack = pack,
                        onToggle = { viewModel.toggleTexturePack(pack.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        // Add Pack Floating Button
        Button(
            onClick = { showAddPackDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp)
                .height(48.dp)
                .testTag("add_pack_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = DragonAmber,
                contentColor = Color(0xFF1E1303)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add pack",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Add pack",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }

    // Download community packs dialog
    if (showDownloadDialog) {
        AlertDialog(
            onDismissRequest = { showDownloadDialog = false },
            title = {
                Text("Download HD Texture Packs", fontWeight = FontWeight.Bold, color = Color.White)
            },
            text = {
                Column {
                    Text(
                        "Select a verified community texture pack to download and inject into DBZ Tenkaichi Tag Team:",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    CommunityTextureOption(
                        name = "Sparking Zero Cel-Shaded UI Pack",
                        size = "180 MB",
                        onDownload = {
                            viewModel.addCustomTexturePack("Sparking Zero Cel-Shaded UI Pack", 180)
                            showDownloadDialog = false
                            Toast.makeText(context, "Pack downloaded and unpacked to textures folder!", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CommunityTextureOption(
                        name = "Tenkaichi 3 PS2 Style Faithful Textures",
                        size = "340 MB",
                        onDownload = {
                            viewModel.addCustomTexturePack("Tenkaichi 3 PS2 Style Faithful Textures", 340)
                            showDownloadDialog = false
                            Toast.makeText(context, "Pack downloaded and active!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showDownloadDialog = false }) {
                    Text("Close", color = DragonAmber)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Add custom pack dialog
    if (showAddPackDialog) {
        AlertDialog(
            onDismissRequest = { showAddPackDialog = false },
            title = { Text("Import Texture Pack (.zip)", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column {
                    Text("Enter pack name or select custom .zip with textures:", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newPackName,
                        onValueChange = { newPackName = it },
                        label = { Text("Texture Pack Name") },
                        placeholder = { Text("e.g. Manga Shading V2") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DragonAmber,
                            unfocusedBorderColor = CardBorderDark,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = newPackName.ifBlank { "Custom HD Texture Pack" }
                        viewModel.addCustomTexturePack(name, 220)
                        newPackName = ""
                        showAddPackDialog = false
                        Toast.makeText(context, "Texture pack added and loaded!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DragonAmber)
                ) {
                    Text("Import .zip", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPackDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun TexturePackItem(
    pack: TexturePack,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
        color = SurfaceDark
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pack.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${pack.resolution} · ${pack.sizeMb} MB · by ${pack.author}",
                    fontSize = 12.sp,
                    color = DragonAmber
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = pack.description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Switch(
                checked = pack.isEnabled,
                onCheckedChange = { onToggle() },
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

@Composable
fun CommunityTextureOption(
    name: String,
    size: String,
    onDownload: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp)),
        color = SurfaceVariantDark
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                Text(size, fontSize = 11.sp, color = TextSecondary)
            }
            Button(
                onClick = onDownload,
                colors = ButtonDefaults.buttonColors(containerColor = DragonAmber),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.height(34.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Text("Get", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
