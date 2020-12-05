package com.github.vootelerotov.aoc2020

import com.github.vootelerotov.util.Util
import java.lang.IllegalArgumentException

fun main(args: Array<String>) {

    val lines = Util.readFileFromArgs(args)

    if (lines.any { it.length != 10} ) {
        throw IllegalArgumentException("There exists a line that does not have 10 chars")
    }

    // Part 1

    val rowsAndColumns = lines
            .map { line -> line
                    .replace('F', '0')
                    .replace('B', '1')
                    .replace('L', '0')
                    .replace('R', '1')
            }
            .map { line -> line.substring(0, 7) to line.substring(7) }
            .map { (x, y) -> x.toLong(2) to y.toLong(2)}


    val seatIds = rowsAndColumns
            .map { (x, y) -> x * 8 + y  }

    println(seatIds.maxOrNull())
    // Part 2

    val sorted = seatIds.sorted();
    val seatsWithGap = sorted.zip(sorted.drop(1)).find { (x, y) -> x + 1 != y }
    println(seatsWithGap)
}