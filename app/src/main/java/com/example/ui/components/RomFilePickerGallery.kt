package com.example.ui.components

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RomFormat
import com.example.model.RomGameFile
import com.example.ui.theme.*
import com.example.viewmodel.DbzTagTeamViewModel

@Composable
fun RomFilePickerGallery(
    viewModel: DbzTagTeamViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val romList by viewModel.romFilesList.collectAsState()

    var showManualAddDialog by remember { mutableStateOf(false) }
    var manualIsoName by remember { mutableStateOf("") }
    var selectedFormat by remember { mutableStateOf(RomFormat.ISO) }

    // Native Android SAF File Picker for .iso and .cso
    val romPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                var fileName = "dbz_custom_rom.iso"
                var sizeBytes = 1024L * 1024L * 1150L // 1.15 GB default

                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                        if (nameIndex != -1) {
                            cursor.getString(nameIndex)?.let { fileName = it }
                        }
                        if (sizeIndex != -1) {
                            sizeBytes = cursor.getLong(sizeIndex).coerceAtLeast(1024L * 1024L * 10L)
                        }
                    }
                }

                viewModel.importRomFile(fileName, sizeBytes, uri.toString())
                Toast.makeText(context, "Loaded $fileName successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error reading file: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
                        .background(Color(0xFF1E283F)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Album,
                        contentDescription = "ROM Disc",
                        tint = DragonAmber,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Galería de Juegos (ISO / CSO)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${romList.size} ROMs disponibles en almacenamiento",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            // Android Storage Picker Button
            Button(
                onClick = {
                    try {
                        romPickerLauncher.launch(
                            arrayOf(
                                "*/*",
                                "application/octet-stream",
                                "application/x-iso9660-image"
                            )
                        )
                    } catch (_: Exception) {
                        showManualAddDialog = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .height(38.dp)
                    .testTag("load_iso_cso_button"),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Cargar ISO/CSO", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List of Imported ISO/CSO ROMs
        romList.forEach { rom ->
            RomGameCard(
                rom = rom,
                viewModel = viewModel,
                onSelect = { viewModel.selectActiveRom(rom.id) },
                onDelete = {
                    if (romList.size > 1) {
                        viewModel.deleteRomFile(rom.id)
                        Toast.makeText(context, "Removed ${rom.fileName}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Cannot remove default core game file.", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Secondary quick add link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { showManualAddDialog = true }
            ) {
                Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = DragonAmber, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Añadir ROM Modificada (.iso/.cso)", color = DragonAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                text = "Compatible con PPSSPP Core",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }

    // Manual / Preset ISO Picker Dialog
    if (showManualAddDialog) {
        AlertDialog(
            onDismissRequest = { showManualAddDialog = false },
            title = {
                Text(
                    text = "Cargar Archivo ISO o CSO",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column {
                    Text(
                        text = "Introduce el nombre del archivo ISO/CSO o selecciona una versión de la comunidad:",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = manualIsoName,
                        onValueChange = { manualIsoName = it },
                        label = { Text("Nombre del archivo") },
                        placeholder = { Text("ej. dbz_ttt_sh_superhero.iso") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DragonAmber,
                            unfocusedBorderColor = CardBorderDark,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Formato:", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedFormat == RomFormat.ISO,
                            onClick = { selectedFormat = RomFormat.ISO },
                            label = { Text("ISO (Sin comprimir)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DragonAmber,
                                selectedLabelColor = Color.Black
                            )
                        )
                        FilterChip(
                            selected = selectedFormat == RomFormat.CSO,
                            onClick = { selectedFormat = RomFormat.CSO },
                            label = { Text("CSO (Comprimido)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFA855F7),
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Preajustes Populares:", fontSize = 12.sp, color = DragonAmber, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    PresetRomItem(
                        name = "DBZ Tag Team: Super Hero Edition (ISO · 1.4 GB)",
                        onClick = {
                            viewModel.importRomFile(
                                "dbz_tag_team_super_hero.iso",
                                1024L * 1024L * 1420L,
                                "/storage/emulated/0/Download/dbz_tag_team_super_hero.iso"
                            )
                            showManualAddDialog = false
                            Toast.makeText(context, "Super Hero ISO cargada!", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    PresetRomItem(
                        name = "DBZ TTT: Dragon Ball AF Edition (CSO · 980 MB)",
                        onClick = {
                            viewModel.importRomFile(
                                "dbz_ttt_af_edition.cso",
                                1024L * 1024L * 980L,
                                "/storage/emulated/0/Download/dbz_ttt_af_edition.cso"
                            )
                            showManualAddDialog = false
                            Toast.makeText(context, "Dragon Ball AF CSO cargada!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = manualIsoName.ifBlank { "dbz_custom_${selectedFormat.name.lowercase()}.${selectedFormat.name.lowercase()}" }
                        viewModel.importRomFile(name, 1024L * 1024L * 1100L, "/storage/emulated/0/PSP/GAME/$name")
                        manualIsoName = ""
                        showManualAddDialog = false
                        Toast.makeText(context, "Archivo cargado y verificado!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DragonAmber)
                ) {
                    Text("Cargar", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualAddDialog = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun RomGameCard(
    rom: RomGameFile,
    viewModel: DbzTagTeamViewModel,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    val isSelected = rom.isSelected
    val borderColor = if (isSelected) DragonAmber else CardBorderDark

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onSelect() },
        color = if (isSelected) Color(0xFF1E283F) else SurfaceVariantDark
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Radio / Active Indicator
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = DragonAmber,
                    unselectedColor = TextSecondary
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = rom.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Format Tag
                    Surface(
                        color = if (rom.format == RomFormat.ISO) Color(0xFF0369A1) else Color(0xFF7E22CE),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = rom.format.name,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${rom.fileName} · ${rom.fileSizeMb} MB · ${rom.region} (${rom.gameId})",
                    fontSize = 12.sp,
                    color = if (isSelected) DragonAmber else TextSecondary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = SuccessGreen, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (rom.isExpanded) "ISO Expandida (${String.format("%.1f", rom.expandedCapacityGb)} GB) · Casillas: ${rom.allocatedModSlots}/${rom.maxModSlots}" else "Header & MD5 Verified · ${rom.fileSizeMb} MB",
                        color = if (rom.isExpanded) DragonAmber else SuccessGreen,
                        fontSize = 11.sp,
                        fontWeight = if (rom.isExpanded) FontWeight.Bold else FontWeight.Normal
                    )
                }

                // If Selected or Expanded: Show ISO Expansion Controls and Casillas para Instalar Mods
                if (isSelected) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = Color(0xFF131D33),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            // Expansion Selector Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.OpenInFull, contentDescription = null, tint = DragonAmber, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "EXPANDIR ISO PARA MODS:",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DragonAmber
                                    )
                                }
                                Surface(
                                    color = if (rom.isExpanded) DragonAmber else Color(0xFF243354),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (rom.isExpanded) "${String.format("%.1f", rom.expandedCapacityGb)} GB ACTIVA" else "1.28 GB BASE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (rom.isExpanded) Color.Black else TextSecondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Expand to 2 GB or 4 GB buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.expandIsoImage(rom.id, 2.0) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (rom.isExpanded && rom.expandedCapacityGb == 2.0) Color(0xFF2563EB) else Color(0xFF202B42),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp)
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Expandir a 2 GB", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.expandIsoImage(rom.id, 4.0) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (rom.isExpanded && rom.expandedCapacityGb == 4.0) Color(0xFF9333EA) else Color(0xFF202B42),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp)
                                ) {
                                    Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Expandir a 4 GB", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // "Casillas dentro de la ISO para instalar mods"
                            Text(
                                text = "Casillas dentro de la ISO para instalar mods (${rom.installedModNames.size}/${rom.maxModSlots} en uso):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Casillas / Chips de mods instalados dentro de la ISO
                            rom.installedModNames.chunked(2).forEach { pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    pair.forEach { modName ->
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFF1E283F)),
                                            color = Color(0xFF1E283F)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    modifier = Modifier.weight(1f),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(Icons.Default.Check, contentDescription = null, tint = DragonAmber, modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = modName,
                                                        fontSize = 10.sp,
                                                        color = Color.White,
                                                        maxLines = 1
                                                    )
                                                }
                                                IconButton(
                                                    onClick = { viewModel.removeModFromIsoSlot(rom.id, modName) },
                                                    modifier = Modifier.size(18.dp)
                                                ) {
                                                    Icon(Icons.Default.Close, contentDescription = "Quitar", tint = TextSecondary, modifier = Modifier.size(12.dp))
                                                }
                                            }
                                        }
                                    }
                                    if (pair.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            // Botón para crear/agregar nueva casilla de mod dentro de la ISO
                            OutlinedButton(
                                onClick = {
                                    val newNum = rom.installedModNames.size + 1
                                    viewModel.installModIntoIsoSlot(rom.id, "Casilla #$newNum (Mod Personalizado)")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(32.dp),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DragonAmber)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = DragonAmber, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ Crear Casilla dentro de la ISO para Instalar Mod", fontSize = 10.sp, color = DragonAmber, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete ROM",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun PresetRomItem(
    name: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF141C2E))
            .clickable { onClick() }
            .padding(10.dp),
        color = Color.Transparent
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(name, fontSize = 12.sp, color = Color.White, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = DragonAmber, modifier = Modifier.size(16.dp))
        }
    }
}
