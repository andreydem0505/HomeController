package com.dementiev_a.homecontroller

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dementiev_a.homecontroller.sensors.SensorFactory
import com.dementiev_a.homecontroller.ui.theme.HomeControllerTheme


class MainActivity : ComponentActivity() {
    private lateinit var sensorFactory: SensorFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sensorTypes = mapOf(
            "Освещение" to Sensor.TYPE_LIGHT,
            "Гироскоп" to Sensor.TYPE_GYROSCOPE,
            "Влажность" to Sensor.TYPE_RELATIVE_HUMIDITY,
            "Ускорение" to Sensor.TYPE_ACCELEROMETER,
            "Вес" to Sensor.TYPE_GRAVITY,
            "Магнитное поле" to Sensor.TYPE_MAGNETIC_FIELD,
            "Расстояние" to Sensor.TYPE_PROXIMITY,
            "Температура" to Sensor.TYPE_AMBIENT_TEMPERATURE,
            "Движение" to Sensor.TYPE_MOTION_DETECT,
            "Давление" to Sensor.TYPE_PRESSURE
        )
        setContent {
            sensorFactory = SensorFactory(getSystemService(Context.SENSOR_SERVICE) as SensorManager)
            HomeControllerTheme {
                Column(
                    modifier = Modifier.fillMaxSize()
                        .padding(20.dp)
                ) {
                    OutputSensorValues(sensorTypes)
                }
            }
            sensorFactory.registerAll()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorFactory.unregisterAll()
    }

    @Composable
    fun OutputSensorValues(sensorTypes: Map<String, Int>) {
        for ((name, type) in sensorTypes) {
            val valueState = remember { mutableStateOf("") }
            val colorState = remember { mutableStateOf(Color.Black) }
            if (sensorFactory.createSensor(type, valueState, colorState)) {
                Text(
                    text = "$name: ${valueState.value}",
                    fontSize = 24.sp,
                    lineHeight = 30.sp,
                    color = colorState.value
                )
            }
        }
    }
}
