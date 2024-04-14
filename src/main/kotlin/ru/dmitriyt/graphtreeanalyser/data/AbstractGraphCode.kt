package ru.dmitriyt.graphtreeanalyser.data

sealed class AbstractGraphCode {
    data class SingleComponent(val code: List<Int>) : AbstractGraphCode()
    data class MultiComponent(val codes: List<List<Int>>) : AbstractGraphCode()
}
