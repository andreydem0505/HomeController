package com.dementiev_a.homecontroller.sensors

class Configs {
    companion object {
        var key: String? = null
        var scaleCoefficient: Int? = null
        var dangerInterval: Int? = null
        const val ANALYZER_CAPACITY: Int = 20
        const val ULTIMATE_TIME_MILLIS: Int = 10_000 // 10 seconds

        const val MIN_SCALE_COEFFICIENT = 1
        const val MAX_SCALE_COEFFICIENT = 10
        const val RECOMMENDED_SCALE_COEFFICIENT = 3
        const val RECOMMENDED_START_DELAY = 0
        const val RECOMMENDED_DANGER_INTERVAL = 10
    }
}