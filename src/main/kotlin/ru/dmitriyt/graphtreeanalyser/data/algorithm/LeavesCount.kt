package ru.dmitriyt.graphtreeanalyser.data.algorithm

import ru.dmitriyt.graphtreeanalyser.data.Graph
import ru.dmitriyt.graphtreeanalyser.data.getLeaves
import ru.dmitriyt.graphtreeanalyser.domain.algorithm.GraphInvariant

data object LeavesCount : GraphInvariant {

    override fun solve(graph: Graph): Int {
        return graph.getLeaves().size
    }
}