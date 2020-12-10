package com.github.vootelerotov.aoc2020.day10

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day10KtTest {

    @Test
    fun testJoltageDiffs() {
        val testData = arrayOf(
            28, 33, 18, 42, 31, 14, 46, 20, 48, 47, 24, 23,
            49, 45, 19, 38, 39, 11, 1, 32, 25, 35, 8, 17, 7, 9,
            4, 2, 34, 10, 3
        ).sorted()

        val findJoltageDiffs = findJoltageDiffs(testData);

        assertEquals(22, findJoltageDiffs[1]?.plus(1))
        assertEquals(10, findJoltageDiffs[3]?.plus(1))
    }

    @Test
    fun testJoltageCombinationsSmaller() {
        val testData = arrayOf(16, 10, 15, 5, 1, 11, 7, 19, 6, 12, 4).sorted()

        assertEquals(
            8,
            findJoltageCombinations(testData.map { it.toLong() }.withIndex().toList()).toInt()
        )
    }

    @Test
    fun testJoltageCombinationsBigger() {
        val testData = arrayOf(
            28, 33, 18, 42, 31, 14, 46, 20, 48, 47, 24, 23,
            49, 45, 19, 38, 39, 11, 1, 32, 25, 35, 8, 17, 7, 9,
            4, 2, 34, 10, 3
        ).sorted()

        assertEquals(
            19208,
            findJoltageCombinations(testData.map { it.toLong() }.withIndex().toList()).toInt()
        )
    }
}