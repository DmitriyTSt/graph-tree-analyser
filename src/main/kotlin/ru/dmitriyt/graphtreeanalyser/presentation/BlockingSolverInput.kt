package ru.dmitriyt.graphtreeanalyser.presentation

import ru.dmitriyt.graphtreeanalyser.domain.Logger
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlin.math.min

sealed interface BlockingSolverInput {

    fun read(partSize: Int): List<String>

    class GengProvider(generator: String, n: Int) : BlockingSolverInput {

        private val commandList = listOfNotNull(
            "./$generator",
            n.toString(),
        ).apply {
            Logger.d("generate command: ${this.joinToString(" ")}")
        }

        private val process = ProcessBuilder(*commandList.toTypedArray())
            .directory(File(System.getProperty("user.dir"))).start()

        private val reader = BufferedReader(
            InputStreamReader(
                process.inputStream
            )
        )

        override fun read(partSize: Int): List<String> {
            val graphs = mutableListOf<String>()
            repeat(partSize) {
                val mayBeGraph = synchronized(this) { reader.readLine() }
                mayBeGraph?.takeIf { !it.startsWith(">") }?.takeIf { it.isNotEmpty() }?.let { graphs.add(it) } ?: run {
                    return@repeat
                }
            }
            return graphs
        }
    }

    class DataInputProvider(
        private val graphs: List<String>,
    ) : BlockingSolverInput {

        private var currentOffset: Int = 0

        @Synchronized
        override fun read(partSize: Int): List<String> {
            if (currentOffset >= graphs.size) return emptyList()

            val taskGraphs = graphs.subList(currentOffset, min(currentOffset + partSize, graphs.size))
            currentOffset += partSize
            return taskGraphs
        }


    }
}