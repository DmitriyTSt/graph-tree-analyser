package ru.dmitriyt.graphtreeanalyser.domain

private const val IS_DEBUG = true

object Logger {

    fun d(any: Any) {
        if (IS_DEBUG) {
            println(any)
        }
    }

    fun i(any: Any) {
        println(any)
    }
}