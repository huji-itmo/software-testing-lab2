package org.example.logarithmic

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class LogBaseTest {

    @Test
    fun `creation succeeds with valid positive base not equal to 1`() {
        val base = LogBase.of(2.0)
        assertEquals(2.0, base.value)
    }

    @Test
    fun `creation succeeds with base between 0 and 1`() {
        val base = LogBase.of(0.5)
        assertEquals(0.5, base.value)
    }

    @Test
    fun `creation fails with negative value`() {
        assertThrows<IllegalArgumentException> {
            LogBase.of(-5.0)
        }
    }

    @Test
    fun `creation fails with zero value`() {
        assertThrows<IllegalArgumentException> {
            LogBase.of(0.0)
        }
    }

    @Test
    fun `creation fails with value equal to 1`() {
        assertThrows<IllegalArgumentException> {
            LogBase.of(1.0)
        }
    }

    @Test
    fun `creation fails with infinity`() {
        assertThrows<IllegalArgumentException> {
            LogBase.of(Double.POSITIVE_INFINITY)
        }
    }

    @Test
    fun `creation fails with NaN`() {
        assertThrows<IllegalArgumentException> {
            LogBase.of(Double.NaN)
        }
    }
}
