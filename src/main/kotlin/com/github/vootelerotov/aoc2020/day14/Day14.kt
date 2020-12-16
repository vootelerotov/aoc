package com.github.vootelerotov.aoc2020.day14

import com.github.vootelerotov.util.Util
import java.lang.IllegalStateException
import java.util.*
import java.util.Collections.singletonList
import kotlin.IllegalArgumentException
import kotlin.collections.HashMap

fun main(args: Array<String>) {

    val lines = Util.readFileFromArgs(args)

    // Part 1

    val instructions = parseInstructions(lines)

    val finalState = instructions.fold(State(Mask("X".repeat(36)), HashMap())) {
        state, instruction -> interpretPart1(state, instruction)
    }

    println(finalState.memory.values.map { toLong(it) }.sum())

    // Part 2

    val part2FinalState = instructions.fold(State(Mask("X".repeat(36)), HashMap())) {
            state, instruction -> interpretPart2(state, instruction)
    }

    println(part2FinalState.memory.values.map { toLong(it) }.sum())

}

private fun parseInstructions(lines: Collection<String>): List<Instruction> {
    val memRegex = Regex("mem\\[(\\d+)] = (\\d+)")

    return lines.map { line ->
        if (line.startsWith("mask = ")) {
            Mask(line.substring("mask = ".length))
        }
        else {
            val (address, value) = memRegex.find(line)!!.destructured
            StateModification(to36Int(address), to36Int(value))
        }
    }
}

private fun to36Int(value: String): Int36 {
    return to36Int(value.toLong(10).toString(2).toLong(2))
}

private fun to36Int(value: Long): Int36 {
    val as36BitInt = value.toString(2).padStart(36, '0')
    if (as36BitInt.length != 36) {
        throw IllegalArgumentException("Not 36 bit")
    }
    return Int36(as36BitInt.map { it == '1' }.toTypedArray())

}

private fun toLong(value: Int36): Long =
    value.bits.map { if (it) '1' else '0' }.joinToString(separator = "").toLong(2)


private fun interpretPart1(state: State, inst: Instruction): State
   = when(inst) {
        is Mask -> State(inst, state.memory)
        is StateModification -> {
            val copyOfMemory = state.memory.toMutableMap()
            copyOfMemory[inst.address] = maskValue(state.mask, inst.value)
            State(state.mask, copyOfMemory)
        }
    }

private fun maskValue(mask: Mask, value: Int36): Int36 =
    value.bits.zip(mask.mask)
        .map { (value, maskBit) ->
            when (maskBit) {
                MaskValues.ZERO -> false
                MaskValues.ONE -> true
                MaskValues.X -> value
            }
        }
        .toTypedArray()
        .let { Int36(it) }

private fun interpretPart2(state: State, inst: Instruction): State =
    when (inst) {
        is Mask -> State(inst, state.memory)
        is StateModification -> {
            val newMemory = state.memory.toMutableMap()
            findAddresses(inst.address, state.mask).forEach { newMemory[it] = inst.value }
            State(state.mask, newMemory)
        }
    }

private fun findAddresses(address: Int36, mask: Mask): List<Int36> =
    findAddresses(address.bits.zip(mask.mask)).map { it.reversed() }.map { Int36(it.toTypedArray()) }

private fun findAddresses(addressBitsWithMask: List<Pair<Boolean, MaskValues>>): List<MutableList<Boolean>> {
    if (addressBitsWithMask.isEmpty()) {
        return singletonList(ArrayList())
    }
    val tails = findAddresses(addressBitsWithMask.drop(1))

    val (valueBit, maskBit) = addressBitsWithMask.first()

    return when (maskBit) {
        MaskValues.ZERO -> tails.map {
            it.add(valueBit)
            it
        }
        MaskValues.ONE -> tails.map {
            it.add(true)
            it
        }
        MaskValues.X -> tails.flatMap {
            val copy = it.toMutableList();
            it.add(true)
            copy.add(false)
            listOf(it, copy)
        }
    }
}


private data class State(val mask: Mask, val memory: Map<Int36, Int36>)

private enum class MaskValues {
    ZERO, ONE, X
}

private sealed class Instruction

private class Mask(asString: String) : Instruction() {

      val mask : List<MaskValues> = asString.map { when(it) {
          '0' -> MaskValues.ZERO
          '1' -> MaskValues.ONE
          'X' -> MaskValues.X
          else -> throw IllegalArgumentException("Not suitable mask string: $asString")
      } }
}

private data class StateModification(val address: Int36, val value: Int36) : Instruction()

private data class Int36(val bits: Array<Boolean>) {

    init {
        if (bits.size != 36) {
            throw IllegalStateException("Not 36 bits")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Int36

        if (!bits.contentEquals(other.bits)) return false

        return true
    }

    override fun hashCode(): Int {
        return bits.contentHashCode()
    }
}