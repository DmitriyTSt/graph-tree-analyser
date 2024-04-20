package ru.dmitriyt.graphtreeanalyser.data.algorithm

import ru.dmitriyt.graphtreeanalyser.data.Graph
import ru.dmitriyt.graphtreeanalyser.domain.algorithm.GraphCondition
import ru.dmitriyt.graphtreeanalyser.domain.algorithm.GraphInvariant

open class AbstractCondition(private val graphInvariant: GraphInvariant, private val invariantValue: Int) : GraphCondition {

    override fun check(graph: Graph): Boolean {
        return graphInvariant.solve(graph) == invariantValue
    }
}
