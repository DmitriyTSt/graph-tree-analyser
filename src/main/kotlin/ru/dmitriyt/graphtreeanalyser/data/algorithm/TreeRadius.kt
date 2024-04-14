package ru.dmitriyt.graphtreeanalyser.data.algorithm

import ru.dmitriyt.graphtreeanalyser.data.GraphCache
import ru.dmitriyt.graphtreeanalyser.data.TreeUtil.bfs
import ru.dmitriyt.graphtreeanalyser.domain.algorithm.GraphInvariant

data object TreeRadius : GraphInvariant {

    override fun solve(graph6: String): Int {
        val graph = GraphCache[graph6]
        val e = graph.mapList.keys.map { bfs(graph, it).max() }
        return e.min()
    }
}