package ru.dmitriyt.graphtreeanalyser.presentation

import ru.dmitriyt.graphtreeanalyser.domain.algorithm.GraphCondition
import ru.dmitriyt.graphtreeanalyser.domain.algorithm.GraphInvariant

fun SolverQueryScope.selectGraphCounts(vararg graphInvariant: GraphInvariant): GraphInvariantSelection {
    return GraphInvariantSelection(graphInvariant.toList())
}

data class GraphInvariantSelection(
    val invariants: List<GraphInvariant>,
)

data class GraphInvariantSelectionWithData(
    val selection: GraphInvariantSelection,
    val inputProvider: () -> BlockingSolverInput,
)

data class GraphInvariantSelectionWithDataAndFilters(
    val selection: GraphInvariantSelection,
    val inputProvider: () -> BlockingSolverInput,
    val filter: GraphCondition,
)

fun generator(generator: String, n: Int): () -> BlockingSolverInput {
    return {
        BlockingSolverInput.GengProvider(generator, n)
    }
}

infix fun GraphInvariantSelection.from(inputProvider: () -> BlockingSolverInput): GraphInvariantSelectionWithData {
    return GraphInvariantSelectionWithData(this, inputProvider)
}

infix fun GraphInvariantSelectionWithData.where(graphCondition: GraphCondition): GraphInvariantSelectionWithDataAndFilters {
    return GraphInvariantSelectionWithDataAndFilters(
        selection = selection,
        inputProvider = inputProvider,
        filter = graphCondition,
    )
}

infix fun GraphInvariantSelectionWithDataAndFilters.or(
    graphCondition: GraphCondition
): GraphInvariantSelectionWithDataAndFilters {
    return copy(
        filter = filter or graphCondition,
    )
}

infix fun GraphInvariantSelectionWithDataAndFilters.and(
    graphCondition: GraphCondition
): GraphInvariantSelectionWithDataAndFilters {
    return copy(
        filter = filter and graphCondition,
    )
}

infix fun GraphCondition.or(graphCondition: GraphCondition) = ComplexOrCondition(this, graphCondition)
infix fun GraphCondition.and(graphCondition: GraphCondition) = ComplexAndCondition(this, graphCondition)

data class ComplexAndCondition(
    val graphConditions: List<GraphCondition>
) : GraphCondition {

    constructor(vararg condition: GraphCondition) : this(condition.toList())

    override fun check(graph6: String): Boolean {
        return graphConditions.all { it.check(graph6) }
    }
}

data class ComplexOrCondition(
    val graphConditions: List<GraphCondition>
) : GraphCondition {

    constructor(vararg condition: GraphCondition) : this(condition.toList())

    override fun check(graph6: String): Boolean {
        return graphConditions.any { it.check(graph6) }
    }
}