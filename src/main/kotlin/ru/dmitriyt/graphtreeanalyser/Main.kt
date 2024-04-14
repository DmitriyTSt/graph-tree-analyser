package ru.dmitriyt.graphtreeanalyser

import kotlinx.coroutines.runBlocking
import ru.dmitriyt.graphtreeanalyser.data.algorithm.ReducedTreePackEquals2
import ru.dmitriyt.graphtreeanalyser.domain.algorithm.toGraphTaskInfo
import ru.dmitriyt.graphtreeanalyser.presentation.BlockingSolver
import ru.dmitriyt.graphtreeanalyser.presentation.BlockingSolverInput

fun main(args: Array<String>): Unit = runBlocking {
    val argsManager = ArgsManager(args)
    val treeWithReducedTreePackEquals2Solver = BlockingSolver(
        isMulti = argsManager.isMulti,
        input = BlockingSolverInput.StdInInputProvider(partSize = argsManager.partSize),
        graphTaskInfo = ReducedTreePackEquals2.toGraphTaskInfo(),
    )

    val treeWithReducedTreePackEquals2 = treeWithReducedTreePackEquals2Solver.solve() as BlockingSolver.Result.Condition

    println("GRAPHS = ${treeWithReducedTreePackEquals2.graphs.size}")
}