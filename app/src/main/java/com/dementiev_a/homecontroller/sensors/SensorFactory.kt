package com.dementiev_a.homecontroller.sensors

import androidx.compose.ui.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorManager

class SensorFactory(private val sensorManager: SensorManager) {
    private var sensorsMap = mutableMapOf<Sensor, SensorListener>()

    fun createSensor(
        name: String,
        type: Int,
        onValueChange: (String) -> Unit,
        onColorChange: (Color) -> Unit
    ): Boolean {
        val sensor = sensorManager.getDefaultSensor(type)
        if (sensor != null) {
            sensorsMap[sensor] = SensorListener(name, onValueChange, onColorChange)
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