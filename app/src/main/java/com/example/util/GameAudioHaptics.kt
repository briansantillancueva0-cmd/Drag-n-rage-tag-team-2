package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlin.concurrent.thread
import kotlin.math.sin

class GameAudioHaptics(private val context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var isSoundEnabled: Boolean = true
    var isHapticsEnabled: Boolean = true

    fun playHaptic(durationMs: Long = 40, amplitude: Int = 180) {
        if (!isHapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255)))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    fun playMeleeHitSound() {
        if (!isSoundEnabled) return
        thread(start = true, name = "HitSound") {
            playToneSweep(startFreq = 160.0, endFreq = 60.0, durationMs = 70, volume = 0.8f)
        }
    }

    fun playKiBlastSound() {
        if (!isSoundEnabled) return
        thread(start = true, name = "KiBlastSound") {
            playToneSweep(startFreq = 880.0, endFreq = 220.0, durationMs = 120, volume = 0.7f)
        }
    }

    fun playKiChargeSound() {
        if (!isSoundEnabled) return
        thread(start = true, name = "KiChargeSound") {
            playToneSweep(startFreq = 220.0, endFreq = 540.0, durationMs = 250, volume = 0.6f)
        }
    }

    fun playSuperBeamSound() {
        if (!isSoundEnabled) return
        thread(start = true, name = "SuperBeamSound") {
            playToneSweep(startFreq = 1200.0, endFreq = 300.0, durationMs = 400, volume = 0.9f)
        }
    }

    fun playTagSwitchSound() {
        if (!isSoundEnabled) return
        thread(start = true, name = "TagSwitchSound") {
            playToneSweep(startFreq = 300.0, endFreq = 900.0, durationMs = 180, volume = 0.75f)
        }
    }

    fun playGuardSound() {
        if (!isSoundEnabled) return
        thread(start = true, name = "GuardSound") {
            playToneSweep(startFreq = 600.0, endFreq = 750.0, durationMs = 60, volume = 0.5f)
        }
    }

    fun playPohdSfx(startFreq: Double = 520.0, endFreq: Double = 180.0, durationMs: Int = 280, sfxType: String = "SFX POHD") {
        if (!isSoundEnabled) return
        playHaptic(durationMs = 45, amplitude = 210)
        thread(start = true, name = "PohdSfx") {
            when {
                sfxType.contains("Sparking") -> {
                    // Modern heavy bass hit with high harmonic snap
                    playToneSweep(startFreq = 850.0, endFreq = 120.0, durationMs = 200, volume = 0.9f)
                }
                sfxType.contains("Clásico") -> {
                    // Vintage 1989 synth laser/ki blast
                    playToneSweep(startFreq = 350.0, endFreq = 700.0, durationMs = 180, volume = 0.75f)
                }
                else -> {
                    // POHD 96kHz remastered stereo resonance sweep
                    playToneSweep(startFreq = startFreq, endFreq = endFreq, durationMs = durationMs, volume = 0.85f)
                }
            }
        }
    }

    fun playAudioVoiceSample(sampleFreq: Double = 440.0) {
        if (!isSoundEnabled) return
        playHaptic(durationMs = 35, amplitude = 170)
        thread(start = true, name = "VoiceSample") {
            // Harmonic 2-tone melodic cadence mimicking anime voice shout
            playToneSweep(startFreq = sampleFreq * 0.8, endFreq = sampleFreq * 1.25, durationMs = 150, volume = 0.8f)
            Thread.sleep(20)
            playToneSweep(startFreq = sampleFreq * 1.25, endFreq = sampleFreq * 0.9, durationMs = 220, volume = 0.75f)
        }
    }

    private var musicPlaybackThread: Thread? = null
    @Volatile
    private var isMusicStopRequested = false

    fun isMusicPlaying(): Boolean = musicPlaybackThread?.isAlive == true

    fun stopSongMelody() {
        isMusicStopRequested = true
        musicPlaybackThread?.interrupt()
        musicPlaybackThread = null
    }

    fun playSongMelody(notes: List<Double>, tempoMs: Long = 200, onDone: () -> Unit = {}) {
        stopSongMelody()
        isMusicStopRequested = false

        musicPlaybackThread = thread(start = true, name = "BgmPlayer") {
            try {
                for (freq in notes) {
                    if (isMusicStopRequested || Thread.currentThread().isInterrupted) break
                    if (freq > 0.0) {
                        playToneSweep(startFreq = freq, endFreq = freq, durationMs = tempoMs.toInt() - 25, volume = 0.5f)
                    } else {
                        Thread.sleep(tempoMs)
                    }
                }
            } catch (_: InterruptedException) {
            } finally {
                onDone()
            }
        }
    }

    private fun playToneSweep(startFreq: Double, endFreq: Double, durationMs: Int, volume: Float) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(100)
            val generatedSnd = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val progress = i.toDouble() / numSamples
                val currentFreq = startFreq + (endFreq - startFreq) * progress
                // Envelope decay
                val envelope = (1.0 - progress) * volume
                val sample = sin(2.0 * Math.PI * currentFreq * t) * envelope
                generatedSnd[i] = (sample * Short.MAX_VALUE).toInt().toShort()
            }

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val audioFormat = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            val track = AudioTrack(
                audioAttributes,
                audioFormat,
                numSamples * 2,
                AudioTrack.MODE_STATIC,
                android.media.AudioManager.AUDIO_SESSION_ID_GENERATE
            )

            track.write(generatedSnd, 0, numSamples)
            track.play()
            Thread.sleep(durationMs.toLong() + 20)
            track.release()
        } catch (_: Exception) {}
    }
}
