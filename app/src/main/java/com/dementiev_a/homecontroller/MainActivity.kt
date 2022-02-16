package com.dementiev_a.homecontroller

import android.content.Intent
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dementiev_a.homecontroller.sensors.Configs
import com.dementiev_a.homecontroller.shared_preferences.SharedPreferencesService
import com.dementiev_a.homecontroller.ui.theme.HomeControllerTheme
import kotlinx.coroutines.delay
import java.lang.NumberFormatException


class MainActivity : ComponentActivity() {
    private lateinit var sps: SharedPreferencesService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sps = SharedPreferencesService(baseContext)
    }

    override fun onStart() {
        super.onStart()
        if (sps.hasUserKey()) {
            render { Settings() }
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private val minScaleCoefficient = 1
    private val maxScaleCoefficient = 10

    @Composable
    private fun Settings() {
        var scaleCoefficient by rememberSaveable { mutableStateOf(sps.readScaleCoefficient().toString()) }
        val scaleCoefficientValidation = {
            text: String -> try {
                text.toInt() in minScaleCoefficient..maxScaleCoefficient
            } catch (e: NumberFormatException) {
                false
            }
        }
        var delay by rememberSaveable { mutableStateOf(sps.readDelay().toString()) }
        val delayValidation = {
            text: String -> try {
                text.toInt() in 0..60
            } catch (e: NumberFormatException) {
                false
            }
        }
        CenterColumn(verticalArrangement = Arrangement.Center) {
            ScaleCoefficientInput(
                value = scaleCoefficient,
                onValueChange = { scaleCoefficient = it },
                inputValidation = scaleCoefficientValidation
            )
            DelayInput(
                value = delay,
                onValueChange = { delay = it },
                inputValidation = delayValidation
            )
        }
        CenterColumn(verticalArrangement = Arrangement.Bottom) {
            StartButton(
                scaleCoefficient = scaleCoefficient,
                delay = delay,
                enabled = scaleCoefficientValidation(scaleCoefficient) && delayValidation(delay)
            )
        }
    }

    @Composable
    private fun ScaleCoefficientInput(
        value: String,
        onValueChange: (String) -> Unit,
        inputValidation: (String) -> Boolean
    ) {
        val isError = !inputValidation(value)
        var openDialog by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
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
            Error(text = "Целое число от $minScaleCoefficient до $maxScaleCoefficient")
        }
        if (openDialog) {
            Alert(text = "Чем меньше значение, тем система будет более чувствительна. " +
                    "Чувствительность изменяется пропорционально. Так, система со " +
                    "значением \"4\" в 2 раза чувствительнее чем со " +
                    "значением \"8\".\nРекомендуемое значение: 3.",
                onCloseDialog = { openDialog = false }
            )
        }
    }

    @Composable
    private fun DelayInput(
        value: String,
        onValueChange: (String) -> Unit,
        inputValidation: (String) -> Boolean
    ) {
        val isError = !inputValidation(value)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Задержка (в минутах)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            isError = isError,
            modifier = Modifier.padding(0.dp, 10.dp),
        )
        if (isError) {
            Error(text = "Допустимые значения: от 0 до 60")
        }
    }

    @Composable
    private fun StartButton(
        scaleCoefficient: String,
        delay: String,
        enabled: Boolean
    ) {
        Button(
            shape = RoundedCornerShape(50),
            onClick = {
                sps.saveScaleCoefficient(scaleCoefficient.toInt())
                Configs.scaleCoefficient = scaleCoefficient.toInt()
                sps.saveDelay(delay.toInt())
                Configs.key = sps.readKey()
                if (delay.toInt() == 0) {
                    startSensorActivity()
                } else {
                    render { Timer(minutesValue = delay.toInt()) }
                }
            },
            enabled = enabled,
            contentPadding = PaddingValues(20.dp, 15.dp)
        ) {
            Text(text = "Запустить")
        }
    }

    @Composable
    private fun Alert(
        text: String,
        onCloseDialog: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onCloseDialog,
            text = {
                Text(text = text)
            },
            buttons = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = onCloseDialog,
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

    @Composable
    private fun Error(text: String) {
        Text(
            text = text,
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption
        )
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
        LaunchedEffect(true) {
            while (minutes > 0 || seconds > 0) {
                if (seconds > 0) {
                    seconds -= 1
                } else {
                    minutes -= 1
                    seconds = 59
                }
                delay(1000)
            }
            startSensorActivity()
        }
    }

    @Composable
    private fun CenterColumn(
        verticalArrangement: Arrangement.Vertical,
        content: @Composable () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }

    private fun render(content: @Composable () -> Unit) {
        setContent {
            HomeControllerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    content()
                }
            }
        }
    }

    private fun startSensorActivity() {
        startActivity(Intent(this, SensorActivity::class.java))
    }
}
