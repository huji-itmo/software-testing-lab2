package org.example.trigonometric

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.trigonometric.interfaces.Cos
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.math.PI
import kotlin.math.cos

class SecantToPrecisionTest {

    private val precision = 1e-6
    private val assertionDelta = 1e-5

    private val mockCos: Cos = mockk(relaxed = true)

    @Test
    fun `calculateAt returns 1 for input 0`() {
        val x = 0.0
        val cosValue = 1.0
        val expected = 1.0

        every { mockCos.calculateAt(x) } returns cosValue

        val secant = SecantToPrecision(precision, mockCos)
        val result = secant.calculateAt(x)

        assertTrue(secant.isDefinedAt(x))
        assertEquals(expected, result, assertionDelta)
        verify { mockCos.calculateAt(x) }
    }

    @Test
    fun `calculateAt returns 2 for PI over 3`() {
        val x = PI / 3
        val cosValue = 0.5
        val expected = 2.0

        every { mockCos.calculateAt(x) } returns cosValue

        val secant = SecantToPrecision(precision, mockCos)
        val result = secant.calculateAt(x)

        assertTrue(secant.isDefinedAt(x))
        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `calculateAt returns -1 for PI`() {
        val x = PI
        val cosValue = -1.0
        val expected = -1.0

        every { mockCos.calculateAt(x) } returns cosValue

        val secant = SecantToPrecision(precision, mockCos)
        val result = secant.calculateAt(x)

        assertTrue(secant.isDefinedAt(x))
        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `calculateAt returns -2 for 2PI over 3`() {
        val x = 2 * PI / 3
        val cosValue = -0.5
        val expected = -2.0

        every { mockCos.calculateAt(x) } returns cosValue

        val secant = SecantToPrecision(precision, mockCos)
        val result = secant.calculateAt(x)

        assertTrue(secant.isDefinedAt(x))
        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `calculateAt returns 1 for 2PI`() {
        val x = 2 * PI
        val cosValue = 1.0
        val expected = 1.0

        every { mockCos.calculateAt(x) } returns cosValue

        val secant = SecantToPrecision(precision, mockCos)
        val result = secant.calculateAt(x)

        assertTrue(secant.isDefinedAt(x))
        assertEquals(expected, result, assertionDelta)
    }

    @ParameterizedTest
    @ValueSource(doubles = [0.0, PI / 6, PI / 4, PI / 3, PI, 5 * PI / 4, 4 * PI / 3, 2 * PI])
    fun `calculateAt matches 1 divided by cos for various defined angles`(x: Double) {
        val cosValue = cos(x)
        val expected = 1.0 / cosValue

        every { mockCos.calculateAt(x) } returns cosValue

        val secant = SecantToPrecision(precision, mockCos)
        val result = secant.calculateAt(x)

        assertTrue(secant.isDefinedAt(x))
        assertEquals(expected, result, assertionDelta)
    }


    @Test
    fun `calculateAt returns NaN when x is exactly PI over 2`() {
        val x = PI / 2
        val secant = SecantToPrecision(precision, mockCos)

        assertFalse(secant.isDefinedAt(x))

        val result = secant.calculateAt(x)
        assertTrue(result.isNaN())
    }

    @Test
    fun `calculateAt returns NaN when x is exactly 3PI over 2`() {
        val x = 3 * PI / 2
        val secant = SecantToPrecision(precision, mockCos)

        assertFalse(secant.isDefinedAt(x))

        val result = secant.calculateAt(x)
        assertTrue(result.isNaN())
    }

    @Test
    fun `calculateAt returns NaN when x is exactly negative PI over 2`() {
        val x = -PI / 2
        val secant = SecantToPrecision(precision, mockCos)

        assertFalse(secant.isDefinedAt(x))

        val result = secant.calculateAt(x)
        assertTrue(result.isNaN())
    }

    @Test
    fun `calculateAt returns NaN when x is exactly 5PI over 2`() {
        val x = 5 * PI / 2
        val secant = SecantToPrecision(precision, mockCos)

        assertFalse(secant.isDefinedAt(x))

        val result = secant.calculateAt(x)
        assertTrue(result.isNaN())
    }


    @Test
    fun `calculateAt returns POSITIVE_INFINITY approaching PI over 2 from left`() {
        val x = PI / 2 - precision / 2
        val secant = SecantToPrecision(precision, mockCos)

        assertFalse(secant.isDefinedAt(x))

        val result = secant.calculateAt(x)
        assertEquals(Double.POSITIVE_INFINITY, result)
    }

    @Test
    fun `calculateAt returns POSITIVE_INFINITY approaching 3PI over 2 from right`() {
        val x = 3 * PI / 2 + precision / 2
        val secant = SecantToPrecision(precision, mockCos)

        assertFalse(secant.isDefinedAt(x))

        val result = secant.calculateAt(x)
        assertEquals(Double.POSITIVE_INFINITY, result)
    }

    @Test
    fun `calculateAt returns POSITIVE_INFINITY approaching negative PI over 2 from right`() {
        val x = -PI / 2 + precision / 2
        val secant = SecantToPrecision(precision, mockCos)

        assertFalse(secant.isDefinedAt(x))

        val result = secant.calculateAt(x)
        assertEquals(Double.POSITIVE_INFINITY, result)
    }


    @Test
    fun `calculateAt returns NEGATIVE_INFINITY approaching PI over 2 from right`() {
        val x = PI / 2 + precision / 2
        val secant = SecantToPrecision(precision, mockCos)

        assertFalse(secant.isDefinedAt(x))

        val result = secant.calculateAt(x)
        assertEquals(Double.NEGATIVE_INFINITY, result)
    }

    @Test
    fun `calculateAt returns NEGATIVE_INFINITY approaching 3PI over 2 from left`() {
        val x = 3 * PI / 2 - precision / 2
        val secant = SecantToPrecision(precision, mockCos)

        assertFalse(secant.isDefinedAt(x))

        val result = secant.calculateAt(x)
        assertEquals(Double.NEGATIVE_INFINITY, result)
    }

    @Test
    fun `calculateAt returns NEGATIVE_INFINITY approaching negative PI over 2 from left`() {
        val x = -PI / 2 - precision / 2
        val secant = SecantToPrecision(precision, mockCos)

        assertFalse(secant.isDefinedAt(x))

        val result = secant.calculateAt(x)
        assertEquals(Double.NEGATIVE_INFINITY, result)
    }


    @Test
    fun `isDefinedAt returns true for values away from odd multiples of PI over 2`() {
        val secant = SecantToPrecision(precision, mockCos)

        assertTrue(secant.isDefinedAt(0.0))
        assertTrue(secant.isDefinedAt(PI / 3))
        assertTrue(secant.isDefinedAt(PI / 4))
        assertTrue(secant.isDefinedAt(PI))
        assertTrue(secant.isDefinedAt(2 * PI))
    }

    @Test
    fun `isDefinedAt returns false for values close to odd multiples of PI over 2`() {
        val secant = SecantToPrecision(precision, mockCos)

        assertFalse(secant.isDefinedAt(PI / 2))
        assertFalse(secant.isDefinedAt(3 * PI / 2))
        assertFalse(secant.isDefinedAt(-PI / 2))
        assertFalse(secant.isDefinedAt(5 * PI / 2))
        assertFalse(secant.isDefinedAt(PI / 2 + precision / 2))
        assertFalse(secant.isDefinedAt(PI / 2 - precision / 2))
    }

    @Test
    fun `isDefinedAt handles large angles correctly`() {
        val secant = SecantToPrecision(precision, mockCos)


        assertFalse(secant.isDefinedAt(10 * PI + PI / 2))


        assertTrue(secant.isDefinedAt(10 * PI))
    }


    @Test
    fun `calculateAt verifies cos is called with original x value`() {
        val x = PI / 4
        every { mockCos.calculateAt(any()) } returns 0.707

        val secant = SecantToPrecision(precision, mockCos)
        secant.calculateAt(x)

        verify { mockCos.calculateAt(x) }
    }

    @Test
    fun `calculateAt does not call cos when undefined`() {
        val x = PI / 2
        val secant = SecantToPrecision(precision, mockCos)

        secant.calculateAt(x)

        verify(exactly = 0) { mockCos.calculateAt(any()) }
    }


    @Test
    fun `calculateAt with real CosToPrecision implementation`() {
        val secant = SecantToPrecision(1e-6)
        val x = PI / 3
        val expected = 1.0 / cos(x)
        val result = secant.calculateAt(x)

        assertTrue(secant.isDefinedAt(x))
        assertEquals(expected, result, 1e-5)
    }

    @Test
    fun `calculateAt with real CosToPrecision for multiple angles`() {
        val secant = SecantToPrecision(1e-6)
        val testAngles = listOf(0.0, PI / 6, PI / 4, PI / 3, PI, 5 * PI / 4, 2 * PI)

        testAngles.forEach { x ->
            val expected = 1.0 / cos(x)
            val result = secant.calculateAt(x)
            assertTrue(secant.isDefinedAt(x))
            assertEquals(expected, result, 1e-5, "Failed for x = $x")
        }
    }

    @Test
    fun `calculateAt returns NaN for undefined points with real implementation`() {
        val secant = SecantToPrecision(1e-6)
        val undefinedAngles = listOf(PI / 2, 3 * PI / 2, -PI / 2, 5 * PI / 2)

        undefinedAngles.forEach { x ->
            val result = secant.calculateAt(x)
            assertFalse(secant.isDefinedAt(x))
            assertTrue(result.isNaN(), "Expected NaN for x = $x")
        }
    }


    @Test
    fun `calculateAt handles very small positive angle`() {
        val x = 1e-10
        val cosValue = cos(x)
        val expected = 1.0 / cosValue

        every { mockCos.calculateAt(x) } returns cosValue

        val secant = SecantToPrecision(precision, mockCos)
        val result = secant.calculateAt(x)

        assertTrue(secant.isDefinedAt(x))
        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `calculateAt handles very small negative angle`() {
        val x = -1e-10
        val cosValue = cos(x)
        val expected = 1.0 / cosValue

        every { mockCos.calculateAt(x) } returns cosValue

        val secant = SecantToPrecision(precision, mockCos)
        val result = secant.calculateAt(x)

        assertTrue(secant.isDefinedAt(x))
        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `calculateAt handles large positive angles`() {
        val x = 100 * PI + PI / 3
        val cosValue = cos(x)
        val expected = 1.0 / cosValue

        every { mockCos.calculateAt(x) } returns cosValue

        val secant = SecantToPrecision(precision, mockCos)
        val result = secant.calculateAt(x)

        assertTrue(secant.isDefinedAt(x))
        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `calculateAt handles large negative angles`() {
        val x = -100 * PI + PI / 3
        val cosValue = cos(x)
        val expected = 1.0 / cosValue

        every { mockCos.calculateAt(x) } returns cosValue

        val secant = SecantToPrecision(precision, mockCos)
        val result = secant.calculateAt(x)

        assertTrue(secant.isDefinedAt(x))
        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `calculateAt preserves sign for negative secant values`() {
        val x = 2 * PI / 3
        val cosValue = -0.5
        val expected = -2.0

        every { mockCos.calculateAt(x) } returns cosValue

        val secant = SecantToPrecision(precision, mockCos)
        val result = secant.calculateAt(x)

        assertTrue(secant.isDefinedAt(x))
        assertEquals(expected, result, assertionDelta)
        assertTrue(result < 0, "Result should be negative for x = $x")
    }

    @Test
    fun `default constructor uses CosToPrecision`() {
        val secant = SecantToPrecision(1e-6)
        val x = PI / 4
        val expected = 1.0 / cos(x)
        val result = secant.calculateAt(x)

        assertTrue(secant.isDefinedAt(x))
        assertEquals(expected, result, 1e-5)
    }

    @Test
    fun `calculateAt handles cos returning very small value`() {
        val x = PI / 3
        val cosValue = 1e-10
        val expected = 1.0 / cosValue

        every { mockCos.calculateAt(x) } returns cosValue

        val secant = SecantToPrecision(precision, mockCos)
        val result = secant.calculateAt(x)

        assertTrue(secant.isDefinedAt(x))
        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `normalizeToPeriod delegates to cos normalizeToPeriod`() {
        val x = 5 * PI
        val expectedNormalized = x % (2 * PI)

        val mockCosWithNormalize: Cos = mockk()
        every { mockCosWithNormalize.normalizeToPeriod(x) } returns expectedNormalized

        val secant = SecantToPrecision(precision, mockCosWithNormalize)
        val result = secant.normalizeToPeriod(x)

        assertEquals(expectedNormalized, result)
        verify { mockCosWithNormalize.normalizeToPeriod(x) }
    }
}