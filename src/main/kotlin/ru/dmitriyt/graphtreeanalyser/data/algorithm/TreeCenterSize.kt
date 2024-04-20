package ru.dmitriyt.graphtreeanalyser.data.algorithm

import ru.dmitriyt.graphtreeanalyser.data.Graph
import ru.dmitriyt.graphtreeanalyser.data.TreeUtil
import ru.dmitriyt.graphtreeanalyser.domain.algorithm.GraphInvariant

data object TreeCenterSize : GraphInvariant {

    override fun solve(graph: Graph): Int {
        return TreeUtil.getTreeCenterVertexes(graph).size
    }
}