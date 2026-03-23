package org.example.trigonometric.interfaces

import org.example.interfaces.MathFunction

interface TrigonometricFunction<T> : MathFunction<T> {
    fun getPeriod(): T;
    fun normalizeToPeriod(x: T): T;
}