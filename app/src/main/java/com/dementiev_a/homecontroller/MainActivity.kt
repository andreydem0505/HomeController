package com.dementiev_a.homecontroller

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
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
import kotlinx.coroutines.delay
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

    private val minScaleCoefficient = 1
    private val maxScaleCoefficient = 10

    @Composable
    private fun Settings() {
        val scaleCoefficientText = rememberSaveable { mutableStateOf(sps.readScaleCoefficient().toString()) }
        val scaleCoefficientValidation = {
            text: String -> try {
                text.toInt() in minScaleCoefficient..maxScaleCoefficient
            } catch (e: NumberFormatException) {
                false
            }
        }
        val delayText = rememberSaveable { mutableStateOf(sps.readDelay().toString()) }
        val delayValidation = {
            text: String -> try {
                text.toInt() >= 0
            } catch (e: NumberFormatException) {
                false
            }
        }
        CenterColumn(verticalArrangement = Arrangement.Center) {
            ScaleCoefficientInput(
                textState = scaleCoefficientText,
                inputValidation = scaleCoefficientValidation
            )
            DelayInput(
                textState = delayText,
                inputValidation = delayValidation
            )
        }
        CenterColumn(verticalArrangement = Arrangement.Bottom) {
            StartButton(
                scaleCoefficientText = scaleCoefficientText,
                delayText = delayText,
                enabled = scaleCoefficientValidation(scaleCoefficientText.value) && delayValidation(delayText.value)
            )
        }
    }

    @Composable
    private fun ScaleCoefficientInput(
        textState: MutableState<String>,
        inputValidation: (String) -> Boolean
    ) {
        val isError = !inputValidation(textState.value)
        var openDialog by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = textState.value,
            onValueChange = {
                textState.value = it
            },
            label = { Text("Уровень чувствительности") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            isError = isError,
            trailingIcon = {
                IconButton(onClick = {
                    openDialog = true
                }) {
                    Icon(Icons.Filled.Info, "about", tint = Color.Gray)
                }
            },
            modifier = Modifier.padding(0.dp, 10.dp)
        )
        if (isError) {
            Text(
                text = "Целое число от $minScaleCoefficient до $maxScaleCoefficient",
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption
            )
        }
        if (openDialog) {
            AlertDialog(
                onDismissRequest = { openDialog = false },
                text = {
                    Text(text = "Чем меньше значение, чем система будет более чувствительна. " +
                            "Чувствительность изменяется пропорционально. Так, система со " +
                            "значением 4 в 2 раза чувствительнее системы с " +
                            "значением 8.\nРекомендуемое значение: 3.")
                },
                buttons = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                openDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Text("OK")
                        }
                    }
                }
            )
        }
    }

    @Composable
    private fun DelayInput(
        textState: MutableState<String>,
        inputValidation: (String) -> Boolean
    ) {
        OutlinedTextField(
            value = textState.value,
            onValueChange = {
                textState.value = it
            },
            label = { Text("Задержка (в минутах)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.padding(0.dp, 10.dp),
            isError = !inputValidation(textState.value)
        )
    }

    @Composable
    private fun StartButton(
        scaleCoefficientText: MutableState<String>,
        delayText: MutableState<String>,
        enabled: Boolean
    ) {
        Button(
            shape = RoundedCornerShape(50),
            onClick = {
                sps.saveScaleCoefficient(scaleCoefficientText.value.toInt())
                Configs.scaleCoefficient = scaleCoefficientText.value.toInt()
                sps.saveDelay(delayText.value.toInt())
                if (delayText.value.toInt() == 0) {
                    render { SensorValues() }
                } else {
                    render { Timer(minutesValue = delayText.value.toInt()) }
                }
            },
            enabled = enabled
        ) {
            Text(text = "Запустить")
        }
    }

    @Composable
    private fun CenterColumn(
        verticalArrangement: Arrangement.Vertical,
        composable: @Composable () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            composable()
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

    @Composable
    private fun Timer(minutesValue: Int) {
        var minutes by remember { mutableStateOf(minutesValue) }
        var seconds by remember { mutableStateOf(0) }
        CenterColumn(verticalArrangement = Arrangement.Center) {
            Text(
                text = "${if (minutes < 10) 0 else ""}$minutes:${if (seconds < 10) 0 else ""}$seconds",
                fontSize = 64.sp
            )
        }
        LaunchedEffect(0) {
            while (minutes > 0 || seconds > 0) {
                if (seconds > 0) {
                    seconds -= 1
                } else {
                    minutes -= 1
                    seconds = 59
                }
                delay(1000)
            }
            render { SensorValues() }
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
