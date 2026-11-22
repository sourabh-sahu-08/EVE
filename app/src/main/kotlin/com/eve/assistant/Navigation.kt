package com.eve.assistant

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.eve.assistant.ui.history.HistoryScreen
import com.eve.assistant.ui.home.HomeScreen
import com.eve.assistant.ui.settings.PrivacyPermissionsScreen
import com.eve.assistant.ui.settings.SettingsScreen
import com.eve.assistant.ui.settings.SettingsViewModel
import com.eve.assistant.ui.settings.VoiceSettingsScreen

/**
 * Root navigation graph managing transitions across all EVE application screens.
 */
@Composable
fun MainNavigation() {
    val backStack = rememberNavBackStack(HomeKey)
    val settingsViewModel: SettingsViewModel = viewModel()

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<HomeKey> {
                HomeScreen(
                    onNavigateToHistory = { backStack.add(HistoryKey) },
                    onNavigateToSettings = { backStack.add(SettingsKey) }
                )
            }
            entry<HistoryKey> {
                HistoryScreen(
                    onNavigateBack = { backStack.removeLastOrNull() }
                )
            }
            entry<SettingsKey> {
                SettingsScreen(
                    onNavigateBack = { backStack.removeLastOrNull() },
                    onNavigateToVoiceSettings = { backStack.add(VoiceSettingsKey) },
                    onNavigateToPrivacy = { backStack.add(PrivacyPermissionsKey) },
                    viewModel = settingsViewModel
                )
            }
            entry<VoiceSettingsKey> {
                VoiceSettingsScreen(
                    onNavigateBack = { backStack.removeLastOrNull() },
                    viewModel = settingsViewModel
                )
            }
            entry<PrivacyPermissionsKey> {
                PrivacyPermissionsScreen(
                    onNavigateBack = { backStack.removeLastOrNull() },
                    viewModel = settingsViewModel
                )
            }
        }
    )
}
