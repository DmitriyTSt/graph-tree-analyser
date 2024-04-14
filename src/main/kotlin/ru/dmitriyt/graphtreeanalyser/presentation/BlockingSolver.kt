package ru.dmitriyt.graphtreeanalyser.presentation

import ru.dmitriyt.graphtreeanalyser.domain.model.GraphResult
import ru.dmitriyt.graphtreeanalyser.domain.model.GraphTaskInfo
import ru.dmitriyt.graphtreeanalyser.domain.model.Task
import ru.dmitriyt.graphtreeanalyser.domain.model.TaskResult
import ru.dmitriyt.graphtreeanalyser.presentation.solver.MultiThreadSolver
import ru.dmitriyt.graphtreeanalyser.presentation.solver.SingleSolver
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration.Companion.milliseconds

class BlockingSolver(
    private val isMulti: Boolean,
    private val input: BlockingSolverInput,
    private val graphTaskInfo: GraphTaskInfo,
) {

    private val taskId = AtomicInteger(0)
    private val total = AtomicInteger(0)
    private var isStartFinishing = AtomicBoolean(false)
    private val processedGraphs = AtomicInteger(0)
    private val lock = Object()
    private val taskResults = mutableListOf<TaskResult>()

    private var startTime = 0L
    private var endTime = 0L

    suspend fun solve(): Result = suspendCoroutine {
        val solver = if (isMulti) {
            MultiThreadSolver(graphTaskInfo)
        } else {
            SingleSolver(graphTaskInfo)
        }
        startTime = System.currentTimeMillis()
        solver.run(
            inputProvider = {
                val graphs = input.provide()
                if (graphs.isNotEmpty()) {
                    val newTaskId = taskId.incrementAndGet()
                    total.getAndAdd(graphs.size)
                    Task(
                        id = newTaskId,
                        graphs = graphs,
                    )
                } else {
                    Task.EMPTY
                }
            },
            resultHandler = { taskResult ->
                processedGraphs.getAndAdd(taskResult.processedGraphs)
                synchronized(lock) {
                    taskResults.add(taskResult)
                }
            },
            onFinish = {
                if (total.get() == processedGraphs.get() && !isStartFinishing.getAndSet(true)) {
                    endTime = System.currentTimeMillis()
                    println(
                        """
                        ----------
                        Task $graphTaskInfo
                        Processed graphs: ${total.get()}
                        Time passed: ${(endTime - startTime).milliseconds}
                        ----------
                    """.trimIndent()
                    )
                    val result = when (graphTaskInfo) {
                        is GraphTaskInfo.Condition -> {
                            Result.Condition(
                                graphs = taskResults.filterIsInstance<TaskResult.Graphs>().flatMap { it.graphs },
                            )
                        }
                        is GraphTaskInfo.Invariant -> {
                            Result.Invariant(
                                invariants = taskResults.filterIsInstance<TaskResult.Invariant>().flatMap { it.results },
                            )
                        }
                    }
                    it.resume(result)
                }
            }
        )
    }

    sealed interface Result {
        data class Condition(
            val graphs: List<String>,
        ) : Result

        data class Invariant(
            val invariants: List<GraphResult>,
        ) : Result
    }
}