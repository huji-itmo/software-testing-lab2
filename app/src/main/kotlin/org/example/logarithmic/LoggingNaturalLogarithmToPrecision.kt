package org.example.logarithmic

import org.example.logging.FunctionLogger
import org.example.logarithmic.interfaces.LogarithmicFunction

class LoggingNaturalLogarithmToPrecision(
    private val precision: Double,
    private val delegate: LogarithmicFunction<Double> = NaturalLogarithmToPrecision(precision),
) : LogarithmicFunction<Double> {

    override fun isDefinedAt(x: Double): Boolean = delegate.isDefinedAt(x)

    override fun calculateAt(x: Double): Double {
        val result = delegate.calculateAt(x)
        FunctionLogger.log("ln", x, result)
        return result
    }
}