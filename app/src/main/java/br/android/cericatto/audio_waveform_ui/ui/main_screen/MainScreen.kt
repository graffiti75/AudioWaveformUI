package br.android.cericatto.audio_waveform_ui.ui.main_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.android.cericatto.audio_waveform_ui.navigation.Route
import br.android.cericatto.audio_waveform_ui.ui.ObserveAsEvents
import br.android.cericatto.audio_waveform_ui.ui.UiEvent
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun MainScreenRoot(
	onNavigate: (Route) -> Unit,
	onNavigateUp: () -> Unit,
	viewModel: MainScreenViewModel = hiltViewModel()
) {
	val state by viewModel.state.collectAsStateWithLifecycle()

	val scope = rememberCoroutineScope()
	val snackbarHostState = remember { SnackbarHostState() }
	val context = LocalContext.current
	ObserveAsEvents(viewModel.events) { event ->
		when (event) {
			is UiEvent.ShowSnackbar -> {
				scope.launch {
					snackbarHostState.showSnackbar(
						message = event.message.asString(context)
					)
				}
			}
			is UiEvent.Navigate -> onNavigate(event.route)
			is UiEvent.NavigateUp -> onNavigateUp()
			else -> Unit
		}
	}

	RequestRecordAudioPermission(viewModel = viewModel)

	MainScreen(
		state = state,
		snackbarHostState = snackbarHostState
	)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
	state: MainScreenState,
	snackbarHostState: SnackbarHostState,
) {
	val context = LocalContext.current
	val audioFile = File(context.cacheDir, "audio.wav")
	if (state.isLoading) {
		Box(
			modifier = Modifier
				.padding(vertical = 20.dp)
				.fillMaxSize()
				.background(Color.White),
			contentAlignment = Alignment.Center
		) {
			CircularProgressIndicator(
				color = MaterialTheme.colorScheme.primary,
				strokeWidth = 4.dp,
				modifier = Modifier.size(64.dp)
			)
		}
	} else {
		Scaffold(
			snackbarHost = {
				SnackbarHost(hostState = snackbarHostState)
			},
		) { _ ->
			AudioPlayer(
				file = audioFile,
				modifier = Modifier
					.padding(vertical = 40.dp),
			)
		}
	}
}

@Preview
@Composable
private fun MainScreenPreview() {
	MainScreen(
		snackbarHostState = SnackbarHostState(),
		state = MainScreenState()
	)
}