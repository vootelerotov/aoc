package com.github.vootelerotov.aoc2020

import java.io.File
import java.lang.IllegalArgumentException

fun main(args: Array<String>) {
    if (args.size != 1) {
        throw IllegalArgumentException("Give one input for input file path")
    }
    val file = File(args[0])
    if (!file.exists()) {
        throw IllegalArgumentException("File with given path does not exist")
    }

    val lines = file.readLines();
    val trees = lines
            .map { line -> line.map { char -> char == '#' }}
    val map = SkiMap(trees);

    // Part 1

    val count = findTreeCount(generateMoves(3, 1, map.length()), map)

    println(count);

    // Part 2

    val slopes = arrayOf(
            Pair(1, 1),
            Pair(3, 1),
            Pair(5, 1),
            Pair(7, 1),
            Pair(1, 2)
    )

    val treeCounts: List<Int> = slopes.map { (x, y) -> generateMoves(x, y, map.length()) }.map { findTreeCount(it, map) }

    val product = treeCounts.map { it.toLong() }.reduce { x, y -> x * y}

    println(product)

}

private fun findTreeCount(legitMoves: Sequence<Pair<Int, Int>>, map: SkiMap) =
     legitMoves.map { (x, y) -> map.isTree(x, y) }.count { x -> x }

private fun generateMoves(x: Int, y: Int, length: Int): Sequence<Pair<Int, Int>> {
    val moves = generateSequence(Pair(x, y)) { (z, w) -> Pair(z + x, w + y) }

    return moves.takeWhile { (_, y) -> y < length }
}

class SkiMap(private val parsedMap: List<List<Boolean>>) {

    init {
        val differentLineEndings = parsedMap
                .map { line -> line.size }
                .distinct().size
        if (differentLineEndings != 1) {
            throw IllegalArgumentException("Messed up map");
        }
    }

    fun length(): Int {
       return parsedMap.size
    }

    private fun mapWidth(): Int {
        return parsedMap[0].size;
    }

    fun isTree(x: Int, y: Int): Boolean {
        if (y >= length()) {
            throw IllegalArgumentException("Out of map");
        }

        return parsedMap[y][(x % mapWidth())]
    }

}