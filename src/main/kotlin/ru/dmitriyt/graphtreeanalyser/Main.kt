package ru.dmitriyt.graphtreeanalyser

import kotlinx.coroutines.runBlocking
import ru.dmitriyt.graphtreeanalyser.data.algorithm.LeavesCount
import ru.dmitriyt.graphtreeanalyser.data.algorithm.ReducedTreePackEquals2
import ru.dmitriyt.graphtreeanalyser.data.algorithm.TreeDiameter
import ru.dmitriyt.graphtreeanalyser.data.algorithm.TreeRadius
import ru.dmitriyt.graphtreeanalyser.presentation.from
import ru.dmitriyt.graphtreeanalyser.presentation.generator
import ru.dmitriyt.graphtreeanalyser.presentation.graphs
import ru.dmitriyt.graphtreeanalyser.presentation.selectGraphCounts
import ru.dmitriyt.graphtreeanalyser.presentation.where

fun main(rawArgs: Array<String>): Unit = runBlocking {
    repeat(20) {
//        val args = ArgsManager(rawArgs).parse()
        val args = Args(
            n = it,
            isMulti = it > 10,
        )

        graphs(n = args.n) {
            multiThreadingEnabled = args.isMulti
//            generate(generator = "gentreeg").filter(ReducedTreePackEquals2).apply {
//                if (args.n <= 10) {
//                    saveImages()
//                }
//                calculateInvariant(LeavesCount)
//                calculateInvariant(TreeDiameter)
//                calculateInvariant(TreeRadius)
//            }

            run {
                selectGraphCounts(LeavesCount, TreeDiameter, TreeRadius) from
                    generator("gentreeg", args.n) where
                    ReducedTreePackEquals2
            }
        }
    }
}

