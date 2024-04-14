package ru.dmitriyt.graphtreeanalyser.domain.solver

import ru.dmitriyt.graphtreeanalyser.domain.model.Task
import ru.dmitriyt.graphtreeanalyser.domain.model.TaskResult

interface TaskSolver {
    fun run(
        inputProvider: () -> Task,
        resultHandler: (TaskResult) -> Unit,
        onFinish: () -> Unit,
    )
}