package br.android.cericatto.audio_waveform_ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
			}
		}
	}
}