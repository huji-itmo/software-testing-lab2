package org.example.trigonometric.interfaces

import kotlin.math.PI

abstract class Tan : TrigonometricFunction<Double> {
    override fun getPeriod() = PI


    override fun normalizeToPeriod(x: Double): Double {
        val period = getPeriod();
        val result = x % period
        return if (result < 0) result + period else result
    }
}