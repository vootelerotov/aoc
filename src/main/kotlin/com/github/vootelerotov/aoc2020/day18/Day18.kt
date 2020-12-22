package com.github.vootelerotov.aoc2020.day18

import com.github.vootelerotov.util.Util

fun main(args: Array<String>) {
    val lines = Util.readFileFromArgs(args)


    val tokens = lines.map { tokenize(it) }

    // Part 1
    val expressions = tokens.map { parseExpression(it) }

    println(expressions.map { calculateValue(it) }.sum())

    // Part 2

    val part2Expressions = tokens.map { parsePart2Expression(it) }

    println(part2Expressions.map { calculateValue(it) }.sum())

}

internal fun calculateValue(expression: Expression): Long =
    when(expression) {
        is SingleNumber -> expression.int.toLong()
        is CombinedExpression -> {
            val left = calculateValue(expression.left);
            val right = calculateValue(expression.right)
            when (expression.op) {
                Operator.PLUS -> left + right
                Operator.TIMES -> left * right
            }
        }
    }

internal fun parse(rawExpression: String): Expression =
    parseExpression(tokenize(rawExpression))

internal fun parsePart2(rawExpression: String): Expression =
    parsePart2Expression(tokenize(rawExpression));

internal fun parseExpression(tokens: List<Token>): Expression {
    if (tokens.isEmpty()) {
        throw IllegalStateException("Empty List?")
    }

    if (tokens.size == 1) {
        return SingleNumber((tokens.first() as Number).int)
    }

    val parser = parseOperation({ true }, { t -> removeParens(t) { parseExpression(it) } })
    return parser.invoke(tokens)
}


internal fun parsePart2Expression(tokens: List<Token>): Expression {
    if (tokens.isEmpty()) {
        throw IllegalStateException("Empty List?")
    }

    if (tokens.size == 1) {
        return SingleNumber((tokens.first() as Number).int)
    }

    val parser = parseOperation(Operator.TIMES, parseOperation(Operator.PLUS) { t -> removeParens(t) { parsePart2Expression(it) }})
    return parser.invoke(tokens)
}

internal fun removeParens(tokens: List<Token>, next: (List<Token>) -> Expression): Expression =
    when (tokens.first()) {
        is OpenParen -> next.invoke(tokens.subList(1, tokens.size - 1))
        else -> next.invoke(tokens)
    }

internal fun parseOperation(op: Operator, next: (List<Token>) -> Expression): (List<Token>) -> Expression =
    parseOperation(op::equals, next)


internal fun parseOperation(pred: (Operator) -> Boolean, next: (List<Token>) -> Expression): (List<Token>) -> Expression {
    return { tokens ->
            val operatorWithIndex = findLastOperator(tokens) { pred(it) }
            if (operatorWithIndex == null) {
                next.invoke(tokens)
            } else {
                val (index, operator ) = operatorWithIndex
                CombinedExpression(
                    parseOperation(pred, next).invoke(tokens.subList(0, index)),
                    operator,
                    parseOperation(pred, next).invoke(tokens.subList(index + 1, tokens.size))
                )
            }

    }
}

internal fun findMatchingOpenParen(i: Int, tokens: List<Token>): Int {
    var closeParentCount = 1
    var index = i
    while (closeParentCount != 0) {
        when (tokens[index--]) {
            OpenParen -> closeParentCount -= 1
            CloseParen -> closeParentCount += 1
        }
    }
    return index + 1
}

internal fun findLastOperator(tokens: List<Token>, pred: (Operator) -> Boolean) : Pair<Int, Operator>? {
    var index = tokens.size-1
    while (0 <= index ) {
        when (val token = tokens[index]) {
            is CloseParen -> index = findMatchingOpenParen(index - 1, tokens)
            is Operation -> if (pred.invoke(token.operation)) {
                return index  to token.operation
            }
        }
        index--
    }
    return null
}

internal fun tokenize(it: String): List<Token> {
    val digits = '0'..'9'
    return it.mapNotNull { when(it) {
        '+' -> Operation(Operator.PLUS)
        '*' -> Operation(Operator.TIMES)
        '(' -> OpenParen
        ')' -> CloseParen
        in digits -> Number(it.toString().toInt())
        else -> if (it.isWhitespace()) null else throw IllegalArgumentException("Unknown char")
    }}

}

internal sealed class Expression

internal data class SingleNumber(val int : Int) : Expression()

internal data class CombinedExpression(val left: Expression, val op: Operator, val right: Expression ): Expression()


internal sealed class Token

internal data class Number(val int: Int) : Token()

internal object OpenParen : Token()

internal object CloseParen: Token()

internal enum class Operator {
    PLUS, TIMES
}

internal data class Operation(val operation: Operator) : Token()