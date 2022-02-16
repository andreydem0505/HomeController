package com.dementiev_a.homecontroller

import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dementiev_a.homecontroller.sensors.SensorFactory
import com.dementiev_a.homecontroller.ui.theme.HomeControllerTheme

class SensorActivity : ComponentActivity() {
    private lateinit var sensorFactory: SensorFactory
    private val sensorTypes = mapOf(
        "Освещение" to android.hardware.Sensor.TYPE_LIGHT,
        "Гироскоп" to android.hardware.Sensor.TYPE_GYROSCOPE,
        "Влажность" to android.hardware.Sensor.TYPE_RELATIVE_HUMIDITY,
        "Ускорение" to android.hardware.Sensor.TYPE_ACCELEROMETER,
        "Вес" to android.hardware.Sensor.TYPE_GRAVITY,
        "Магнитное поле" to android.hardware.Sensor.TYPE_MAGNETIC_FIELD,
        "Расстояние" to android.hardware.Sensor.TYPE_PROXIMITY,
        "Температура" to android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE,
        "Движение" to android.hardware.Sensor.TYPE_MOTION_DETECT,
        "Давление" to android.hardware.Sensor.TYPE_PRESSURE
    )
    private var toCreate = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorFactory = SensorFactory(applicationContext.getSystemService(SENSOR_SERVICE) as SensorManager)
        setContent {
            HomeControllerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SensorValues()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorFactory.unregisterAll()
    }

    @Composable
    private fun SensorValues() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            for ((name, type) in sensorTypes) {
                Sensor(name, type)
            }
        }
        toCreate = false
        sensorFactory.registerAll()
    }

    @Composable
    private fun Sensor(name: String, type: Int) {
        var value by remember { mutableStateOf("") }
        var color by remember { mutableStateOf(Color.Black) }
        var toShow = true
        if (toCreate) {
            toShow = sensorFactory.createSensor(name, type, { value = it }, { color = it })
        }
        if (toShow) {
            Column(
                modifier = Modifier.padding(0.dp, 10.dp)
            ) {
                Text(
                    text = "$name:",
                    fontSize = 24.sp,
                    color = color
                )
                Text(
                    textAlign = TextAlign.Center,
                    text = value,
                    fontSize = 18.sp,
                    color = color
                )
            }
        }
    }
}
