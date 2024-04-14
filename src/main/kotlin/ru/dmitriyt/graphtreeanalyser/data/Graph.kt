package ru.dmitriyt.graphtreeanalyser.data

import kotlin.math.ceil
import kotlin.math.ln

data class Graph(
    val mapList: Map<Int, List<Int>>,
) {
    val n: Int = mapList.size

    companion object {
        fun fromSparse6(codeSparse6: String): Graph {
            val code = codeSparse6.substring(1)
            val byteReader = ByteReader6(code)

            val n = byteReader.getNumber()
            val k = ceil(ln(n.toDouble()) / ln(2.0)).toInt()
            val map = mutableMapOf<Int, MutableList<Int>>()
            repeat(n) {
                map[it] = mutableListOf()
            }

            var v = 0

            while (byteReader.haveBits(k + 1)) {
                val b = byteReader.getBit()
                val x = byteReader.getBits(k)


                if (b != 0) {
                    v++
                }

                if (v >= n) {
                    break
                }

                if (x > v) {
                    v = x
                } else {
                    map[x]?.add(v)
                    map[v]?.add(x)
                }
            }

            return Graph(map)
        }
    }

    fun filterVertexes(isAdd: (vertex: Int) -> Boolean): Graph {
        return Graph(
            mapList.filter { isAdd(it.key) }.map { entry ->
                entry.key to entry.value.filter { isAdd(it) }
            }.toMap()
        )
    }
}
