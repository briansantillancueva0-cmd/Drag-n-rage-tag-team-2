package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.NavTab
import com.example.ui.components.AppBottomBar
import com.example.ui.screens.*
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.DbzTagTeamViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp(
    viewModel: DbzTagTeamViewModel = viewModel()
) {
    val currentTab by viewModel.selectedTab.collectAsState()
    val battleState by viewModel.battleState.collectAsState()
    val isStoryModeOpen by viewModel.isStoryModeOpen.collectAsState()

    if (battleState.isPlaying) {
        BackHandler {
            viewModel.pauseBattle()
        }
        BattleArenaScreen(
            viewModel = viewModel,
            onExit = { viewModel.exitBattle() },
            modifier = Modifier.fillMaxSize()
        )
    } else if (isStoryModeOpen) {
        StoryModeScreen(
            viewModel = viewModel,
            onBack = { viewModel.closeStoryMode() },
            modifier = Modifier.fillMaxSize()
        )
    } else {
        // Handle Back button to return to Home tab if on another tab
        if (currentTab != NavTab.HOME) {
            BackHandler {
                viewModel.selectTab(NavTab.HOME)
            }
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark),
            contentWindowInsets = WindowInsets.statusBars,
            bottomBar = {
                AppBottomBar(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Crossfade(targetState = currentTab, label = "TabCrossfade") { tab ->
                    when (tab) {
                        NavTab.HOME -> HomeScreen(
                            viewModel = viewModel,
                            onPlayClick = { viewModel.startBattle() }
                        )
                        NavTab.TEXTURE -> TexturesScreen(viewModel = viewModel)
                        NavTab.MODS -> ModsScreen(viewModel = viewModel)
                        NavTab.CONTROLS -> ControlsScreen(viewModel = viewModel)
                        NavTab.CHEATS -> CheatsScreen(viewModel = viewModel)
                        NavTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
