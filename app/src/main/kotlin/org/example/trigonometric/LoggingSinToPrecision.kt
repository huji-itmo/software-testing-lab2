package org.example.trigonometric

import org.example.logging.FunctionLogger
import org.example.trigonometric.interfaces.Sin

class LoggingSinToPrecision(
    private val precision: Double,
    private val delegate: Sin = SinToPrecision(precision),
) : Sin() {

    override fun isDefinedAt(x: Double): Boolean = delegate.isDefinedAt(x)

    override fun calculateAt(x: Double): Double {
        val result = delegate.calculateAt(x)
        FunctionLogger.log("sin", x, result)
        return result
    }

    override fun getPeriod() = delegate.getPeriod()

    override fun normalizeToPeriod(x: Double) = delegate.normalizeToPeriod(x)
}