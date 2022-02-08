package com.dementiev_a.homecontroller

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dementiev_a.homecontroller.sensors.Configs
import com.dementiev_a.homecontroller.sensors.SensorFactory
import com.dementiev_a.homecontroller.shared_preferences.SharedPreferencesService
import com.dementiev_a.homecontroller.ui.theme.HomeControllerTheme
import java.lang.NumberFormatException


class MainActivity : ComponentActivity() {
    private lateinit var sensorFactory: SensorFactory
    private lateinit var sps: SharedPreferencesService
    private val sensorTypes = mapOf(
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sps = SharedPreferencesService(baseContext)
        sensorFactory = SensorFactory(getSystemService(Context.SENSOR_SERVICE) as SensorManager)
    }

    override fun onStart() {
        super.onStart()
        if (sps.hasUserKey()) {
            render { Settings() }
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorFactory.unregisterAll()
    }

    @Composable
    private fun Settings() {
        var scaleCoefficientText by rememberSaveable { mutableStateOf(sps.readScaleCoefficient().toString()) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = scaleCoefficientText,
                onValueChange = {
                    scaleCoefficientText = it
                },
                label = { Text("Уровень чувствительности") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                shape = RoundedCornerShape(50),
                onClick = {
                    sps.saveScaleCoefficient(scaleCoefficientText.toInt())
                    Configs.scaleCoefficient = scaleCoefficientText.toInt()
                    render { SensorValues() }
                },
                enabled = try {
                        scaleCoefficientText.toInt()
                        true
                    } catch (e: NumberFormatException) {
                        false
                    }
            ) {
                Text(text = "Запустить")
            }
        }
    }

    @Composable
    private fun SensorValues() {
        Configs.key = sps.readKey()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            for ((name, type) in sensorTypes) {
                Sensor(name, type)
            }
        }
        sensorFactory.registerAll()
    }

    @Composable
    private fun Sensor(name: String, type: Int) {
        val valueState = remember { mutableStateOf("") }
        val colorState = remember { mutableStateOf(Color.Black) }
        if (sensorFactory.createSensor(name, type, valueState, colorState)) {
            Text(
                text = "$name: ${valueState.value}",
                fontSize = 24.sp,
                lineHeight = 30.sp,
                color = colorState.value
            )
        }
    }

    private fun render(composable: @Composable () -> Unit) {
        setContent {
            HomeControllerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    composable()
                }
            }
        }
    }
}
