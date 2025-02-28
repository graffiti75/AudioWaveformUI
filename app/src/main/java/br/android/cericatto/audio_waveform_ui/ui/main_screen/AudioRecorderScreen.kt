package br.android.cericatto.audio_waveform_ui.ui.main_screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.android.cericatto.audio_waveform_ui.R
import br.android.cericatto.audio_waveform_ui.audio.AudioPlayerWithControls
import java.io.File

@Composable
fun RequestRecordAudioPermission(viewModel: MainScreenViewModel) {
	var permissionGranted by remember { mutableStateOf(false) }
	var showDialog by remember { mutableStateOf(!viewModel.isRecordAudioPermissionGranted()) }
	val context = LocalContext.current

	// Launcher to request the RECORD_AUDIO permission.
	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestPermission()
	) { isGranted ->
		// Handle the permission result.
		permissionGranted = isGranted
		if (!isGranted) {
			showDialog = true
		}
	}

	if (showDialog) {
		AlertDialog(
			onDismissRequest = {
				showDialog = false
			},
			title = {
				Text(
					text = context.getString(R.string.record_audio_permission_dialog_title)
				)
			},
			text = {
				Text(
					text = context.getString(R.string.permission_needed)
				)
			},
			confirmButton = {
				TextButton(
					onClick = {
						showDialog = false
						launcher.launch(Manifest.permission.RECORD_AUDIO)
					}
				) {
					Text(
						text = context.getString(R.string.dialog__allow)
					)
				}
			},
			dismissButton = {
				TextButton(
					onClick = {
						showDialog = false
					}
				) {
					Text(
						text = context.getString(R.string.dialog__cancel)
					)
				}
			}
		)
	}
}

@Composable
fun AudioPlayer(
	file: File,
	modifier: Modifier
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
		modifier = modifier.fillMaxSize()
			.padding(10.dp)
	) {
		AudioPlayerWithControls(file = file)
	}
}

@Preview
@Composable
private fun AudioPlayerPreview() {
	val context = LocalContext.current
	AudioPlayer(
		file = File(context.cacheDir, "audio.wav"),
		modifier = Modifier
	)
}