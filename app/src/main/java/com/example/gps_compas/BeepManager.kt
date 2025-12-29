package com.example.gps_compas

import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.*

object BeepManager {
    private var isBeeping = false  // prevent overlapping beeps

    fun beepSeries() {
        if (isBeeping) return  // already running, skip

        isBeeping = true

        CoroutineScope(Dispatchers.Default).launch {
            val toneGen = ToneGenerator(AudioManager.STREAM_ALARM, 100)
            repeat(1) { // 3 beeps
                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 200)
                delay(300) // pause between beeps
            }
            toneGen.release()
            isBeeping = false
        }
    }
}