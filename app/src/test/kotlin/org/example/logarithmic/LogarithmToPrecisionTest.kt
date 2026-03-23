package org.example.logarithmic

import org.example.logarithmic.interfaces.LogarithmicFunction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.ln

class LogarithmToPrecisionTest {

    private class StubLogFunction(private val returnValue: Double) : LogarithmicFunction {
        override fun isDefinedAt(x: Double) = true
        override fun calculateAt(x: Double) = returnValue
    }

    private class MathBasedStubLogFunction : LogarithmicFunction {
        override fun isDefinedAt(x: Double) = x > 0.0
        override fun calculateAt(x: Double) = ln(x)
    }

    @Test
    fun `isDefinedAt returns true when x is greater than precision`() {
        val base = LogBase.of(10.0)
        val precision = 1e-10
        val logFunc = LogarithmToPrecision(base, precision)

        assertTrue(logFunc.isDefinedAt(1.0))
        assertTrue(logFunc.isDefinedAt(precision + 0.0001))
    }

    @Test
    fun `isDefinedAt returns false when x is less than or equal to precision`() {
        val base = LogBase.of(10.0)
        val precision = 1e-10
        val logFunc = LogarithmToPrecision(base, precision)

        assertFalse(logFunc.isDefinedAt(precision))
        assertFalse(logFunc.isDefinedAt(0.0))
        assertFalse(logFunc.isDefinedAt(-5.0))
    }

    @Test
    fun `calculateAt returns NEGATIVE_INFINITY when x is less or equal precision`() {
        val base = LogBase.of(10.0)
        val precision = 1e-5
        val logFunc = LogarithmToPrecision(base, precision)

        assertEquals(Double.NEGATIVE_INFINITY, logFunc.calculateAt(precision))
        assertEquals(Double.NEGATIVE_INFINITY, logFunc.calculateAt(0.0))
    }

    @Test
    fun `calculateAt returns NaN when input is NaN`() {
        val base = LogBase.of(10.0)
        val precision = 1e-5
        val logFunc = LogarithmToPrecision(base, precision)

        assertTrue(logFunc.calculateAt(Double.NaN).isNaN())
    }

    @Test
    fun `calculateAt performs change of base formula correctly using stub`() {
        val base = LogBase.of(2.0)
        val precision = 1e-10

        val stubLn = object : LogarithmicFunction {
            override fun isDefinedAt(x: Double) = true
            override fun calculateAt(x: Double): Double {
                return if (x == 2.0) 2.0 else 10.0
            }
        }

        val logFunc = LogarithmToPrecision(base, precision, stubLn)

        assertEquals(5.0, logFunc.calculateAt(5.0), 1e-10)
    }

    @Test
    fun `calculateAt computes correct mathematical value with MathBasedStub`() {
        val base = LogBase.of(2.0)
        val precision = 1e-10
        val lnStub = MathBasedStubLogFunction()

        val logFunc = LogarithmToPrecision(base, precision, lnStub)

        assertEquals(3.0, logFunc.calculateAt(8.0), 1e-10)
    }

    @Test
    fun `calculateAt computes correct value for base 10`() {
        val base = LogBase.of(10.0)
        val precision = 1e-10
        val lnStub = MathBasedStubLogFunction()

        val logFunc = LogarithmToPrecision(base, precision, lnStub)

        assertEquals(2.0, logFunc.calculateAt(100.0), 1e-10)
    }

    @Test
    fun `constructor precalculates lnOfBase correctly`() {
        val base = LogBase.of(Math.E)
        val precision = 1e-10
        val lnStub = MathBasedStubLogFunction()

        val logFunc = LogarithmToPrecision(base, precision, lnStub)

        assertEquals(1.0, logFunc.calculateAt(Math.E), 1e-10)
    }
}
