package com.dementiev_a.homecontroller.sensors

class Configs {
    companion object {
        var key: String? = null
        var scaleCoefficient: Int? = null
        var dangerInterval: Int? = null
        const val ANALYZER_CAPACITY: Int = 20
        const val ULTIMATE_TIME_MILLIS: Int = 10_000 // 10 seconds
    }
}