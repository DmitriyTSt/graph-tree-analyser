package ru.dmitriyt.graphtreeanalyser.domain

private const val IS_DEBUG = true

private const val ANSI_RESET = "\u001B[0m"
private const val ANSI_RED = "\u001B[31m"

object Logger {

    fun d(any: Any) {
        if (IS_DEBUG) {
            println(any)
        }
    }

    fun e(any: Any) {
        println(ANSI_RED + any + ANSI_RESET)
    }

    fun i(any: Any) {
        println(any)
    }
}