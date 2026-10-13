package ua.kpi.smartgrid.domain

import kotlin.math.sqrt

/**
 * Формули для системи розподілу електроенергії (з практичної роботи №1).
 */
object Calculator {

    // P = √3 · U · I · cosφ, кВт (U у кВ)
    fun calculatePower(voltage: Double, current: Double, cosPhi: Double): Double {
        return sqrt(3.0) * voltage * current * cosPhi
    }

    // ΔP = 3 · I² · R, кВт
    fun calculateLosses(current: Double, resistance: Double): Double {
        return 3 * current * current * resistance / 1000
    }

    // Завантаження у відсотках
    fun calculateLoadPercent(load: Double, capacity: Double): Double {
        if (capacity <= 0.0) return 0.0
        return load / capacity * 100
    }

    // Статус: до 80 % — норма, до 100 % — високе, більше — перевантаження
    fun getLoadStatus(percent: Double): String {
        return when {
            percent <= 80 -> "Норма"
            percent <= 100 -> "Високе навантаження"
            else -> "Перевантаження"
        }
    }
}
