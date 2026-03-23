package org.example.trigonometric

import org.example.trigonometric.interfaces.Sin
import java.math.BigInteger
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow


class SinToPrecision(
    private val precision: Double,
) : Sin() {

    companion object {
        const val MAX_ITERATIONS = 10000;

        fun factorial(n: Int): Long {
            return (1..n).fold(1L) { acc, i -> acc * i }
        }

        private fun bigFactorial(n: Int): BigInteger {
            var result = BigInteger.ONE
            for (i in 2..n) {
                result = result.multiply(BigInteger.valueOf(i.toLong()))
            }
            return result
        }
    }

    override fun calculateAt(x: Double): Double {
        var normalized = normalizeToPeriod(x)
        val period = getPeriod()
        if (normalized > PI) normalized -= period
        else if (normalized <= -PI) normalized += period

        var result = 0.0
        for (n in 0..MAX_ITERATIONS) {
            val denominator = if (2 * n + 1 <= 20) {
                factorial(2 * n + 1).toDouble()
            } else {
                bigFactorial(2 * n + 1).toDouble()
            }
            val term = (-1.0).pow(n.toDouble()) * normalized.pow((2 * n + 1).toDouble()) / denominator
            if (abs(term) < precision) break
            result += term
        }

        return result
    }
}