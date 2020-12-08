package com.github.vootelerotov.aoc2020

import com.github.vootelerotov.util.Util

fun main(args: Array<String>) {
    val lines = Util.readFileFromArgs(args);

    val bags: MutableMap<Pair<String, String>, Bag> = HashMap()

    val bagsToRules = lines.map { line ->
        val split = line.split("contain")
        if (split.size != 2) {
            throw IllegalArgumentException("Splitting by contain failed $line")
        }
        split[0].trim() to split[1].trim()
    }

   bagsToRules
        .map { (x, y) -> getBag(bags, x) to y }
        .forEach{ (bag, rawRules) -> bag.rules.addAll(parseRules(rawRules, bags)) }


    val shinyBag = bags[(Pair("shiny", "gold"))] ?: throw IllegalArgumentException("No shiny gold bag!")

    // Part 1
    println(canContain(bags.values, shinyBag))

    // Part 2
    println(containsHowMany(shinyBag) - 1)

}

fun containsHowMany(bag: Bag): Long {
    return bag.rules
        .map { (quantity, innerBag) -> quantity.toLong() * (containsHowMany(innerBag)) }
        .fold(1) { x, y  -> x?.plus(y) } ?: throw IllegalArgumentException("No bag in rules")
}

fun canContain(bags: Collection<Bag>, bag: Bag): Int {
    val cache : MutableMap<Bag, Boolean> = HashMap()

    return bags.filter { canContain(it, bag, cache) }.count()
}

fun canContain(bag: Bag, targetBag: Bag, cache: MutableMap<Bag, Boolean>) : Boolean {
    if (bag == targetBag) {
        return false
    }
    val cachedValue = cache[bag]
    if (cachedValue != null) {
        return cachedValue
    }
    val rules = bag.rules

    return rules.any { ( _, innerBag) -> innerBag == targetBag || canContain(innerBag, targetBag, cache)}
}

fun parseRules(rawRules: String, bags: MutableMap<Pair<String, String>, Bag>): List<Rule> {
    return rawRules.trim('.').split(',')
        .filter { x -> x != "no other bags" }
        .map { rawRule ->
            val trimmed = rawRule.trim()
            val numberToDesc = trimmed.indexOf(' ')
            if (numberToDesc == -1) {
                throw IllegalArgumentException("Unexpected rule $trimmed")
            }
            trimmed.take(numberToDesc).toInt() to getBag(bags, trimmed.substring(numberToDesc + 1))
        }
        .map { (number, innerBag) -> Rule(number, innerBag)  }

}

fun getBag(bags: MutableMap<Pair<String, String>, Bag>, input: String): Bag {
    val modifierAndColor = getModifierAndColor(input)
    val split = modifierAndColor.split(" ")
    if (split.size != 2) {
        throw IllegalArgumentException("Cannot split bag declaration: $modifierAndColor")
    }
    return bags.computeIfAbsent(split[0] to split[1]) { (x, y) -> Bag(x, y)}
}


private fun getModifierAndColor(input: String): String {
    val end = if (input.endsWith(" bags")) " bags" else " bag"
    if (input.substring(input.length - end.length) != end) {
        throw IllegalArgumentException("Unexpected format: $input, tried ending $end")
    }
    return input.substring(0, input.length - end.length)
}

data class Rule(val quantity: Int, val innerBag: Bag)

data class Bag(val colorModifier: String, val color: String) {
    val rules : MutableList<Rule> = ArrayList()
}
