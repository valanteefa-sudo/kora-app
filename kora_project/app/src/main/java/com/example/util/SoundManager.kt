package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

object SoundManager {
    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * Plays a crisp referee start whistle (single long whistle with trill) + crowd cheer
     */
    fun playStartWhistle() {
        scope.launch {
            playWhistlePattern(longArrayOf(700))
            playCrowdCheer(durationMs = 1800)
        }
    }

    /**
     * Plays a referee finish match whistle (3 whistles: short, short, long) + crowd cheer
     */
    fun playFinishWhistle() {
        scope.launch {
            playWhistlePattern(longArrayOf(250, 150, 250, 150, 800))
            playCrowdCheer(durationMs = 2500)
        }
    }

    /**
     * Plays a goal celebration crowd cheer & whistle
     */
    fun playGoalCheer() {
        scope.launch {
            playWhistlePattern(longArrayOf(400))
            playCrowdCheer(durationMs = 2000)
        }
    }

    /**
     * Plays stadium crowd cheer sound effect
     */
    fun playCrowdCheerOnly() {
        scope.launch {
            playCrowdCheer(durationMs = 2000)
        }
    }

    private fun playWhistlePattern(durations: LongArray) {
        val sampleRate = 44100
        var totalSamples = 0
        durations.forEach { totalSamples += ((it * sampleRate) / 1000).toInt() }

        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(totalSamples * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            val buffer = ShortArray(totalSamples)
            var bufferOffset = 0

            var isSound = true
            for (durMs in durations) {
                val numSamples = ((durMs * sampleRate) / 1000).toInt()
                if (isSound) {
                    for (i in 0 until numSamples) {
                        val t = i.toDouble() / sampleRate
                        val freq1 = 2800.0
                        val freq2 = 3150.0
                        val trill = 1.0 + 0.25 * sin(2.0 * PI * 28.0 * t)
                        val val1 = sin(2.0 * PI * freq1 * trill * t)
                        val val2 = sin(2.0 * PI * freq2 * trill * t)

                        val envelope = when {
                            i < 500 -> i / 500.0
                            i > numSamples - 1000 -> (numSamples - i) / 1000.0
                            else -> 1.0
                        }

                        val sample = ((val1 + val2) * 0.4 * envelope * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                        buffer[bufferOffset + i] = sample.toShort()
                    }
                } else {
                    for (i in 0 until numSamples) {
                        buffer[bufferOffset + i] = 0
                    }
                }
                bufferOffset += numSamples
                isSound = !isSound
            }

            audioTrack.write(buffer, 0, totalSamples)
            audioTrack.play()

            scope.launch {
                delay(durations.sum() + 200)
                audioTrack.stop()
                audioTrack.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playCrowdCheer(durationMs: Long) {
        val sampleRate = 22050
        val numSamples = ((durationMs * sampleRate) / 1000).toInt()
        val buffer = ShortArray(numSamples)

        val random = Random(System.currentTimeMillis())
        var lowpass = 0.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / numSamples

            val env = when {
                progress < 0.15 -> progress / 0.15
                progress > 0.7 -> (1.0 - progress) / 0.3
                else -> 1.0 + 0.2 * sin(2.0 * PI * 2.0 * t)
            }

            val whiteNoise = (random.nextDouble() * 2.0 - 1.0)
            lowpass = lowpass + 0.15 * (whiteNoise - lowpass)

            val cheerTone = sin(2.0 * PI * 220.0 * t) * 0.2 + sin(2.0 * PI * 330.0 * t) * 0.15 + sin(2.0 * PI * 440.0 * t) * 0.1
            val blended = (lowpass * 0.7 + cheerTone * 0.3) * env * 0.5

            val sample = (blended * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            buffer[i] = sample.toShort()
        }

        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(numSamples * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, numSamples)
            audioTrack.play()

            scope.launch {
                delay(durationMs + 300)
                audioTrack.stop()
                audioTrack.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
