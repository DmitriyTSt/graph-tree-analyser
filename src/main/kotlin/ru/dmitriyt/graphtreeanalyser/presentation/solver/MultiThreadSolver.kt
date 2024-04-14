package ru.dmitriyt.graphtreeanalyser.presentation.solver

import ru.dmitriyt.graphtreeanalyser.domain.model.GraphTaskInfo
import ru.dmitriyt.graphtreeanalyser.domain.model.Task
import ru.dmitriyt.graphtreeanalyser.domain.model.TaskResult
import ru.dmitriyt.graphtreeanalyser.domain.solver.TaskSolver
import kotlin.concurrent.thread

/**
 * Многопоточный вычислитель
 */
class MultiThreadSolver(private val graphTaskInfo: GraphTaskInfo) : TaskSolver {

    override fun run(inputProvider: () -> Task, resultHandler: (TaskResult) -> Unit, onFinish: () -> Unit) {
        val nCpu = Runtime.getRuntime().availableProcessors()
        val threads = IntRange(0, nCpu).map {
            thread {
                SingleSolver(graphTaskInfo).run(inputProvider, resultHandler, onFinish)
            }
        }
        threads.map { it.join() }
    }
}