package org.example.logarithmic

@JvmInline
value class LogBase(val value: Double) {
    init {
        require(value > 0.0) { "Logarithm base must be positive." }
        require(value != 1.0) { "Logarithm base cannot be 1.0 (ln(1) is 0, causing division by zero)." }
        require(value.isFinite()) { "Logarithm base must be a finite number." }
    }

    companion object {
        fun of(value: Double): LogBase = LogBase(value)
    }
}