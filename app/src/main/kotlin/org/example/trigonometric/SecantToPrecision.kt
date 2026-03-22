package org.example.trigonometric

import org.example.trigonometric.interfaces.Cos
import org.example.trigonometric.interfaces.Secant
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

class SecantToPrecision(
    private val precision: Double,
    private val cos: Cos = CosToPrecision(precision),
) : Secant() {

    override fun isDefinedAt(x: Double): Boolean {
        val period = getPeriod()
        val halfPeriod = period / 2

        val normalizedX = ((x % period) + period) % period

        val distToPiOver2 = abs(normalizedX - PI / 2)
        val distTo3PiOver2 = abs(normalizedX - 3 * PI / 2)
        val minDist = min(distToPiOver2, distTo3PiOver2)

        return minDist >= precision
    }


    override fun calculateAt(x: Double): Double {
        if (!isDefinedAt(x)) {
            val halfPeriod = getPeriod() / 2

            val asymptoteIdx = ((x - PI / 2) / halfPeriod).roundToInt()
            val asymptote = PI / 2 + asymptoteIdx * halfPeriod
            val delta = x - asymptote

            if (delta == 0.0) return Double.NaN

            val isNegative = if (asymptoteIdx % 2 == 0) {
                delta > 0
            } else {
                delta < 0
            }

            return if (isNegative) Double.NEGATIVE_INFINITY else Double.POSITIVE_INFINITY
        }
        return 1.0 / cos.calculateAt(x)
    }

    override fun normalizeToPeriod(x: Double) = cos.normalizeToPeriod(x);

}
