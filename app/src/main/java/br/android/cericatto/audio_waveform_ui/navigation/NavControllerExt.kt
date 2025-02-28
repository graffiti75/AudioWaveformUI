package br.android.cericatto.audio_waveform_ui.navigation

import androidx.navigation.NavController
import br.android.cericatto.audio_waveform_ui.ui.UiEvent

fun NavController.navigate(event: UiEvent.Navigate) {
    this.navigate(event.route)
}