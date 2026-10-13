package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SaveStateEntity
import com.example.ui.theme.*
import com.example.viewmodel.DbzTagTeamViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SaveStateManagementSheet(
    viewModel: DbzTagTeamViewModel,
    onStateLoaded: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allSaveStates by viewModel.allSaveStates.collectAsState()
    val romList by viewModel.romFilesList.collectAsState()
    val activeRom = romList.firstOrNull { it.isSelected } ?: romList.first()

    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedSlot by remember { mutableStateOf(1) }
    var stateTitleInput by remember { mutableStateOf("") }

    val filteredStates = allSaveStates.filter { it.romFileName == activeRom.fileName }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(SurfaceDark)
            .border(1.dp, CardBorderDark, RoundedCornerShape(22.dp))
            .padding(18.dp)
    ) {
        // Header
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
                        imageVector = Icons.Default.Save,
                        contentDescription = "Savestates Room DB",
                        tint = DragonAmber,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Gestor de Savestates (Room DB)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Para: ${activeRom.fileName} (${filteredStates.size} guardados)",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            // Quick Save Button
            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = DragonAmber,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .height(36.dp)
                    .testTag("new_save_state_button"),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Nuevo Slot", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Save state items
        if (filteredStates.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No hay savestates para esta ROM.", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Pulsa 'Nuevo Slot' para guardar el estado del combate.", color = DragonAmber, fontSize = 11.sp)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filteredStates.forEach { state ->
                    SaveStateCard(
                        saveState = state,
                        onLoad = {
                            viewModel.loadSaveState(state)
                            onStateLoaded()
                            Toast.makeText(context, "Estado cargado: ${state.title}", Toast.LENGTH_SHORT).show()
                        },
                        onDelete = {
                            viewModel.deleteSaveState(state.id)
                            Toast.makeText(context, "Savestate eliminado", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    // New Savestate Dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = {
                Text(
                    text = "Guardar Savestate en Room",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column {
                    Text(
                        text = "Selecciona el slot de guardado para ${activeRom.fileName}:",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Slot Picker Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        (1..5).forEach { slot ->
                            FilterChip(
                                selected = selectedSlot == slot,
                                onClick = { selectedSlot = slot },
                                label = { Text("Slot $slot", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DragonAmber,
                                    selectedLabelColor = Color.Black
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = stateTitleInput,
                        onValueChange = { stateTitleInput = it },
                        label = { Text("Etiqueta o Título") },
                        placeholder = { Text("ej. Fase Final vs Frieza") },
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
                        viewModel.createSaveState(selectedSlot, stateTitleInput)
                        stateTitleInput = ""
                        showCreateDialog = false
                        Toast.makeText(context, "Savestate guardado en base de datos!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DragonAmber)
                ) {
                    Text("Guardar", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun SaveStateCard(
    saveState: SaveStateEntity,
    onLoad: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val formattedDate = remember(saveState.timestamp) { dateFormat.format(Date(saveState.timestamp)) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, CardBorderDark, RoundedCornerShape(14.dp)),
        color = SurfaceVariantDark
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Slot badge
                    Surface(
                        color = if (saveState.isAutoSave) Color(0xFF0284C7) else DragonAmber,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (saveState.isAutoSave) "AUTOSAVE" else "SLOT ${saveState.slotNumber}",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = saveState.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = formattedDate,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Snapshot Battle Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${saveState.p1FighterName} vs ${saveState.cpuFighterName}",
                    fontSize = 11.sp,
                    color = DragonAmber
                )
                Text(
                    text = saveState.notes,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDelete,
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = DangerRed, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Borrar", color = DangerRed, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onLoad,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cargar Estado", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
