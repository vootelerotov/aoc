package com.github.vootelerotov.aoc2020

import com.github.vootelerotov.util.Util

fun main(args: Array<String>) {
    val lines = Util.readFileFromArgs(args)

    val groups = Util.split(lines) { x -> x.isBlank() }

    // Part 1
    println(groups.map { findDistinctElementsInGroup(it) }.sum())

    // Part 2
    println(groups.map { findCommonElementsInGroup(it) }.sum())
}

private fun findDistinctElementsInGroup(it: Collection<String>): Int  =
    it.fold(HashSet()) { acc : MutableSet<Char>, string -> string.toCharArray().union(acc).toMutableSet() }.size

private fun findCommonElementsInGroup(it: Collection<String>): Int  =
    it.fold(HashSet(CharRange('a', 'z').toList())) {
            acc : MutableSet<Char>, string -> string.toCharArray().intersect(acc).toMutableSet()
    }.size