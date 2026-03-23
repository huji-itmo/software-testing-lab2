package org.example.trigonometric

import org.example.logging.FunctionLogger
import org.example.trigonometric.interfaces.Cos
import org.example.trigonometric.interfaces.Sin
import kotlin.math.PI

class LoggingCosToPrecision(
    private val precision: Double,
    private val delegate: Cos = CosToPrecision(precision),
) : Cos() {

    override fun isDefinedAt(x: Double): Boolean = delegate.isDefinedAt(x)

    override fun calculateAt(x: Double): Double {
        val result = delegate.calculateAt(x)
        FunctionLogger.log("cos", x, result)
        return result
    }

    override fun getPeriod() = delegate.getPeriod()

    override fun normalizeToPeriod(x: Double) = delegate.normalizeToPeriod(x)
}