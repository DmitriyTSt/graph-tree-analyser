package ru.dmitriyt.graphtreeanalyser.data.repository

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import ru.dmitriyt.graphtreeanalyser.domain.SolveResult
import ru.dmitriyt.graphtreeanalyser.domain.model.GraphResult
import ru.dmitriyt.graphtreeanalyser.domain.model.GraphTaskInfo
import java.io.File

object SolveRepository {

    private val solveStorage = File("results").apply {
        if (!exists()) {
            mkdir()
        }
    }

    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun get(graphTaskInfo: GraphTaskInfo, n: Int): SolveResult? {
        val file = getSolveFile(graphTaskInfo, n)
        if (!file.exists()) return null
        return runCatching {
            when (graphTaskInfo) {
                is GraphTaskInfo.Condition -> {
                    val typeToken = object : TypeToken<List<String>>() {}.type
                    SolveResult.Condition(
                        graphs = gson.fromJson(file.readLines().joinToString("\n"), typeToken),
                    )
                }
                is GraphTaskInfo.Invariant -> {
                    val typeToken = object : TypeToken<Map<String, Int>>() {}.type
                    SolveResult.Invariant(
                        invariants = gson.fromJson<Map<String, Int>>(file.readLines().joinToString("\n"), typeToken)
                            .map { GraphResult(it.key, it.value) },
                    )
                }
            }
        }
            .onFailure {
                it.printStackTrace()
            }
            .getOrNull()
    }

    fun set(graphTaskInfo: GraphTaskInfo, n: Int, solveResult: SolveResult) {
        val file = getSolveFile(graphTaskInfo, n)
        file.createNewFile()
        when (solveResult) {
            is SolveResult.Condition -> {
                file.writeText(gson.toJson(solveResult.graphs))
            }
            is SolveResult.Invariant -> {
                file.writeText(gson.toJson(solveResult.invariants.map { it.graph6 to it.invariant }.toMap()))
            }
        }
    }

    private fun getSolveFile(graphTaskInfo: GraphTaskInfo, n: Int): File {
        val graphTaskName = when (graphTaskInfo) {
            is GraphTaskInfo.Invariant -> graphTaskInfo.task.toString()
            is GraphTaskInfo.Condition -> graphTaskInfo.task.toString()
        }
        return File(solveStorage, "${graphTaskName}_$n.json")
    }
}