package com.github.vootelerotov.aoc2020.day8

import com.github.vootelerotov.util.Util

fun main(args: Array<String>) {
    val lines = Util.readFileFromArgs(args)

    val opCodes = lines.map { toInstruction(it) }

    // Part 1
    println(run(opCodes))

    // Part 2
    println(runWithBranching(opCodes))
}

fun run(code: List<Instruction>): Int {
    if (code.isEmpty()) {
        return 0
    }

    val visited: MutableSet<Instruction> = HashSet()
    var acc = 0

    var index = 0
    var currentOpcode  = code[index]
    while (visited.add(currentOpcode)) {
        acc += currentOpcode.toAdd()
        index += currentOpcode.toJump()
        if (index == code.size) {
            return acc
        }
        currentOpcode = code[index]
    }
    return acc
}

fun runWithBranching(code: List<Instruction>, initialIndex: Int = 0, initialAcc: Int = 0, branched : Boolean = false ): Pair<TerminationStatus, Int> {
    if (code.isEmpty()) {
        return TerminationStatus.NORMAL to 0
    }

    val visited: MutableSet<Instruction> = HashSet()
    var acc = initialAcc

    var index = initialIndex
    var currentOpcode  = code[index]
    while (visited.add(currentOpcode)) {
        val alternativeOpcode = currentOpcode.convert()
        if (!branched && alternativeOpcode != null) {
            val newCode = code.toMutableList()
            newCode[index] = alternativeOpcode
            val result = runWithBranching(newCode, index, acc, true)
            if (result.first == TerminationStatus.NORMAL) {
                return result
            }
        }

        acc += currentOpcode.toAdd()
        index += currentOpcode.toJump()
        if (index == code.size) {
           return TerminationStatus.NORMAL to acc
        }
        currentOpcode = code[index]
    }
    return TerminationStatus.LOOP to acc
}

enum class TerminationStatus {
  NORMAL, LOOP
}

fun toInstruction(it: String): Instruction {
    val instruction = it.split(" ")
    return when (instruction[0]) {
        Opcode.NOP.inCode -> Nop(instruction[1].toInt())
        Opcode.ACC.inCode -> Acc(instruction[1].toInt())
        Opcode.JUMP.inCode -> Jmp(instruction[1].toInt())
        else -> throw IllegalArgumentException("Unknown code ${instruction[0]}!")
    }
}

enum class Opcode(val inCode: String) {
    NOP("nop"), JUMP("jmp"), ACC("acc")
}

interface Instruction {

    fun toAdd() : Int

    fun toJump() : Int

    fun convert() : Instruction?
}

private class Nop(private val value: Int) : Instruction {
    override fun toAdd(): Int = 0

    override fun toJump(): Int = 1

    override fun convert(): Instruction = Jmp(value);
}

private class Acc(val amount: Int) : Instruction {

    override fun toAdd(): Int = amount

    override fun toJump(): Int = 1

    override fun convert(): Instruction? = null;
}

private class Jmp(val step: Int) : Instruction {
    override fun toAdd(): Int = 0

    override fun toJump(): Int = step

    override fun convert(): Instruction = Nop(step);
}