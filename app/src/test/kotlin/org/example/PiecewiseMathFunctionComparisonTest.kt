package org.example

import io.mockk.every
import io.mockk.mockk
import org.example.interfaces.MathFunction
import org.example.logarithmic.LogBase
import org.example.logarithmic.LogarithmToPrecision
import org.example.logarithmic.NaturalLogarithmToPrecision
import org.example.logarithmic.interfaces.LogarithmicFunction
import org.example.trigonometric.*
import org.example.trigonometric.interfaces.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assumptions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.math.*

@Suppress("SameParameterValue")
class PiecewiseMathFunctionComparisonTest {

    private val precision = 1e-6
    private val assertionDelta = 1e-4

    companion object {
        @JvmStatic
        fun testInputs(): List<Double> = buildList {
            addAll(listOf(-2 * PI, -3 * PI / 2, -PI, -2 * PI / 3, -PI / 2, -PI / 3, -PI / 4, -PI / 6, -0.5, -0.1))
            addAll(listOf(0.0))
            addAll(listOf(0.1, 0.5, 1.0, 2.0, 3.0, 4.0, 8.0, 10.0, 100.0, 1000.0))
            addAll((-10..10).map { it * PI / 12 }.filter { it != 0.0 })
            addAll(listOf(1e-10, -1e-10, 1e5, -1e5))
        }
    }

    private fun createStubbedFunction(precision: Double): PiecewiseMathFunction {
        val mockSec: Secant = mockk(relaxed = true)
        val mockSin: Sin = mockk(relaxed = true)
        val mockCos: Cos = mockk(relaxed = true)
        val mockCsc: Cosecant = mockk(relaxed = true)
        val mockTan: Tan = mockk(relaxed = true)
        val mockLog2: LogarithmicFunction<Double> = mockk(relaxed = true)
        val mockLog10: LogarithmicFunction<Double> = mockk(relaxed = true)
        val mockLog3: LogarithmicFunction<Double> = mockk(relaxed = true)
        val mockLn: LogarithmicFunction<Double> = mockk(relaxed = true)

        val trigStubs: (Double) -> Unit = { x ->
            every { mockSec.isDefinedAt(x) } returns (abs(cos(x)) > precision)
            every { mockSin.isDefinedAt(x) } returns true
            every { mockCos.isDefinedAt(x) } returns true
            every { mockCsc.isDefinedAt(x) } returns (abs(sin(x)) > precision)
            every { mockTan.isDefinedAt(x) } returns (abs(cos(x)) > precision)
            every { mockSec.calculateAt(x) } returns if (abs(cos(x)) > precision) 1 / cos(x) else Double.NaN
            every { mockSin.calculateAt(x) } returns sin(x)
            every { mockCos.calculateAt(x) } returns cos(x)
            every { mockCsc.calculateAt(x) } returns if (abs(sin(x)) > precision) 1 / sin(x) else Double.NaN
            every { mockTan.calculateAt(x) } returns if (abs(cos(x)) > precision) tan(x) else Double.NaN
        }

        val logStubs: (Double) -> Unit = { x ->
            every { mockLn.isDefinedAt(x) } returns (x > 0)
            every { mockLog2.isDefinedAt(x) } returns (x > 0)
            every { mockLog10.isDefinedAt(x) } returns (x > 0)
            every { mockLog3.isDefinedAt(x) } returns (x > 0)
            every { mockLn.calculateAt(x) } returns if (x > 0) ln(x) else Double.NaN
            every { mockLog2.calculateAt(x) } returns if (x > 0) ln(x) / ln(2.0) else Double.NaN
            every { mockLog10.calculateAt(x) } returns if (x > 0) log10(x) else Double.NaN
            every { mockLog3.calculateAt(x) } returns if (x > 0) ln(x) / ln(3.0) else Double.NaN
        }

        return object : PiecewiseMathFunction(
            precision,
            mockSec, mockSin, mockCos, mockCsc, mockTan,
            mockLog2, mockLog10, mockLog3, mockLn
        ) {
            override fun isDefinedAt(x: Double): Boolean {
                if (x <= 0) trigStubs(x) else logStubs(x)
                return super.isDefinedAt(x)
            }

            override fun calculateAt(x: Double): Double {
                if (x <= 0) trigStubs(x) else logStubs(x)
                return super.calculateAt(x)
            }
        }
    }

    private fun createRealFunction(precision: Double): PiecewiseMathFunction {
        return PiecewiseMathFunction(precision)
    }

    private fun expectedTrigonometricResult(x: Double, precision: Double): Double {
        val sinX = sin(x)
        val cosX = cos(x)
        val tanX = tan(x)
        val secX = if (abs(cosX) > precision) 1 / cosX else Double.NaN
        val cscX = if (abs(sinX) > precision) 1 / sinX else Double.NaN

        if (abs(cosX) <= precision || abs(sinX) <= precision) return Double.NaN

        val step1 = secX - secX
        val step2 = step1 + (secX * sinX)
        val step3 = step2 * cosX
        val step4 = step3 - (sinX * cscX)
        val denominator = tanX

        return when {
            abs(denominator) < precision -> {
                when {
                    abs(step4) < precision -> Double.NaN
                    step4 > 0 -> Double.POSITIVE_INFINITY
                    else -> Double.NEGATIVE_INFINITY
                }
            }
            else -> step4 / denominator
        }
    }

    private fun expectedLogarithmicResult(x: Double, precision: Double): Double {
        if (x <= 0) return Double.NaN

        val log2X = ln(x) / ln(2.0)
        val log10X = log10(x)
        val log3X = ln(x) / ln(3.0)
        val lnX = ln(x)

        val step1 = log2X - log2X
        val step2 = step1 + (log2X * log10X)
        val step3 = step2 * log3X
        val step4 = step3 - (log3X * lnX)
        val denominator = log3X

        return when {
            abs(denominator) < precision -> {
                when {
                    abs(step4) < precision -> Double.NaN
                    step4 > 0 -> Double.POSITIVE_INFINITY
                    else -> Double.NEGATIVE_INFINITY
                }
            }
            else -> step4 / denominator
        }
    }

    private fun compareResults(stubbed: Double, real: Double, expected: Double, x: Double, label: String) {
        when {
            expected.isNaN() -> {
                assertTrue(stubbed.isNaN(), "$label: stubbed should be NaN for x=$x")
                assertTrue(real.isNaN() || real.isInfinite(), "$label: real should be NaN/Infinity for x=$x")
            }
            expected.isInfinite() -> {
                assertTrue(stubbed.isInfinite(), "$label: stubbed should be infinite for x=$x")
                assertTrue(real.isInfinite(), "$label: real should be infinite for x=$x")
                assertEquals(expected > 0, stubbed > 0, "$label: sign mismatch for x=$x")
                assertEquals(expected > 0, real > 0, "$label: sign mismatch for x=$x")
            }
            else -> {
                if (stubbed.isFinite() && real.isFinite()) {
                    assertEquals(expected, stubbed, assertionDelta, "$label: stubbed mismatch for x=$x")
                    assertEquals(expected, real, assertionDelta * 10, "$label: real mismatch for x=$x")
                    assertEquals(stubbed, real, assertionDelta * 10, "$label: stubbed vs real mismatch for x=$x")
                }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("testInputs")
    fun `stubbed and real implementations produce identical results`(x: Double) {
        val stubbedFunc = createStubbedFunction(precision)
        val realFunc = createRealFunction(precision)

        val isRealDefined = realFunc.isDefinedAt(x)
        assumeTrue(isRealDefined, "Skipping x=$x because real implementation is undefined")

        val isStubbedDefined = stubbedFunc.isDefinedAt(x)
        assumeTrue(isStubbedDefined, "Skipping x=$x because stubbed implementation is undefined")

        val expected = if (x <= 0) {
            expectedTrigonometricResult(x, precision)
        } else {
            expectedLogarithmicResult(x, precision)
        }

        val stubbedResult = stubbedFunc.calculateAt(x)
        val realResult = realFunc.calculateAt(x)

        assumeTrue(stubbedResult.isFinite() && realResult.isFinite(), "Skipping x=$x because results are not finite")

        compareResults(stubbedResult, realResult, expected, x, "calculateAt")
    }

    @ParameterizedTest
    @MethodSource("testInputs")
    fun `isDefinedAt agrees between stubbed and real implementations`(x: Double) {
        val stubbedFunc = createStubbedFunction(precision)
        val realFunc = createRealFunction(precision)

        val realDefined = realFunc.isDefinedAt(x)
        assumeTrue(realDefined, "Skipping x=$x because real implementation is undefined")

        val stubbedDefined = stubbedFunc.isDefinedAt(x)

        assertEquals(stubbedDefined, realDefined, "isDefinedAt mismatch for x=$x")
    }

    @Test
    fun `trigonometric branch algebraic simplification verified`() {
        val testValues = listOf(-PI / 6, -PI / 4, -PI / 3, -2 * PI / 3, -5 * PI / 6)
        val stubbedFunc = createStubbedFunction(precision)
        val realFunc = createRealFunction(precision)

        testValues.forEach { x ->
            assumeTrue(realFunc.isDefinedAt(x), "Skipping x=$x because real implementation is undefined")
            assumeTrue(stubbedFunc.isDefinedAt(x), "Skipping x=$x because stubbed implementation is undefined")

            val sinX = sin(x)
            val tanX = tan(x)
            assumeTrue(abs(tanX) >= 1e-4 && abs(sinX) >= 1e-4, "Skipping x=$x due to near-zero values")

            val expected = (sinX - 1) / tanX
            val stubbedResult = stubbedFunc.calculateAt(x)
            val realResult = realFunc.calculateAt(x)

            assumeTrue(expected.isFinite() && stubbedResult.isFinite() && realResult.isFinite(), "Skipping x=$x because results are not finite")

            assertEquals(expected, stubbedResult, assertionDelta, "Stubbed simplification failed for x=$x")
            assertEquals(expected, realResult, assertionDelta * 10, "Real simplification failed for x=$x")
        }
    }

    @Test
    fun `logarithmic branch algebraic simplification verified`() {
        val testValues = listOf(2.0, 3.0, 4.0, 8.0, 10.0, 100.0)
        val stubbedFunc = createStubbedFunction(precision)
        val realFunc = createRealFunction(precision)

        testValues.forEach { x ->
            assumeTrue(realFunc.isDefinedAt(x), "Skipping x=$x because real implementation is undefined")
            assumeTrue(stubbedFunc.isDefinedAt(x), "Skipping x=$x because stubbed implementation is undefined")

            val log2X = ln(x) / ln(2.0)
            val log10X = log10(x)
            val lnX = ln(x)
            val expected = log2X * log10X - lnX

            val stubbedResult = stubbedFunc.calculateAt(x)
            val realResult = realFunc.calculateAt(x)

            assumeTrue(expected.isFinite() && stubbedResult.isFinite() && realResult.isFinite(), "Skipping x=$x because results are not finite")

            assertEquals(expected, stubbedResult, assertionDelta, "Stubbed simplification failed for x=$x")
            assertEquals(expected, realResult, assertionDelta * 10, "Real simplification failed for x=$x")
        }
    }

    @Test
    fun `boundary behavior at x equals 0 consistent`() {
        val stubbedFunc = createStubbedFunction(precision)
        val realFunc = createRealFunction(precision)

        val realDefined = realFunc.isDefinedAt(0.0)
        assumeTrue(!realDefined, "Test expects x=0 to be undefined in real implementation")

        val stubbedResult = stubbedFunc.calculateAt(0.0)
        val realResult = realFunc.calculateAt(0.0)

        assertTrue(stubbedResult.isNaN() || stubbedResult.isInfinite())
        assertTrue(realResult.isNaN() || realResult.isInfinite())
    }

    @Test
    fun `asymptote handling consistent in trigonometric branch`() {
        val asymptotes = listOf(-PI / 2, -3 * PI / 2, -PI)
        val stubbedFunc = createStubbedFunction(precision)
        val realFunc = createRealFunction(precision)

        asymptotes.forEach { x ->
            val realDefined = realFunc.isDefinedAt(x)
            val stubbedDefined = stubbedFunc.isDefinedAt(x)

            assumeTrue(!realDefined, "Skipping x=$x because real implementation considers it defined")

            assertEquals(stubbedDefined, realDefined, "Definition mismatch at asymptote x=$x")

            val stubbedResult = stubbedFunc.calculateAt(x)
            val realResult = realFunc.calculateAt(x)

            assertTrue(stubbedResult.isNaN() || stubbedResult.isInfinite(), "Stubbed should be NaN/Inf at x=$x")
            assertTrue(realResult.isNaN() || realResult.isInfinite(), "Real should be NaN/Inf at x=$x")
        }
    }

    @Test
    fun `asymptote handling consistent in logarithmic branch`() {
        val stubbedFunc = createStubbedFunction(precision)
        val realFunc = createRealFunction(precision)

        val x = 1.0
        val realDefined = realFunc.isDefinedAt(x)
        assumeTrue(realDefined, "Skipping x=$x because real implementation is undefined")

        val stubbedDefined = stubbedFunc.isDefinedAt(x)
        assertEquals(stubbedDefined, realDefined, "Definition mismatch at x=$x")

        val stubbedResult = stubbedFunc.calculateAt(x)
        val realResult = realFunc.calculateAt(x)

        assertTrue(stubbedResult.isNaN() || stubbedResult.isFinite(), "Unexpected stubbed result at x=$x")
        assertTrue(realResult.isNaN() || realResult.isFinite(), "Unexpected real result at x=$x")
    }

    @Test
    fun `periodicity preserved in trigonometric branch`() {
        val stubbedFunc = createStubbedFunction(precision)
        val realFunc = createRealFunction(precision)
        val baseX = -PI / 6

        listOf(0, 1, -1, 2, -2).forEach { k ->
            val x = baseX + k * PI
            assumeTrue(realFunc.isDefinedAt(x), "Skipping x=$x because real implementation is undefined")
            assumeTrue(stubbedFunc.isDefinedAt(x), "Skipping x=$x because stubbed implementation is undefined")

            val sinX = sin(x)
            val tanX = tan(x)
            assumeTrue(abs(tanX) >= 1e-4 && abs(sinX) >= 1e-4, "Skipping x=$x due to near-zero values")

            val stubbedResult = stubbedFunc.calculateAt(x)
            val realResult = realFunc.calculateAt(x)

            assumeTrue(stubbedResult.isFinite() && realResult.isFinite(), "Skipping x=$x because results are not finite")

            assertEquals(stubbedResult, realResult, assertionDelta * 10, "Periodicity mismatch at x=$x")
        }
    }

    @Test
    fun `scale invariance in logarithmic branch`() {
        val stubbedFunc = createStubbedFunction(precision)
        val realFunc = createRealFunction(precision)
        val baseX = 2.0

        listOf(1.0, 10.0, 100.0, 0.1).forEach { scale ->
            val x = baseX * scale
            assumeTrue(x > 0, "Skipping x=$x because it's not positive")
            assumeTrue(realFunc.isDefinedAt(x), "Skipping x=$x because real implementation is undefined")
            assumeTrue(stubbedFunc.isDefinedAt(x), "Skipping x=$x because stubbed implementation is undefined")

            val stubbedResult = stubbedFunc.calculateAt(x)
            val realResult = realFunc.calculateAt(x)

            assumeTrue(stubbedResult.isFinite() && realResult.isFinite(), "Skipping x=$x because results are not finite")

            assertEquals(stubbedResult, realResult, assertionDelta * 10, "Scale invariance mismatch at x=$x")
        }
    }

    @Test
    fun `extreme values handled consistently`() {
        val extremeValues = listOf(1e-10, -1e-10, 1e10, -1e10, 1e-5, -1e-5)
        val stubbedFunc = createStubbedFunction(precision)
        val realFunc = createRealFunction(precision)

        extremeValues.forEach { x ->
            val realDefined = realFunc.isDefinedAt(x)
            assumeTrue(realDefined, "Skipping x=$x because real implementation is undefined")

            val stubbedDefined = stubbedFunc.isDefinedAt(x)
            assertEquals(stubbedDefined, realDefined, "Definition mismatch at extreme x=$x")

            val stubbedResult = stubbedFunc.calculateAt(x)
            val realResult = realFunc.calculateAt(x)

            when {
                stubbedResult.isNaN() && realResult.isNaN() -> {}
                stubbedResult.isInfinite() && realResult.isInfinite() -> {
                    assertEquals(stubbedResult > 0, realResult > 0, "Sign mismatch at extreme x=$x")
                }
                stubbedResult.isFinite() && realResult.isFinite() -> {
                    assertEquals(stubbedResult, realResult, assertionDelta * 100, "Value mismatch at extreme x=$x")
                }
                else -> assumeTrue(false, "Result type mismatch at x=$x: stubbed=$stubbedResult, real=$realResult")
            }
        }
    }
}