package com.github.vootelerotov.aoc2020.day15

fun main() {

    val input = arrayListOf(0,14,1,3,7,9)

    val turns = generateTurnSequence(input)

    // Part 1
    println(turns.drop(2020 - 1).first())

    // Part 2
    println(turns.drop(30_000_000 - 1).first())

}

internal fun generateTurnSequence(input: List<Int>): Sequence<Int> {

    val inputAsMapToIndex: MutableMap<Int, Int> =
        input.withIndex().map { it.value to it.index }.take(input.size - 1).toMap(HashMap())

    val turnsAfterFirstOnesWithMap = generateSequence({
        val lastIndex = input.size - 1
        val lastNumber = input[lastIndex]
        findNextStep(inputAsMapToIndex, lastIndex, lastNumber)
    }, { (map, lastIndex, lastNumber) ->
        findNextStep(map, lastIndex, lastNumber)
    })

    val turnsAfterFirstOnes = turnsAfterFirstOnesWithMap.map { (_, _, number) -> number }

    return sequenceOf(input.asSequence(), turnsAfterFirstOnes).flatten()
}

private fun findNextStep(
    inputAsIndexedMap: MutableMap<Int, Int>,
    lastIndex: Int,
    lastNumber: Int
): Triple<MutableMap<Int, Int>, Int, Int> {
    val lastTimeNumberWasSaidPreviously = inputAsIndexedMap[lastNumber]
    val currentNumber = if (lastTimeNumberWasSaidPreviously != null) {
        lastIndex - lastTimeNumberWasSaidPreviously
    } else {
        0
    }
    inputAsIndexedMap[lastNumber] = lastIndex
    return Triple(inputAsIndexedMap, lastIndex + 1, currentNumber)
}