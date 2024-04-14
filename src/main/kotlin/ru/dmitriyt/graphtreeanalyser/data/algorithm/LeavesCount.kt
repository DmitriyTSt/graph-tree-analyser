package ru.dmitriyt.graphtreeanalyser.data.algorithm

import ru.dmitriyt.graphtreeanalyser.data.GraphCache
import ru.dmitriyt.graphtreeanalyser.data.getLeaves
import ru.dmitriyt.graphtreeanalyser.domain.algorithm.GraphInvariant

data object LeavesCount : GraphInvariant {
    override fun solve(graph6: String): Int {
        val graph = GraphCache[graph6]
        return graph.getLeaves().size
    }
}