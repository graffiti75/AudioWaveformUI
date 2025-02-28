package br.android.cericatto.audio_waveform_ui.audio

import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

// AudioPlayerState holds all the state needed for the player
data class AudioPlayerState(
	val isPlaying: Boolean = false,
	val progress: Float = 0f,
	val waveformData: WaveformData? = null
)

/**
 * AudioPlayerController manages MediaPlayer lifecycle and state updates.
  */
class AudioPlayerController(
	private val file: File,
	private val coroutineScope: CoroutineScope
) {
	// State management using StateFlow.
	private val _state = MutableStateFlow(AudioPlayerState())
	val state: StateFlow<AudioPlayerState> = _state.asStateFlow()

	// MediaPlayer instance.
	private var mediaPlayer: MediaPlayer? = null
	private var progressJob: Job? = null

	init {
		// Initialize MediaPlayer and load waveform data
		initializePlayer()
		loadWaveformData()
	}

	private fun initializePlayer() {
		mediaPlayer = MediaPlayer().apply {
			setDataSource(file.path)
			prepare()

			setOnCompletionListener {
				coroutineScope.launch {
					_state.update { it.copy(
						isPlaying = false,
						progress = 0f
					)}
				}
			}
		}
	}

	private fun loadWaveformData() {
		coroutineScope.launch {
			val data = processWaveFile(file)
			_state.update { it.copy(waveformData = data) }
		}
	}

	fun togglePlayPause() {
		mediaPlayer?.let { player ->
			val currentState = _state.value

			if (currentState.isPlaying) {
				pausePlayback()
			} else {
				if (currentState.progress >= 1f) {
					player.seekTo(0)
					_state.update { it.copy(progress = 0f) }
				}
				startPlayback()
			}
		}
	}

	private fun startPlayback() {
		mediaPlayer?.start()
		_state.update { it.copy(isPlaying = true) }
		startProgressTracking()
	}

	private fun pausePlayback() {
		mediaPlayer?.pause()
		_state.update { it.copy(isPlaying = false) }
		progressJob?.cancel()
	}

	private fun startProgressTracking() {
		progressJob?.cancel()
		progressJob = coroutineScope.launch {
			while (isActive) {
				mediaPlayer?.let { player ->
					val progress = player.currentPosition.toFloat() / player.duration
					_state.update { it.copy(progress = progress) }
				}
				delay(16) // Approximately 60 FPS
			}
		}
	}

	fun release() {
		progressJob?.cancel()
		mediaPlayer?.release()
		mediaPlayer = null
	}
}