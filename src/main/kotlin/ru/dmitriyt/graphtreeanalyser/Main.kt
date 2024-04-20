package ru.dmitriyt.graphtreeanalyser

import kotlinx.coroutines.runBlocking
import ru.dmitriyt.graphtreeanalyser.data.algorithm.ReducedTreePack2Type
import ru.dmitriyt.graphtreeanalyser.data.algorithm.ReducedTreePackEquals2
import ru.dmitriyt.graphtreeanalyser.presentation.graphs

fun main(rawArgs: Array<String>): Unit = runBlocking {
//    repeat(20) {
//        val args = ArgsManager(rawArgs).parse()
    val args = Args(
        n = 9,
        isMulti = 9 > 10,
    )

    graphs(n = args.n) {
        multiThreadingEnabled = args.isMulti
        generate(generator = "gentreeg").filter(ReducedTreePackEquals2).apply {
            calculateInvariant(ReducedTreePack2Type)
        }
    }
//    }
}

