package org.example.trigonometric

import org.example.trigonometric.interfaces.Cos
import org.example.trigonometric.interfaces.Sin
import kotlin.math.PI

class CosToPrecision(
    private val precision: Double,
    private val sin: Sin = SinToPrecision(precision),
) : Cos() {
    override fun calculateAt(x: Double) = sin.calculateAt(PI / 2 - x);
}
