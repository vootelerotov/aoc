package com.github.vootelerotov.aoc2020.day12

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day12KtTest {

    @Test
    fun testPart2() {
        val moves = arrayListOf("F10", "N3", "F7", "R90", "F11")

        val wayPointCommands = parseCommandsForPart2(moves)
        val part2AbsoluteCommands = getAbsoluteCommands(wayPointCommands, WayPointLocation(1, 10))
        assertEquals(286, calculateManhattanDistance(part2AbsoluteCommands))
    }
}