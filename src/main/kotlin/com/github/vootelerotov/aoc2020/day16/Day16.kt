package com.github.vootelerotov.aoc2020.day16

import com.github.vootelerotov.util.Util

fun main(args: Array<String>) {
    val lines = Util.readFileFromArgs(args);

    val (rules, myTicket, tickets) = parseInfo(lines)

    // Part 1

    val sum = tickets.map { findSumOnInvalidFields(it, rules) }.sum();
    println(sum)

    // Part 2

    println(tickets.size)
    val validTickets = tickets.filter { ticket -> ticket.elements.all { number -> rules.any { ruleMatches(it, number) } } }
    println(validTickets.size)

    val matchingRules = findMatchingRules(validTickets, rules.toSet())

    val allDepartureFieldsMultiplied = matchingRules
        .asSequence()
        .filter { (_, rule) -> rule.name.startsWith("departure") }
        .map { (index, _) -> index }
        .map { myTicket.elements[it] }
        .map { it.toLong() }
        .reduce { acc, value -> acc * value }

    println(allDepartureFieldsMultiplied)

}

internal fun findMatchingRules(tickets: List<Ticket>, rules: Set<Rule>, solvedIndices: MutableSet<Int> = HashSet()) : MutableSet<Pair<Int, Rule>> {
    if (rules.isEmpty()) {
        return HashSet()
    }

    val matchedRules = tickets.asSequence().map { it.elements.withIndex() }.map { elements ->
        elements
            .filter { it.index !in solvedIndices }
            .map { it.index to matchingRules(it.value, rules).toSet() }

    }

    val intersectedRules =  matchedRules.reduce{ acc, x -> acc.zip(x).map { (first, second) ->
        val (index, firstRules) = first
        val (_, secondRules) = second

        index to firstRules.intersect(secondRules)
    }}

    val solvedRules = intersectedRules
        .filter { (_, rules) -> rules.size == 1 }
        .map { (index, rules) -> index to rules.first() }

    val remainingRules = rules.toMutableSet()
    solvedRules.forEach {
        solvedIndices.add(it.first)
        remainingRules.remove(it.second)
    }

    val restOfMatchingRules = findMatchingRules(tickets, remainingRules, solvedIndices)
    restOfMatchingRules.addAll(solvedRules);

    return restOfMatchingRules

}

private fun matchingRules(number: Int, rules: Collection<Rule>): Collection<Rule> =
    rules.filter { ruleMatches(it, number) }

internal fun parseInfo(lines: Collection<String>): Triple<List<Rule>, Ticket, List<Ticket>> {
    val splitLines = Util.split(lines, String::isEmpty).toList()

    if (splitLines.size != 3) {
        throw IllegalArgumentException("Unexpected input")
    }

    val rules = parseRules(splitLines[0])

    val myTicket = parseTicket(splitLines[1].drop(1).first())

    val tickets = parseTickets(splitLines[2].drop(1))
    return Triple(rules, myTicket, tickets)
}

internal fun findSumOnInvalidFields(ticket: Ticket, rules: List<Rule>): Long
 = ticket.elements.filter { number -> !rules.any { ruleMatches(it, number) } }.map { it.toLong() }.sum()

private fun parseRules(rawRules: Collection<String>): List<Rule> {

    val regex = Regex("([^:]+): (\\d+)-(\\d+) or (\\d+)-(\\d+)")
    return rawRules.map {
        val (name, firstStart, firstEnd, secondStart, secondEnd) = regex.find(it)!!.destructured
        Rule(name, firstStart.toInt()..firstEnd.toInt() to secondStart.toInt()..secondEnd.toInt())
    }
}

private fun parseTickets(rawTickets: Collection<String>): List<Ticket> =
    rawTickets.map { parseTicket(it)}

private fun parseTicket(rawTicket: String): Ticket =
    rawTicket.split(',').map { it.toInt() }.let { Ticket(it) }

private fun ruleMatches(rule: Rule, value: Int) : Boolean =
    value in rule.ranges.first || value in rule.ranges.second

internal data class Rule(val name: String, val ranges: Pair<IntRange, IntRange>)

internal data class Ticket(val elements: List<Int>)
