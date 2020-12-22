package com.github.vootelerotov.aoc2020.day17

import com.github.vootelerotov.util.Util

fun main(args: Array<String>) {
    val lines = Util.readFileFromArgs(args)

    val initialState = parsePowerPlantState(lines)

    // Part 1

    val after6Iterations = calculateStateAfterNthIteration(initialState, 6) {neighbors(it)}
    println(after6Iterations.activeCubeSet.size)

    // Part 2

    val initialStateInHyperCoordinates = parsePowerPlantStateIn4Dimensions(lines)

    val after6IterationsInHyperRoom = calculateStateAfterNthIteration(initialStateInHyperCoordinates, 6) { neighborsInHyperRoom(it) }
    println(after6IterationsInHyperRoom.activeCubeSet.size)
}

internal fun parsePowerPlantState(lines: Collection<String>): PowerPlantState<Triple<Int, Int, Int>> =
    PowerPlantState(lines.withIndex()
        .flatMap { (y, row ) -> row.withIndex()
            .mapNotNull { (x, elem) -> if (elem == '#') Triple(x, y, 0) else null } }.toSet())

internal fun parsePowerPlantStateIn4Dimensions(lines: Collection<String>): PowerPlantState<HyperCoordinates> =
    PowerPlantState(parsePowerPlantState(lines).activeCubeSet.map { (x, y, z) -> HyperCoordinates(x, y, z , 0) }.toSet())


internal fun <T> calculateStateAfterNthIteration(initial: PowerPlantState<T>, n: Int, neighborFunction: (T) -> Collection<T> ) : PowerPlantState<T> =
    generateSequence(initial) { calculateNewState(it, neighborFunction) }.drop(n).first()

internal fun <T> calculateNewState(current: PowerPlantState<T>, neighborFunction: (T) -> Collection<T>) : PowerPlantState<T> =
    PowerPlantState(current.activeCubeSet
        .flatMap { neighborFunction.invoke(it) }
        .filter { newStateAt(current, it, neighborFunction) == CubeState.ACTIVE }.toSet())


private fun <T> newStateAt(currentState: PowerPlantState<T>, coordinates: T, neighborFunction: (T) -> Collection<T>): CubeState =
    if (currentState.getState(coordinates) == CubeState.ACTIVE) {
        if (activeNeighbors(currentState, coordinates, neighborFunction) in 2..3) CubeState.ACTIVE else CubeState.INACTIVE
    } else {
        if (activeNeighbors(currentState, coordinates, neighborFunction) == 3) CubeState.ACTIVE else CubeState.INACTIVE
    }


private fun <T> activeNeighbors(currentState: PowerPlantState<T>, coordinates: T, neighborFunction: (T) -> Collection<T>) : Int =
    neighborFunction.invoke(coordinates).map { currentState.getState(it) }.filter { it == CubeState.ACTIVE }.count()


private fun neighbors(coordinates: Triple<Int, Int, Int>): Collection<Triple<Int, Int, Int>> {
    val (x, y , z) = coordinates
    return Util.crossProduct(Util.crossProduct(neighbors(x), neighbors(y)), neighbors(z))
        .map { (inner, z) ->
            val (x, y) = inner
            Triple(x, y, z)
        }
        .filter { it != coordinates }
}

private fun neighborsInHyperRoom(coordinates: HyperCoordinates): Collection<HyperCoordinates> {
    val (x, y, z ,w) = coordinates
    val firstThreeCoordinates = Triple(x, y, z)
    return Util.crossProduct(neighbors(firstThreeCoordinates).union(arrayListOf(firstThreeCoordinates)), neighbors(w))
        .map { (triple, w) ->
            val (x, y, z) = triple
            HyperCoordinates(x, y, z, w)
        }
        .filter { it != coordinates }

}

private fun neighbors(x : Int): Collection<Int> = arrayListOf(x - 1, x, x + 1)

internal data class HyperCoordinates(val x: Int, val y: Int, val z: Int, val w: Int)

internal enum class CubeState(val representation: Char) {
    ACTIVE('#'), INACTIVE('.')
}

internal class PowerPlantState<T>(val activeCubeSet: Set<T>) {

    fun getState(coordinates: T): CubeState =
        if (activeCubeSet.contains(coordinates)) CubeState.ACTIVE else CubeState.INACTIVE

}