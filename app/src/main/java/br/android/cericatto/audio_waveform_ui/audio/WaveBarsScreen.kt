package br.android.cericatto.audio_waveform_ui.audio

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedWaveBars(
	limit: Int = 4,
	barHeight: Dp = 45.dp,
	spacing: Dp = 1.dp,
	modifier: Modifier = Modifier
) {
	val animationProgress = remember { Animatable(0f) }

	LaunchedEffect(Unit) {
		animationProgress.animateTo(
			targetValue = 1f,
			animationSpec = tween(
				durationMillis = limit * 1000, // Total duration = limit seconds
				easing = LinearEasing
			)
		)
	}

	val density = LocalDensity.current
	val barHeightPx = with(density) { barHeight.toPx() }
	val spacingPx = with(density) { spacing.toPx() }

	Box(
		modifier = modifier
			.fillMaxSize()
			.background(Color.Gray),
		contentAlignment = Alignment.Center
	) {
		Canvas(
			modifier = Modifier
				.fillMaxWidth()
				.height(barHeight)
				.clipToBounds()
		) {
			val totalBarWidth = (size.width - (limit - 1) * spacingPx) / limit
			val progressPerBar = 1f / limit
			val currentProgress = animationProgress.value

			// Draw each bar.
			for (i in 0 until limit) {
				val barStartProgress = i * progressPerBar
				val barEndProgress = (i + 1) * progressPerBar
				val fillWidth = when {
					currentProgress < barStartProgress -> 0f
					currentProgress >= barEndProgress -> totalBarWidth
					else -> {
						val barProgress = (currentProgress - barStartProgress) / progressPerBar
						totalBarWidth * barProgress
					}
				}

				val leftOffset = i * (totalBarWidth + spacingPx)
				// Draw white background for entire bar.
				drawRect(
					color = Color.White,
					topLeft = Offset(leftOffset, 0f),
					size = Size(totalBarWidth, barHeightPx)
				)

				// Draw blue fill portion.
				drawRect(
					color = Color.Blue,
					topLeft = Offset(leftOffset, 0f),
					size = Size(fillWidth, barHeightPx)
				)
			}
		}
	}
}

// Usage example
@Composable
fun WaveBarsScreen() {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
		modifier = Modifier.background(Color.Green)
			.fillMaxWidth()
			.height(45.dp)
	) {
		AnimatedWaveBars(
			limit = 30,
			spacing = 1.dp
		)
	}
}

// Preview
@Preview(showBackground = true)
@Composable
fun WaveBarsPreview() {
	WaveBarsScreen()
}