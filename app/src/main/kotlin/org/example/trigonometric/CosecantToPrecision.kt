package org.example.trigonometric

import com.google.common.primitives.Doubles.min
import org.example.trigonometric.interfaces.Cosecant
import org.example.trigonometric.interfaces.Sin
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.roundToInt

class CosecantToPrecision(
    private val precision: Double,
    private val sin: Sin = SinToPrecision(precision),
) : Cosecant() {

    override fun isDefinedAt(x: Double): Boolean {
        val period = getPeriod()  // 2π

        val normalizedX = ((x % period) + period) % period

        val distToZero = min(normalizedX, period - normalizedX)
        val distToPi = abs(normalizedX - PI)
        val minDist = min(distToZero, distToPi)

        return minDist >= precision
    }

    override fun calculateAt(x: Double): Double {
        if (!isDefinedAt(x)) {
            val period = getPeriod()

            val asymptoteIdx = (x / PI).roundToInt()
            val asymptote = asymptoteIdx * PI
            val delta = x - asymptote

            if (delta == 0.0) return Double.NaN

            val isNegative = if (asymptoteIdx % 2 == 0) {
                delta < 0
            } else {
                delta > 0
            }

            return if (isNegative) Double.NEGATIVE_INFINITY else Double.POSITIVE_INFINITY
        }
        return 1.0 / sin.calculateAt(x)
    }


    override fun normalizeToPeriod(x: Double) = sin.normalizeToPeriod(x);
}