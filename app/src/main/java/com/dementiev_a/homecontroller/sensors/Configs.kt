package com.dementiev_a.homecontroller.sensors

class Configs {
    companion object {
        var key: String? = null
        const val ANALYZER_CAPACITY: Int = 20
        const val ULTIMATE_TIME_MILLIS: Int = 10_000 // 10 seconds
        const val DEFAULT_COEFFICIENT: Float = 3f
        const val DANGER_DELAY: Int = 600_000 // 10 minutes
    }
}