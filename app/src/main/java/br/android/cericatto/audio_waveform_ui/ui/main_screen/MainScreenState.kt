package br.android.cericatto.audio_waveform_ui.ui.main_screen

data class MainScreenState(
	val isLoading : Boolean = true,
	val progress: Int = 0,
	val duration: Int = 0
)