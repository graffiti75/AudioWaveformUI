package br.android.cericatto.audio_waveform_ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.android.cericatto.audio_waveform_ui.ui.main_screen.MainScreenRoot

@Composable
fun NavHostComposable() {
	val navController = rememberNavController()
	NavHost(
		navController = navController,
		startDestination = Route.MainScreen
	) {
		composable<Route.MainScreen> {
			MainScreenRoot()
		}
	}
}