package ru.dmitriyt.graphtreeanalyser.data.algorithm

import ru.dmitriyt.graphtreeanalyser.data.AbstractGraphCode
import ru.dmitriyt.graphtreeanalyser.data.Graph
import ru.dmitriyt.graphtreeanalyser.data.getAbstractCode
import ru.dmitriyt.graphtreeanalyser.data.getLeaves
import ru.dmitriyt.graphtreeanalyser.domain.algorithm.GraphInvariant

data object ReducedTreePack : GraphInvariant {

    override fun solve(graph: Graph): Int {
        val treePack = mutableListOf<AbstractGraphCode>()

        val leaves = graph.getLeaves()

        leaves.forEach { vertex ->
            val newGraph = graph.filterVertexes { it != vertex }
            treePack.add(newGraph.getAbstractCode())
        }

        return treePack.toSet().size
    }
}