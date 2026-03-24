package org.example.logarithmic

import org.example.logarithmic.interfaces.LogarithmicFunction
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.ln

class NaturalLogarithmToPrecision(
    private val precision: Double,
) : LogarithmicFunction<Double> {

    companion object {
        const val MAX_ITERATIONS = 10000;
        private val LN2 = ln(2.0)
    }

    override fun isDefinedAt(x: Double) = x > 0;

    override fun calculateAt(x: Double): Double {
        if (x <= 0) return if (x == 0.0) Double.NEGATIVE_INFINITY else Double.NaN
        if (x == 1.0) return 0.0
        if (x.isNaN()) return Double.NaN

        return computeLn(x)
    }

    //ln(x) = m * ln(2) + ln(y) where x = y * 2^m
    private fun computeLn(x: Double): Double {
        var m = 0
        var y = x

        while (y > 1.5) {
            y /= 2.0
            m++
        }
        while (y < 1.0 / 1.5) {
            y *= 2.0
            m--
        }

        val simpleResult = computeLnSimple(y)
        return m * LN2 + simpleResult
    }

    private fun computeLnSimple(x: Double): Double {
        val y = x - 1.0
        var result = 0.0
        for (n in 1..MAX_ITERATIONS) {
            val term = (-1.0).pow((n + 1).toDouble()) * y.pow((n).toDouble()) / (n)
            if (abs(term) < precision) break
            result += term
        }
        return result
    }
}