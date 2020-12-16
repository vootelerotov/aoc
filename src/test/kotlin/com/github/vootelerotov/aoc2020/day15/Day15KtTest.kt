package com.github.vootelerotov.aoc2020.day15

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day15KtTest {

    @Test
    fun testGenerateTurnSequence() {
        val tenFirstNumbers = generateTurnSequence(arrayListOf(0, 3, 6)).take(10).toList()
        assertIterableEquals(arrayListOf(0, 3, 6, 0, 3, 3, 1, 0, 4, 0), tenFirstNumbers)
    }
}