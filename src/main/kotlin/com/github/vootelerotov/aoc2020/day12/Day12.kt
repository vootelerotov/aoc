package com.github.vootelerotov.aoc2020.day12

import com.github.vootelerotov.util.Util
import java.lang.IllegalArgumentException
import java.util.Collections.singletonList
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

fun main(args: Array<String>) {
    val lines = Util.readFileFromArgs(args)

    // Part 1

    val commands = parseCommandsForPart1(lines)

    val part1AbsoluteCommands = getAbsoluteCommands(commands, Direction.EAST)

    println(calculateManhattanDistance(part1AbsoluteCommands))

    // Part 2

    val wayPointCommands = parseCommandsForPart2(lines)

    val part2AbsoluteCommands = getAbsoluteCommands(wayPointCommands, WayPointLocation(1, 10))

    println(calculateManhattanDistance(part2AbsoluteCommands))

}

private enum class Turn {
    LEFT, RIGHT
}

private enum class Move {
    FORWARD
}

enum class Direction {
    NORTH, EAST, SOUTH, WEST
}

internal fun parseCommandsForPart1(lines: Collection<String>): List<Command<Direction>> {
    return lines.map { line ->
        val commandChar = line[0]
        val units = line.substring(1).toInt()
        when(commandChar) {
            'N' -> AbsoluteCommand(Direction.NORTH, units)
            'S' -> AbsoluteCommand(Direction.SOUTH, units)
            'E' -> AbsoluteCommand(Direction.EAST, units)
            'W' -> AbsoluteCommand(Direction.WEST, units)
            'L' -> TurnCommand(Turn.LEFT, units)
            'R' -> TurnCommand(Turn.RIGHT, units)
            'F' -> MoveCommand(Move.FORWARD, units)
            else -> throw IllegalArgumentException("$commandChar is not a known command")
        }
    }
}

internal fun parseCommandsForPart2(lines: Collection<String>): List<Command<WayPointLocation>> {
    return lines.map { line ->
        val commandChar = line[0]
        val units = line.substring(1).toInt()
        when(commandChar) {
            'N' -> WayPointMoveCommand(Direction.NORTH, units)
            'S' -> WayPointMoveCommand(Direction.SOUTH, units)
            'E' -> WayPointMoveCommand(Direction.EAST, units)
            'W' -> WayPointMoveCommand(Direction.WEST, units)
            'L' -> WayPointRotateCommand(Turn.LEFT, units)
            'R' -> WayPointRotateCommand(Turn.RIGHT, units)
            'F' -> MoveCommandBasedOnWaypoint(Move.FORWARD, units)
            else -> throw IllegalArgumentException("$commandChar is not a known command")
        }
    }
}

internal fun <T> getAbsoluteCommands(commands: List<Command<T>>, startingLocation: T) =
    commands.fold(startingLocation to ArrayList<AbsoluteCommand>()) { (currentDir, acc), cmd: Command<T> ->
        val (newDir, absCommands) = cmd.toAbsolute(currentDir)
        acc.addAll(absCommands)
        newDir to acc
    }.second

internal fun calculateManhattanDistance(absoluteCommands: ArrayList<AbsoluteCommand>) : Int {
    val (xFinal, yFinal) = absoluteCommands.fold(0 to 0) { (x, y), cmd ->
        when (cmd.dir) {
            Direction.NORTH -> x + cmd.units to y
            Direction.EAST -> x to y + cmd.units
            Direction.SOUTH -> x - cmd.units to y
            Direction.WEST -> x to y - cmd.units
        }
    }

    return xFinal.absoluteValue + yFinal.absoluteValue
}

private fun <T> transform(current: T, times: Int, turn: (T) -> T): T =
    if (times == 0) current else transform(turn.invoke(current), times - 1, turn)

private fun turnRight(currentDirection: Direction, times: Int) =
    transform(currentDirection, times) { dir -> turnRight(dir) }

private fun turnLeft(currentDirection: Direction, times: Int) =
    transform(currentDirection, times) { dir -> turnLeft(dir) }

private fun turnRight(currentDirection: Direction) =
    when (currentDirection) {
        Direction.NORTH -> Direction.EAST
        Direction.EAST -> Direction.SOUTH
        Direction.SOUTH -> Direction.WEST
        Direction.WEST -> Direction.NORTH
    }

private fun turnLeft(currentDirection: Direction) =
    when (currentDirection) {
        Direction.NORTH -> Direction.WEST
        Direction.WEST -> Direction.SOUTH
        Direction.SOUTH -> Direction.EAST
        Direction.EAST -> Direction.NORTH
    }


class AbsoluteCommand(val dir: Direction,  val units: Int) : Command<Direction> {

    override fun toAbsolute(current: Direction): Pair<Direction, List<AbsoluteCommand>> = current to singletonList(this)

    override fun toString(): String {
        return "AbsoluteCommand(dir=$dir, units=$units)"
    }
}

private class TurnCommand(private val turn: Turn, private val units: Int) : Command<Direction> {

    override fun toAbsolute(current: Direction): Pair<Direction, List<AbsoluteCommand>> =
        when (turn) {
            Turn.LEFT -> turnLeft(current, units / 90) to emptyList()
            Turn.RIGHT -> turnRight(current, units / 90) to emptyList()
        }
}

private class MoveCommand(private val relativeDirection: Move, private val units: Int) : Command<Direction> {

    override fun toAbsolute(current: Direction): Pair<Direction, List<AbsoluteCommand>> =
        when (relativeDirection) {
            Move.FORWARD -> current to singletonList(AbsoluteCommand(current, units))
        }
}


private class WayPointMoveCommand(private val dir : Direction, private val units: Int): Command<WayPointLocation>
{
    override fun toAbsolute(current: WayPointLocation): Pair<WayPointLocation, List<AbsoluteCommand>>  {
        val (currentX, currentY) = current
        val newWayPointLocation = when(dir) {
            Direction.NORTH -> WayPointLocation(currentX + units, currentY)
            Direction.EAST -> WayPointLocation(currentX, currentY + units)
            Direction.SOUTH -> WayPointLocation(currentX - units, currentY)
            Direction.WEST -> WayPointLocation(currentX, currentY - units)
        }
        return newWayPointLocation to emptyList()
    }
}

private fun rotateWayPointLeft(current : WayPointLocation): WayPointLocation =
    WayPointLocation(current.y, current.x.times(-1))

private fun rotateWayPointRight(current: WayPointLocation): WayPointLocation =
    WayPointLocation(current.y.times(-1),current.x
)

private fun rotateWayPointLeft(current: WayPointLocation, times: Int) =
    transform(current, times) {wayPoint -> rotateWayPointLeft(wayPoint) }

private fun rotateWayPointRight(current: WayPointLocation, times: Int) =
    transform(current, times) {wayPoint -> rotateWayPointRight(wayPoint)}

private class WayPointRotateCommand(private val turn: Turn, private val units: Int): Command<WayPointLocation> {
    override fun toAbsolute(current: WayPointLocation): Pair<WayPointLocation, List<AbsoluteCommand>> {
        val times = units / 90
        return when (turn) {
            Turn.LEFT -> rotateWayPointLeft(current, times)
            Turn.RIGHT -> rotateWayPointRight(current, times)
        } to emptyList()
    }
}

private class MoveCommandBasedOnWaypoint(private val moveDirection: Move, private val units: Int) : Command<WayPointLocation> {
    override fun toAbsolute(current: WayPointLocation): Pair<WayPointLocation, List<AbsoluteCommand>> {
        return when(moveDirection) {
            Move.FORWARD -> current to arrayListOf(
                AbsoluteCommand(if (current.x > 0) Direction.NORTH else Direction.SOUTH, current.x.absoluteValue * units),
                AbsoluteCommand(if (current.y > 0) Direction.EAST else Direction.WEST, current.y.absoluteValue * units)
            )
        }
    }
}

data class WayPointLocation(val x: Int, val y: Int)

interface Command<T> {

    fun toAbsolute(current: T): Pair<T, Collection<AbsoluteCommand>>
}

