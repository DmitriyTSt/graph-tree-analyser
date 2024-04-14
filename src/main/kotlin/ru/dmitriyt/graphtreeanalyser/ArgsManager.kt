package ru.dmitriyt.graphtreeanalyser

class ArgsManager(_args: Array<String>) {
    private val args = _args.toList()

    val isMulti = args.contains("-m")
    val partSize = getParam("-p")?.toIntOrNull() ?: DEFAULT_PART_SIZE
    val needSaving = args.contains("-s")
    val isDebug = args.contains("-d")

    private fun getParam(key: String): String? {
        return args.indexOf(key).takeIf { it > -1 }?.let { args.getOrNull(it + 1) }
    }

    companion object {
        const val DEFAULT_PART_SIZE = 64
    }
}