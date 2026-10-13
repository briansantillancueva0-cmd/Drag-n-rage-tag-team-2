package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.CwCheat
import com.example.ui.theme.*
import com.example.viewmodel.DbzTagTeamViewModel

@Composable
fun CheatsScreen(
    viewModel: DbzTagTeamViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val unlockEverything by viewModel.unlockEverything.collectAsState()
    val cheatsList by viewModel.cheatsList.collectAsState()

    var showAddCheatDialog by remember { mutableStateOf(false) }
    var newCheatName by remember { mutableStateOf("") }
    var newCheatCode by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "CHEATS",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            color = Color.White,
            letterSpacing = 1.sp
        )
        Text(
            text = "Shenron's wishes",
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 2.dp, bottom = 18.dp)
        )

        // Card 1: Unlock Everything (Shenron's Wish matching Screenshot 4)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, CardBorderDark, RoundedCornerShape(20.dp)),
            color = SurfaceDark
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF282420)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = "Unlock",
                            tint = DragonAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Unlock everything",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Glowing Dragon Ball Icon
                    Box(
                        modifier = Modifier
                            .size(64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(DragonGold.copy(alpha = 0.6f), Color.Transparent)
                                    )
                                )
                                .blur(8.dp)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.img_launcher_icon_1791904167194),
                            contentDescription = "Dragon Ball Shenron Wish",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = "Every character, stage, song and item, and maximum Zeni. It is the debug function the developers left in the game, the same one as in the PC port.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Apply when the game starts",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Switch(
                        checked = unlockEverything,
                        onCheckedChange = {
                            viewModel.setUnlockEverything(it)
                            if (it) {
                                Toast.makeText(context, "Shenron granted your wish! All characters and max Zeni unlocked!", Toast.LENGTH_SHORT).show()
                            }
                        },
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

        // Card 2: How it works (matching Screenshot 4)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, CardBorderDark, RoundedCornerShape(20.dp)),
            color = SurfaceDark
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E2638)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = KiCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "How it works",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "The unlock is written to the game's save (memory card) and cannot be undone by the app. It is applied once, when the game starts, and the switch turns itself off again.\n\nPCSX2 / PPSSPP cheats (.pnach / cwcheat) apply runtime memory patching natively for Tenkaichi Tag Team.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // CWCheats Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CWCHEAT CODES (${cheatsList.size})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )

            TextButton(onClick = { showAddCheatDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null, tint = DragonAmber, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Code", color = DragonAmber, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // CWCheat cards
        cheatsList.forEach { cheat ->
            CheatItemCard(
                cheat = cheat,
                onToggle = { viewModel.toggleCheat(cheat.id) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    // Add Cheat Dialog
    if (showAddCheatDialog) {
        AlertDialog(
            onDismissRequest = { showAddCheatDialog = false },
            title = { Text("Add CWCheat Code", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newCheatName,
                        onValueChange = { newCheatName = it },
                        label = { Text("Cheat Name") },
                        placeholder = { Text("e.g. Infinite Sparking Timer") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DragonAmber,
                            unfocusedBorderColor = CardBorderDark,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newCheatCode,
                        onValueChange = { newCheatCode = it },
                        label = { Text("Hex Code") },
                        placeholder = { Text("_L 0x203A45C0 0x000003E8") },
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
                        val name = newCheatName.ifBlank { "Custom Cheat Code" }
                        viewModel.addCustomCheat(name, newCheatCode.ifBlank { "_L 0x20000000 0x00000001" })
                        newCheatName = ""
                        newCheatCode = ""
                        showAddCheatDialog = false
                        Toast.makeText(context, "Cheat injected into cwcheat database!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DragonAmber)
                ) {
                    Text("Save Cheat", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCheatDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun CheatItemCard(
    cheat: CwCheat,
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
                    text = cheat.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = cheat.description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = cheat.codeHex.lines().firstOrNull() ?: "",
                    fontSize = 11.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    color = DragonAmber
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Switch(
                checked = cheat.isEnabled,
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
