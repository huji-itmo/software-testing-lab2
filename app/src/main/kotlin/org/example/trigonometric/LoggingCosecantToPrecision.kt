package org.example.trigonometric

import org.example.logging.FunctionLogger
import org.example.trigonometric.interfaces.Cosecant
import org.example.trigonometric.interfaces.Sin

class LoggingCosecantToPrecision(
    private val precision: Double,
    private val delegate: Cosecant = CosecantToPrecision(precision),
) : Cosecant() {

    override fun isDefinedAt(x: Double): Boolean = delegate.isDefinedAt(x)

    override fun calculateAt(x: Double): Double {
        val result = delegate.calculateAt(x)
        FunctionLogger.log("csc", x, result)
        return result
    }

    override fun getPeriod() = delegate.getPeriod()

    override fun normalizeToPeriod(x: Double) = delegate.normalizeToPeriod(x)
}