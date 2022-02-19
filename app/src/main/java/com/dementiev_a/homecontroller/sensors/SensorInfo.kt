package com.dementiev_a.homecontroller.sensors

data class SensorInfo(
    val name: String,
    val type: Int,
    val onValueChange: (String) -> Unit,
    val onDangerChange: (Boolean) -> Unit
)
