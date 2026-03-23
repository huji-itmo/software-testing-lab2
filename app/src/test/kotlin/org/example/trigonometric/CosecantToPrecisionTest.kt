package org.example.trigonometric

import io.mockk.every
import io.mockk.mockk
import org.example.trigonometric.interfaces.Sin
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.math.PI
import kotlin.math.sin

class CosecantToPrecisionTest {

    private val precision = 1e-6
    private val assertionDelta = 1e-5

    private val mockSin: Sin = mockk()

    @Test
    fun `calculateAt returns reciprocal of sin when defined`() {
        val x = PI / 6  // sin(π/6) = 0.5, csc(π/6) = 2.0
        val sinValue = 0.5
        val expected = 1.0 / sinValue

        every { mockSin.calculateAt(x) } returns sinValue

        val cosecant = CosecantToPrecision(precision, mockSin)
        val result = cosecant.calculateAt(x)

        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `calculateAt returns NaN when x is exactly multiple of PI`() {
        val x = PI
        val cosecant = CosecantToPrecision(precision, mockSin)

        assertFalse(cosecant.isDefinedAt(x))

        val result = cosecant.calculateAt(x)
        assertTrue(result.isNaN())
    }

    @Test
    fun `calculateAt returns NaN when x is 0`() {
        val x = 0.0
        val cosecant = CosecantToPrecision(precision, mockSin)

        assertFalse(cosecant.isDefinedAt(x))

        val result = cosecant.calculateAt(x)
        assertTrue(result.isNaN())
    }

    @Test
    fun `calculateAt returns POSITIVE_INFINITY when approaching from positive side`() {
        val x = precision / 2
        val cosecant = CosecantToPrecision(precision, mockSin)

        assertFalse(cosecant.isDefinedAt(x))

        val result = cosecant.calculateAt(x)
        assertEquals(Double.POSITIVE_INFINITY, result)
    }

    @Test
    fun `calculateAt returns NEGATIVE_INFINITY when approaching from negative side`() {
        val x = -precision / 2
        val cosecant = CosecantToPrecision(precision, mockSin)

        assertFalse(cosecant.isDefinedAt(x))

        val result = cosecant.calculateAt(x)
        assertEquals(Double.NEGATIVE_INFINITY, result)
    }

    @Test
    fun `calculateAt returns POSITIVE_INFINITY near 2PI from positive side`() {
        val x = 2 * PI + precision / 2
        val cosecant = CosecantToPrecision(precision, mockSin)

        val result = cosecant.calculateAt(x)
        assertEquals(Double.POSITIVE_INFINITY, result)
    }

    @Test
    fun `calculateAt returns NEGATIVE_INFINITY near 2PI from negative side`() {
        val x = 2 * PI - precision / 2
        val cosecant = CosecantToPrecision(precision, mockSin)

        val result = cosecant.calculateAt(x)
        assertEquals(Double.NEGATIVE_INFINITY, result)
    }

    @ParameterizedTest
    @ValueSource(doubles = [PI / 6, PI / 4, PI / 3, PI / 2, 5 * PI / 6])
    fun `calculateAt matches 1 divided by sin for various angles`(x: Double) {
        val sinValue = sin(x)
        val expected = 1.0 / sinValue

        every { mockSin.calculateAt(x) } returns sinValue

        val cosecant = CosecantToPrecision(precision, mockSin)
        val result = cosecant.calculateAt(x)

        assertTrue(cosecant.isDefinedAt(x))
        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `isDefinedAt returns true for values away from multiples of PI`() {
        val cosecant = CosecantToPrecision(precision, mockSin)

        assertTrue(cosecant.isDefinedAt(PI / 6))
        assertTrue(cosecant.isDefinedAt(PI / 2))
        assertTrue(cosecant.isDefinedAt(5 * PI / 6))
    }

    @Test
    fun `isDefinedAt returns false for values close to multiples of PI`() {
        val cosecant = CosecantToPrecision(precision, mockSin)

        assertFalse(cosecant.isDefinedAt(0.0))
        assertFalse(cosecant.isDefinedAt(PI))
        assertFalse(cosecant.isDefinedAt(2 * PI))
        assertFalse(cosecant.isDefinedAt(precision / 2))
        assertFalse(cosecant.isDefinedAt(-precision / 2))
    }

    @Test
    fun `calculateAt handles negative angles correctly`() {
        val x = -PI / 6  // sin(-π/6) = -0.5, csc(-π/6) = -2.0
        val sinValue = -0.5
        val expected = 1.0 / sinValue

        every { mockSin.calculateAt(x) } returns sinValue

        val cosecant = CosecantToPrecision(precision, mockSin)
        val result = cosecant.calculateAt(x)

        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `calculateAt throws no exception for large angles`() {
        val x = 100 * PI + PI / 6
        val sinValue = 0.5
        val expected = 1.0 / sinValue

        every { mockSin.calculateAt(x) } returns sinValue

        val cosecant = CosecantToPrecision(precision, mockSin)
        val result = cosecant.calculateAt(x)

        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `calculateAt handles sin returning zero gracefully`() {
        val x = PI / 6
        every { mockSin.calculateAt(x) } returns 0.0

        val cosecant = CosecantToPrecision(precision, mockSin)

        // This should return Infinity since 1/0 = Infinity
        val result = cosecant.calculateAt(x)
        assertTrue(result.isInfinite())
    }
}