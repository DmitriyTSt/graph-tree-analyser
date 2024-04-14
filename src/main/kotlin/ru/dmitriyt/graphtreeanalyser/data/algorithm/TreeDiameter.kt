package ru.dmitriyt.graphtreeanalyser.data.algorithm

import ru.dmitriyt.graphtreeanalyser.data.GraphCache
import ru.dmitriyt.graphtreeanalyser.data.TreeUtil.bfs
import ru.dmitriyt.graphtreeanalyser.domain.algorithm.GraphInvariant

data object TreeDiameter : GraphInvariant {

    override fun solve(graph6: String): Int {
        val graph = GraphCache[graph6]
        val u = bfs(graph, 0).withIndex().maxBy { it.value }.index
        return bfs(graph, u).max()
    }
}