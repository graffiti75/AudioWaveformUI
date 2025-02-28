package br.android.cericatto.audio_waveform_ui.ui.main_screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import br.android.cericatto.audio_waveform_ui.ui.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
	private val context: Context
): ViewModel() {

	private val _events = Channel<UiEvent>()
	val events = _events.receiveAsFlow()

	private val _state = MutableStateFlow(MainScreenState())
	val state: StateFlow<MainScreenState> = _state.asStateFlow()

	init {
		_state.update { state ->
			state.copy(
				isLoading = false
			)
		}
	}

	/**
	 * Audio Methods
	 */

	fun isRecordAudioPermissionGranted(): Boolean {
		return ContextCompat.checkSelfPermission(
			context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
	}
}