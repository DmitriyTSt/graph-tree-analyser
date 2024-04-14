package ru.dmitriyt.graphtreeanalyser.domain

import ru.dmitriyt.graphtreeanalyser.domain.model.GraphResult

sealed interface SolveResult {
    data class Condition(
        val graphs: List<String>,
    ) : SolveResult

    data class Invariant(
        val invariants: List<GraphResult>,
    ) : SolveResult
}