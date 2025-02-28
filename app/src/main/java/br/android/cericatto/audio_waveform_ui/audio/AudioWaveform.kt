package br.android.cericatto.audio_waveform_ui.audio

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.android.cericatto.audio_waveform_ui.ui.main_screen.MainScreenAction
import br.android.cericatto.audio_waveform_ui.ui.main_screen.MainScreenState
import br.android.cericatto.audio_waveform_ui.ui.theme.audioBarBackgroundColor
import br.android.cericatto.audio_waveform_ui.ui.theme.audioBarChartWave
import br.android.cericatto.audio_waveform_ui.ui.theme.audioBarProgressColor
import java.io.File

/**
 * Data class to hold both amplitude data and duration.
 */
data class WaveformData(
	val amplitudes: List<Float>,
	val durationInSeconds: Float
)

@Composable
fun AudioPlayerWithControls(
	onAction: (MainScreenAction) -> Unit,
	state: MainScreenState,
	file: File
) {
	// Create the controller with proper lifecycle scope.
	val coroutineScope = rememberCoroutineScope()

	// Remember the controller instance.
	val controller = remember(file) {
		AudioPlayerController(onAction, file, coroutineScope)
	}

	// Collect the state as a Compose State.
	val playerState by controller.state.collectAsState()

	// Cleanup when the composable is disposed.
	DisposableEffect(controller) {
		onDispose {
			controller.release()
		}
	}

	Row(
		modifier = Modifier
			.padding(horizontal = 5.dp) // Outer padding to give room for the shadow
			.shadow(
				elevation = 5.dp,
				shape = RoundedCornerShape(20.dp),
//				ambientColor = audioBarBackgroundShadowColor, // Optional custom ambient shadow
//				spotColor = audioBarBackgroundShadowColor     // Optional custom spot shadow
			)
			.background(
				color = audioBarBackgroundColor,
				shape = RoundedCornerShape(20.dp)
			)
			.height(72.dp)
			.fillMaxWidth()
			.padding(8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.Start
	) {
		if (state.isLoading) {
			Box(
				contentAlignment = Alignment.Center,
				modifier = Modifier.fillMaxWidth()
			) {
				CircularProgressIndicator(
					color = audioBarProgressColor,
					strokeWidth = 3.dp,
					modifier = Modifier
						.padding(10.dp)
						.size(30.dp)
				)
			}
		} else {
			IconButton(
				onClick = { controller.togglePlayPause() },
				modifier = Modifier
					.padding(end = 8.dp)
					.background(
						color = Color.White,
						shape = RoundedCornerShape(25.dp)
					)
			) {
				Icon(
					imageVector = if (playerState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
					contentDescription = if (playerState.isPlaying) "Pause" else "Play",
					tint = audioBarProgressColor,
					modifier = Modifier.size(40.dp)
				)
			}
		}
		Box(
			modifier = Modifier.weight(1f)
		) {
			playerState.waveformData?.let { data ->
				AudioWaveform(
					onAction = onAction,
					waveformData = data,
					currentProgress = playerState.progress,
					modifier = Modifier.fillMaxWidth()
				)
			}
		}
	}
}

@Composable
private fun AudioWaveform(
	onAction: (MainScreenAction) -> Unit,
	waveformData: WaveformData,
	currentProgress: Float,
	modifier: Modifier = Modifier,
	backgroundColor: Color = Color.Transparent,
	barColor: Color = audioBarChartWave,
	playedBarColor: Color = audioBarProgressColor.copy(alpha = 0.1f),
	indicatorColor: Color = audioBarProgressColor.copy(alpha = 0.9f)
) {
	Canvas(
		modifier = modifier
			.fillMaxWidth()
			.height(48.dp)
			.background(
				color = backgroundColor,
				shape = RoundedCornerShape(20.dp)
			)
	) {
		val canvasWidth = size.width
		val canvasHeight = size.height

		// Calculate bar width based on canvas width and duration
		val totalBars = waveformData.amplitudes.size
		val scaledBarWidth = (canvasWidth / totalBars) * 0.8f // 80% of available space for bars
		val scaledSpacing = (canvasWidth / totalBars) * 0.2f  // 20% for spacing

		// Draw all bars
		waveformData.amplitudes.forEachIndexed { index, amplitude ->
			val x = index * (scaledBarWidth + scaledSpacing)
			val barHeight = canvasHeight * kotlin.math.abs(amplitude)
			val startY = (canvasHeight - barHeight) / 2

			// Determine if this bar has been played
			val progressPosition = (currentProgress * canvasWidth)
			val isPlayed = x <= progressPosition

			// Draw amplitude bar with appropriate color
			drawLine(
				color = if (isPlayed) playedBarColor else barColor,
				start = Offset(x, startY),
				end = Offset(x, startY + barHeight),
				strokeWidth = scaledBarWidth
			)

			// Draw progress indicator (ball)
			drawProgressIndicator(
				progress = currentProgress,
				canvasWidth = canvasWidth,
				canvasHeight = canvasHeight,
				color = indicatorColor
			)
		}
	}
}

private fun DrawScope.drawProgressIndicator(
	progress: Float,
	canvasWidth: Float,
	canvasHeight: Float,
	color: Color
) {
	val x = progress * canvasWidth
	val radius = 8.dp.toPx()

	drawCircle(
		color = color,
		radius = radius,
		center = Offset(x, canvasHeight / 2)
	)
}

@Preview
@Composable
private fun AudioPlayerWithControlsPreview() {
	AudioPlayerWithControls(
		onAction = {},
		state = MainScreenState(),
		file = File("audio.wav")
	)
}

@Preview
@Composable
private fun AudioWaveformPreview() {
	AudioWaveform(
		onAction = {},
		waveformData = WaveformData(
			amplitudes = listOf(
				0.0f, 0.0026550293f, 0.0029296875f, 0.0035095215f, -0.0029296875f,
				-0.0030212402f, 0.0026550293f, 0.0027770996f, -0.0026550293f, -0.0031738281f,
				-0.002380371f, 0.009490967f, -0.009613037f, 0.0061035156f, -0.033294678f,
				-0.09576416f, -0.23312378f, 0.4482727f, 0.48446655f, -0.13415527f,
				-0.26391602f, -0.41088867f, -0.3270874f, -0.16595459f, 0.2050476f,
				0.16671753f, 0.22183228f, 0.45864868f, 0.4064331f, 0.23181152f,
				0.20007324f, 0.52215576f, 0.590271f, 0.58200073f, 0.49273682f,
				0.28399658f, 0.1461792f, -0.112701416f, -0.13308716f, -0.0987854f,
				-0.18139648f, -0.13647461f, -0.09335327f, 0.0463562f, -0.035217285f,
				-0.0087890625f, -0.005645752f, -0.0033569336f, 0.015289307f, -0.0042419434f,
				0.0021972656f
			),
			durationInSeconds = 40f
		),
		modifier = Modifier,
		currentProgress = 0f
	)
}