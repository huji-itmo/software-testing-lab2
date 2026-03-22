package org.example.trigonometric.interfaces

import org.example.interfaces.TrigonometricFunction
import kotlin.math.PI

abstract class Cos: TrigonometricFunction<Double> {
    override fun getPeriod() = 2 * PI

    override fun isDefinedAt(x: Double): Boolean = x.isFinite()

    override fun normalizeToPeriod(x: Double): Double {
        val period = getPeriod();
        val result = x % period
        return if (result < 0) result + period else result
    }
}