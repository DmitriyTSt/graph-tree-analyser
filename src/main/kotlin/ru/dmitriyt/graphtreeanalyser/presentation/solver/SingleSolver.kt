package ru.dmitriyt.graphtreeanalyser.presentation.solver

import ru.dmitriyt.graphtreeanalyser.domain.model.GraphResult
import ru.dmitriyt.graphtreeanalyser.domain.model.GraphTaskInfo
import ru.dmitriyt.graphtreeanalyser.domain.model.Task
import ru.dmitriyt.graphtreeanalyser.domain.model.TaskResult
import ru.dmitriyt.graphtreeanalyser.domain.solver.TaskSolver

/**
 * Однопоточный вычислитель
 */
class SingleSolver(private val graphTaskInfo: GraphTaskInfo) : TaskSolver {

    override fun run(inputProvider: () -> Task, resultHandler: (TaskResult) -> Unit, onFinish: () -> Unit) {
        var task = inputProvider()
        var graphs = getGraphsFromTask(task)
        while (task != Task.EMPTY) {
            resultHandler(
                when (graphTaskInfo) {
                    is GraphTaskInfo.Invariant -> TaskResult.Invariant(
                        task.id,
                        graphs.size,
                        graphs.map { GraphResult(it, graphTaskInfo.task.solve(it)) },
                    )
                    is GraphTaskInfo.Condition -> TaskResult.Graphs(
                        task.id,
                        graphs.size,
                        graphs.filter { graphTaskInfo.task.check(it) },
                    )
                }

            )
            task = inputProvider()
            graphs = getGraphsFromTask(task)
        }
        onFinish()
    }

    private fun getGraphsFromTask(task: Task): List<String> {
        val list = task.graphs
        return list
    }
}