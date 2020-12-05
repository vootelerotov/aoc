package com.github.vootelerotov.aoc2020

import com.github.vootelerotov.util.Util
import java.lang.Exception
import java.lang.IllegalArgumentException

fun main(args: Array<String>) {
    val lines = Util.readFileFromArgs(args);
    val passports = parsePassportInfo(lines)

    val parsedPassports = passports.map { passportInfo ->
        passportInfo.map { rawEntry ->
            val entryElements = rawEntry.split(':')
            if (entryElements.size != 2) {
                throw IllegalArgumentException("Messed up entry")
            }
            entryElements[0] to entryElements[1]
        }.toMap()
    }

    // Part 1
    val partOneValidator = Part1Validator()

    parsedPassports.map { Passport(it) }.count { partOneValidator.isValid(it) }.let { println(it) }

    // Part 2

    val partTwoValidator = Part2Validator()

    parsedPassports.map { Passport(it) }.count { partTwoValidator.isValid(it) }.let { println(it) }

}

private fun parsePassportInfo(lines: Collection<String>): Set<List<String>> {
    val acc = lines.fold(Acc()) { acc, line ->
        if (line.isEmpty()) {
            acc.collector.add(acc.current)
            acc.current = ArrayList();
        } else {
            acc.current += line.split("\\s+".toRegex())
        }
        acc
    }
    acc.collector.add(acc.current);
    return acc.collector;
}

class Passport constructor(passportInfo: Map<String, String>) {
    val birthYear: String? = passportInfo["byr"]
    val issueYear: String? = passportInfo["iyr"]
    val expirationYear: String? = passportInfo["eyr"]
    val height: String? = passportInfo["hgt"]
    val hairColor: String? = passportInfo["hcl"]
    val eyeColor: String? = passportInfo["ecl"]
    val passportId: String? = passportInfo["pid"]
    val countryId: String? = passportInfo["cid"]
}

interface PassportValidator {
    fun isValid(passport: Passport): Boolean
}

private class Part1Validator : PassportValidator {

    override fun isValid(passport : Passport): Boolean {
        return passport.birthYear != null && passport.issueYear != null
                && passport.expirationYear != null && passport.height != null
                && passport.hairColor != null && passport.eyeColor != null
                && passport.passportId != null
    }
}


private class Part2Validator : PassportValidator{

    override fun isValid(passport: Passport): Boolean {
        return passport.birthYear?.toInt() in 1920..2002
                && passport.issueYear?.toInt() in 2010..2020
                && passport.expirationYear?.toInt() in 2020..2030
                && passport.height?.let { Height(it) }?.isValid() ?: false
                && passport.hairColor?.let {HairColor(it)}?.isValid() ?: false
                && passport.eyeColor != null && isValidEyecolor(passport.eyeColor)
                && passport.passportId != null && isValidPassport(passport.passportId)
    }

    private fun isValidPassport(passportId: String): Boolean {
        if (passportId.length != 9) {
            return false;
        }

        val numbers = CharRange('0', '9')
        return passportId.map { numbers.contains(it) }.all { it }
    }

    private fun isValidEyecolor(eyeColor: String): Boolean {
        return eyeColor in arrayOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")
    }

    private class Height constructor(str : String) {
        val unit : String = str.substring(str.length - 2)
        val value : Int?

        init {
            var localValue : Int?= null;
            try {
                localValue = str.substring(0, str.length - 2).toInt()
            }
            catch (e : Exception) {

            }
            value = localValue
        }


        fun isValid(): Boolean {
            if (value == null) {
                return false
            }
            if (unit == "cm" ) {
                return value in 150..193
            }
            else if (unit == "in") {
                return value in 59..76
            }
            return false
        }
    }

    private class HairColor(val rawValue : String) {

        val charRange = CharRange('a', 'f');
        val numRange = CharRange('0', '9')

        fun isValid(): Boolean {
            if (rawValue.length != 7) {
                return false;
            }
            if (rawValue[0] != '#') {
                return false
            }

            return rawValue.substring(1).map { x ->  charRange.contains(x) || numRange.contains(x)}.all { it }
        }

    }
}


private class Acc(var current: List<String> = ArrayList(), val collector: MutableSet<List<String>> = HashSet()) {

}

