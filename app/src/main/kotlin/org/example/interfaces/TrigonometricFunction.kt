package org.example.interfaces

interface TrigonometricFunction<T : Number> : MathFunction<T> {
    fun getPeriod(): T;
    fun normalizeToPeriod(x: T): T;
}