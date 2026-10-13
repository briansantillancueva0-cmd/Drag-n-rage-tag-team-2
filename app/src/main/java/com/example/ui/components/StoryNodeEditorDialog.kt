package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Fighter
import com.example.model.StoryDialogLine
import com.example.model.StoryMission
import com.example.model.StorySaga
import com.example.ui.theme.*
import com.example.viewmodel.DbzTagTeamViewModel

@Composable
fun StoryNodeEditorDialog(
    saga: StorySaga,
    viewModel: DbzTagTeamViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedMission by remember { mutableStateOf<StoryMission?>(saga.missions.firstOrNull()) }
    var isEditingDetails by remember { mutableStateOf(false) }

    // Mission edit fields
    var editTitle by remember { mutableStateOf("") }
    var editStage by remember { mutableStateOf("") }
    var editObjective by remember { mutableStateOf("") }
    var editReward by remember { mutableStateOf("") }
    var editNodeType by remember { mutableStateOf("COMBATE") }
    var isBoss by remember { mutableStateOf(false) }

    LaunchedEffect(selectedMission) {
        selectedMission?.let { m ->
            editTitle = m.title
            editStage = m.stageName
            editObjective = m.objectiveDescription
            editReward = m.rewardRewardStr
            editNodeType = m.nodeType
            isBoss = m.isBossBattle
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.92f)
            .testTag("story_node_editor_dialog"),
        containerColor = Color(0xFF0F172A),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountTree,
                        contentDescription = "Editor de Nodos",
                        tint = DragonAmber,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "EDITOR DE NODOS · MODO HISTORIA",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = saga.title,
                            fontSize = 11.sp,
                            color = DragonAmber
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top control tabs / bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MAPA DE NODOS (DRAGON WALKER):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )

                    // Add new node button
                    Button(
                        onClick = {
                            val nextChapterNum = (saga.missions.maxOfOrNull { it.chapterNumber } ?: 0) + 1
                            val newMission = StoryMission(
                                id = "m_custom_${System.currentTimeMillis()}",
                                title = "Capítulo $nextChapterNum: Batalla What-If",
                                sagaEra = saga.title,
                                chapterNumber = nextChapterNum,
                                p1Fighter = Fighter("goku_custom", "Goku", "Guerrero Z", 1200f, 1200f, 100f, 60f, "Super Kamehameha", 60f, 0xFFF97316),
                                p2Fighter = Fighter("vegeta_custom", "Vegeta", "Super Saiyajin", 1150f, 1150f, 100f, 60f, "Final Flash", 60f, 0xFF3B82F6),
                                cpu1Fighter = Fighter("broly_custom", "Broly", "LSSJ Berserk", 1600f, 1600f, 100f, 90f, "Blaster Meteor", 70f, 0xFF10B981),
                                cpu2Fighter = null,
                                stageName = "Planeta en Ruinas",
                                objectiveDescription = "Derrota al rival legendario usando el trabajo en equipo de Tag Team.",
                                dialogLines = listOf(
                                    StoryDialogLine("Goku", "¡No nos rendiremos jamás!", "goku"),
                                    StoryDialogLine("Broly", "¡¡¡KAKAROTTOOO!!!", "broly")
                                ),
                                isCompleted = false,
                                rankGrade = "Z",
                                rewardRewardStr = "Desbloqueado: Cápsula Potara What-If + 30,000 Zeni",
                                isBossBattle = false,
                                nodeX = (saga.missions.size * 0.25f + 0.15f) % 0.85f,
                                nodeY = 0.5f,
                                nextNodeIds = emptyList(),
                                nodeType = "WHAT_IF"
                            )
                            viewModel.addMissionNode(saga.id, newMission)
                            selectedMission = newMission
                            Toast.makeText(context, "¡Nuevo nodo de misión añadido!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DragonAmber, contentColor = Color.Black)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Agregar Nodo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Interactive 2D Graph Map Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF090D18))
                        .border(1.dp, CardBorderDark, RoundedCornerShape(14.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Grid lines for graph
                        for (i in 1..4) {
                            drawLine(
                                color = Color(0xFF1E293B).copy(alpha = 0.5f),
                                start = Offset(0f, h * (i / 5f)),
                                end = Offset(w, h * (i / 5f)),
                                strokeWidth = 1f
                            )
                            drawLine(
                                color = Color(0xFF1E293B).copy(alpha = 0.5f),
                                start = Offset(w * (i / 5f), 0f),
                                end = Offset(w * (i / 5f), h),
                                strokeWidth = 1f
                            )
                        }

                        // Draw connection paths between mission nodes
                        for (mission in saga.missions) {
                            val startX = (mission.nodeX * w).coerceIn(20f, w - 20f)
                            val startY = (mission.nodeY * h).coerceIn(20f, h - 20f)

                            for (nextId in mission.nextNodeIds) {
                                val target = saga.missions.firstOrNull { it.id == nextId }
                                if (target != null) {
                                    val endX = (target.nodeX * w).coerceIn(20f, w - 20f)
                                    val endY = (target.nodeY * h).coerceIn(20f, h - 20f)

                                    // Draw arrow connection line
                                    val path = Path().apply {
                                        moveTo(startX, startY)
                                        cubicTo(
                                            (startX + endX) / 2f, startY,
                                            (startX + endX) / 2f, endY,
                                            endX, endY
                                        )
                                    }
                                    drawPath(
                                        path = path,
                                        color = if (mission.nodeType == "JEFE") Color(0xFFEF4444) else DragonAmber,
                                        style = Stroke(width = 2.5.dp.toPx())
                                    )
                                }
                            }
                        }
                    }

                    // Nodes representation overlay
                    saga.missions.forEach { mission ->
                        val isSelected = mission.id == selectedMission?.id
                        Box(
                            modifier = Modifier
                                .offset(
                                    x = ((mission.nodeX * 280).dp).coerceIn(10.dp, 280.dp),
                                    y = ((mission.nodeY * 110).dp).coerceIn(10.dp, 120.dp)
                                )
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        mission.isBossBattle -> Color(0xFFEF4444)
                                        mission.nodeType == "WHAT_IF" -> Color(0xFFA855F7)
                                        isSelected -> DragonAmber
                                        else -> SurfaceVariantDark
                                    }
                                )
                                .border(
                                    2.dp,
                                    if (isSelected) Color.White else Color(saga.colorHex),
                                    CircleShape
                                )
                                .clickable { selectedMission = mission },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${mission.chapterNumber}",
                                color = if (isSelected && !mission.isBossBattle) Color.Black else Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Node Selector Carousel
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(saga.missions, key = { it.id }) { mission ->
                        val isSelected = mission.id == selectedMission?.id
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, if (isSelected) DragonAmber else CardBorderDark, RoundedCornerShape(10.dp))
                                .clickable { selectedMission = mission },
                            color = if (isSelected) Color(0xFF1E283F) else SurfaceDark
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Nodo ${mission.chapterNumber}: ${mission.title.take(18)}...",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Node Inspector & Edit Panel
                selectedMission?.let { current ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceDark)
                            .padding(12.dp)
                    ) {
                        item {
                            Text(
                                text = "PARÁMETROS DEL NODO SELECCIONADO:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DragonAmber
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Chapter Title
                            OutlinedTextField(
                                value = editTitle,
                                onValueChange = { editTitle = it },
                                label = { Text("Título de la Misión") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            // Stage
                            OutlinedTextField(
                                value = editStage,
                                onValueChange = { editStage = it },
                                label = { Text("Escenario / Arena") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            // Objective
                            OutlinedTextField(
                                value = editObjective,
                                onValueChange = { editObjective = it },
                                label = { Text("Condición de Victoria / Objetivo") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            // Reward
                            OutlinedTextField(
                                value = editReward,
                                onValueChange = { editReward = it },
                                label = { Text("Recompensa al Ganar") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Node Type & Boss Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = isBoss,
                                        onCheckedChange = { isBoss = it }
                                    )
                                    Text("Es Batalla de Jefe (Boss)", fontSize = 12.sp, color = Color.White)
                                }

                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Tipo: $editNodeType",
                                        fontSize = 11.sp,
                                        color = DragonAmber,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Action buttons: Save node, Connect next node, Delete node
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val updated = current.copy(
                                            title = editTitle,
                                            stageName = editStage,
                                            objectiveDescription = editObjective,
                                            rewardRewardStr = editReward,
                                            isBossBattle = isBoss
                                        )
                                        viewModel.updateMissionNode(saga.id, updated)
                                        selectedMission = updated
                                        Toast.makeText(context, "¡Nodo guardado exitosamente!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Guardar", fontSize = 12.sp)
                                }

                                // Delete node
                                OutlinedButton(
                                    onClick = {
                                        viewModel.deleteMissionNode(saga.id, current.id)
                                        selectedMission = saga.missions.firstOrNull { it.id != current.id }
                                        Toast.makeText(context, "Nodo eliminado de la saga", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(0.8f),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Eliminar", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = DragonAmber, contentColor = Color.Black)
            ) {
                Text("Listo", fontWeight = FontWeight.Bold)
            }
        }
    )
}
