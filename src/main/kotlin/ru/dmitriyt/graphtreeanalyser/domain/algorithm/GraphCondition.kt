package ru.dmitriyt.graphtreeanalyser.domain.algorithm

import ru.dmitriyt.graphtreeanalyser.domain.model.GraphTaskInfo

interface GraphCondition {
    fun check(graph6: String): Boolean
}

fun GraphCondition.toGraphTaskInfo(): GraphTaskInfo {
    return GraphTaskInfo.Condition(this)
}