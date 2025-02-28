package br.android.cericatto.audio_waveform_ui.ui.main_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.io.File

@Composable
fun MainScreenRoot(
	viewModel: MainScreenViewModel = hiltViewModel()
) {
	val state by viewModel.state.collectAsStateWithLifecycle()
	val snackbarHostState = remember { SnackbarHostState() }

	RequestRecordAudioPermission(
		viewModel = viewModel
	)
	MainScreen(
		onAction = viewModel::onAction,
		state = state,
		snackbarHostState = snackbarHostState
	)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
	onAction: (MainScreenAction) -> Unit,
	state: MainScreenState,
	snackbarHostState: SnackbarHostState,
) {
	val context = LocalContext.current
	val audioFile = File(context.cacheDir, "audio.wav")
	Scaffold(
		snackbarHost = {
			SnackbarHost(hostState = snackbarHostState)
		},
	) { _ ->
		AudioPlayer(
			onAction = onAction,
			state = state,
			file = audioFile,
			modifier = Modifier
				.padding(vertical = 40.dp),
		)
	}
}

@Preview
@Composable
private fun MainScreenPreview() {
	MainScreen(
		onAction = {},
		snackbarHostState = SnackbarHostState(),
		state = MainScreenState()
	)
}