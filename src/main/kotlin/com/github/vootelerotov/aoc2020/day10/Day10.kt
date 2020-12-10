package com.github.vootelerotov.aoc2020.day10

import com.github.vootelerotov.util.Util
import java.math.BigInteger
import java.util.concurrent.ConcurrentHashMap

fun main(args: Array<String>) {
    val lines = Util.readFileFromArgs(args)

    val sortedAdapters = lines.map { it.toInt() }.sorted();

    //Part 1

    val joltageDiffsCounts = findJoltageDiffs(sortedAdapters)

    println(joltageDiffsCounts[1]?.plus(1)?.times(joltageDiffsCounts[3]!! + 1))

    //Part 2

    val combinations = findJoltageCombinations(sortedAdapters.map { it.toLong() }.withIndex().toList())
    println(combinations.toString())

}

fun findJoltageCombinations(
    sortedAdapters: List<IndexedValue<Long>>,
    index: Int = 0,
    lastValue: Long = 0,
    cache: MutableMap<Int, BigInteger> = ConcurrentHashMap()
): BigInteger {
    if (index == sortedAdapters.size) {
        return BigInteger.valueOf(1)
    }
    return sortedAdapters
        .subList(index, sortedAdapters.size)
        .takeWhile { it.value <= lastValue + 3 }
        .map { elem ->
            Util.recursiveProofComputeIfAbsent(cache, elem.index) {
                findJoltageCombinations(
                    sortedAdapters,
                    elem.index + 1,
                    elem.value,
                    cache
                )
            }
        }
        .fold(BigInteger.valueOf(0)) { x, y -> x.add(y) }
}

fun findJoltageDiffs(sortedAdapters: List<Int>): Map<Int, Int> {
    return sortedAdapters.zipWithNext()
        .map { (x, y) -> y - x }
        .groupBy { x -> x }
        .mapValues { (_, elements) -> elements.size }
}