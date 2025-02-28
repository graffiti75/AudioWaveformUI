package br.android.cericatto.audio_waveform_ui.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
	@Serializable
	data object MainScreen: Route
}