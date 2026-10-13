package com.example.ui.components

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.viewmodel.DbzTagTeamViewModel

/**
 * DBZ Tenkaichi Tag Team Character & Color Editor
 * Matches the BBQ Tool UI (MainPage / ViewPj) and In-Game Color Selection.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TttCharacterModEditor(
    viewModel: DbzTagTeamViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bbqSlots by viewModel.bbqSlots.collectAsState()
    val bbqFileState by viewModel.bbqFileState.collectAsState()
    val selectedSubTab by viewModel.selectedTttEditorSubTab.collectAsState()

    var showAddSlotDialog by remember { mutableStateOf(false) }
    var newSlotFamily by remember { mutableStateOf("Saibaiman 1") }
    var newSlotColor by remember { mutableStateOf(1) }
    var newSlotHexId by remember { mutableStateOf("0001328A") }
    var newSlotComment by remember { mutableStateOf("Nuevo Personaje Mod") }

    // File picker launcher
    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "00013277-000033DD.BBQ"
            viewModel.openBbqFile(path = uri.toString(), fileName = fileName)
            Toast.makeText(context, "Archivo cargado: $fileName", Toast.LENGTH_SHORT).show()
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, CardBorderDark, RoundedCornerShape(20.dp)),
        color = SurfaceDark
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Header Banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(DragonAmber)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "EDITOR DBZ TTT (BBQ / PCK 1)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = DragonAmber
                        )
                    }
                    Text(
                        text = "Edición de mallas, paletas de color y slots de personajes",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                // Add Slot Button
                Button(
                    onClick = { showAddSlotDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DragonAmber,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Agregar Slot", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Windows-Style Tabs Bar: MainPage | ViewPj | Color Reference
            Surface(
                color = Color(0xFF161C2C),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TttEditorSubTab.values().forEach { tab ->
                        val isSelected = selectedSubTab == tab
                        val activeColor by animateColorAsState(
                            targetValue = if (isSelected) Color(0xFF6B46C1) else Color.Transparent,
                            label = "tab_color"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(activeColor)
                                .clickable { viewModel.selectTttEditorSubTab(tab) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = tab.label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(24.dp)
                                            .height(2.dp)
                                            .background(DragonAmber)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-Tab Content
            when (selectedSubTab) {
                TttEditorSubTab.MAIN_PAGE -> {
                    MainPageView(
                        viewModel = viewModel,
                        bbqSlots = bbqSlots,
                        bbqFileState = bbqFileState,
                        onOpenFile = { fileLauncher.launch(arrayOf("*/*")) },
                        onAddSlot = { showAddSlotDialog = true }
                    )
                }
                TttEditorSubTab.VIEW_PJ -> {
                    ViewPjView(
                        viewModel = viewModel,
                        bbqSlots = bbqSlots,
                        onAddSlot = { showAddSlotDialog = true }
                    )
                }
                TttEditorSubTab.COLOR_REFERENCE -> {
                    ColorReferenceGameView(
                        viewModel = viewModel,
                        bbqSlots = bbqSlots
                    )
                }
            }
        }
    }

    // Modal Dialog: Agregar nuevo slot de personaje
    if (showAddSlotDialog) {
        AlertDialog(
            onDismissRequest = { showAddSlotDialog = false },
            title = {
                Text(
                    text = "Agregar Slot de Personaje (TTT)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Configura el personaje y color para agregarlo a la lista de ranuras:",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = newSlotFamily,
                        onValueChange = { newSlotFamily = it },
                        label = { Text("Personaje / Familia") },
                        placeholder = { Text("ej. Saibaiman 2, Goku SSJ3, Broly") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Seleccionar Color de Traje (1 a 4):", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        (1..4).forEach { colNum ->
                            val isSelected = newSlotColor == colNum
                            val colHex = when (colNum) {
                                1 -> 0xFF22C55E
                                2 -> 0xFF3B82F6
                                3 -> 0xFFF59E0B
                                4 -> 0xFF8B5CF6
                                else -> 0xFFEAB308
                            }
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        2.dp,
                                        if (isSelected) DragonAmber else CardBorderDark,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { newSlotColor = colNum },
                                color = Color(colHex)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "$colNum Color",
                                        color = if (colNum == 3 || colNum == 1) Color.Black else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newSlotHexId,
                        onValueChange = { newSlotHexId = it },
                        label = { Text("ID Hexadecimal (pzs3us1)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newSlotComment,
                        onValueChange = { newSlotComment = it },
                        label = { Text("Comentario / Comando") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = newSlotFamily.ifBlank { "Saibaiman Custom" }
                        viewModel.addBbqSlot(
                            hexId = newSlotHexId,
                            characterFamily = name,
                            colorNumber = newSlotColor,
                            comment = newSlotComment
                        )
                        showAddSlotDialog = false
                        Toast.makeText(context, "Slot $name ($newSlotColor Color) agregado!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DragonAmber)
                ) {
                    Text("Agregar Slot", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSlotDialog = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

/**
 * MainPage Sub-Tab matching Screenshot 3
 */
@Composable
private fun MainPageView(
    viewModel: DbzTagTeamViewModel,
    bbqSlots: List<TttBbqCharacterSlot>,
    bbqFileState: TttBbqFileState,
    onOpenFile: () -> Unit,
    onAddSlot: () -> Unit
) {
    val context = LocalContext.current
    val currentSlot = bbqSlots.getOrNull(bbqFileState.currentOrderIndex) ?: bbqSlots.firstOrNull()

    var idText by remember(currentSlot) { mutableStateOf(currentSlot?.hexId ?: "00013277") }
    var colText by remember(currentSlot) { mutableStateOf((currentSlot?.colorNumber ?: 1).toString()) }
    var comText by remember(currentSlot) { mutableStateOf(currentSlot?.commentTag ?: "Saibaiman 3") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F1523))
            .padding(14.dp)
    ) {

        // Top Action Buttons: [Open File] [Save File] [Close File]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onOpenFile,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B46C1),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
            ) {
                Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Open File", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    viewModel.saveBbqFile()
                    Toast.makeText(context, "Archivo guardado exitosamente!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF374151),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save File", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    viewModel.closeBbqFile()
                    Toast.makeText(context, "Archivo cerrado", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF374151),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Close File", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // "ruta file" Label
        Text(
            text = "ruta file",
            fontSize = 11.sp,
            color = KiCyan,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = bbqFileState.filePath,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF1E283F))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // "Orden" Section with [back], 3D Canvas / Sprite, [next]
        Text(
            text = "Orden",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // [back] button
            Button(
                onClick = { viewModel.prevBbqOrder() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF374151),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .width(72.dp)
                    .height(38.dp)
            ) {
                Text("back", fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            // Center: Character / Model Preview Box
            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp)),
                color = Color(0xFF182030)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val activeColor = Color(currentSlot?.colorHex ?: 0xFF22C55E)
                    CharacterModelCanvasPreview(
                        characterFamily = currentSlot?.characterFamily ?: "Saibaiman",
                        color = activeColor,
                        colorNumber = currentSlot?.colorNumber ?: 1
                    )
                }
            }

            // [next] button
            Button(
                onClick = { viewModel.nextBbqOrder() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF374151),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .width(72.dp)
                    .height(38.dp)
            ) {
                Text("next", fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Order indicator
        Text(
            text = "Slot ${bbqFileState.currentOrderIndex + 1} de ${bbqSlots.size} · ${currentSlot?.characterFamily ?: ""}",
            fontSize = 11.sp,
            color = DragonAmber,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Form Row: [ Id ]  [ Col ]  [ Com ]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = idText,
                onValueChange = { idText = it },
                label = { Text("Id", fontSize = 10.sp) },
                singleLine = true,
                modifier = Modifier.weight(1.2f)
            )

            OutlinedTextField(
                value = colText,
                onValueChange = { colText = it },
                label = { Text("Col", fontSize = 10.sp) },
                singleLine = true,
                modifier = Modifier.weight(0.8f)
            )

            OutlinedTextField(
                value = comText,
                onValueChange = { comText = it },
                label = { Text("Com", fontSize = 10.sp) },
                singleLine = true,
                modifier = Modifier.weight(1.6f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // [ Save Character ] Big Button
        Button(
            onClick = {
                val colNum = colText.toIntOrNull()?.coerceIn(1, 4) ?: 1
                if (currentSlot != null) {
                    viewModel.saveCurrentBbqSlot(
                        slotNumber = currentSlot.slotNumber,
                        hexId = idText,
                        colorNumber = colNum,
                        comment = comText,
                        characterFamily = comText.substringBefore('(').trim().ifBlank { currentSlot.characterFamily }
                    )
                    Toast.makeText(context, "¡Personaje ${currentSlot.characterName} guardado en el archivo!", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6B46C1),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Save Character", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * ViewPj Sub-Tab matching Screenshot 1
 */
@Composable
private fun ViewPjView(
    viewModel: DbzTagTeamViewModel,
    bbqSlots: List<TttBbqCharacterSlot>,
    onAddSlot: () -> Unit
) {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lista de Personajes (ViewPj): ${bbqSlots.size} ranuras",
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )

            TextButton(onClick = onAddSlot) {
                Icon(Icons.Default.Add, contentDescription = null, tint = DragonAmber, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("+ Agregar", color = DragonAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        bbqSlots.forEach { slot ->
            ViewPjSlotCard(
                slot = slot,
                onColorChange = { newCol ->
                    viewModel.updateSlotColorNumber(slot.slotNumber, newCol)
                    Toast.makeText(context, "Slot ${slot.slotNumber}: cambiado a $newCol Color", Toast.LENGTH_SHORT).show()
                },
                onFamilyChange = { newFamily ->
                    viewModel.updateSlotCharacterFamily(slot.slotNumber, newFamily)
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

/**
 * Individual Card inside ViewPj matching Screenshot 1
 */
@Composable
private fun ViewPjSlotCard(
    slot: TttBbqCharacterSlot,
    onColorChange: (Int) -> Unit,
    onFamilyChange: (String) -> Unit
) {
    val families = listOf("Saibaiman 1", "Saibaiman 2", "Saibaiman 3", "Goku SSJ3", "Vegeta Majin", "Broly LSSJ", "Gohan Definitivo")
    var expandedFamilyMenu by remember { mutableStateOf(false) }
    var expandedColorMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
        color = Color(0xFF141A29)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Slot number matching Screenshot 1 (2, 3, 4, etc.)
            Text(
                text = "${slot.slotNumber}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.width(28.dp)
            )

            // Character Thumbnail (Saibaiman portrait / icon)
            Surface(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.5.dp, Color(slot.colorHex), RoundedCornerShape(8.dp)),
                color = Color(0xFF0F1523)
            ) {
                CharacterModelCanvasPreview(
                    characterFamily = slot.characterFamily,
                    color = Color(slot.colorHex),
                    colorNumber = slot.colorNumber
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Two Dropdowns column matching Screenshot 1:
            // Top: "Saibaiman 3"
            // Bottom: "4 Color"
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Dropdown 1: Character Family
                Box {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .border(1.dp, CardBorderDark, RoundedCornerShape(6.dp))
                            .clickable { expandedFamilyMenu = true },
                        color = Color(0xFF1E283F)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = slot.characterFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expandedFamilyMenu,
                        onDismissRequest = { expandedFamilyMenu = false }
                    ) {
                        families.forEach { fam ->
                            DropdownMenuItem(
                                text = { Text(fam, fontSize = 12.sp) },
                                onClick = {
                                    onFamilyChange(fam)
                                    expandedFamilyMenu = false
                                }
                            )
                        }
                    }
                }

                // Dropdown 2: Color ("1 Color", "2 Color", "3 Color", "4 Color")
                Box {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .border(1.dp, CardBorderDark, RoundedCornerShape(6.dp))
                            .clickable { expandedColorMenu = true },
                        color = Color(0xFF1E283F)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(slot.colorHex))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${slot.colorNumber} Color",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expandedColorMenu,
                        onDismissRequest = { expandedColorMenu = false }
                    ) {
                        (1..4).forEach { colNum ->
                            val colHex = when (colNum) {
                                1 -> 0xFF22C55E
                                2 -> 0xFF3B82F6
                                3 -> 0xFFF59E0B
                                4 -> 0xFF8B5CF6
                                else -> 0xFFEAB308
                            }
                            DropdownMenuItem(
                                leadingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(Color(colHex))
                                    )
                                },
                                text = { Text("$colNum Color", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    onColorChange(colNum)
                                    expandedColorMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * In-Game Color Selection Reference matching Screenshot 4:
 * Top: "SELECCION DE PERSONAJE" (Goku SSJ3, J1, J2 CPU, etc.)
 * Bottom: "Seleccionar Color: Color 1, Color 2, Color 3, Color 4"
 */
@Composable
private fun ColorReferenceGameView(
    viewModel: DbzTagTeamViewModel,
    bbqSlots: List<TttBbqCharacterSlot>
) {
    var selectedColorIndex by remember { mutableStateOf(1) } // 1, 2, 3, 4

    val activeGokuColor = when (selectedColorIndex) {
        1 -> Color(0xFFF59E0B) // Color 1: Naranja Goku Tradicional
        2 -> Color(0xFF3B82F6) // Color 2: Azul Gi Alternativo
        3 -> Color(0xFFEF4444) // Color 3: Rojo Torneo Kaio
        4 -> Color(0xFF1E293B) // Color 4: Negro Gi Oscuro
        else -> Color(0xFFF59E0B)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF091024))
            .padding(14.dp)
    ) {
        // Game Header matching Screenshot 4
        Surface(
            color = Color(0xFF1A365D),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SELECCION DE PERSONAJE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    color = Color.White
                )
                Text(
                    text = "@KASTOMODDER15 DBZ TTT",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = DragonAmber
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Fighter Slots Preview (J1, J2 CPU, J3 CPU, J4 CPU)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("J1 Goku SSJ3", "J2 CPU ?", "J3 CPU ?", "J4 CPU ?").forEachIndexed { idx, label ->
                val isP1 = idx == 0
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(70.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            1.dp,
                            if (isP1) DragonAmber else CardBorderDark,
                            RoundedCornerShape(8.dp)
                        ),
                    color = if (isP1) Color(0xFF1E293B) else Color(0xFF111827)
                ) {
                    Column(
                        modifier = Modifier.padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isP1) "J1" else "CPU",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isP1) DragonAmber else TextSecondary
                        )
                        Text(
                            text = if (isP1) "SSJ3" else "?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isP1) activeGokuColor else TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // "Seleccionar Color" Vertical / Horizontal Picker matching Screenshot 4
        Text(
            text = "Seleccionar Color",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Presiona una casilla para previsualizar el cambio de traje en el juego:",
            fontSize = 11.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (1..4).forEach { colNum ->
                val isSelected = selectedColorIndex == colNum
                val colHex = when (colNum) {
                    1 -> 0xFFF59E0B
                    2 -> 0xFF3B82F6
                    3 -> 0xFFEF4444
                    4 -> 0xFF1E293B
                    else -> 0xFFF59E0B
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            2.dp,
                            if (isSelected) DragonAmber else CardBorderDark,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            selectedColorIndex = colNum
                            viewModel.audioHaptics.playTagSwitchSound()
                            viewModel.audioHaptics.playHaptic(20, 110)
                        },
                    color = Color(colHex)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Color $colNum",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = if (colNum == 1) Color.Black else Color.White
                        )
                        if (isSelected) {
                            Text(
                                text = "▲ ACTIVO",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (colNum == 1) Color.Black else DragonAmber
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // In-Game Color Details Card
        Surface(
            color = Color(0xFF141E34),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(activeGokuColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Goku Super Saiyan 3 (Color $selectedColorIndex)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when (selectedColorIndex) {
                        1 -> "Traje canónico naranja y azul con aura dorada de Super Saiyan 3."
                        2 -> "Paleta alternativa azul oscuro (Vegeta Gi style) de Tenkaichi Tag Team."
                        3 -> "Traje rojo estilo Kaioken y mangas Daizenshuu."
                        4 -> "Traje oscuro / Shadow Goku para batallas de torneo 2v2."
                        else -> ""
                    },
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

/**
 * Animated Canvas Preview for Character / Kart / Saibaiman
 */
@Composable
private fun CharacterModelCanvasPreview(
    characterFamily: String,
    color: Color,
    colorNumber: Int,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize().padding(6.dp)) {
        val w = size.width
        val h = size.height

        when {
            characterFamily.contains("Saibaiman", ignoreCase = true) -> {
                // Draw Saibaiman face/silhouette
                // Head
                drawCircle(
                    color = color,
                    radius = w * 0.35f,
                    center = Offset(w * 0.5f, h * 0.5f)
                )
                // Red glowing eyes
                drawCircle(
                    color = Color.Red,
                    radius = w * 0.08f,
                    center = Offset(w * 0.38f, h * 0.45f)
                )
                drawCircle(
                    color = Color.Red,
                    radius = w * 0.08f,
                    center = Offset(w * 0.62f, h * 0.45f)
                )
                // Eye pupils
                drawCircle(
                    color = Color.Black,
                    radius = w * 0.03f,
                    center = Offset(w * 0.38f, h * 0.45f)
                )
                drawCircle(
                    color = Color.Black,
                    radius = w * 0.03f,
                    center = Offset(w * 0.62f, h * 0.45f)
                )
                // Mouth grin
                val mouthPath = Path().apply {
                    moveTo(w * 0.35f, h * 0.65f)
                    quadraticBezierTo(w * 0.5f, h * 0.78f, w * 0.65f, h * 0.65f)
                }
                drawPath(mouthPath, color = Color.Black)
            }
            characterFamily.contains("Goku", ignoreCase = true) -> {
                // Draw Goku SSJ3 long hair silhouette & face
                // Golden long spiky hair
                val hairPath = Path().apply {
                    moveTo(w * 0.2f, h * 0.15f)
                    lineTo(w * 0.5f, h * 0.05f)
                    lineTo(w * 0.8f, h * 0.15f)
                    lineTo(w * 0.9f, h * 0.6f)
                    lineTo(w * 0.7f, h * 0.9f)
                    lineTo(w * 0.3f, h * 0.9f)
                    lineTo(w * 0.1f, h * 0.6f)
                    close()
                }
                drawPath(hairPath, color = Color(0xFFFBBF24))

                // Gi body
                drawCircle(
                    color = color,
                    radius = w * 0.22f,
                    center = Offset(w * 0.5f, h * 0.7f)
                )
                // Face
                drawCircle(
                    color = Color(0xFFFFD7AA),
                    radius = w * 0.2f,
                    center = Offset(w * 0.5f, h * 0.45f)
                )
                // Cyan eyes
                drawCircle(
                    color = Color(0xFF06B6D4),
                    radius = w * 0.04f,
                    center = Offset(w * 0.42f, h * 0.45f)
                )
                drawCircle(
                    color = Color(0xFF06B6D4),
                    radius = w * 0.04f,
                    center = Offset(w * 0.58f, h * 0.45f)
                )
            }
            else -> {
                // General Fighter
                drawCircle(
                    color = color,
                    radius = w * 0.32f,
                    center = Offset(w * 0.5f, h * 0.5f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.8f),
                    radius = w * 0.1f,
                    center = Offset(w * 0.5f, h * 0.45f)
                )
            }
        }
    }
}
