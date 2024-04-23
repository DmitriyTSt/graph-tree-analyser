package ru.dmitriyt.graphtreeanalyser.domain.algorithm

import ru.dmitriyt.graphtreeanalyser.data.Graph
import ru.dmitriyt.graphtreeanalyser.data.GraphCache
import ru.dmitriyt.graphtreeanalyser.domain.Logger
import ru.dmitriyt.graphtreeanalyser.domain.model.GraphTaskInfo

interface GraphInvariant {
    fun solve(graph6: String): Int {
        Logger.i("\n")
        Logger.i(graph6)
        return solve(GraphCache[graph6])
    }

    fun solve(graph: Graph): Int
}

fun GraphInvariant.toGraphTaskInfo(): GraphTaskInfo {
    return GraphTaskInfo.Invariant(this)
}