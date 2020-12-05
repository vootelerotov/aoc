package com.github.vootelerotov.aoc2020

import com.github.vootelerotov.util.Util

fun main(args: Array<String>) {

    val lines = Util.readFileFromArgs(args);

    // Part 1

    val sortedInput = lines.map { Integer.valueOf(it) }.sorted()

    val pair = sortedInput
        .flatMap { x -> sortedInput.map { Pair(x,it) } }
        .find { (x, y) -> x + y == 2020 }

    println(pair?.let { (x, y) -> x * y })

    // Part 2

    val triple = sortedInput
            .flatMap { x -> sortedInput.map { Pair(x, it) } }
            .flatMap { (x, y) -> sortedInput.map { Triple(x, y ,it) } }
            .find { (x, y, z) -> x + y + z == 2020 }

    println(triple?.let { (x, y, z) -> x * y * z })
}