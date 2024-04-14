package ru.dmitriyt.graphtreeanalyser

class ArgsManager(rawArgs: Array<String>) {
    private val rawArgsList = rawArgs.toList()

    fun parse(): Args {
        return Args(
            n = n,
            isMulti = isMulti,
            partSize = partSize,
        )
    }

    private val isMulti: Boolean get() = rawArgsList.contains("-m")
    private val partSize: Int get() = getParam("-p")?.toIntOrNull() ?: Args.DEFAULT_PART_SIZE
    private val n: Int get() = getParam("-n")?.toIntOrNull() ?: error("-n param is required")

    private fun getParam(key: String): String? {
        return rawArgsList.indexOf(key).takeIf { it > -1 }?.let { rawArgsList.getOrNull(it + 1) }
    }
}
