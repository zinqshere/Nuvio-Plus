package com.nuvio.app.features.player

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.BaseAudioProcessor
import java.nio.ByteBuffer
import kotlin.math.roundToInt

/**
 * Applies Nuvio's 100%..200% player gain directly to decoded PCM audio.
 *
 * Android STREAM_MUSIC remains responsible for 0%..100%. Above 100%, the
 * system stream is held at maximum and this processor applies the additional
 * linear gain inside Media3's audio pipeline.
 */
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
internal class NuvioVolumeGainAudioProcessor : BaseAudioProcessor() {
    override fun onConfigure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        // Leave unsupported PCM encodings untouched instead of failing playback.
        // Media3 treats NOT_SET as an inactive processor and passes audio through.
        return if (
            inputAudioFormat.encoding == C.ENCODING_PCM_16BIT ||
            inputAudioFormat.encoding == C.ENCODING_PCM_FLOAT
        ) {
            inputAudioFormat
        } else {
            AudioProcessor.AudioFormat.NOT_SET
        }
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        if (!inputBuffer.hasRemaining()) return

        val gain = AndroidPlayerVolumeBoost.fraction.coerceIn(0f, 2f).coerceAtLeast(1f)
        val outputBuffer = replaceOutputBuffer(inputBuffer.remaining())
        outputBuffer.order(inputBuffer.order())

        when (inputAudioFormat.encoding) {
            C.ENCODING_PCM_16BIT -> {
                while (inputBuffer.remaining() >= 2) {
                    val sample = inputBuffer.short.toInt()
                    val boosted = (sample * gain)
                        .roundToInt()
                        .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                    outputBuffer.putShort(boosted.toShort())
                }
            }

            C.ENCODING_PCM_FLOAT -> {
                while (inputBuffer.remaining() >= 4) {
                    val sample = inputBuffer.float
                    outputBuffer.putFloat((sample * gain).coerceIn(-1f, 1f))
                }
            }
        }

        outputBuffer.flip()
    }
}
