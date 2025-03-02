package br.android.cericatto.audio_waveform_ui.audio

fun formatDuration(progress: Int, duration: Int) =
	"${formatTime(progress)}/${formatTime(duration)}"

fun formatTime(seconds: Int): String {
	val minutes = seconds / 60
	val secs = seconds % 60
	return "$minutes:${secs.toString().padStart(2, '0')}"
}