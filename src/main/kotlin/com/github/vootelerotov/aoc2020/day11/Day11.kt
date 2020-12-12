package com.github.vootelerotov.aoc2020.day11

import com.github.vootelerotov.util.Util

fun main(args: Array<String>) {
    val lines = Util.readFileFromArgs(args)

    val floorPlan = floorPlan(lines)

    // Part 1

    findNumberOfFinalOccupiedSeats(floorPlan) { plan -> newPart1Plan(plan) }

    // Part 2

    findNumberOfFinalOccupiedSeats(floorPlan) {  plan -> newPart2Plan(plan) }

}

private fun findNumberOfFinalOccupiedSeats(
    floorPlan: FloorPlan,
    newPlan: (FloorPlan) -> FloorPlan
) {
    val plan = generateSequence(floorPlan, newPlan)

    val stablePlan = plan.zipWithNext().first { (previous, new) -> previous == new }.first

    val occupiedSeats = stablePlan.seats().count { (x, y) -> stablePlan.getState(x, y) == State.OCCUPIED }

    println(occupiedSeats)
}

internal fun floorPlan(lines: Collection<String>) =
    FloorPlan(lines.map { line -> line.map { toState(it) } })

private fun toState(char: Char): State {
    return when (char) {
        'L' -> State.EMPTY
        '#' -> State.OCCUPIED
        '.' -> State.FLOOR
        else -> throw IllegalArgumentException("Unexpected char $char")
    }
}


internal fun newPlan(plan: FloorPlan, relevant: (FloorPlan, Int, Int) -> List<Pair<Int, Int>>, maximumOccupied: Int): FloorPlan {
    return FloorPlan((0.until(plan.length())
        .map { x -> 0.until(plan.width()).map { y -> newState(plan, x, y, relevant.invoke(plan, x, y), maximumOccupied) } })
    )
}

internal fun newPart1Plan(plan: FloorPlan): FloorPlan {
    return newPlan(plan, { plan, x, y -> adjacentSeats(plan, x, y) }, 4)
}

internal fun newPart2Plan(plan: FloorPlan): FloorPlan {
    return newPlan(plan, { plan, x, y -> visibleSeats(plan, x, y) }, 5)
}

private fun newState(plan: FloorPlan, x: Int, y: Int, relevantSeats: List<Pair<Int, Int>>, maximumOccupied: Int): State {
    val currentState = plan.getState(x, y)

    val relevantOccupiedSeats = relevantSeats
        .filter { (z, w) -> plan.getState(z, w) == State.OCCUPIED }
        .count()

    if (currentState == State.EMPTY && relevantOccupiedSeats == 0) {
        return State.OCCUPIED
    } else if (currentState == State.OCCUPIED && relevantOccupiedSeats >= maximumOccupied) {
        return State.EMPTY
    }

    return currentState
}

internal fun visibleSeats(plan: FloorPlan, x: Int, y: Int): List<Pair<Int, Int>> {
    val adjacentSpots = adjacentSpots(plan, x, y)
    val withGradients = adjacentSpots.zip(adjacentSpots
        .map { (w, z) -> (w - x) to (z - y) })

    return withGradients.map { (start, gradient) ->
        generateSequence(start) { (w, z) ->
            val newX = w + gradient.first
            val newY = z + gradient.second
            if (insidePlan(plan, newX, newY)) newX to newY else null
        }
    }.mapNotNull { it.firstOrNull { (w, z) -> plan.getState(w, z) != State.FLOOR } }
}

internal fun adjacentSeats(plan: FloorPlan, x: Int, y: Int): List<Pair<Int, Int>> {
    return adjacentSpots(plan, x, y)
        .filter { (x, y) -> plan.getState(x, y) != State.FLOOR }
}

private fun adjacentSpots(
    plan: FloorPlan,
    x: Int,
    y: Int
) : List<Pair<Int, Int>> {

    return Util.crossProduct(((x - 1)..(x + 1)).toList(), ((y - 1)..(y + 1)).toList())
        .filter {  (x, y ) -> insidePlan(plan, x, y) }
        .filter { it != x to y }
}

private fun insidePlan(plan: FloorPlan, x: Int, y: Int) =
    x in 0.until(plan.length()) && y in 0.until(plan.width())

internal enum class State(val representation: Char) {
    EMPTY('L'), OCCUPIED('#'), FLOOR('.')
}

internal class FloorPlan(private val seatMap: List<List<State>>) {

    fun length(): Int {
        return seatMap.size
    }

    fun width(): Int {
        return seatMap[0].size
    }

    fun getState(x: Int, y: Int): State {
        return seatMap[x][y]
    }

    fun seats(): List<Pair<Int, Int>> {
        return seatMap.withIndex().flatMap { first -> first.value.indices.map { first.index to it } }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FloorPlan

        if (seatMap != other.seatMap) return false

        return true
    }

    override fun hashCode(): Int {
        return seatMap.hashCode()
    }

    override fun toString(): String {
        return seatMap.joinToString(separator = "\n") { row -> row.map { seat -> seat.representation }.joinToString("") }
    }
}