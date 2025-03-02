package br.android.cericatto.audio_waveform_ui.ui.main_screen

sealed interface MainScreenAction {
	data object OnAmplitudesLoaded : MainScreenAction
	data class OnProgressChanged(val progress: Int, val duration: Int) : MainScreenAction
}