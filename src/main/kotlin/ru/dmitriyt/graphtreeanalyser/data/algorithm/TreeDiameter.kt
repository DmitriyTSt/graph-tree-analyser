package ru.dmitriyt.graphtreeanalyser.data.algorithm

import ru.dmitriyt.graphtreeanalyser.data.Graph
import ru.dmitriyt.graphtreeanalyser.data.TreeUtil.bfs
import ru.dmitriyt.graphtreeanalyser.domain.algorithm.GraphInvariant

data object TreeDiameter : GraphInvariant {

    override fun solve(graph: Graph): Int {
        val u = bfs(graph, 0).withIndex().maxBy { it.value }.index
        return bfs(graph, u).max()
    }
}