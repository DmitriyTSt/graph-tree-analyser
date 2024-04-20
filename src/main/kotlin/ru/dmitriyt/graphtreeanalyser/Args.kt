package ru.dmitriyt.graphtreeanalyser

class Args(
    val n: Int,
    val isMulti: Boolean = DEFAULT_IS_MULTI,
    val partSize: Int = DEFAULT_PART_SIZE,
    val useCache: Boolean = false,
) {
    companion object {
        const val DEFAULT_IS_MULTI = false
        const val DEFAULT_PART_SIZE = 64
    }
}