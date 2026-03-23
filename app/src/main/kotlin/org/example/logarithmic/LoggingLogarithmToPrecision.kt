package org.example.logarithmic

import org.example.logging.FunctionLogger
import org.example.logarithmic.interfaces.LogarithmicFunction

class LoggingLogarithmToPrecision(
    private val base: LogBase,
    private val precision: Double,
    private val delegate: LogarithmicFunction<Double> = LogarithmToPrecision(base, precision),
) : LogarithmicFunction<Double> {

    override fun isDefinedAt(x: Double): Boolean = delegate.isDefinedAt(x)

    override fun calculateAt(x: Double): Double {
        val result = delegate.calculateAt(x)
        FunctionLogger.log("log_${base.value}", x, result)
        return result
    }
}