package com.github.vootelerotov.aoc2020.day11

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day11KtTest {

    @Test
    fun testNewPlan() {
        val testPlan = arrayOf(
            "L.LL.LL.LL",
            "LLLLLLL.LL",
            "L.L.L..L..",
            "LLLL.LL.LL",
            "L.LL.LL.LL",
            "L.LLLLL.LL",
            "..L.L.....",
            "LLLLLLLLLL",
            "L.LLLLLL.L",
            "L.LLLLL.LL")
        val floorPlan = floorPlan(testPlan.toList())

        assertEquals(testPlan.joinToString("\n"), floorPlan.toString())

        val after2Iterations = newPart1Plan(newPart1Plan(floorPlan))

        val expectedAfterTwoIterations = arrayOf(
        "#.LL.L#.##",
        "#LLLLLL.L#",
        "L.L.L..L..",
        "#LLL.LL.L#",
        "#.LL.LL.LL",
        "#.LLLL#.##",
        "..L.L.....",
        "#LLLLLLLL#",
        "#.LLLLLL.L",
        "#.#LLLL.##")

        assertEquals(expectedAfterTwoIterations.joinToString("\n"), after2Iterations.toString())

    }

    @Test
    fun testPart2Visibility() {
        val visibilityExample = arrayOf(
            ".............",
            ".L.L.#.#.#.#.",
            "............."
        )

        val plan = floorPlan(visibilityExample.toList());

        val visibleSeats = visibleSeats(plan, 1, 1);

        println(visibleSeats)

        assertEquals(1, visibleSeats.size)
    }




}