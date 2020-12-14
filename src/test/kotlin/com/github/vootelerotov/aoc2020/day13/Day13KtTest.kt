package com.github.vootelerotov.aoc2020.day13

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day13KtTest {

    @Test
    fun testEarliestPossibleTime() {
        val earliestDeparture = 939
        val (id, time) = findEarliestPossibleDeparture(arrayListOf(7, 13, 59, 31, 19), earliestDeparture)
        assertEquals(295, (time - earliestDeparture) * id)
    }

    @Test
    fun testEarliestTimeSuitableForAll() {
        val input = arrayOf("7","13","x","x","59","x","31","19").joinToString(separator = ",")
        assertEquals(1068781, findTimeCompatibleWithAllBuses(parseBusIdsWithIndex(input)))
    }
}