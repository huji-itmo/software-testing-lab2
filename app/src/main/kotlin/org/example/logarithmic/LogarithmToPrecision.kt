package org.example.logarithmic

import org.example.logarithmic.interfaces.LogarithmicFunction

class LogarithmToPrecision(
    private val base: LogBase,
    private val precision: Double,
    private val ln: LogarithmicFunction<Double> = NaturalLogarithmToPrecision(precision),
    private val lnOfBase: Double = ln.calculateAt(base.value),
) : LogarithmicFunction<Double> {

    override fun isDefinedAt(x: Double) = x > precision;

    override fun calculateAt(x: Double) =
        if (!isDefinedAt(x)) if (x <= precision) Double.NEGATIVE_INFINITY else Double.NaN
        else ln.calculateAt(x) / lnOfBase;
}