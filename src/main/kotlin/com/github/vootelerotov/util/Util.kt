package com.github.vootelerotov.util

import java.io.File
import java.lang.IllegalArgumentException

class Util {

    companion object {

         fun readFileFromArgs(args: Array<String>): Collection<String> {
            if (args.size != 1) {
                throw IllegalArgumentException("Give one input for input file path")
            }
            val file = File(args[0])
            if (!file.exists()) {
                throw IllegalArgumentException("File with given path does not exsist")
            }
            return file.readLines();
        }
    }
}