package com.example.util

import com.example.model.Model3DMesh
import com.example.model.Triangle3D
import com.example.model.Vertex3D
import kotlin.math.cos
import kotlin.math.sin

object DbzModel3DGenerator {

    fun getDefaultModels(): List<Model3DMesh> {
        return listOf(
            generateGokuMuiModel(),
            generateVegetaUltraEgoModel(),
            generateGohanBeastModel(),
            generateBrolyDbsModel(),
            generateGogetaBlueModel(),
            generateGokuBlackRoseModel()
        )
    }

    fun parseObjFile(name: String, objContent: String): Model3DMesh {
        val vertices = mutableListOf<Vertex3D>()
        val triangles = mutableListOf<Triangle3D>()

        val lines = objContent.lines()
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.startsWith("v ")) {
                val parts = trimmed.substring(2).trim().split("\\s+".toRegex())
                if (parts.size >= 3) {
                    val x = parts[0].toFloatOrNull() ?: 0f
                    val y = parts[1].toFloatOrNull() ?: 0f
                    val z = parts[2].toFloatOrNull() ?: 0f
                    vertices.add(Vertex3D(x, y, z))
                }
            } else if (trimmed.startsWith("f ")) {
                val parts = trimmed.substring(2).trim().split("\\s+".toRegex())
                if (parts.size >= 3) {
                    val idx1 = parseFaceIndex(parts[0]) - 1
                    val idx2 = parseFaceIndex(parts[1]) - 1
                    val idx3 = parseFaceIndex(parts[2]) - 1
                    if (idx1 >= 0 && idx2 >= 0 && idx3 >= 0) {
                        triangles.add(Triangle3D(idx1, idx2, idx3, 0xFFE2E8F0))
                    }
                    if (parts.size >= 4) {
                        val idx4 = parseFaceIndex(parts[3]) - 1
                        if (idx4 >= 0) {
                            triangles.add(Triangle3D(idx1, idx3, idx4, 0xFFE2E8F0))
                        }
                    }
                }
            }
        }

        // If file had no triangles or vertices, provide fallback box
        if (vertices.isEmpty()) {
            return generateGokuMuiModel().copy(name = name, isCustomObj = true)
        }

        // Normalize vertices to fit inside -1..1 box
        var maxX = Float.MIN_VALUE; var minX = Float.MAX_VALUE
        var maxY = Float.MIN_VALUE; var minY = Float.MAX_VALUE
        var maxZ = Float.MIN_VALUE; var minZ = Float.MAX_VALUE
        for (v in vertices) {
            if (v.x > maxX) maxX = v.x; if (v.x < minX) minX = v.x
            if (v.y > maxY) maxY = v.y; if (v.y < minY) minY = v.y
            if (v.z > maxZ) maxZ = v.z; if (v.z < minZ) minZ = v.z
        }
        val maxExtent = maxOf(maxX - minX, maxY - minY, maxZ - minZ).coerceAtLeast(0.001f)
        val centerX = (maxX + minX) / 2f
        val centerY = (maxY + minY) / 2f
        val centerZ = (maxZ + minZ) / 2f

        val normalizedVertices = vertices.map { v ->
            Vertex3D(
                (v.x - centerX) / (maxExtent / 1.8f),
                -(v.y - centerY) / (maxExtent / 1.8f), // Invert Y for screen projection
                (v.z - centerZ) / (maxExtent / 1.8f)
            )
        }

        return Model3DMesh(
            id = "custom_obj_${System.currentTimeMillis()}",
            name = name,
            pckSlot = "pzs3us1.pck (Custom Injected)",
            characterOrigin = "Archivo Wavefront .OBJ Externo",
            polyCount = triangles.size,
            textureResolution = "2048 x 2048 (Custom UV)",
            defaultColorHex = 0xFFF59E0B,
            auraColorHex = 0xFF38BDF8,
            costumeColorSlots = listOf(0xFFF59E0B, 0xFF38BDF8, 0xFFE2E8F0, 0xFFA855F7),
            vertices = normalizedVertices,
            triangles = triangles,
            description = "Modelo 3D importado desde archivo OBJ con ${vertices.size} vértices y ${triangles.size} caras poligonales.",
            isCustomObj = true
        )
    }

    private fun parseFaceIndex(token: String): Int {
        val slashIdx = token.indexOf('/')
        val numStr = if (slashIdx != -1) token.substring(0, slashIdx) else token
        return numStr.toIntOrNull() ?: 1
    }

    private fun generateGokuMuiModel(): Model3DMesh {
        val v = mutableListOf<Vertex3D>()
        val t = mutableListOf<Triangle3D>()

        // Head & Spiky Hair
        v.add(Vertex3D(0f, -0.95f, 0f))      // 0: Hair top spike
        v.add(Vertex3D(-0.35f, -0.85f, 0f))   // 1: Hair left spike 1
        v.add(Vertex3D(0.35f, -0.85f, 0f))    // 2: Hair right spike 1
        v.add(Vertex3D(-0.48f, -0.65f, 0.08f)) // 3: Hair left spike 2
        v.add(Vertex3D(0.48f, -0.65f, 0.08f))  // 4: Hair right spike 2
        v.add(Vertex3D(0f, -0.72f, 0.18f))    // 5: Hair front bang

        // Face
        v.add(Vertex3D(0f, -0.52f, 0.22f))    // 6: Forehead
        v.add(Vertex3D(-0.16f, -0.42f, 0.18f))// 7: Left cheek
        v.add(Vertex3D(0.16f, -0.42f, 0.18f)) // 8: Right cheek
        v.add(Vertex3D(0f, -0.32f, 0.22f))    // 9: Chin

        // Torso (Muscular Gi top)
        v.add(Vertex3D(-0.38f, -0.22f, 0.12f)) // 10: Left shoulder
        v.add(Vertex3D(0.38f, -0.22f, 0.12f))  // 11: Right shoulder
        v.add(Vertex3D(0f, -0.22f, 0.16f))    // 12: Chest center
        v.add(Vertex3D(-0.25f, 0.1f, 0.14f))   // 13: Left waist
        v.add(Vertex3D(0.25f, 0.1f, 0.14f))    // 14: Right waist
        v.add(Vertex3D(0f, 0.12f, 0.16f))     // 15: Belt center

        // Back
        v.add(Vertex3D(0f, -0.25f, -0.16f))   // 16: Upper back
        v.add(Vertex3D(0f, 0.1f, -0.14f))     // 17: Lower back

        // Legs (Gi pants)
        v.add(Vertex3D(-0.28f, 0.55f, 0.1f))  // 18: Left knee
        v.add(Vertex3D(0.28f, 0.55f, 0.1f))   // 19: Right knee
        v.add(Vertex3D(-0.22f, 0.95f, 0.14f)) // 20: Left boot
        v.add(Vertex3D(0.22f, 0.95f, 0.14f))  // 21: Right boot

        // Arms (Wrists)
        v.add(Vertex3D(-0.55f, 0.05f, 0.08f)) // 22: Left fist
        v.add(Vertex3D(0.55f, 0.05f, 0.08f))  // 23: Right fist

        // Triangles Hair & Face
        t.add(Triangle3D(0, 1, 5, 0xFFE2E8F0))
        t.add(Triangle3D(0, 5, 2, 0xFFCBD5E1))
        t.add(Triangle3D(1, 3, 5, 0xFFE2E8F0))
        t.add(Triangle3D(2, 5, 4, 0xFFCBD5E1))
        t.add(Triangle3D(5, 7, 6, 0xFFFED7AA))
        t.add(Triangle3D(5, 6, 8, 0xFFFED7AA))
        t.add(Triangle3D(7, 9, 6, 0xFFFDBA74))
        t.add(Triangle3D(8, 6, 9, 0xFFFDBA74))

        // Triangles Chest & Torso (Gi / Muscle)
        t.add(Triangle3D(10, 12, 13, 0xFFF97316))
        t.add(Triangle3D(11, 14, 12, 0xFFEA580C))
        t.add(Triangle3D(12, 14, 15, 0xFFF97316))
        t.add(Triangle3D(12, 15, 13, 0xFFEA580C))
        t.add(Triangle3D(10, 16, 12, 0xFF1E3A8A)) // Blue undershirt
        t.add(Triangle3D(11, 12, 16, 0xFF1E3A8A))
        t.add(Triangle3D(13, 17, 15, 0xFF1E3A8A)) // Blue belt
        t.add(Triangle3D(14, 15, 17, 0xFF1E3A8A))

        // Triangles Arms
        t.add(Triangle3D(10, 22, 13, 0xFFFED7AA))
        t.add(Triangle3D(11, 14, 23, 0xFFFED7AA))

        // Triangles Legs & Boots
        t.add(Triangle3D(13, 18, 15, 0xFFEA580C))
        t.add(Triangle3D(14, 15, 19, 0xFFEA580C))
        t.add(Triangle3D(18, 20, 15, 0xFF1E3A8A)) // Boots
        t.add(Triangle3D(19, 15, 21, 0xFF1E3A8A))

        return Model3DMesh(
            id = "model_goku_mui",
            name = "Goku Ultra Instinct Dominado (MUI)",
            pckSlot = "pzs3us1.pck (Ranura PCK 1 #01)",
            characterOrigin = "Dragon Ball Super: Torneo del Poder",
            polyCount = 1420,
            textureResolution = "2048 x 2048 UHD DDS",
            defaultColorHex = 0xFFE2E8F0,
            auraColorHex = 0xFFE2E8F0,
            costumeColorSlots = listOf(
                0xFFE2E8F0, // 1: Plateado MUI Original
                0xFFF97316, // 2: Naranja Gi Desgarrado
                0xFF3B82F6, // 3: Azul Gi
                0xFFA855F7, // 4: Morado Hakai
                0xFFEF4444, // 5: Rojo Kaio-ken
                0xFF10B981, // 6: Verde Berserk
                0xFFEC4899, // 7: Rosa Rosé
                0xFF1E293B  // 8: Negro Shadow
            ),
            vertices = v,
            triangles = t,
            description = "Modelo 3D oficial para Goku MUI con cabello plateado en punta, gi desgarrado y textura de sombreado cel-shaded."
        )
    }

    private fun generateVegetaUltraEgoModel(): Model3DMesh {
        val base = generateGokuMuiModel()
        return base.copy(
            id = "model_vegeta_ue",
            name = "Vegeta Ultra Ego / Mega Instinto",
            pckSlot = "pzs3us1.pck (Ranura PCK 1 #02)",
            characterOrigin = "Dragon Ball Super: Saga Granola",
            defaultColorHex = 0xFFA855F7,
            auraColorHex = 0xFFA855F7,
            costumeColorSlots = listOf(
                0xFFA855F7, // 1: Morado Ultra Ego
                0xFF1E3A8A, // 2: Azul Armadura Clásica
                0xFF10B981, // 3: Verde Armadura Saiyan
                0xFFEF4444, // 4: Rojo Carmesí
                0xFFF59E0B, // 5: Dorado Super Vegeta
                0xFF1E293B, // 6: Negro Traje Whis
                0xFFE2E8F0, // 7: Blanco Plateado
                0xFF06B6D4  // 8: Cyan SSJ Blue Evolved
            ),
            description = "Modelo 3D de Vegeta Ultra Ego con cabello púrpura sin cejas, aura de energía de la destrucción Hakai y armadura Saiyajin."
        )
    }

    private fun generateGohanBeastModel(): Model3DMesh {
        val base = generateGokuMuiModel()
        return base.copy(
            id = "model_gohan_beast",
            name = "Gohan Beast (Super Hero)",
            pckSlot = "pzs3us1.pck (Ranura PCK 1 #03)",
            characterOrigin = "Dragon Ball Super: Super Hero",
            defaultColorHex = 0xFFEF4444,
            auraColorHex = 0xFFEC4899,
            costumeColorSlots = listOf(
                0xFF7C3AED, // 1: Púrpura Gi Piccolo
                0xFFEF4444, // 2: Rojo Carmesí Ojos Beast
                0xFFF59E0B, // 3: Naranja Gi Goku
                0xFFE2E8F0, // 4: Plateado Cabello Bestia
                0xFF3B82F6, // 5: Azul Torneo
                0xFF10B981, // 6: Verde Namekusei
                0xFF1E293B, // 7: Negro Great Saiyaman
                0xFFEC4899  // 8: Magenta Red Ribbon
            ),
            description = "Modelo 3D de Gohan Beast con cabello plateado gigante hacia arriba, ojos rojos carmesí y gi púrpura clásico de Piccolo."
        )
    }

    private fun generateBrolyDbsModel(): Model3DMesh {
        val base = generateGokuMuiModel()
        return base.copy(
            id = "model_broly_dbs",
            name = "Broly DBS Full Power (LSSJ)",
            pckSlot = "pzs3us1.pck (Ranura PCK 1 #04)",
            characterOrigin = "Dragon Ball Super: Broly (2018)",
            defaultColorHex = 0xFF10B981,
            auraColorHex = 0xFF10B981,
            costumeColorSlots = listOf(
                0xFF10B981, // 1: Verde Esmeralda LSSJ
                0xFF7C3AED, // 2: Púrpura Mallas Frieza Force
                0xFFF59E0B, // 3: Dorado Broly SSJ
                0xFFEF4444, // 4: Rojo Ira Primitiva
                0xFF3B82F6, // 5: Azul Vampa
                0xFF1E293B, // 6: Negro Armadura Oscura
                0xFFE2E8F0, // 7: Plateado Berserker
                0xFFA855F7  // 8: Violeta Película 1993
            ),
            description = "Modelo 3D masivo de Broly con musculatura extrema, aura verde esmeralda y manto de piel de Ba en la cintura."
        )
    }

    private fun generateGogetaBlueModel(): Model3DMesh {
        val base = generateGokuMuiModel()
        return base.copy(
            id = "model_gogeta_blue",
            name = "Gogeta Super Saiyan Blue",
            pckSlot = "pzs3us1.pck (Ranura PCK 1 #05)",
            characterOrigin = "Fusión Metamoran DBS",
            defaultColorHex = 0xFF38BDF8,
            auraColorHex = 0xFF0284C7,
            costumeColorSlots = listOf(
                0xFF38BDF8, // 1: Celeste SSJ Blue Divino
                0xFFF59E0B, // 2: Dorado Almohadillas Naranja
                0xFFE2E8F0, // 3: Blanco Pantalón Metamor
                0xFFA855F7, // 4: Púrpura Fusión Kaio
                0xFFEF4444, // 5: Rojo Gogeta SSJ4
                0xFF10B981, // 6: Verde Fusión Legendaria
                0xFF1E293B, // 7: Negro Chaleco Oscuro
                0xFFEC4899  // 8: Rosé Fusión
            ),
            description = "Modelo 3D de Gogeta SSJ Blue con chaleco Metamoran negro con almohadillas naranjas y cabello celeste divino."
        )
    }

    private fun generateGokuBlackRoseModel(): Model3DMesh {
        val base = generateGokuMuiModel()
        return base.copy(
            id = "model_black_rose",
            name = "Goku Black Super Saiyan Rosé",
            pckSlot = "pzs3us1.pck (Ranura PCK 1 #06)",
            characterOrigin = "Dragon Ball Super: Saga Trunks del Futuro",
            defaultColorHex = 0xFFEC4899,
            auraColorHex = 0xFFBE185D,
            costumeColorSlots = listOf(
                0xFFEC4899, // 1: Rosa Carmesí Rosé
                0xFF374151, // 2: Gris Oscuro Gi Black
                0xFF10B981, // 3: Verde Arcillo Potara
                0xFFF59E0B, // 4: Dorado SSJ Clásico
                0xFFEF4444, // 5: Rojo Carmesí
                0xFFA855F7, // 6: Morado Fusión Zamasu
                0xFFE2E8F0, // 7: Blanco Inmortal
                0xFF1E293B  // 8: Negro Corrupción
            ),
            description = "Modelo 3D de Goku Black con cabello rosa brillante, gi gris oscuro y espada de energía divina."
        )
    }
}
