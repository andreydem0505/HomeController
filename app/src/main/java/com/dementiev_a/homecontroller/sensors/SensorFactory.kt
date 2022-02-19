package com.dementiev_a.homecontroller.sensors

import android.hardware.Sensor
import android.hardware.SensorManager

class SensorFactory(private val sensorManager: SensorManager) {
    private var sensorsMap = mutableMapOf<Sensor, SensorListener>()

    fun createSensor(sensorInfo: SensorInfo): Boolean {
        val sensor = sensorManager.getDefaultSensor(sensorInfo.type)
        if (sensor != null) {
            sensorsMap[sensor] = SensorListener(sensorInfo)
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