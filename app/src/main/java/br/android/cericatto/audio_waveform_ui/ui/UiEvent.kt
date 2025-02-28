package br.android.cericatto.audio_waveform_ui.ui

import br.android.cericatto.audio_waveform_ui.navigation.Route

sealed class UiEvent {
	data class Navigate(val route: Route): UiEvent()
	data object NavigateUp: UiEvent()
	data class ShowErrorSnackbar(val messages: List<UiText>): UiEvent()
	data class ShowSnackbar(val message: UiText): UiEvent()
}