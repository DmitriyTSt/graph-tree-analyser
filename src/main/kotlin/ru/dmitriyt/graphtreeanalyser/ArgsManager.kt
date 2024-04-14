package ru.dmitriyt.graphtreeanalyser

import ru.dmitriyt.graphtreeanalyser.ArgsManager.Companion.DEFAULT_PART_SIZE

class ArgsManager(_args: Array<String>) {
    private val args = _args.toList()

    fun parse(): Args {
        return Args(
            n = n,
            isMulti = isMulti,
            partSize = partSize,
        )
    }

    private val isMulti: Boolean get() = args.contains("-m")
    private val partSize: Int get() = getParam("-p")?.toIntOrNull() ?: DEFAULT_PART_SIZE
    private val n: Int get() = getParam("-n")?.toIntOrNull() ?: error("-n param is required")

    private fun getParam(key: String): String? {
        return args.indexOf(key).takeIf { it > -1 }?.let { args.getOrNull(it + 1) }
    }

    companion object {
        const val DEFAULT_PART_SIZE = 64
    }
}

class Args(
    val n: Int,
    val isMulti: Boolean = false,
    val partSize: Int = DEFAULT_PART_SIZE,
)