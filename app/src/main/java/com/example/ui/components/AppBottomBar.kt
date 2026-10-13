package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NavTab
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.DragonAmber
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextSecondary

@Composable
fun AppBottomBar(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDark)
            .windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = SurfaceDark,
        contentColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavTab.values().forEach { tab ->
            val isSelected = currentTab == tab
            val icon = getTabIcon(tab)

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = tab.title,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    indicatorColor = Color(0xFF1E3A8A),
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                ),
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
            )
        }
    }
}

private fun getTabIcon(tab: NavTab): ImageVector {
    return when (tab) {
        NavTab.HOME -> Icons.Default.Home
        NavTab.TEXTURE -> Icons.Default.Texture
        NavTab.MODS -> Icons.Default.Extension
        NavTab.CONTROLS -> Icons.Default.SportsEsports
        NavTab.CHEATS -> Icons.Default.Lock
        NavTab.SETTINGS -> Icons.Default.Settings
    }
}
