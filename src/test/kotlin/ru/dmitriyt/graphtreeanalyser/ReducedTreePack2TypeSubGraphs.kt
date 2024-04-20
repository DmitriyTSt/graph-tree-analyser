package ru.dmitriyt.graphtreeanalyser

import org.junit.jupiter.api.Test
import ru.dmitriyt.graphtreeanalyser.data.GraphCache
import ru.dmitriyt.graphtreeanalyser.data.TreeUtil
import ru.dmitriyt.graphtreeanalyser.data.algorithm.ReducedTreePack2Type
import ru.dmitriyt.graphtreeanalyser.data.algorithm.ReducedTreePackEquals1

class ReducedTreePack2TypeSubGraphs {

    @Test
    fun test() {
        val graph6 = ":H`ESWTlV"
        val subGraphs = ReducedTreePack2Type.getProbSubGraphs(GraphCache[graph6])
        println(subGraphs)
        val graph1 = subGraphs[0]
        val graph2 = subGraphs[1]
        println("graph1 is RTP1 = ${ReducedTreePackEquals1.check(graph1)}")
        println("graph1 tree center = ${TreeUtil.getTreeCenterVertexes(graph1)}")
        println("graph2 is RTP1 = ${ReducedTreePackEquals1.check(graph2)}")
        println("graph2 tree center = ${TreeUtil.getTreeCenterVertexes(graph2)}")

        println("isPath1 = ${ReducedTreePack2Type.isPath(graph1, setOf(0, 1, 5))}")
        println("isPath2 = ${ReducedTreePack2Type.isPath(graph2, setOf(0, 1, 5))}")
        println("type = ${ReducedTreePack2Type.solve(graph6)}")
    }
}