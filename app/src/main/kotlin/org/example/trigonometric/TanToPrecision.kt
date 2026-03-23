package org.example.trigonometric

import org.example.trigonometric.interfaces.Cos
import org.example.trigonometric.interfaces.Sin
import org.example.trigonometric.interfaces.Tan
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.roundToInt


class TanToPrecision(
    private val precision: Double,
    private val sin: Sin = SinToPrecision(precision),
    private val cos: Cos = CosToPrecision(precision),
    ) : Tan() {
    override fun isDefinedAt(x: Double): Boolean {
        val period = getPeriod()

        val halfPeriod = period / 2

        val normalizedX = ((x % period) + period) % period

        val distToZero = abs(normalizedX - halfPeriod)

        return distToZero >= precision
    }

    override fun calculateAt(x: Double): Double {
        if (!isDefinedAt(x)) {
            val period = getPeriod()

            val asymptoteIdx = ((x - PI / 2) / period).roundToInt()
            val asymptote = PI / 2 + asymptoteIdx * period
            val delta = x - asymptote

            if (delta == 0.0) return Double.NaN

            val isNegative = if (asymptoteIdx % 2 == 0) {
                delta > 0
            } else {
                delta < 0
            }

            return if (isNegative) Double.NEGATIVE_INFINITY else Double.POSITIVE_INFINITY
        }

        return sin.calculateAt(x) / cos.calculateAt(x)
    }
}