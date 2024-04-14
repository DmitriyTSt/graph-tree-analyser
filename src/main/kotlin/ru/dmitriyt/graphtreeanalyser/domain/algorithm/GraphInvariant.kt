package ru.dmitriyt.graphtreeanalyser.domain.algorithm

import ru.dmitriyt.graphtreeanalyser.domain.model.GraphTaskInfo

interface GraphInvariant {
    fun solve(graph6: String): Int
}

fun GraphInvariant.toGraphTaskInfo(): GraphTaskInfo {
    return GraphTaskInfo.Invariant(this)
}