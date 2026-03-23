package org.example

import org.example.logging.FunctionLogger
import org.example.logarithmic.LogBase
import org.example.logarithmic.LoggingLogarithmToPrecision
import org.example.logarithmic.LoggingNaturalLogarithmToPrecision
import org.example.trigonometric.*
import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.beust.jcommander.ParameterException

class Args {
    @Parameter(names = ["--start"], description = "Start value", required = true)
    var start: Double = 0.0

    @Parameter(names = ["--end"], description = "End value", required = true)
    var end: Double = 0.0

    @Parameter(names = ["--step"], description = "Step size", required = true)
    var step: Double = 0.0

    @Parameter(names = ["--precision"], description = "Precision (default: 1e-10)")
    var precision: Double = 1e-10
}

fun main(args: Array<String>) {
    val arguments = Args()
    val jcommander = JCommander.Builder()
        .addObject(arguments)
        .build()

    jcommander.parse(*args)

    if (arguments.step <= 0) {
        println("Error: step must be positive")
        return
    }

    val piecewiseFunc = createLoggingPiecewiseMathFunction(arguments.precision)

    var x = arguments.start
    while (x <= arguments.end + arguments.precision) {
        if (piecewiseFunc.isDefinedAt(x)) {
            val result = piecewiseFunc.calculateAt(x)
            FunctionLogger.log("piecewise", x, result)
            println("x = $x, result = $result")
        } else {
            println("x = $x, result = undefined")
        }
        x += arguments.step
    }
}

private fun createLoggingPiecewiseMathFunction(precision: Double): PiecewiseMathFunction {
    val sin = LoggingSinToPrecision(precision)
    val cos = LoggingCosToPrecision(precision)
    val tan = LoggingTanToPrecision(precision)
    val sec = LoggingSecantToPrecision(precision)
    val csc = LoggingCosecantToPrecision(precision)

    val log2 = LoggingLogarithmToPrecision(LogBase(2.0), precision)
    val log10 = LoggingLogarithmToPrecision(LogBase(10.0), precision)
    val log3 = LoggingLogarithmToPrecision(LogBase(3.0), precision)
    val ln = LoggingNaturalLogarithmToPrecision(precision)

    return PiecewiseMathFunction(
        precision = precision,
        sec = sec,
        sin = sin,
        cos = cos,
        csc = csc,
        tan = tan,
        log2 = log2,
        log10 = log10,
        log3 = log3,
        ln = ln
    )
}