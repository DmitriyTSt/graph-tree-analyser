package ru.dmitriyt.graphtreeanalyser.data

import java.util.concurrent.ConcurrentHashMap

object GraphCache {

    private val cache = ConcurrentHashMap<String, Graph>()

    operator fun get(graph6: String): Graph {
        return cache[graph6] ?: run {
            val graph = Graph.fromSparse6(graph6)
            cache[graph6] = graph
            graph
        }
    }
}