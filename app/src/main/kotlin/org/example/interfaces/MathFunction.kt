package org.example.interfaces

interface MathFunction<T: Number> {
    fun isDefinedAt(x: T): Boolean;
    fun calculateAt(x: T): T;
}