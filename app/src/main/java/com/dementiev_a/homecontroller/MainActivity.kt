package com.dementiev_a.homecontroller

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
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

    @Composable
    private fun Settings() {
        var scaleCoefficient by rememberSaveable { mutableStateOf(sps.readScaleCoefficient().toString()) }
        var startDelay by rememberSaveable { mutableStateOf(sps.readStartDelay().toString()) }
        var dangerInterval by rememberSaveable { mutableStateOf(sps.readDangerInterval().toString()) }
        val scaleCoefficientValidation = checkIntInRange(Configs.MIN_SCALE_COEFFICIENT..Configs.MAX_SCALE_COEFFICIENT)
        val startDelayValidation = checkIntInRange(0..60)
        val dangerIntervalValidation = checkIntInRange(1..60)
        CenterColumn(verticalArrangement = Arrangement.Center) {
            ScaleCoefficientInput(
                value = scaleCoefficient,
                onValueChange = { scaleCoefficient = it },
                inputValidation = scaleCoefficientValidation
            )
            StartDelayInput(
                value = startDelay,
                onValueChange = { startDelay = it },
                inputValidation = startDelayValidation
            )
            DangerIntervalInput(
                value = dangerInterval,
                onValueChange = { dangerInterval = it },
                inputValidation = dangerIntervalValidation
            )
        }
        CenterColumn(verticalArrangement = Arrangement.Bottom) {
            StartButton(
                scaleCoefficient = scaleCoefficient,
                startDelay = startDelay,
                dangerInterval = dangerInterval,
                enabled = scaleCoefficientValidation(scaleCoefficient)
                        && startDelayValidation(startDelay)
                        && dangerIntervalValidation(dangerInterval)
            )
        }
    }

    private fun checkIntInRange(range: IntRange) = {
        text: String -> try {
            text.toInt() in range
        } catch (e: NumberFormatException) {
            false
        }
    }

    @Composable
    private fun ScaleCoefficientInput(
        value: String,
        onValueChange: (String) -> Unit,
        inputValidation: (String) -> Boolean
    ) {
        var openDialog by remember { mutableStateOf(false) }
        OutlinedInput(
            value = value,
            isError = !inputValidation(value),
            onValueChange = onValueChange,
            labelText = stringResource(R.string.main_activity_sensitivity_level),
            errorText = stringResource(R.string.main_activity_integer_from_to, Configs.MIN_SCALE_COEFFICIENT, Configs.MAX_SCALE_COEFFICIENT),
        ) {
            AboutIconButton { openDialog = true }
        }
        if (openDialog) {
            Alert(text = stringResource(R.string.main_activity_scale_coefficient_input_alert, Configs.RECOMMENDED_SCALE_COEFFICIENT),
                onCloseDialog = { openDialog = false }
            )
        }
    }

    @Composable
    private fun StartDelayInput(
        value: String,
        onValueChange: (String) -> Unit,
        inputValidation: (String) -> Boolean
    ) {
        OutlinedInput(
            value = value,
            isError = !inputValidation(value),
            onValueChange = onValueChange,
            labelText = stringResource(R.string.main_activity_start_delay_label),
            errorText = stringResource(R.string.main_activity_error_not_in_range, 0, 60),
            trailingIcon = {}
        )
    }

    @Composable
    private fun DangerIntervalInput(
        value: String,
        onValueChange: (String) -> Unit,
        inputValidation: (String) -> Boolean
    ) {
        var openDialog by remember { mutableStateOf(false) }
        OutlinedInput(
            value = value,
            isError = !inputValidation(value),
            onValueChange = onValueChange,
            labelText = stringResource(R.string.main_activity_delay_interval_label),
            errorText = stringResource(R.string.main_activity_error_not_in_range, 1, 60)
        ) {
            AboutIconButton { openDialog = true }
        }
        if (openDialog) {
            Alert(
                text = stringResource(R.string.main_activity_danger_interval_input_alert),
                onCloseDialog = { openDialog = false }
            )
        }
    }

    @Composable
    private fun AboutIconButton(onClick: () -> Unit) {
        IconButton(onClick = onClick) {
            Icon(Icons.Filled.Info, "about", tint = Color.Gray)
        }
    }

    @Composable
    private fun OutlinedInput(
        value: String,
        isError: Boolean,
        onValueChange: (String) -> Unit,
        labelText: String,
        errorText: String,
        trailingIcon: @Composable () -> Unit
    ) {
        val focusManager = LocalFocusManager.current
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(labelText) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            isError = isError,
            singleLine = true,
            trailingIcon = trailingIcon,
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            modifier = Modifier.padding(0.dp, 10.dp)
        )
        if (isError) {
            Error(text = errorText)
        }
    }

    @Composable
    private fun StartButton(
        scaleCoefficient: String,
        startDelay: String,
        dangerInterval: String,
        enabled: Boolean
    ) {
        Button(
            shape = RoundedCornerShape(50),
            onClick = {
                sps.saveScaleCoefficient(scaleCoefficient.toInt())
                sps.saveStartDelay(startDelay.toInt())
                sps.saveDangerInterval(dangerInterval.toInt())
                Configs.scaleCoefficient = scaleCoefficient.toInt()
                Configs.key = sps.readKey()
                Configs.dangerInterval = dangerInterval.toInt() * 60_000
                if (startDelay.toInt() == 0) {
                    startSensorActivity()
                } else {
                    render { Timer(minutesValue = startDelay.toInt()) }
                }
            },
            enabled = enabled,
            contentPadding = PaddingValues(20.dp, 15.dp)
        ) {
            Text(text = stringResource(R.string.main_activity_start))
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
                            contentColor = MaterialTheme.colors.surface
                        )
                    ) {
                        Text(stringResource(R.string.ok))
                    }
                }
            }
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
    private fun Error(text: String) {
        Text(
            text = text,
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption
        )
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
