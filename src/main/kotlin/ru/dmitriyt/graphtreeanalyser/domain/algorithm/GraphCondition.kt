package ru.dmitriyt.graphtreeanalyser.domain.algorithm

import ru.dmitriyt.graphtreeanalyser.data.Graph
import ru.dmitriyt.graphtreeanalyser.data.GraphCache
import ru.dmitriyt.graphtreeanalyser.domain.model.GraphTaskInfo

interface GraphCondition {
    fun check(graph6: String): Boolean = check(GraphCache[graph6])
    fun check(graph: Graph): Boolean
}

fun GraphCondition.toGraphTaskInfo(): GraphTaskInfo {
    return GraphTaskInfo.Condition(this)
}