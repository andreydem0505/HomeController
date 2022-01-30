package com.dementiev_a.homecontroller.sensors

import androidx.compose.ui.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.runtime.MutableState

class SensorFactory(private val sensorManager: SensorManager) {
    private var sensorsMap = mutableMapOf<Sensor, SensorListener>()

    fun createSensor(sensorType: Int,
                     valueReference: MutableState<String>,
                     colorReference: MutableState<Color>
    ): Boolean {
        val sensor = sensorManager.getDefaultSensor(sensorType)
        if (sensor != null) {
            sensorsMap[sensor] = SensorListener(valueReference, colorReference)
            return true
        }
        return false
    }

    fun registerAll() {
        for (entry in sensorsMap) {
            sensorManager.registerListener(entry.value, entry.key, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun unregisterAll() {
        for (entry in sensorsMap) {
            sensorManager.unregisterListener(entry.value)
        }
    }
}