package ru.dmitriyt.graphtreeanalyser.presentation

import ru.dmitriyt.graphtreeanalyser.data.repository.SolveRepository
import ru.dmitriyt.graphtreeanalyser.domain.Logger
import ru.dmitriyt.graphtreeanalyser.domain.SolveResult
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
    private val n: Int,
    private val input: BlockingSolverInput,
    private val graphTaskInfo: GraphTaskInfo,
    private val partSize: Int,
) {

    private val taskId = AtomicInteger(0)
    private val total = AtomicInteger(0)
    private var isStartFinishing = AtomicBoolean(false)
    private val processedGraphs = AtomicInteger(0)
    private val lock = Object()
    private val taskResults = mutableListOf<TaskResult>()

    private var startTime = 0L
    private var endTime = 0L

    suspend fun solve(): SolveResult = suspendCoroutine {
        val cachedResult: SolveResult? = null //SolveRepository.get(graphTaskInfo, n)
        if (cachedResult != null) {
            it.resume(cachedResult)
            return@suspendCoroutine
        }
        val solver = if (isMulti) {
            MultiThreadSolver(graphTaskInfo)
        } else {
            SingleSolver(graphTaskInfo)
        }
        startTime = System.currentTimeMillis()
        solver.run(
            inputProvider = {
                val graphs = input.read(partSize)
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
                    Logger.d(
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
                            SolveResult.Condition(
                                graphs = taskResults.filterIsInstance<TaskResult.Graphs>().flatMap { it.graphs },
                            )
                        }
                        is GraphTaskInfo.Invariant -> {
                            SolveResult.Invariant(
                                invariants = taskResults.filterIsInstance<TaskResult.Invariant>().flatMap { it.results },
                            )
                        }
                    }
                    SolveRepository.set(graphTaskInfo, n, result)
                    it.resume(result)
                }
            }
        )
    }
}