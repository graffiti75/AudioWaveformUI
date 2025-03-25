package br.android.cericatto.audio_waveform_ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.android.cericatto.audio_waveform_ui.audio.WaveBarsScreen
import br.android.cericatto.audio_waveform_ui.navigation.NavHostComposable
import br.android.cericatto.audio_waveform_ui.ui.theme.AudioWaveformUITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			AudioWaveformUITheme {
				NavHostComposable()
				/*
				Surface(
					modifier = Modifier.fillMaxSize()
						.background(MaterialTheme.colorScheme.background)
						.padding(horizontal = 10.dp)
						.background(MaterialTheme.colorScheme.background),
					color = MaterialTheme.colorScheme.background
				) {
					WaveBarsScreen()
				}
				 */
			}
		}
	}
}