package com.example.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Model3DMesh
import com.example.model.TextureRenderMode
import com.example.ui.theme.*
import com.example.util.DbzModel3DGenerator
import com.example.viewmodel.DbzTagTeamViewModel
import kotlin.math.*

@Composable
fun ObjPckModelViewer(
    viewModel: DbzTagTeamViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val models = viewModel.available3DModels.collectAsState().value
    val selectedModel = viewModel.selected3DModel.collectAsState().value
    val textureMode = viewModel.selectedTextureRenderMode.collectAsState().value
    val costumeSlotIndex = viewModel.selectedCostumeSlotIndex.collectAsState().value

    var yawAngle by remember { mutableFloatStateOf(0.4f) }
    var pitchAngle by remember { mutableFloatStateOf(0.1f) }
    var modelZoom by remember { mutableFloatStateOf(1.0f) }
    var showAuraGlow by remember { mutableStateOf(true) }
    var autoRotate by remember { mutableStateOf(false) }

    // Launcher for loading external .obj files from device storage
    val objPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val content = inputStream?.bufferedReader()?.use { reader -> reader.readText() } ?: ""
                val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "custom_model.obj"
                viewModel.loadCustomObjFile(fileName, content)
                Toast.makeText(context, "Modelo OBJ cargado: $fileName", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error al leer archivo OBJ: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Auto rotate effect
    LaunchedEffect(autoRotate) {
        if (autoRotate) {
            while (true) {
                kotlinx.coroutines.delay(16)
                yawAngle = (yawAngle + 0.02f) % (2 * Math.PI.toFloat())
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceDark)
            .border(1.dp, CardBorderDark, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF282420)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ViewInAr,
                        contentDescription = "3D Viewer",
                        tint = DragonAmber,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "VISOR 3D DE MODS OBJ & PCK 1",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Mallas 3D, Mapeo UV y Texturas DDS de Personajes",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            // Quick auto-rotate button
            IconButton(
                onClick = { autoRotate = !autoRotate },
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (autoRotate) DragonAmber else SurfaceVariantDark)
            ) {
                Icon(
                    imageVector = Icons.Default.Autorenew,
                    contentDescription = "Auto rotar",
                    tint = if (autoRotate) Color.Black else DragonAmber,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3D Canvas Viewport
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF16213A), Color(0xFF090D18)),
                        radius = 450f
                    )
                )
                .border(1.dp, DragonAmber.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        yawAngle = (yawAngle + dragAmount.x * 0.015f) % (2 * Math.PI.toFloat())
                        pitchAngle = (pitchAngle - dragAmount.y * 0.015f).coerceIn(-1.2f, 1.2f)
                    }
                }
                .testTag("viewport_3d_canvas")
        ) {
            // Real-time Canvas 3D projection
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val centerX = canvasWidth / 2f
                val centerY = canvasHeight / 2f
                val scale = min(canvasWidth, canvasHeight) * 0.42f * modelZoom

                val cosY = cos(yawAngle)
                val sinY = sin(yawAngle)
                val cosX = cos(pitchAngle)
                val sinX = sin(pitchAngle)

                // Project vertices into 2D camera space
                data class ProjectedVertex(val screenX: Float, val screenY: Float, val z: Float)
                val projectedVertices = selectedModel.vertices.map { v ->
                    // Yaw (around Y axis)
                    val x1 = v.x * cosY + v.z * sinY
                    val z1 = -v.x * sinY + v.z * cosY
                    // Pitch (around X axis)
                    val y2 = v.y * cosX - z1 * sinX
                    val z2 = v.y * sinX + z1 * cosX

                    val cameraDist = 3.2f
                    val factor = cameraDist / (cameraDist + z2)
                    ProjectedVertex(
                        screenX = centerX + x1 * factor * scale,
                        screenY = centerY + y2 * factor * scale,
                        z = z2
                    )
                }

                // Render dynamic Dragon Ball Ki Aura behind/around 3D character
                if (showAuraGlow) {
                    val auraBaseColor = Color(selectedModel.auraColorHex)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                auraBaseColor.copy(alpha = 0.35f),
                                auraBaseColor.copy(alpha = 0.12f),
                                Color.Transparent
                            ),
                            center = Offset(centerX, centerY),
                            radius = scale * 1.35f
                        ),
                        radius = scale * 1.35f,
                        center = Offset(centerX, centerY)
                    )

                    // Electric arcs / sparks for SSJ2 / MUI
                    val sparkPath = Path().apply {
                        moveTo(centerX - scale * 0.6f, centerY + scale * 0.4f)
                        lineTo(centerX - scale * 0.75f, centerY)
                        lineTo(centerX - scale * 0.5f, centerY - scale * 0.3f)
                        lineTo(centerX - scale * 0.65f, centerY - scale * 0.6f)
                    }
                    drawPath(
                        path = sparkPath,
                        color = Color.White.copy(alpha = 0.7f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                // Painter's algorithm: sort triangles back to front based on average Z
                data class RenderTriangle(
                    val v1: ProjectedVertex,
                    val v2: ProjectedVertex,
                    val v3: ProjectedVertex,
                    val avgZ: Float,
                    val baseTint: Long
                )

                val trianglesToDraw = selectedModel.triangles.mapNotNull { tri ->
                    if (tri.v1 < projectedVertices.size && tri.v2 < projectedVertices.size && tri.v3 < projectedVertices.size) {
                        val p1 = projectedVertices[tri.v1]
                        val p2 = projectedVertices[tri.v2]
                        val p3 = projectedVertices[tri.v3]
                        val avgZ = (p1.z + p2.z + p3.z) / 3f
                        RenderTriangle(p1, p2, p3, avgZ, tri.colorTint)
                    } else null
                }.sortedByDescending { it.avgZ }

                // Draw faces
                for (tri in trianglesToDraw) {
                    val path = Path().apply {
                        moveTo(tri.v1.screenX, tri.v1.screenY)
                        lineTo(tri.v2.screenX, tri.v2.screenY)
                        lineTo(tri.v3.screenX, tri.v3.screenY)
                        close()
                    }

                    when (textureMode) {
                        TextureRenderMode.WIREFRAME_3D -> {
                            drawPath(
                                path = path,
                                color = KiCyan,
                                style = Stroke(width = 1.5.dp.toPx())
                            )
                        }
                        TextureRenderMode.MANGA_INK -> {
                            // Manga Screentone black & white style
                            drawPath(path = path, color = Color.White)
                            drawPath(
                                path = path,
                                color = Color.Black,
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                        TextureRenderMode.ORIGINAL_PSP -> {
                            // Stepped low-poly retro shading
                            val paletteColor = Color(selectedModel.costumeColorSlots.getOrElse(costumeSlotIndex) { selectedModel.defaultColorHex })
                            val depthFactor = ((tri.avgZ + 1f) / 2f).coerceIn(0.4f, 1.0f)
                            val finalColor = Color(
                                red = paletteColor.red * depthFactor,
                                green = paletteColor.green * depthFactor,
                                blue = paletteColor.blue * depthFactor,
                                alpha = 0.95f
                            )
                            drawPath(path = path, color = finalColor)
                            drawPath(path = path, color = Color.Black.copy(alpha = 0.4f), style = Stroke(width = 1.dp.toPx()))
                        }
                        TextureRenderMode.CEL_SHADED_HD -> {
                            // Cel-Shading HD with light specular and bold anime contour
                            val slotColor = selectedModel.costumeColorSlots.getOrElse(costumeSlotIndex) { selectedModel.defaultColorHex }
                            val baseColor = if (tri.baseTint != 0xFFE2E8F0) Color(tri.baseTint) else Color(slotColor)
                            val lightFactor = if (tri.avgZ > 0f) 1.0f else 0.75f
                            val shadedColor = Color(
                                red = (baseColor.red * lightFactor).coerceIn(0f, 1f),
                                green = (baseColor.green * lightFactor).coerceIn(0f, 1f),
                                blue = (baseColor.blue * lightFactor).coerceIn(0f, 1f),
                                alpha = 1.0f
                            )
                            drawPath(path = path, color = shadedColor)
                            drawPath(
                                path = path,
                                color = Color(0xFF0F172A),
                                style = Stroke(width = 1.5.dp.toPx())
                            )
                        }
                    }
                }
            }

            // Overlay Badges & Stats
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${selectedModel.pckSlot} · ${selectedModel.polyCount} polígonos",
                        color = DragonAmber,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Textura: ${textureMode.label}",
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            // Touch interaction hint
            Text(
                text = "↺ Arrastra para rotar en 360°",
                color = TextSecondary.copy(alpha = 0.7f),
                fontSize = 10.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Character / Mesh Selector Carousel (PCK 1)
        Text(
            text = "PERSONAJES PCK 1 & MODELOS 3D:",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = DragonAmber,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(models, key = { it.id }) { model ->
                val isSelected = model.id == selectedModel.id
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            1.dp,
                            if (isSelected) DragonAmber else CardBorderDark,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.select3DModel(model.id) },
                    color = if (isSelected) Color(0xFF23304E) else SurfaceVariantDark
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(model.defaultColorHex))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = model.name,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Texture Mode Selector Chips
        Text(
            text = "MODO DE TEXTURIZADO Y SOMBREADO:",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = DragonAmber,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(TextureRenderMode.values()) { mode ->
                val isSelected = mode == textureMode
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setTextureRenderMode(mode) },
                    label = { Text(mode.label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DragonAmber,
                        selectedLabelColor = Color.Black,
                        containerColor = SurfaceVariantDark,
                        labelColor = TextSecondary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Color Palettes (Color 1, 2, 3, 4) with SFX POHD Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PALETA DE COLOR DEL MODELO:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = DragonAmber,
                letterSpacing = 0.5.sp
            )

            // Audio SFX POHD Preview Button for this color!
            OutlinedButton(
                onClick = {
                    viewModel.playColorPohdSfx("col_blue")
                    Toast.makeText(context, "SFX POHD 96kHz resonando en estéreo!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.height(28.dp),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = KiCyan, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("SFX POHD", fontSize = 10.sp, color = KiCyan, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val colorLabels = listOf("Color 1 (Normal)", "Color 2 (Alt)", "Color 3 (Manga)", "Color 4 (God Ki)")
            colorLabels.forEachIndexed { index, label ->
                val isSelected = costumeSlotIndex == index
                val swatchColor = Color(selectedModel.costumeColorSlots.getOrElse(index) { 0xFFF59E0B })
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            1.dp,
                            if (isSelected) DragonAmber else CardBorderDark,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { viewModel.setCostumeSlotIndex(index) },
                    color = if (isSelected) Color(0xFF23304E) else SurfaceVariantDark
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(swatchColor)
                                .border(1.dp, Color.White, CircleShape)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Color ${index + 1}",
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons Row: Load OBJ, Inject into PCK 1, Export
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Load OBJ file from Android storage
            OutlinedButton(
                onClick = {
                    objPickerLauncher.launch("*/*")
                },
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = KiCyan)
            ) {
                Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Cargar .OBJ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Inject into PCK 1
            Button(
                onClick = {
                    viewModel.injectTextureIntoPck1(selectedModel.id, textureMode)
                    Toast.makeText(context, "¡Textura y Malla 3D inyectadas en pzs3us1.pck (PCK 1)!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DragonAmber,
                    contentColor = Color.Black
                )
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Inyectar PCK 1", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
