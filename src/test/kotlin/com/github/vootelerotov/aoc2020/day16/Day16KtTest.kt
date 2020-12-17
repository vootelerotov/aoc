package com.github.vootelerotov.aoc2020.day16

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class Day16KtTest {

    @Test
    fun findSumOnInvalidFields() {
        val info = """
            class: 1-3 or 5-7
            row: 6-11 or 33-44
            seat: 13-40 or 45-50

            your ticket:
            7,1,14

            nearby tickets:
            7,3,47
            40,4,50
            55,2,20
            38,6,12
        """.trimIndent()
        val (rules, _, tickets) = parseInfo(info.split('\n'))
        assertEquals(0, findSumOnInvalidFields(tickets[0], rules))
        assertEquals(55, findSumOnInvalidFields(tickets[2], rules))
    }

    @Test
    fun testFindingMatchingRules() {
        val info = """
            class: 0-1 or 4-19
            row: 0-5 or 8-19
            seat: 0-13 or 16-19
            
            your ticket:
            11,12,13
            
            nearby tickets:
            3,9,18
            15,1,5
            5,14,9
        """.trimIndent()
        val (rules, _, tickets) = parseInfo(info.split('\n'))
        val matchingRules = findMatchingRules(tickets, rules.toHashSet());
        assertTrue(matchingRules.map { (index, rule) -> index to rule.name }.containsAll(arrayListOf(
            0 to "row",
            1 to "class",
            2 to "seat"
        )))

    }

}