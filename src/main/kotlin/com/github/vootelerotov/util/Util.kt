package com.github.vootelerotov.util

import java.io.File
import java.lang.IllegalArgumentException
import java.math.BigInteger
import java.util.function.Predicate

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

        fun <T> split(collection: Collection<T>, predicate: Predicate<T>): Collection<Collection<T>> {
            val acc = ArrayList<Collection<T>>()

            var currentAcc = ArrayList<T>();
            for (t in collection) {
                if (predicate.test(t)) {
                    acc.add(currentAcc)
                    currentAcc = ArrayList()
                }
                else {
                    currentAcc.add(t)
                }
            }
            acc.add(currentAcc);

            return acc;
        }

         fun recursiveProofComputeIfAbsent(
            map: MutableMap<Int, BigInteger>,
            key: Int,
            function: () -> BigInteger
        ): BigInteger {
            val cachedValue = map[key]
            return if (cachedValue != null) {
                cachedValue
            } else {
                val value = function.invoke()
                map.putIfAbsent(key, value)
                value
            }
        }

        fun <T, S> crossProduct(first: Collection<T>, second: Collection<S>) = first.flatMap { x -> second.map { y -> x to y} }
    }
}

