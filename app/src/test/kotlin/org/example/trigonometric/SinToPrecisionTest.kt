package org.example.trigonometric

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

class SinToPrecisionTest {


    private val safePrecision = 1e-6


    private val assertionDelta = 1e-5

    @Test
    fun `calculateAt returns 0 for input 0`() {
        val sinCalc = SinToPrecision(safePrecision)
        val result = sinCalc.calculateAt(0.0)
        assertEquals(0.0, result, assertionDelta)
    }

    @Test
    fun `calculateAt returns 1 for PI over 2`() {
        val sinCalc = SinToPrecision(safePrecision)
        val result = sinCalc.calculateAt(PI / 2)
        assertEquals(1.0, result, assertionDelta)
    }

    @Test
    fun `calculateAt returns 0 for PI`() {
        val sinCalc = SinToPrecision(safePrecision)
        val result = sinCalc.calculateAt(PI)
        assertEquals(0.0, result, assertionDelta)
    }

    @Test
    fun `calculateAt returns -1 for negative PI over 2`() {
        val sinCalc = SinToPrecision(safePrecision)
        val result = sinCalc.calculateAt(-PI / 2)
        assertEquals(-1.0, result, assertionDelta)
    }

    @Test
    fun `calculateAt handles negative input correctly`() {
        val sinCalc = SinToPrecision(safePrecision)
        val x = -PI / 6
        val expected = sin(x)
        val actual = sinCalc.calculateAt(x)
        assertEquals(expected, actual, assertionDelta)
    }

    @Test
    fun `calculateAt respects periodicity for large angles`() {
        val sinCalc = SinToPrecision(safePrecision)

        val x = (2 * PI) + (PI / 2)
        val expected = 1.0
        val actual = sinCalc.calculateAt(x)
        assertEquals(expected, actual, assertionDelta)
    }

    @ParameterizedTest
    @ValueSource(doubles = [0.0, 0.5, 1.0, 1.5, 3.0])
    fun `calculateAt matches Kotlin Math sin for various inputs`(x: Double) {
        val sinCalc = SinToPrecision(safePrecision)
        val expected = sin(x)
        val actual = sinCalc.calculateAt(x)
        assertEquals(expected, actual, assertionDelta)
    }

    @Test
    fun `higher precision yields result closer to Math sin within safe limits`() {
        val x = 1.0
        val lowPrecisionCalc = SinToPrecision(1e-2)
        val highPrecisionCalc = SinToPrecision(1e-6)

        val expected = sin(x)
        val lowResult = lowPrecisionCalc.calculateAt(x)
        val highResult = highPrecisionCalc.calculateAt(x)


        val lowError = abs(expected - lowResult)
        val highError = abs(expected - highResult)

        assert(highError <= lowError) {
            "High precision error ($highError) should be <= Low precision error ($lowError)"
        }
    }


    @Test
    fun `factorial calculates correct values for small n`() {
        Assertions.assertEquals(1, SinToPrecision.Companion.factorial(0))
        Assertions.assertEquals(1, SinToPrecision.Companion.factorial(1))
        Assertions.assertEquals(2, SinToPrecision.Companion.factorial(2))
        Assertions.assertEquals(6, SinToPrecision.Companion.factorial(3))
        Assertions.assertEquals(24, SinToPrecision.Companion.factorial(4))
        Assertions.assertEquals(120, SinToPrecision.Companion.factorial(5))
    }

    @Test
    fun `factorial overflows for n greater than 21`() {

        val fact20 = SinToPrecision.Companion.factorial(20)
        assert(fact20 > 0) { "20! should be positive" }


        val fact21 = SinToPrecision.Companion.factorial(21)

        assert(fact21 < 0 || fact21.toInt() == 0) {
            "21! overflows Long. Expected negative/garbage, got $fact21"
        }
    }

    @ParameterizedTest
    @ValueSource(doubles = [-3.2, -3.1, -3.0, -2.9, -2.8])
    fun `calculateAt matches Kotlin Math sin at high precision for values near negative PI`(x: Double) {
        val sinCalc = SinToPrecision(1e-10)
        val expected = sin(x)
        val actual = sinCalc.calculateAt(x)
        assertEquals(expected, actual, 1e-5)
    }

    @ParameterizedTest
    @ValueSource(doubles = [2.5, 2.6, 2.7, 2.8, 2.9, 3.0])
    fun `calculateAt matches Kotlin Math sin at high precision for values near positive PI`(x: Double) {
        val sinCalc = SinToPrecision(1e-10)
        val expected = sin(x)
        val actual = sinCalc.calculateAt(x)
        assertEquals(expected, actual, 1e-5)
    }
}