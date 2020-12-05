package com.github.vootelerotov.aoc2020

import com.github.vootelerotov.util.Util
import java.lang.IllegalArgumentException

fun main(args: Array<String>) {

    val lines = Util.readFileFromArgs(args)

    val part1ReqCreator: (Triple<Char, Int, Int>) -> ((String) -> Boolean) = {  (char, minimumNumber, maximumNumber) ->
        { password -> password.count { it == char } in minimumNumber..maximumNumber }
    }

    countValidPassports(lines, part1ReqCreator);


    // Part 2

    val part2ReqCreator: (Triple<Char, Int, Int>) -> ((String) -> Boolean) = {  (char, firstIndex, secondIndex) ->
        { password -> (password[firstIndex] == char).xor(password[secondIndex] == char) }
    }

    countValidPassports(lines, part2ReqCreator);

}

private fun countValidPassports(lines: Collection<String>, reqCreator: (Triple<Char, Int, Int>) -> ((String) -> Boolean)) {
    val count = lines
        .map { x -> x.split(':') }
        .map { Pair(it[0], it[1]) }
        .map { (req, pw) -> Pair(reqCreator(parseRequirement(req)), pw) }
        .filter { (req, pw) -> req.invoke(pw) }
        .count()

    println(count)
}

fun parseRequirement(req: String): Triple<Char, Int, Int>  {
    val splitBySpace = req.split(" ")
    if (splitBySpace.size != 2) {
        throw IllegalArgumentException("Messed input")
    }

    val requiredChar = splitBySpace[1]
    if (splitBySpace[1].length != 1) {
        throw IllegalArgumentException("Messed char")
    }

    val indexes = splitBySpace[0]
    val indexesSplitByDash = indexes.split("-")

    if (indexesSplitByDash.size != 2) {
        throw IllegalArgumentException("Messed input")
    }

    return Triple(requiredChar[0], Integer.parseInt(indexesSplitByDash[0]), Integer.parseInt(indexesSplitByDash[1]))

}