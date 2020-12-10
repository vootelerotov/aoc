package com.github.vootelerotov.aoc2020.day9

import com.github.vootelerotov.util.Util

fun main(args: Array<String>) {
    val numbers = Util.readFileFromArgs(args).map { it.toLong() }

    // Part 1
    val firstIndex = (25 until numbers.size).first {
        val crossProduct = crossProduct(numbers.subList(it - 25, it))
        val sums = crossProduct.map { pair -> (pair.first + pair.second) to pair }.sortedBy { (sum, _) -> sum }
        val exists = sums.binarySearchBy(numbers[it], selector = { (sum, _) -> sum }) >= 0
        !exists
    }

    val firstAnswer = numbers[firstIndex]

    // Part 2
    val subList = numbers.indices.map {
        generateSequence(it to numbers[it])
        { (index, _) ->
            if (index > numbers.size) null else (index + 1) to numbers[index + 1]
        }.map { (_, y) -> y }
    }.map { findContiguousSetWithValue(it, firstAnswer) }.first { it != null }

    println(subList)
    val sorted = subList?.sorted()
    println(sorted?.first()?.plus(sorted.last()))
}

fun findContiguousSetWithValue(it: Sequence<Long>, firstAnswer: Long): List<Long>? {
    var sum: Long = 0
    val subList: MutableList<Long> = ArrayList();
    for (l in it) {
        sum += l
        subList.add(l)

        if (sum == firstAnswer) {
            return subList
        }
        if (sum > firstAnswer) {
            return null
        }
    }
    return null
}


fun <T> crossProduct(list: List<T>): List<Pair<T, T>> {
    return list.flatMapIndexed { index, value ->
        list.subList(0, index).map { value to it }
    }
}