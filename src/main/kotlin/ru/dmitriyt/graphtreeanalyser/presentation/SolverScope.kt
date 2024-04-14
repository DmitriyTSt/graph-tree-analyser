package ru.dmitriyt.graphtreeanalyser.presentation

import ru.dmitriyt.graphtreeanalyser.domain.Logger
import ru.dmitriyt.graphtreeanalyser.domain.SolveResult
import ru.dmitriyt.graphtreeanalyser.domain.algorithm.GraphCondition
import ru.dmitriyt.graphtreeanalyser.domain.algorithm.GraphInvariant
import ru.dmitriyt.graphtreeanalyser.domain.algorithm.toGraphTaskInfo

suspend fun graphs(n: Int, block: suspend SolveScope.() -> Unit) {
    SolveScope(n).block()
}

class SolverQueryScope

class SolveScope(
    private val n: Int,
) {
    var multiThreadingEnabled = false

    fun generate(generator: String): BlockingSolverInput {
        return BlockingSolverInput.GengProvider(generator = generator, n = n)
    }

    suspend fun run(queryProvider: SolverQueryScope.() -> GraphInvariantSelectionWithDataAndFilters) {
        val query = SolverQueryScope().queryProvider()
        query.inputProvider().filter(query.filter).apply {
            query.selection.invariants.forEach {
                calculateInvariant(it)
            }
        }
    }

    suspend fun BlockingSolverInput.filter(
        graphCondition: GraphCondition,
        partSize: Int = 1024,
    ): BlockingSolverInput {
        val conditionSolver = BlockingSolver(
            isMulti = multiThreadingEnabled,
            n = n,
            input = this,
            graphTaskInfo = graphCondition.toGraphTaskInfo(),
            partSize = partSize,
        )

        val conditionResult = conditionSolver.solve() as SolveResult.Condition

        Logger.i("----- N = $n $graphCondition  : ${conditionResult.graphs.size} -----")

        return BlockingSolverInput.DataInputProvider(conditionResult.graphs)
    }

    fun BlockingSolverInput.saveImages() {
        val graphDrawer = GraphDrawer()
        provide(Int.MAX_VALUE).forEach {
            graphDrawer.drawImage(it)
        }
    }

    suspend fun BlockingSolverInput.calculateInvariant(
        graphInvariant: GraphInvariant,
        partSize: Int = 256
    ): SolveResult.Invariant {
        val invariantSolver = BlockingSolver(
            isMulti = multiThreadingEnabled,
            n = n,
            input = this,
            graphTaskInfo = graphInvariant.toGraphTaskInfo(),
            partSize = partSize,
        )

        val invariantResult = invariantSolver.solve() as SolveResult.Invariant

        if (invariantResult.invariants.isNotEmpty()) {
            Logger.i("----- $graphInvariant value : count -----")
            val minInvariant = invariantResult.invariants.minOfOrNull { it.invariant }!!
            val maxInvariant = invariantResult.invariants.maxOfOrNull { it.invariant }!!
            for (i in minInvariant..maxInvariant) {
                Logger.i("$graphInvariant $i\t:\t${invariantResult.invariants.count { it.invariant == i }}")
            }
        }

        return invariantResult
    }
}
