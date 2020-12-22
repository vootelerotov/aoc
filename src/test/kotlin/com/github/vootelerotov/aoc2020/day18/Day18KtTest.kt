package com.github.vootelerotov.aoc2020.day18

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day18KtTest {

    @Test
    fun testSimpleNoParenExpression() {
        val value = calculateValue(parse("2 * 3"))
        assertEquals(6, value)
    }

    @Test
    fun testExampleOne() {
        assertEquals(26, calculateValue(parse("2 * 3 + (4 * 5)")))
    }

    @Test
    fun testExampleTwo() {
        assertEquals(437, calculateValue(parse("5 + (8 * 3 + 9 + 3 * 4 * 3)")))
    }

    @Test
    fun testPart2Example() {
        assertEquals(46, calculateValue(parsePart2("2 * 3 + (4 * 5)")))
    }

    @Test
    fun testPart2Example2() {
        assertEquals(51, calculateValue(parsePart2("1 + (2 * 3) + (4 * (5 + 6))")))
    }

    @Test
    fun testPart2Example3() {
        assertEquals(669060, calculateValue(parsePart2("5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))")))
    }

    @Test
    fun testPart2Example4() {
        assertEquals(23340, calculateValue(parsePart2("((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2")))
    }
}