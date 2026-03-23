package org.example

import org.example.interfaces.MathFunction
import org.example.logarithmic.LogBase
import org.example.logarithmic.LogarithmToPrecision
import org.example.logarithmic.NaturalLogarithmToPrecision
import org.example.logarithmic.interfaces.LogarithmicFunction
import org.example.trigonometric.CosToPrecision
import org.example.trigonometric.CosecantToPrecision
import org.example.trigonometric.SecantToPrecision
import org.example.trigonometric.SinToPrecision
import org.example.trigonometric.TanToPrecision
import org.example.trigonometric.interfaces.*
import kotlin.math.abs

/**
 * Piecewise mathematical function implementing:
 *
 * For x <= 0:
 *   (((((sec(x) - sec(x)) + (sec(x) * sin(x))) * cos(x)) - (sin(x) * csc(x))) / tan(x))
 *
 * For x > 0:
 *   (((((log_2(x) - log_2(x)) + (log_2(x) * log_10(x))) * log_3(x)) - (log_3(x) * ln(x))) / log_3(x))
 */
open class PiecewiseMathFunction(
    private val precision: Double,

    private val sec: Secant = SecantToPrecision(precision),
    private val sin: Sin = SinToPrecision(precision),
    private val cos: Cos = CosToPrecision(precision),
    private val csc: Cosecant = CosecantToPrecision(precision),
    private val tan: Tan = TanToPrecision(precision),


    private val log2: LogarithmicFunction<Double> = LogarithmToPrecision(LogBase(2.0), precision),
    private val log10: LogarithmicFunction<Double> = LogarithmToPrecision(LogBase(10.0), precision),
    private val log3: LogarithmicFunction<Double> = LogarithmToPrecision(LogBase(3.0), precision),
    private val ln: LogarithmicFunction<Double> = NaturalLogarithmToPrecision(precision),
) : MathFunction<Double> {

    override fun isDefinedAt(x: Double): Boolean {
        return if (x <= 0) {
            sec.isDefinedAt(x) &&
                    sin.isDefinedAt(x) &&
                    cos.isDefinedAt(x) &&
                    csc.isDefinedAt(x)
        } else {
            ln.isDefinedAt(x) &&
                    log2.isDefinedAt(x) &&
                    log10.isDefinedAt(x) &&
                    log3.isDefinedAt(x)
        }
    }

    override fun calculateAt(x: Double): Double {
        return if (x <= 0) {
            calculateTrigonometricBranch(x)
        } else {
            calculateLogarithmicBranch(x)
        }
    }

    /**
     * Calculates: (((((sec(x) - sec(x)) + (sec(x) * sin(x))) * cos(x)) - (sin(x) * csc(x))) / tan(x))
     * Note: tan(x) is derived as sin(x) / cos(x) since no Tangent dependency is injected
     */
    private fun calculateTrigonometricBranch(x: Double): Double {
        val secX = sec.calculateAt(x)
        val sinX = sin.calculateAt(x)
        val cosX = cos.calculateAt(x)
        val cscX = csc.calculateAt(x)
        val tanX = tan.calculateAt(x)

        val step1 = secX - secX                          // sec(x) - sec(x) = 0
        val step2 = step1 + (secX * sinX)                // 0 + sec(x) * sin(x)
        val step3 = step2 * cosX                         // (sec(x) * sin(x)) * cos(x)
        val step4 = step3 - (sinX * cscX)                // ... - (sin(x) * csc(x))
        val denominator = tanX                           // tan(x)

        return when {
            abs(denominator) < precision -> {
                when {
                    abs(step4) < precision -> Double.NaN           // 0/0 indeterminate
                    step4 > 0 -> Double.POSITIVE_INFINITY          // positive / ~0
                    else -> Double.NEGATIVE_INFINITY               // negative / ~0
                }
            }
            else -> step4 / denominator
        }
    }

    /**
     * Calculates: (((((log_2(x) - log_2(x)) + (log_2(x) * log_10(x))) * log_3(x)) - (log_3(x) * ln(x))) / log_3(x))
     */
    private fun calculateLogarithmicBranch(x: Double): Double {
        val log2X = log2.calculateAt(x)
        val log10X = log10.calculateAt(x)
        val log3X = log3.calculateAt(x)
        val lnX = ln.calculateAt(x)

        // Evaluate expression step-by-step as written (for testability)
        val step1 = log2X - log2X                          // log_2(x) - log_2(x) = 0
        val step2 = step1 + (log2X * log10X)               // 0 + log_2(x) * log_10(x)
        val step3 = step2 * log3X                          // (log_2(x) * log_10(x)) * log_3(x)
        val step4 = step3 - (log3X * lnX)                  // ... - (log_3(x) * ln(x))
        val denominator = log3X                            // log_3(x)

        return when {
            abs(denominator) < precision -> {
                when {
                    abs(step4) < precision -> Double.NaN           // 0/0 indeterminate
                    step4 > 0 -> Double.POSITIVE_INFINITY          // positive / ~0
                    else -> Double.NEGATIVE_INFINITY               // negative / ~0
                }
            }
            else -> step4 / denominator
        }
    }
}