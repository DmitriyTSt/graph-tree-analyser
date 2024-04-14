package ru.dmitriyt.graphtreeanalyser.domain.model

data class Task(
    val id: Int,
    val graphs: List<String>,
) {
    companion object {
        val EMPTY = Task(-1, emptyList())
    }
}