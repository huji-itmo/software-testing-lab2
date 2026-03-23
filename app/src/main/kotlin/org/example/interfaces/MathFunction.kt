package org.example.interfaces

interface MathFunction<T> {
    fun isDefinedAt(x: T): Boolean;
    fun calculateAt(x: T): T;
}