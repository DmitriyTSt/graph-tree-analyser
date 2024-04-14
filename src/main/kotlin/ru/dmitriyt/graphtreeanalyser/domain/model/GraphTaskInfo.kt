package ru.dmitriyt.graphtreeanalyser.domain.model

import ru.dmitriyt.graphtreeanalyser.domain.algorithm.GraphCondition
import ru.dmitriyt.graphtreeanalyser.domain.algorithm.GraphInvariant

sealed class GraphTaskInfo {
    data class Invariant(
        val task: GraphInvariant,
    ) : GraphTaskInfo()

    data class Condition(
        val task: GraphCondition,
    ) : GraphTaskInfo()
}