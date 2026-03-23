package org.example.logarithmic

import org.example.logarithmic.interfaces.LogarithmicFunction
import kotlin.math.abs
import kotlin.math.pow

class NaturalLogarithmToPrecision(
    private val precision: Double,
) : LogarithmicFunction {

    companion object {
        const val MAX_ITERATIONS = 10000;
    }

    override fun isDefinedAt(x: Double) = x > 0;

    override fun calculateAt(x: Double): Double {
        var result = 0.0
        for (n in 1..MAX_ITERATIONS) {
            val term = (-1.0).pow((n + 1).toDouble()) * x.pow((n).toDouble()) / (n)
            if (abs(term) < precision) break
            result += term
        }

        return result
    }
}