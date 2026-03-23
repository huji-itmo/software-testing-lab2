package org.example.logarithmic

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.math.ln

class NaturalLogarithmToPrecisionTest {

    private val precision = 1e-10
    private val assertionDelta = 1e-5

    @Test
    fun `calculateAt returns 0 for input 1`() {
        val lnFunc = NaturalLogarithmToPrecision(precision)
        assertEquals(0.0, lnFunc.calculateAt(1.0), assertionDelta)
    }

    @ParameterizedTest
    @ValueSource(doubles = [1.0, 2.0, 3.0, 10.0, 100.0, Math.E])
    fun `calculateAt matches Kotlin Math ln for various inputs greater than 1`(x: Double) {
        val lnFunc = NaturalLogarithmToPrecision(precision)
        val expected = ln(x)
        val actual = lnFunc.calculateAt(x)
        assertEquals(expected, actual, assertionDelta)
    }

    @ParameterizedTest
    @ValueSource(doubles = [0.5, 0.1, 0.01, 0.001])
    fun `calculateAt matches Kotlin Math ln for inputs between 0 and 1`(x: Double) {
        val lnFunc = NaturalLogarithmToPrecision(precision)
        val expected = ln(x)
        val actual = lnFunc.calculateAt(x)
        assertEquals(expected, actual, assertionDelta)
    }

    @Test
    fun `calculateAt returns negative infinity for x equals 0`() {
        val lnFunc = NaturalLogarithmToPrecision(precision)
        assertEquals(Double.NEGATIVE_INFINITY, lnFunc.calculateAt(0.0))
    }

    @Test
    fun `calculateAt returns NaN for negative input`() {
        val lnFunc = NaturalLogarithmToPrecision(precision)
        assertEquals(true, lnFunc.calculateAt(-1.0).isNaN())
    }

    @Test
    fun `calculateAt returns NaN for NaN input`() {
        val lnFunc = NaturalLogarithmToPrecision(precision)
        assertEquals(true, lnFunc.calculateAt(Double.NaN).isNaN())
    }
}