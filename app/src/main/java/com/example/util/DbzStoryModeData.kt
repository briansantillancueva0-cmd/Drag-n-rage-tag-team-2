package com.example.util

import com.example.model.Fighter
import com.example.model.StoryDialogLine
import com.example.model.StoryMission
import com.example.model.StorySaga

object DbzStoryModeData {

    fun getAllSagas(): List<StorySaga> {
        return listOf(
            StorySaga(
                id = "saga_saiyan",
                title = "Saga Saiyajin",
                period = "Año 762 · Planeta Tierra",
                description = "Los guerreros Saiyajin Raditz, Nappa y el Príncipe Vegeta invaden la Tierra en busca de las Esferas del Dragón.",
                colorHex = 0xFFF59E0B,
                missions = listOf(
                    StoryMission(
                        id = "m_saiyan_1",
                        title = "Llegada Inesperada: Raditz",
                        sagaEra = "Saga Saiyajin",
                        chapterNumber = 1,
                        p1Fighter = Fighter("goku_base", "Goku", "Guerrero Z", 1000f, 1000f, 100f, 40f, "Kamehameha", 50f, 0xFFF97316),
                        p2Fighter = Fighter("piccolo_base", "Piccolo", "Namekuseijin", 950f, 950f, 100f, 50f, "Makankosappo", 60f, 0xFF10B981),
                        cpu1Fighter = Fighter("raditz_base", "Raditz", "Saiyajin Invasor", 1050f, 1050f, 100f, 50f, "Doble Domingo", 50f, 0xFF8B5CF6),
                        cpu2Fighter = null,
                        stageName = "Llanuras Rocosas (Tierra)",
                        objectiveDescription = "Inmoviliza a Raditz y ejecuta el Makankosappo con Piccolo.",
                        dialogLines = listOf(
                            StoryDialogLine("Raditz", "¿Kakarotto? ¿Cómo pudiste olvidar tu misión de conquistar este planeta?", "raditz"),
                            StoryDialogLine("Goku", "¡Mi nombre es Goku y este mundo es mi hogar!", "goku"),
                            StoryDialogLine("Piccolo", "Prepárate Raditz, no saldrás vivo de este lugar.", "piccolo")
                        ),
                        isCompleted = true,
                        rankGrade = "Z",
                        rewardRewardStr = "Desbloqueado: Goku (Kaio-ken) + 15,000 D-Points"
                    ),
                    StoryMission(
                        id = "m_saiyan_2",
                        title = "La Furia de los Guerreros Z contra Nappa",
                        sagaEra = "Saga Saiyajin",
                        chapterNumber = 2,
                        p1Fighter = Fighter("goku_kaioken", "Goku", "Kaio-ken x3", 1050f, 1050f, 100f, 60f, "Kaio-ken Kamehameha", 60f, 0xFFEF4444),
                        p2Fighter = Fighter("gohan_kid", "Gohan", "Niño", 850f, 850f, 100f, 30f, "Masenko", 40f, 0xFFFBBF24),
                        cpu1Fighter = Fighter("nappa_base", "Nappa", "Guerrero de Élite", 1100f, 1100f, 100f, 40f, "Cañón Rompedor", 55f, 0xFF6B7280),
                        cpu2Fighter = null,
                        stageName = "Campo de Batalla Rocoso",
                        objectiveDescription = "Derrota al gigante Nappa antes de que venza a Gohan.",
                        dialogLines = listOf(
                            StoryDialogLine("Nappa", "¡Jajaja! ¿Este mocoso cree que puede tocarme?", "nappa"),
                            StoryDialogLine("Goku", "¡Nappa! ¡Pagarás por lo que le hiciste a Yamcha, Chaoz y Ten Shin Han!", "goku")
                        ),
                        isCompleted = true,
                        rankGrade = "S",
                        rewardRewardStr = "Cápsula Potara Z: Espíritu de Lucha + 20,000 D-Points"
                    ),
                    StoryMission(
                        id = "m_saiyan_3",
                        title = "Duelo Legendario en el Páramo: Vegeta",
                        sagaEra = "Saga Saiyajin",
                        chapterNumber = 3,
                        p1Fighter = Fighter("goku_kaioken", "Goku", "Kaio-ken x4", 1000f, 1000f, 100f, 70f, "Kamehameha Kaio-ken x4", 70f, 0xFFEF4444),
                        p2Fighter = null,
                        cpu1Fighter = Fighter("vegeta_scouter", "Vegeta", "Príncipe Saiyajin", 1200f, 1200f, 100f, 60f, "Cañón Galick", 60f, 0xFF3B82F6),
                        cpu2Fighter = null,
                        stageName = "Desierto de Rocas Rocosas",
                        objectiveDescription = "Contrarresta el Cañón Galick de Vegeta con el Kamehameha Kaio-ken x4.",
                        dialogLines = listOf(
                            StoryDialogLine("Vegeta", "¡Un guerrero de clase baja como tú jamás podrá superar al Príncipe de los Saiyajin!", "vegeta"),
                            StoryDialogLine("Goku", "¡Incluso un don nadie puede superar a un élite si entrena lo suficiente!", "goku"),
                            StoryDialogLine("Vegeta", "¡Desaparece junto con esta esfera miserable! ¡CAÑÓN GALICK!", "vegeta")
                        ),
                        isCompleted = true,
                        rankGrade = "Z",
                        rewardRewardStr = "Desbloqueado: Vegeta (Scouter) + Traje Alternativo Color 2",
                        isBossBattle = true
                    )
                )
            ),
            StorySaga(
                id = "saga_frieza",
                title = "Saga de Namek / Freezer",
                period = "Año 762 · Planeta Namek",
                description = "En un planeta alienígena Namek con tres soles, el tirano galáctico Freezer masacra a los habitantes en busca de la inmortalidad.",
                colorHex = 0xFF3B82F6,
                missions = listOf(
                    StoryMission(
                        id = "m_frieza_1",
                        title = "Fuerzas Especiales Ginyu",
                        sagaEra = "Saga de Namek",
                        chapterNumber = 4,
                        p1Fighter = Fighter("goku_base", "Goku", "Llegada a Namek", 1050f, 1050f, 100f, 50f, "Golpe Meteoro", 50f, 0xFFF97316),
                        p2Fighter = Fighter("vegeta_namek", "Vegeta", "Namek", 980f, 980f, 100f, 40f, "Ataque Big Bang", 55f, 0xFF3B82F6),
                        cpu1Fighter = Fighter("ginyu", "Capitán Ginyu", "Fuerzas Ginyu", 1000f, 1000f, 100f, 50f, "Cambio de Cuerpo", 60f, 0xFF8B5CF6),
                        cpu2Fighter = Fighter("recoome", "Recoome", "Fuerzas Ginyu", 950f, 950f, 100f, 40f, "Ultra Ataque Recoome", 50f, 0xFFF97316),
                        stageName = "Planeta Namek (Campos Verdes)",
                        objectiveDescription = "Vence a las Fuerzas Ginyu antes de que roben las Esferas del Dragón.",
                        dialogLines = listOf(
                            StoryDialogLine("Ginyu", "¡Somos las Fuerzas Especiales Ginyu! ¡Prepárate!", "ginyu"),
                            StoryDialogLine("Goku", "Sus niveles de Ki son impresionantes, pero no retrocederé.", "goku")
                        ),
                        isCompleted = false,
                        rankGrade = "A",
                        rewardRewardStr = "Cápsula: Semilla del Ermitaño + 25,000 D-Points"
                    ),
                    StoryMission(
                        id = "m_frieza_2",
                        title = "El Super Saiyajin Legendario",
                        sagaEra = "Saga de Namek",
                        chapterNumber = 5,
                        p1Fighter = Fighter("goku_ssj", "Goku", "Super Saiyajin", 1200f, 1200f, 100f, 70f, "Super Kamehameha", 60f, 0xFFFBBF24),
                        p2Fighter = null,
                        cpu1Fighter = Fighter("frieza_final", "Freezer", "Forma Final 100%", 1250f, 1250f, 100f, 70f, "Disco Destructor Mortal", 65f, 0xFFA855F7),
                        cpu2Fighter = null,
                        stageName = "Planeta Namek Destruido (Lava y Rayos)",
                        objectiveDescription = "Destruye a Freezer en Namek antes de que el núcleo del planeta estalle.",
                        dialogLines = listOf(
                            StoryDialogLine("Freezer", "¡Maldito simio estúpido! ¿Quién demonios eres tú?", "frieza"),
                            StoryDialogLine("Goku", "Soy el saiyajin que vino desde la Tierra con el propósito de liquidarte. Soy el legendario Super Saiyajin.", "goku"),
                            StoryDialogLine("Freezer", "¡A este planeta le quedan sólo cinco minutos de vida!", "frieza")
                        ),
                        isCompleted = false,
                        rankGrade = "Z",
                        rewardRewardStr = "Desbloqueado: Goku SSJ + Planeta Namek Destruido (Escenario)",
                        isBossBattle = true
                    )
                )
            ),
            StorySaga(
                id = "saga_cell",
                title = "Saga de los Androides y Cell",
                period = "Año 767 · Torneo de Cell",
                description = "El Dr. Gero crea androides bio-mecánicos con las células de los mejores luchadores del universo para matar a Son Goku.",
                colorHex = 0xFF10B981,
                missions = listOf(
                    StoryMission(
                        id = "m_cell_1",
                        title = "El Torneo de Cell: Kamehameha Padre e Hijo",
                        sagaEra = "Saga de Cell",
                        chapterNumber = 6,
                        p1Fighter = Fighter("gohan_ssj2", "Gohan", "Super Saiyajin 2", 1300f, 1300f, 100f, 80f, "Kamehameha Padre e Hijo", 70f, 0xFFFBBF24),
                        p2Fighter = Fighter("goku_ghost", "Goku", "Alma del Más Allá", 1000f, 1000f, 100f, 50f, "Kamehameha Instantáneo", 50f, 0xFF60A5FA),
                        cpu1Fighter = Fighter("cell_perfect", "Cell", "Forma Super Perfecta", 1350f, 1350f, 100f, 80f, "Solar Kamehameha", 70f, 0xFF22C55E),
                        cpu2Fighter = null,
                        stageName = "Ring del Torneo de Cell (Destruido)",
                        objectiveDescription = "Libera la furia dormida de Gohan SSJ2 y choca tu Kamehameha contra el Solar Kamehameha de Cell.",
                        dialogLines = listOf(
                            StoryDialogLine("Cell", "¡Este es el final de todo, Gohan! ¡Desaparecerás tú y todo el Sistema Solar!", "cell"),
                            StoryDialogLine("Goku", "¡Gohan, no tengas miedo! ¡Confía en el poder que llevas dentro!", "goku"),
                            StoryDialogLine("Gohan", "¡¡¡HAAAAA!!!", "gohan")
                        ),
                        isCompleted = false,
                        rankGrade = "Z",
                        rewardRewardStr = "Desbloqueado: Gohan SSJ2 Joven + Ring de Cell (Escenario)",
                        isBossBattle = true
                    )
                )
            ),
            StorySaga(
                id = "saga_buu",
                title = "Saga de Majin Buu",
                period = "Año 774 · Tierra y Mundo Sagrado",
                description = "El hechicero Babidi despierta a la criatura ancestral más destructiva: el demonio Majin Buu.",
                colorHex = 0xFFEC4899,
                missions = listOf(
                    StoryMission(
                        id = "m_buu_1",
                        title = "La Fusión Suprema: Vegetto",
                        sagaEra = "Saga de Majin Buu",
                        chapterNumber = 7,
                        p1Fighter = Fighter("vegetto_base", "Vegetto", "Super Saiyajin", 1400f, 1400f, 100f, 90f, "Espada de Espíritu", 65f, 0xFF38BDF8),
                        p2Fighter = null,
                        cpu1Fighter = Fighter("super_buu", "Super Buu", "Gohan Absorbido", 1380f, 1380f, 100f, 80f, "Super Kamehameha Rosa", 70f, 0xFFEC4899),
                        cpu2Fighter = null,
                        stageName = "Planeta Sagrado de los Kaios",
                        objectiveDescription = "Humilla a Super Buu usando la agilidad y técnicas de la Fusión Potara.",
                        dialogLines = listOf(
                            StoryDialogLine("Vegetto", "¡Listo! La combinación entre Vegeta y Kakarotto... ¡Yo soy Vegetto!", "vegetto"),
                            StoryDialogLine("Super Buu", "¡No te creas la gran cosa por haberte fusionado!", "buu")
                        ),
                        isCompleted = false,
                        rankGrade = "Z",
                        rewardRewardStr = "Desbloqueado: Vegetto Super Saiyajin + 35,000 D-Points",
                        isBossBattle = true
                    )
                )
            ),
            StorySaga(
                id = "saga_broly",
                title = "Saga de Broly: El Super Saiyajin Legendario",
                period = "Año 767 / 780 · Nuevo Planeta Vegeta & Vampa Ártico",
                description = "El guerrero de poder ilimitado despierta. Broly desata su ira incontrolable con su legendario ki verde esmeralda contra los Guerreros Z y la fusión Gogeta.",
                colorHex = 0xFF10B981,
                isBrolySaga = true,
                missions = listOf(
                    StoryMission(
                        id = "m_broly_1",
                        title = "Estalla el Duelo: Trampa en el Nuevo Planeta Vegeta",
                        sagaEra = "Saga de Broly",
                        chapterNumber = 10,
                        p1Fighter = Fighter("goku_ssj", "Goku", "Super Saiyajin", 1100f, 1100f, 100f, 65f, "Super Kamehameha", 60f, 0xFFFBBF24),
                        p2Fighter = Fighter("vegeta_ssj", "Vegeta", "Super Saiyajin", 1080f, 1080f, 100f, 60f, "Big Bang Attack", 60f, 0xFF38BDF8),
                        cpu1Fighter = Fighter("broly_restrained", "Broly", "Super Saiyajin Restringido", 1300f, 1300f, 100f, 75f, "Disparador de Trampa", 60f, 0xFF10B981),
                        cpu2Fighter = Fighter("paragus", "Paragus", "Comandante Saiyajin", 900f, 900f, 100f, 30f, "Disparo Mortal", 40f, 0xFF64748B),
                        stageName = "Palacio Real del Nuevo Planeta Vegeta",
                        objectiveDescription = "Descubre la trampa de Paragus y resiste el descomunal poder de Broly antes de que el cometa Gumori colisione.",
                        dialogLines = listOf(
                            StoryDialogLine("Paragus", "¡Jajaja! ¡Los saiyajin de la Tierra caerán en esta trampa y mi hijo gobernará la galaxia!", "paragus"),
                            StoryDialogLine("Vegeta", "¡¿Un guerrero legendario?! ¡No existe nadie superior al Príncipe Vegeta!", "vegeta"),
                            StoryDialogLine("Broly", "Kakarotto... Kakarotto... ¡¡¡KAKAROTTOOO!!!", "broly")
                        ),
                        isCompleted = false,
                        rankGrade = "Z",
                        rewardRewardStr = "Desbloqueado: Broly Restringido + Nuevo Planeta Vegeta (Escenario)",
                        isBossBattle = false,
                        nodeX = 0.2f,
                        nodeY = 0.35f,
                        nextNodeIds = listOf("m_broly_2"),
                        nodeType = "COMBATE"
                    ),
                    StoryMission(
                        id = "m_broly_2",
                        title = "Poder Ilimitado: El Despertar del Legendario Super Saiyajin",
                        sagaEra = "Saga de Broly",
                        chapterNumber = 11,
                        p1Fighter = Fighter("goku_zpower", "Goku", "Z-Fighter Power Boost", 1250f, 1250f, 100f, 85f, "Puño Dragón Milagroso", 70f, 0xFFF59E0B),
                        p2Fighter = Fighter("piccolo_sync", "Piccolo", "Guerrero Namekiano", 1050f, 1050f, 100f, 60f, "Makankosappo", 60f, 0xFF10B981),
                        cpu1Fighter = Fighter("broly_lssj", "Broly", "Super Saiyajin Legendario (LSSJ)", 1650f, 1650f, 100f, 95f, "Omega Blaster Gigantesco", 75f, 0xFF10B981),
                        cpu2Fighter = null,
                        stageName = "Ruinas Volcánicas del Planeta en Llamas",
                        objectiveDescription = "Reúne la energía de tus compañeros caídos para derribar al titán indestructible antes de que destruya el planeta.",
                        dialogLines = listOf(
                            StoryDialogLine("Broly", "¡Mi ki está aumentando... se desborda! ¡¿Monstruo?! ¡No, soy el mismísimo demonio!", "broly"),
                            StoryDialogLine("Goku", "¡Amigos... denme toda la energía que les queda! ¡No dejaré que destruyas este universo!", "goku"),
                            StoryDialogLine("Broly", "¡¿Acaso crees que esa insignificante basura puede dañarme?! ¡¡MUERE!!", "broly")
                        ),
                        isCompleted = false,
                        rankGrade = "Z",
                        rewardRewardStr = "Desbloqueado: Broly SSJ Legendario PCK 1 + Cápsula Potara 'Ki Desbordante'",
                        isBossBattle = true,
                        nodeX = 0.5f,
                        nodeY = 0.5f,
                        nextNodeIds = listOf("m_broly_3"),
                        nodeType = "JEFE"
                    ),
                    StoryMission(
                        id = "m_broly_3",
                        title = "Furia Desatada: Broly DBS Full Power vs Gogeta Blue",
                        sagaEra = "Saga de Broly (DBS)",
                        chapterNumber = 12,
                        p1Fighter = Fighter("gogeta_blue", "Gogeta", "Super Saiyan Blue", 1550f, 1550f, 100f, 95f, "God Punisher / Stardust Breaker", 70f, 0xFF38BDF8),
                        p2Fighter = Fighter("goku_god", "Goku", "Super Saiyan God", 1400f, 1400f, 100f, 80f, "God Binding Kamehameha", 60f, 0xFFEF4444),
                        cpu1Fighter = Fighter("broly_dbs_fp", "Broly DBS", "Berserk Full Power LSSJ", 1700f, 1700f, 100f, 100f, "Gigantic Roar Destructivo", 80f, 0xFF10B981),
                        cpu2Fighter = null,
                        stageName = "Tierra Ártica Destruida en Lava Multidimensional",
                        objectiveDescription = "Utiliza la técnica suprema del Stardust Breaker para purificar la furia incontrolable de Broly DBS.",
                        dialogLines = listOf(
                            StoryDialogLine("Gogeta", "¡Ni Goku ni Vegeta! ¡Somos el encargado de detenerte! ¡Gogeta Blue!", "gogeta"),
                            StoryDialogLine("Broly", "¡¡¡GRAAAAUUUUGH... RUUUUUUARGH!!!", "broly"),
                            StoryDialogLine("Gogeta", "¡Esto se acaba aquí, Broly! ¡¡STARDUST BREAKER!!", "gogeta")
                        ),
                        isCompleted = false,
                        rankGrade = "Z",
                        rewardRewardStr = "Desbloqueado: Gogeta Blue PCK 1 + Broly DBS Traje Furia + 50,000 D-Points",
                        isBossBattle = true,
                        nodeX = 0.8f,
                        nodeY = 0.65f,
                        nextNodeIds = emptyList(),
                        nodeType = "JEFE"
                    )
                )
            ),
            StorySaga(
                id = "saga_mods_dbs",
                title = "Saga Torneo del Poder & Super Hero (MODS DBS)",
                period = "Año 780 · Mundo de la Nada y Tierra DBS",
                description = "Capítulos de historia modificados integrando el Roster de PCK 1: Goku Ultra Instinct, Vegeta Ultra Ego y Gohan Beast.",
                colorHex = 0xFFA855F7,
                missions = listOf(
                    StoryMission(
                        id = "m_dbs_1",
                        title = "Límite Roto: Goku Ultra Instinct vs Jiren",
                        sagaEra = "Torneo del Poder (DBS)",
                        chapterNumber = 8,
                        p1Fighter = Fighter("goku_mui", "Goku", "Ultra Instinct Dominado", 1500f, 1500f, 100f, 100f, "Kamehameha Divino Plateado", 70f, 0xFFE2E8F0),
                        p2Fighter = Fighter("vegeta_ue", "Vegeta", "Ultra Ego", 1480f, 1480f, 100f, 90f, "Hakai Galick Gun", 65f, 0xFFA855F7),
                        cpu1Fighter = Fighter("jiren_full", "Jiren", "Full Power Máximo", 1550f, 1550f, 100f, 90f, "Impacto Magnetrón", 70f, 0xFFEF4444),
                        cpu2Fighter = Fighter("toppo_god", "Toppo", "Dios de la Destrucción", 1400f, 1400f, 100f, 80f, "Esfera Hakai", 65f, 0xFFA855F7),
                        stageName = "Mundo de la Nada (Plataforma Kachi Katchin)",
                        objectiveDescription = "Usa el auto-esquive del Ultra Instinct para contraatacar la fuerza absoluta de Jiren.",
                        dialogLines = listOf(
                            StoryDialogLine("Jiren", "Son Goku... La confianza no engendra nada más que debilidad.", "jiren"),
                            StoryDialogLine("Goku", "¡No peleo solo! ¡Los sentimientos de mis amigos son mi fuerza!", "goku"),
                            StoryDialogLine("Vegeta", "¡Muéstrales el orgullo de la raza guerrera saiyajin, Kakarotto!", "vegeta")
                        ),
                        isCompleted = false,
                        rankGrade = "Z",
                        rewardRewardStr = "Desbloqueado: Goku MUI PCK 1 + Plataforma del Torneo del Poder (Escenario)",
                        isBossBattle = true,
                        nodeX = 0.35f,
                        nodeY = 0.45f,
                        nextNodeIds = listOf("m_dbs_2"),
                        nodeType = "JEFE"
                    ),
                    StoryMission(
                        id = "m_dbs_2",
                        title = "El Despertar de la Bestia: Gohan Beast vs Cell Max",
                        sagaEra = "DBS Super Hero",
                        chapterNumber = 9,
                        p1Fighter = Fighter("gohan_beast", "Gohan", "Beast", 1520f, 1520f, 100f, 95f, "Makankosappo Demoníaco Beast", 70f, 0xFFEF4444),
                        p2Fighter = Fighter("piccolo_orange", "Piccolo", "Orange Gigante", 1450f, 1450f, 100f, 85f, "Furia Namekiana", 60f, 0xFFF97316),
                        cpu1Fighter = Fighter("cell_max", "Cell Max", "Monstruo Gigante", 1600f, 1600f, 100f, 90f, "Rayo Cataclísmico", 75f, 0xFFEF4444),
                        cpu2Fighter = null,
                        stageName = "Base de la Patrulla Roja (DBS)",
                        objectiveDescription = "Carga el Makankosappo mientras Orange Piccolo sujeta al gigantesco Cell Max.",
                        dialogLines = listOf(
                            StoryDialogLine("Piccolo", "¡Gohan, reúne toda tu energía! ¡Yo lo retendré!", "piccolo"),
                            StoryDialogLine("Gohan", "¡Este es mi poder definitivo! ¡¡MAKANKOSAPPO!!", "gohan")
                        ),
                        isCompleted = false,
                        rankGrade = "Z",
                        rewardRewardStr = "Desbloqueado: Gohan Beast PCK 1 + Orange Piccolo",
                        isBossBattle = true,
                        nodeX = 0.7f,
                        nodeY = 0.6f,
                        nextNodeIds = emptyList(),
                        nodeType = "JEFE"
                    )
                )
            )
        )
    }
}
