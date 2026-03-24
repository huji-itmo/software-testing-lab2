package org.example.trigonometric

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.trigonometric.interfaces.Cos
import org.example.trigonometric.interfaces.Sin
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.tan

class TanToPrecisionTest {

    private val precision = 1e-6
    private val assertionDelta = 1e-5

    private val mockSin: Sin = mockk()
    private val mockCos: Cos = mockk()

    @Test
    fun `calculateAt returns 0 for input 0`() {
        val x = 0.0
        val sinValue = 0.0
        val cosValue = 1.0
        val expected = 0.0

        every { mockSin.calculateAt(x) } returns sinValue
        every { mockCos.calculateAt(x) } returns cosValue

        val tanCalc = TanToPrecision(precision, mockSin, mockCos)
        val result = tanCalc.calculateAt(x)

        assertEquals(expected, result, assertionDelta)
        verify { mockSin.calculateAt(x) }
        verify { mockCos.calculateAt(x) }
    }

    @Test
    fun `calculateAt returns 1 for PI over 4`() {
        val x = PI / 4
        val sinValue = kotlin.math.sin(x)
        val cosValue = kotlin.math.cos(x)
        val expected = 1.0

        every { mockSin.calculateAt(x) } returns sinValue
        every { mockCos.calculateAt(x) } returns cosValue

        val tanCalc = TanToPrecision(precision, mockSin, mockCos)
        val result = tanCalc.calculateAt(x)

        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `calculateAt returns sqrt3 for PI over 3`() {
        val x = PI / 3
        val sinValue = kotlin.math.sin(x)
        val cosValue = kotlin.math.cos(x)
        val expected = kotlin.math.sqrt(3.0)

        every { mockSin.calculateAt(x) } returns sinValue
        every { mockCos.calculateAt(x) } returns cosValue

        val tanCalc = TanToPrecision(precision, mockSin, mockCos)
        val result = tanCalc.calculateAt(x)

        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `calculateAt returns negative value for negative angle`() {
        val x = -PI / 4
        val sinValue = kotlin.math.sin(x)
        val cosValue = kotlin.math.cos(x)
        val expected = -1.0

        every { mockSin.calculateAt(x) } returns sinValue
        every { mockCos.calculateAt(x) } returns cosValue

        val tanCalc = TanToPrecision(precision, mockSin, mockCos)
        val result = tanCalc.calculateAt(x)

        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `calculateAt returns 0 for PI`() {
        val x = PI
        val sinValue = 0.0
        val cosValue = -1.0
        val expected = 0.0

        every { mockSin.calculateAt(x) } returns sinValue
        every { mockCos.calculateAt(x) } returns cosValue

        val tanCalc = TanToPrecision(precision, mockSin, mockCos)
        val result = tanCalc.calculateAt(x)

        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `calculateAt returns POSITIVE_INFINITY when approaching PI over 2 from left`() {
        val x = PI / 2 - precision / 2
        val tanCalc = TanToPrecision(precision, mockSin, mockCos)
        val result = tanCalc.calculateAt(x)
        assertTrue(result.isInfinite() && result > 0)
    }

    @Test
    fun `calculateAt returns NEGATIVE_INFINITY when approaching PI over 2 from right`() {
        val x = PI / 2 + precision / 2
        val tanCalc = TanToPrecision(precision, mockSin, mockCos)
        val result = tanCalc.calculateAt(x)
        assertTrue(result.isInfinite() && result < 0)
    }

    @Test
    fun `calculateAt returns NaN exactly at asymptote`() {
        val x = PI / 2

        val tanCalc = TanToPrecision(precision, mockSin, mockCos)
        val result = tanCalc.calculateAt(x)

        assertTrue(result.isNaN())
    }

    @Test
    fun `calculateAt returns infinity at 3PI over 2`() {
        val x = 3 * PI / 2

        val tanCalc = TanToPrecision(precision, mockSin, mockCos)
        val result = tanCalc.calculateAt(x)

        assertTrue(!result.isFinite())
    }

    @Test
    fun `calculateAt handles negative asymptote at negative PI over 2`() {
        val x = -PI / 2

        val tanCalc = TanToPrecision(precision, mockSin, mockCos)
        val result = tanCalc.calculateAt(x)

        assertTrue(result.isInfinite() || result.isNaN())
    }

    @Test
    fun `isDefinedAt returns false at PI over 2`() {
        val x = PI / 2
        val tanCalc = TanToPrecision(precision, mockSin, mockCos)

        assertFalse(tanCalc.isDefinedAt(x))
    }

    @Test
    fun `isDefinedAt returns false at 3PI over 2`() {
        val x = 3 * PI / 2
        val tanCalc = TanToPrecision(precision, mockSin, mockCos)

        assertFalse(tanCalc.isDefinedAt(x))
    }

    @Test
    fun `isDefinedAt returns false at negative PI over 2`() {
        val x = -PI / 2
        val tanCalc = TanToPrecision(precision, mockSin, mockCos)

        assertFalse(tanCalc.isDefinedAt(x))
    }

    @Test
    fun `isDefinedAt returns true at safe distance from asymptote`() {
        val x = PI / 2 - precision * 2
        val tanCalc = TanToPrecision(precision, mockSin, mockCos)

        assertTrue(tanCalc.isDefinedAt(x))
    }

    @Test
    fun `isDefinedAt returns true at 0`() {
        val x = 0.0
        val tanCalc = TanToPrecision(precision, mockSin, mockCos)

        assertTrue(tanCalc.isDefinedAt(x))
    }

    @Test
    fun `isDefinedAt returns true at PI over 4`() {
        val x = PI / 4
        val tanCalc = TanToPrecision(precision, mockSin, mockCos)

        assertTrue(tanCalc.isDefinedAt(x))
    }

    @Test
    fun `isDefinedAt respects periodicity`() {
        val tanCalc = TanToPrecision(precision, mockSin, mockCos)
        assertFalse(tanCalc.isDefinedAt(PI / 2))
        assertFalse(tanCalc.isDefinedAt(PI / 2 + PI))
    }
    

    @ParameterizedTest
    @ValueSource(
        doubles = [
            0.0, PI / 6, PI / 4, PI / 3,
            -PI / 6, -PI / 4, -PI / 3,
            PI, 2 * PI, -PI, -2 * PI,
        ],
    )
    fun `calculateAt matches Kotlin Math tan for defined inputs`(x: Double) {
        val normalized = ((x % PI) + PI) % PI
        val distToAsymptote = abs(normalized - PI / 2)
        if (distToAsymptote < precision * 10) return

        val sinValue = kotlin.math.sin(x)
        val cosValue = kotlin.math.cos(x)
        val expected = tan(x)

        every { mockSin.calculateAt(x) } returns sinValue
        every { mockCos.calculateAt(x) } returns cosValue

        val tanCalc = TanToPrecision(precision, mockSin, mockCos)
        val result = tanCalc.calculateAt(x)

        assertEquals(expected, result, assertionDelta, "Failed for x = $x")
    }

    @Test
    fun `calculateAt verifies sin and cos are called with input x when defined`() {
        val x = PI / 6
        every { mockSin.calculateAt(x) } returns 0.5
        every { mockCos.calculateAt(x) } returns kotlin.math.cos(PI / 6)

        val tanCalc = TanToPrecision(precision, mockSin, mockCos)
        tanCalc.calculateAt(x)

        verify { mockSin.calculateAt(x) }
        verify { mockCos.calculateAt(x) }
    }

    @Test
    fun `calculateAt does not call sin or cos when undefined at asymptote`() {
        val x = PI / 2
        every { mockSin.calculateAt(any()) } returns 1.0
        every { mockCos.calculateAt(any()) } returns 0.0

        val tanCalc = TanToPrecision(precision, mockSin, mockCos)
        tanCalc.calculateAt(x)

        verify(exactly = 0) { mockSin.calculateAt(any()) }
        verify(exactly = 0) { mockCos.calculateAt(any()) }
    }

    @Test
    fun `calculateAt handles very small positive angle`() {
        val x = 1e-10
        val expected = tan(x)

        val tanCalc = TanToPrecision(precision, mockSin, mockCos)

        every { mockSin.calculateAt(x) } returns kotlin.math.sin(x)
        every { mockCos.calculateAt(x) } returns kotlin.math.cos(x)

        val result = tanCalc.calculateAt(x)

        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `calculateAt handles very small negative angle`() {
        val x = -1e-10
        val expected = tan(x)

        every { mockSin.calculateAt(x) } returns kotlin.math.sin(x)
        every { mockCos.calculateAt(x) } returns kotlin.math.cos(x)

        val tanCalc = TanToPrecision(precision, mockSin, mockCos)
        val result = tanCalc.calculateAt(x)

        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `calculateAt handles large positive angle`() {
        val x = 100 * PI + PI / 6
        val normalized = ((x % PI) + PI) % PI
        val distToAsymptote = abs(normalized - PI / 2)
        if (distToAsymptote < precision * 10) return

        val expected = tan(x)
        every { mockSin.calculateAt(x) } returns kotlin.math.sin(x)
        every { mockCos.calculateAt(x) } returns kotlin.math.cos(x)

        val tanCalc = TanToPrecision(precision, mockSin, mockCos)
        val result = tanCalc.calculateAt(x)

        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `calculateAt handles large negative angle`() {
        val x = -100 * PI - PI / 6
        val normalized = ((x % PI) + PI) % PI
        val distToAsymptote = abs(normalized - PI / 2)
        if (distToAsymptote < precision * 10) return

        val expected = tan(x)
        every { mockSin.calculateAt(x) } returns kotlin.math.sin(x)
        every { mockCos.calculateAt(x) } returns kotlin.math.cos(x)

        val tanCalc = TanToPrecision(precision, mockSin, mockCos)
        val result = tanCalc.calculateAt(x)

        assertEquals(expected, result, assertionDelta)
    }

    @Test
    fun `calculateAt with real SinToPrecision and CosToPrecision for PI over 4`() {
        val tanCalc = TanToPrecision(1e-6)
        val x = PI / 4
        val expected = tan(x)
        val result = tanCalc.calculateAt(x)

        assertEquals(expected, result, 1e-5)
    }

    @ParameterizedTest
    @ValueSource(doubles = [0.0, PI / 6, PI / 4, PI / 3, -PI / 6, -PI / 4, PI, 2 * PI])
    fun `calculateAt with real implementations matches Kotlin tan for various inputs`(x: Double) {
        val normalized = ((x % PI) + PI) % PI
        val distToAsymptote = abs(normalized - PI / 2)
        if (distToAsymptote < 1e-4) return

        val tanCalc = TanToPrecision(1e-6)
        val expected = tan(x)
        val result = tanCalc.calculateAt(x)

        assertEquals(expected, result, 1e-4, "Failed for x = $x")
    }

    @Test
    fun `calculateAt returns infinity at asymptote with real implementations`() {
        val tanCalc = TanToPrecision(1e-6)
        val result = tanCalc.calculateAt(PI / 2)

        assertTrue(result.isNaN() || result.isInfinite())
    }

    @Test
    fun `calculateAt sign of infinity near asymptote from left`() {
        val tanCalc = TanToPrecision(1e-6)
        val x = PI / 2 - 1e-7
        val result = tanCalc.calculateAt(x)

        assertTrue(result > 0 && result.isInfinite() || result > 1e10)
    }

    @Test
    fun `calculateAt sign of infinity near asymptote from right`() {
        val tanCalc = TanToPrecision(1e-6)
        val x = PI / 2 + 1e-7
        val result = tanCalc.calculateAt(x)

        assertTrue(result < 0 && result.isInfinite() || result < -1e10)
    }

    @Test
    fun `default constructor uses SinToPrecision and CosToPrecision with same precision`() {
        val tanCalc = TanToPrecision(1e-6)
        val x = PI / 6
        val expected = tan(x)
        val result = tanCalc.calculateAt(x)

        assertEquals(expected, result, 1e-5)
    }

    @Test
    fun `custom sin and cos dependencies are used`() {
        val customSin: Sin = mockk()
        val customCos: Cos = mockk()
        val x = PI / 4

        every { customSin.calculateAt(x) } returns 0.5
        every { customCos.calculateAt(x) } returns 0.5

        val tanCalc = TanToPrecision(1e-6, customSin, customCos)
        val result = tanCalc.calculateAt(x)

        assertEquals(1.0, result, assertionDelta)
        verify { customSin.calculateAt(x) }
        verify { customCos.calculateAt(x) }
    }

    @Test
    fun `calculateAt respects period of PI`() {
        val tanCalc = TanToPrecision(1e-6)
        val baseX = PI / 6
        val result1 = tanCalc.calculateAt(baseX)
        val result2 = tanCalc.calculateAt(baseX + PI)
        val result3 = tanCalc.calculateAt(baseX - PI)

        assertEquals(result1, result2, assertionDelta)
        assertEquals(result1, result3, assertionDelta)
    }

    @Test
    fun `isDefinedAt respects period of PI for asymptotes`() {
        val tanCalc = TanToPrecision(1e-6)

        assertFalse(tanCalc.isDefinedAt(PI / 2))
        assertFalse(tanCalc.isDefinedAt(PI / 2 + PI))
        assertFalse(tanCalc.isDefinedAt(PI / 2 - PI))
        assertFalse(tanCalc.isDefinedAt(PI / 2 + 2 * PI))
    }
}
