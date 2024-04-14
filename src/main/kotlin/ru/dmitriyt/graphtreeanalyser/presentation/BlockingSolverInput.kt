package ru.dmitriyt.graphtreeanalyser.presentation

import kotlin.math.min

sealed interface BlockingSolverInput {

    fun provide(): List<String>

    class StdInInputProvider(private val partSize: Int) : BlockingSolverInput {
        override fun provide(): List<String> {
            val graphs = mutableListOf<String>()
            repeat(partSize) {
                readlnOrNull()?.let { graphs.add(it) } ?: run {
                    return@repeat
                }
            }
            return graphs
        }
    }

    class DataInputProvider(
        private val partSize: Int,
        private val graphs: List<String>,
    ) : BlockingSolverInput {
        private var currentOffset: Int = 0

        @Synchronized
        override fun provide(): List<String> {
            if (currentOffset >= graphs.size) return emptyList()

            val taskGraphs = graphs.subList(currentOffset, min(currentOffset + partSize, graphs.size))
            currentOffset += partSize
            return taskGraphs
        }
    }
}