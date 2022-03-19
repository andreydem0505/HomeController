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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dementiev_a.homecontroller.sensors.SensorFactory
import com.dementiev_a.homecontroller.sensors.SensorInfo
import com.dementiev_a.homecontroller.ui.theme.HomeControllerTheme

class SensorActivity : ComponentActivity() {
    private lateinit var sensorFactory: SensorFactory
    private val sensorTypes = mapOf(
        getString(R.string.sensor_light) to android.hardware.Sensor.TYPE_LIGHT,
        getString(R.string.sensor_gyroscope) to android.hardware.Sensor.TYPE_GYROSCOPE,
        getString(R.string.sensor_humidity) to android.hardware.Sensor.TYPE_RELATIVE_HUMIDITY,
        getString(R.string.sensor_accelerometer) to android.hardware.Sensor.TYPE_ACCELEROMETER,
        getString(R.string.sensor_gravity) to android.hardware.Sensor.TYPE_GRAVITY,
        getString(R.string.sensor_magnetic_field) to android.hardware.Sensor.TYPE_MAGNETIC_FIELD,
        getString(R.string.sensor_proximity) to android.hardware.Sensor.TYPE_PROXIMITY,
        getString(R.string.sensor_temperature) to android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE,
        getString(R.string.sensor_motion) to android.hardware.Sensor.TYPE_MOTION_DETECT,
        getString(R.string.sensor_pressure) to android.hardware.Sensor.TYPE_PRESSURE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorFactory = SensorFactory(applicationContext.getSystemService(SENSOR_SERVICE) as SensorManager)
        setContent {
            HomeControllerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SensorList()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorFactory.unregisterAll()
    }

    @Composable
    private fun SensorList() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            for ((name, type) in sensorTypes) {
                val viewModel = SensorViewModel()
                if (sensorFactory.createSensor(SensorInfo(
                        name = name,
                        type = type,
                        onValueChange = { viewModel.onValueChange(it) },
                        onDangerChange = { viewModel.onDangerChange(it) }
                    ))) {
                    Sensor(name = name, viewModel = viewModel)
                }
            }
        }
        sensorFactory.registerAll()
    }

    class SensorViewModel : ViewModel() {
        private val _value = MutableLiveData("")
        val valueData: LiveData<String> = _value
        private val _danger = MutableLiveData(false)
        val dangerData: LiveData<Boolean> = _danger

        fun onValueChange(value: String) {
            _value.postValue(value)
        }

        fun onDangerChange(danger: Boolean) {
            _danger.postValue(danger)
        }
    }

    @Composable
    private fun Sensor(name: String, viewModel: SensorViewModel) {
        val value by viewModel.valueData.observeAsState("")
        val danger by viewModel.dangerData.observeAsState(false)
        Column(
            modifier = Modifier.padding(0.dp, 10.dp)
        ) {
            Text(
                text = "$name:",
                fontSize = 24.sp,
                color = getColor(danger)
            )
            Text(
                text = value,
                fontSize = 18.sp,
                color = getColor(danger)
            )
        }
    }

    @Composable
    private fun getColor(danger: Boolean) = if (danger) MaterialTheme.colors.error else MaterialTheme.colors.onSurface
}
