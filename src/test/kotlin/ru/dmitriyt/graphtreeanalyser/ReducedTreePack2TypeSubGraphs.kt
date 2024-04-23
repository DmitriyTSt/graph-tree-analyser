package ru.dmitriyt.graphtreeanalyser

import org.junit.jupiter.api.Test
import ru.dmitriyt.graphtreeanalyser.data.GraphCache
import ru.dmitriyt.graphtreeanalyser.data.TreeUtil
import ru.dmitriyt.graphtreeanalyser.data.algorithm.ReducedTreePack2Type
import ru.dmitriyt.graphtreeanalyser.data.algorithm.ReducedTreePackEquals1
import kotlin.test.assertEquals

class ReducedTreePack2TypeSubGraphs {

    // [:H`ESgp`^, :H`EKWTjB, :H`EKWT`B, :H`EKIOlB, :H`EKIO`B]

    @Test
    fun test() {
        val graph6 = ":H`ESgp`^"
        val type = ReducedTreePack2Type.solve(GraphCache[graph6])
        assertEquals(1, type)
    }
}