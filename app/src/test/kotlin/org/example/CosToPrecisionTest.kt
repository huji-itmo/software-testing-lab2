package org.example.trigonometric

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.trigonometric.interfaces.Sin
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos

class CosToPrecisionTest {

    private val precision = 1e-6
    private val assertionDelta = 1e-5

    private val mockSin: Sin = mockk()

    @Test
    fun `calculateAt returns 1 for input 0`() {
        val x = 0.0
        val expectedSinInput = PI / 2 - x  // π/2
        val sinValue = 1.0  // sin(π/2) = 1
        val expected = 1.0  // cos(0) = 1

        every { mockSin.calculateAt(expectedSinInput) } returns sinValue

        val cosCalc = CosToPrecision(precision, mockSin)
        val result = cosCalc.calculateAt(x)

        assertEquals(expected, result, assertionDelta)
        verify { mockSin.calculateAt(expectedSinInput) }
    }

    @Test
    fun `calculateAt returns 0 for PI over 2`() {
        val x = PI / 2
        val expectedSinInput = PI / 2 - x  // 0
        val sinValue = 0.0  // sin(0) = 0
        val expected = 0.0  // cos(π/2) = 0

        every { mockSin.calculateAt(expectedSinInput) } returns sinValue

        val cosCalc = CosToPrecision(precision, mockSin)
        val result = cosCalc.calculateAt(x)

        assertEquals(expected, result, assertionDelta)
        verify { mockSin.calculateAt(expectedSinInput) }
    }

    @Test
    fun `calculateAt returns -1 for PI`() {
        val x = PI
        val expectedSinInput = PI / 2 - x  // -π/2
        val sinValue = -1.0  // sin(-π/2) = -1
        val expected = -1.0  // cos(π) = -1

        every { mockSin.calculateAt(expectedSinInput) } returns sinValue

        val cosCalc = CosToPrecision(precision, mockSin)
        val result = cosCalc.calculateAt(x)

        assertEquals(expected, result, assertionDelta)
        verify { mockSin.calculateAt(expectedSinInput) }
    }

    @Test
    fun `calculateAt returns 0 for 3PI over 2`() {
        val x = 3 * PI / 2
        val expectedSinInput = PI / 2 - x  // -π
        val sinValue = 0.0  // sin(-π) = 0
        val expected = 0.0  // cos(3π/2) = 0

        every { mockSin.calculateAt(expectedSinInput) } returns sinValue

        val cosCalc = CosToPrecision(precision, mockSin)
        val result = cosCalc.calculateAt(x)

        assertEquals(expected, result, assertionDelta)
        verify { mockSin.calculateAt(expectedSinInput) }
    }

    @Test
    fun `calculateAt returns 1 for 2PI`() {
        val x = 2 * PI
        val expectedSinInput = PI / 2 - x  // -3π/2
        val sinValue = 1.0  // sin(-3π/2) = 1
        val expected = 1.0  // cos(2π) = 1

        every { mockSin.calculateAt(expectedSinInput) } returns sinValue

        val cosCalc = CosToPrecision(precision, mockSin)
        val result = cosCalc.calculateAt(x)

        assertEquals(expected, result, assertionDelta)
        verify { mockSin.calculateAt(expectedSinInput) }
    }

    @Test
    fun `calculateAt handles negative input correctly`() {
        val x = -PI / 3
        val expectedSinInput = PI / 2 - x  // π/2 - (-π/3) = 5π/6
        val sinValue = 0.5  // sin(5π/6) = 0.5
        val expected = 0.5  // cos(-π/3) = 0.5

        every { mockSin.calculateAt(expectedSinInput) } returns sinValue

        val cosCalc = CosToPrecision(precision, mockSin)
        val result = cosCalc.calculateAt(x)

        assertEquals(expected, result, assertionDelta)
        verify { mockSin.calculateAt(expectedSinInput) }
    }

    @ParameterizedTest
    @ValueSource(doubles = [0.0, PI / 6, PI / 4, PI / 3, PI / 2, 2 * PI / 3, 3 * PI / 4, 5 * PI / 6, PI])
    fun `calculateAt matches Kotlin Math cos for various inputs`(x: Double) {
        val expected = cos(x)
        val expectedSinInput = PI / 2 - x
        val sinValue = kotlin.math.sin(expectedSinInput)

        every { mockSin.calculateAt(expectedSinInput) } returns sinValue

        val cosCalc = CosToPrecision(precision, mockSin)
        val result = cosCalc.calculateAt(x)

        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `calculateAt verifies sin is called with transformed value PI over 2 minus x`() {
        val x = PI / 4
        val expectedSinInput = PI / 2 - x

        every { mockSin.calculateAt(any()) } returns 0.707

        val cosCalc = CosToPrecision(precision, mockSin)
        cosCalc.calculateAt(x)

        verify { mockSin.calculateAt(expectedSinInput) }
    }

    @Test
    fun `calculateAt handles large angles correctly`() {
        val x = 10 * PI + PI / 3
        val expectedSinInput = PI / 2 - x
        val sinValue = kotlin.math.sin(expectedSinInput)
        val expected = cos(x)

        every { mockSin.calculateAt(expectedSinInput) } returns sinValue

        val cosCalc = CosToPrecision(precision, mockSin)
        val result = cosCalc.calculateAt(x)

        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `calculateAt with real SinToPrecision implementation`() {
        // Test without mocking to verify end-to-end functionality
        val cosCalc = CosToPrecision(1e-6)
        val x = PI / 3
        val expected = cos(x)
        val result = cosCalc.calculateAt(x)

        assertEquals(expected, result, 1e-5)
    }

    @Test
    fun `calculateAt with real SinToPrecision for multiple angles`() {
        val cosCalc = CosToPrecision(1e-6)
        val testAngles = listOf(0.0, PI / 6, PI / 4, PI / 3, PI / 2, PI, 3 * PI / 2, 2 * PI)

        testAngles.forEach { x ->
            val expected = cos(x)
            val result = cosCalc.calculateAt(x)
            assertEquals(expected, result, 1e-5, "Failed for x = $x")
        }
    }

    @Test
    fun `calculateAt handles very small positive angle`() {
        val x = 1e-10
        val expectedSinInput = PI / 2 - x
        val sinValue = kotlin.math.sin(expectedSinInput)
        val expected = cos(x)

        every { mockSin.calculateAt(expectedSinInput) } returns sinValue

        val cosCalc = CosToPrecision(precision, mockSin)
        val result = cosCalc.calculateAt(x)

        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `calculateAt handles very small negative angle`() {
        val x = -1e-10
        val expectedSinInput = PI / 2 - x
        val sinValue = kotlin.math.sin(expectedSinInput)
        val expected = cos(x)

        every { mockSin.calculateAt(expectedSinInput) } returns sinValue

        val cosCalc = CosToPrecision(precision, mockSin)
        val result = cosCalc.calculateAt(x)

        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `default constructor uses SinToPrecision`() {
        val cosCalc = CosToPrecision(1e-6)
        val x = PI / 4
        val expected = cos(x)
        val result = cosCalc.calculateAt(x)

        assertEquals(expected, result, 1e-5)
    }

    @Test
    fun `calculateAt preserves sign for negative cosine values`() {
        val x = 2 * PI / 3  // cos(2π/3) = -0.5
        val expectedSinInput = PI / 2 - x
        val sinValue = kotlin.math.sin(expectedSinInput)
        val expected = cos(x)

        every { mockSin.calculateAt(expectedSinInput) } returns sinValue

        val cosCalc = CosToPrecision(precision, mockSin)
        val result = cosCalc.calculateAt(x)

        assertEquals(expected, result, assertionDelta)
        assertTrue(result < 0, "Result should be negative for x = $x")
    }

    @Test
    fun `calculateAt identity cos(x) = sin(PI over 2 - x) is maintained`() {
        val testAngles = listOf(-PI, -PI / 2, 0.0, PI / 2, PI, 3 * PI / 2, 2 * PI)

        testAngles.forEach { x ->
            val expectedSinInput = PI / 2 - x
            val sinValue = kotlin.math.sin(expectedSinInput)
            val expected = cos(x)

            every { mockSin.calculateAt(expectedSinInput) } returns sinValue

            val cosCalc = CosToPrecision(precision, mockSin)
            val result = cosCalc.calculateAt(x)

            assertEquals(expected, result, assertionDelta, "Identity failed for x = $x")
        }
    }
}