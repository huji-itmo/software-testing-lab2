package org.example.trigonometric.interfaces

import org.example.interfaces.TrigonometricFunction
import kotlin.math.PI

abstract class Secant : TrigonometricFunction<Double> {

    override fun getPeriod() = 2 * PI
}