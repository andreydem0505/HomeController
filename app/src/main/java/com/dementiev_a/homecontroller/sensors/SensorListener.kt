package com.dementiev_a.homecontroller.sensors

import androidx.compose.ui.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import androidx.compose.runtime.MutableState
import kotlin.concurrent.thread
import kotlin.math.abs

class SensorListener(
    private var valueReference: MutableState<String>,
    private var colorReference: MutableState<Color>
) : SensorEventListener {
    private var lastValues: FloatArray? = null
    private var framesProcessed: Int = 0
    private var ultimateDifference: Float = .0F
    private var lastChanged = System.currentTimeMillis()
    private var lastDanger: Long? = null

    override fun onSensorChanged(event: SensorEvent?) {
        val currentValues = event?.values!!
        if (System.currentTimeMillis() - lastChanged > Constants.ULTIMATE_TIME_MILLIS) {
            danger()
        } else {
            if (framesProcessed < Constants.ANALYZER_CAPACITY) {
                analyze(currentValues)
                framesProcessed++
                if (framesProcessed == Constants.ANALYZER_CAPACITY) {
                    ultimateDifference *= Constants.DEFAULT_COEFFICIENT
                    framesProcessed++
                }
            } else {
                checkDifference(currentValues)
            }
        }

        lastChanged = System.currentTimeMillis()
        updateView()
    }

    private fun updateView() {
        valueReference.value = if (lastValues?.size == 1) {
            String.format("%.2f", lastValues?.get(0))
        } else {
            String.format("(X=%.2f, Y=%.2f, Z=%.2f)",
                lastValues?.get(0),
                lastValues?.get(1),
                lastValues?.get(2))
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun danger() {
        if (lastDanger != null) {
            if (System.currentTimeMillis() - lastDanger!! < Constants.DANGER_DELAY) {
                return
            }
        }
        lastDanger = System.currentTimeMillis()

        colorReference.value = Color.Red
        thread {
            Thread.sleep(5_000)
            colorReference.value = Color.Black
        }
    }

    private fun analyze(currentValues: FloatArray) {
        if (lastValues != null) {
            for (i in currentValues.indices) {
                val difference = abs(currentValues[i] - lastValues!![i])
                if (difference > ultimateDifference) {
                    ultimateDifference = difference
                }
                lastValues!![i] = currentValues[i]
            }
        } else {
            lastValues = FloatArray(currentValues.size)
            for (i in currentValues.indices) {
                lastValues!![i] = currentValues[i]
            }
        }
    }

    private fun checkDifference(currentValues: FloatArray) {
        for (i in currentValues.indices) {
            if (abs(currentValues[i] - lastValues!![i]) >= ultimateDifference) {
                danger()
            }
            lastValues!![i] = currentValues[i]
        }
    }
}