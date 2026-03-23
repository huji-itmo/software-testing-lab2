package org.example.trigonometric.interfaces

import org.example.trigonometric.interfaces.TrigonometricFunction
import kotlin.math.PI

abstract class Cosecant : TrigonometricFunction<Double> {

    override fun getPeriod() = 2 * PI
}