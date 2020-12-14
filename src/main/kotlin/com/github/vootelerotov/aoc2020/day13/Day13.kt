package com.github.vootelerotov.aoc2020.day13

import com.github.vootelerotov.util.Util
import java.lang.IllegalArgumentException

fun main(args: Array<String>) {
    val lines = Util.readFileFromArgs(args).toList()

    if (lines.size != 2) {
        throw IllegalArgumentException("Expecting two lines")
    }

    // Part 1
    val busIds = parseBusIds(lines[1])

    val earliestPossibleDeparture = lines[0].toInt()

    val (busId, earliestViableDeparture) = findEarliestPossibleDeparture(busIds, earliestPossibleDeparture)

    println((earliestViableDeparture - earliestPossibleDeparture) * busId)

    // Part 2

    val busIdsWithIndex = parseBusIdsWithIndex(lines[1])
    val t = findTimeCompatibleWithAllBuses(busIdsWithIndex)
    println(t)
}

internal fun findTimeCompatibleWithAllBuses(busIdsWithIndex: List<Pair<Long, Int>>) =
    busIdsWithIndex.fold(1L to generateSequence(0L) { current -> current + 1L }) { (step, seq), (id, index) ->
        val firstCandidateThatFitsCurrent = seq.first { t -> (t + index) % id == 0L }
        val newStep = id * step
        val suitableTimes = generateSequence(firstCandidateThatFitsCurrent) { current -> current + newStep }
        newStep to suitableTimes
    }.second.first()

internal fun findEarliestPossibleDeparture(busIds: List<Int>, earliestViableDeparture: Int) =
    busIds.map { id ->
        val lastDepartureBeforeEarliestViableDeparture = earliestViableDeparture % id
        val nextDeparture = lastDepartureBeforeEarliestViableDeparture + id
        id to (earliestViableDeparture - nextDeparture)
    }.minByOrNull { (_, departure) -> departure }!!

private fun parseBusIds(busLine: String): List<Int> =
    busLine.split(',').filter { it != "x" }.map { it.toInt() }

internal fun parseBusIdsWithIndex(busLine: String): List<Pair<Long, Int>> =
    busLine.split(',').withIndex().filter { it.value != "x" }.map { it.value.toLong() to it.index }