package br.android.cericatto.audio_waveform_ui.audio

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.roundToInt

/**
 * Extract Amplitude data from WAV file.
 *
 * @param wavFile The file where the data will be extracted.
 * @param barsPerSecond The desired number of bars to have per second.
 */
suspend fun processWaveFile(
	wavFile: File,
	barsPerSecond: Int = 1
): WaveformData = withContext(Dispatchers.IO) {
	println("---------------> processWaveFile() -> wavFile: $wavFile")
	getWavFileSizeInKb(wavFile)
	val duration = getWavDurationInSeconds(wavFile)

	// Read WAV PCM data and get the Amplitudes.
	val samples = mutableListOf<Float>()
	val buffer = ByteArray(2048)
	var bytesRead: Int
	val fis = FileInputStream(wavFile)
	val header = ByteArray(44)
	fis.read(header)
	while (fis.read(buffer).also { bytesRead = it } > 0) {
		for (i in 0 until bytesRead step 2) {
			if (i + 1 < bytesRead) {
				val sample = ByteBuffer
					.wrap(buffer.slice(i..i + 1).toByteArray())
					.order(ByteOrder.LITTLE_ENDIAN)
					.short
				samples.add(sample / 32768f)
			}
		}
	}
	fis.close()

	// Calculate compression based on desired bars per second.
	val desiredBars = (duration * barsPerSecond).roundToInt()
	val compressionFactor = samples.size / desiredBars
	val compressedAmplitudes = samples.chunked(compressionFactor) { chunk ->
		chunk.maxByOrNull { kotlin.math.abs(it) } ?: 0f
	}
	println("<-----")
	WaveformData(compressedAmplitudes, duration)
}

suspend fun getWavFileSizeInKb(wavFile: File): Float = withContext(Dispatchers.IO) {
	println("---------------> getWavFileSizeInKb() -> wavFile: $wavFile")
	val fis = FileInputStream(wavFile)
	val header = ByteArray(44) // Read the standard 44-byte header.
	fis.read(header)
	fis.close()

	// Verify "RIFF" identifier (bytes 0-3).
	val riffId = String(header.slice(0..3).toByteArray())
	if (riffId != "RIFF") {
		throw IllegalArgumentException("Not a valid WAV file: 'RIFF' identifier missing")
	}

	// Extract ChunkSize from bytes 4-7.
	val chunkSize = ByteBuffer.wrap(header.slice(4..7).toByteArray())
		.order(ByteOrder.LITTLE_ENDIAN)
		.int

	// Calculate total file size in bytes.
	val totalSizeBytes = chunkSize + 8

	// Convert to kilobytes (1 KB = 1024 bytes).
	val totalSizeKb = totalSizeBytes.toFloat() / 1024f

	println("ChunkSize (bytes 4-7): $chunkSize")
	println("Total File Size (bytes): $totalSizeBytes")
	println("Total File Size (KB): $totalSizeKb")
	println("Total File Size (MB): ${totalSizeKb / 1024f}")
	println("<-----")

	return@withContext totalSizeKb
}

suspend fun getWavDurationInSeconds(wavFile: File): Float = withContext(Dispatchers.IO) {
	println("---------------> getWavDurationInSeconds() -> wavFile: $wavFile")
	val fis = FileInputStream(wavFile)
	val headerBuffer = ByteArray(44)
	fis.read(headerBuffer)

	// Print the first 44 bytes for debugging
	println("Header (first 44 bytes): ${headerBuffer.joinToString(" ") { it.toUByte().toString(16).padStart(2, '0') }}")

	// Verify RIFF and WAVE identifiers
	if (String(headerBuffer.slice(0..3).toByteArray()) != "RIFF") {
		throw IllegalArgumentException("Not a valid WAV file: 'RIFF' missing")
	}
	if (String(headerBuffer.slice(8..11).toByteArray()) != "WAVE") {
		throw IllegalArgumentException("Not a valid WAV file: 'WAVE' missing")
	}

	// Read the entire file into a buffer to find the 'data' chunk
	val fileSize = wavFile.length().toInt()
	val fullBuffer = ByteArray(fileSize)
	fis.close() // Close and reopen to reset position
	FileInputStream(wavFile).use { it.read(fullBuffer) }

	// Find the 'data' chunk dynamically.
	var dataChunkOffset = -1
	for (i in 0 until fileSize - 4) {
		if (String(fullBuffer.slice(i..i + 3).toByteArray()) == "data") {
			dataChunkOffset = i
			break
		}
	}
	if (dataChunkOffset == -1) {
		throw IllegalArgumentException("No 'data' chunk found in WAV file")
	}
	println("Found 'data' chunk at offset: $dataChunkOffset")

	// Extract fields from standard positions.
	val numChannels = ByteBuffer.wrap(headerBuffer.slice(22..23).toByteArray())
		.order(ByteOrder.LITTLE_ENDIAN).short.toInt()
	val sampleRate = ByteBuffer.wrap(headerBuffer.slice(24..27).toByteArray())
		.order(ByteOrder.LITTLE_ENDIAN).int
	val bitsPerSample = ByteBuffer.wrap(headerBuffer.slice(34..35).toByteArray())
		.order(ByteOrder.LITTLE_ENDIAN).short.toInt()

	// Extract dataSize from the 'data' chunk (4 bytes after 'data').
	val dataSize = ByteBuffer.wrap(
			fullBuffer.slice(dataChunkOffset + 4..dataChunkOffset + 7).toByteArray()
		)
		.order(ByteOrder.LITTLE_ENDIAN).int

	// Calculate duration.
	val bytesPerSample = bitsPerSample / 8
	val duration = if (bytesPerSample > 0 && numChannels > 0 && sampleRate > 0) {
		dataSize.toFloat() / (bytesPerSample * numChannels * sampleRate)
	} else {
		0f
	}

	println("numChannels: $numChannels")
	println("sampleRate: $sampleRate")
	println("bitsPerSample: $bitsPerSample")
	println("bytesPerSample: $bytesPerSample")
	println("dataSize: $dataSize")
	println("duration: $duration seconds")

	println("<-----")
	return@withContext duration
}