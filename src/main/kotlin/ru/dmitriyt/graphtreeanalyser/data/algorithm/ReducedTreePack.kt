package ru.dmitriyt.graphtreeanalyser.data.algorithm

import ru.dmitriyt.graphtreeanalyser.data.AbstractGraphCode
import ru.dmitriyt.graphtreeanalyser.data.GraphCache
import ru.dmitriyt.graphtreeanalyser.data.getAbstractCode
import ru.dmitriyt.graphtreeanalyser.data.getLeaves
import ru.dmitriyt.graphtreeanalyser.domain.algorithm.GraphInvariant

data object ReducedTreePack : GraphInvariant {
    override fun solve(graph6: String): Int {
        val treePack = mutableListOf<AbstractGraphCode>()

        val graph = GraphCache[graph6]
        val leaves = graph.getLeaves()

        leaves.forEach { vertex ->
            val newGraph = graph.filterVertexes { it != vertex }
            treePack.add(newGraph.getAbstractCode())
        }

        return treePack.toSet().size
    }
}