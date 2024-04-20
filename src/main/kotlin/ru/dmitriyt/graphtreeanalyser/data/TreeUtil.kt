package ru.dmitriyt.graphtreeanalyser.data

fun Graph.getAbstractCode(): AbstractGraphCode {
    val components = getComponents()

    fun getTreeCode(tree: Graph): List<Int> {
        val center = TreeUtil.getTreeCenter(tree)
        return TreeUtil.getCanonicalCode(tree, center)
    }

    return if (components.size == 1) {
        AbstractGraphCode.SingleComponent(getTreeCode(components.first()))
    } else {
        AbstractGraphCode.MultiComponent(components.map { getTreeCode(it) }.sortedBy { it.toCodeString() })
    }
}

fun Graph.getComponents(): List<Graph> {
    val components = mutableListOf<List<Int>>()
    val vertexes = mapList.keys
    val visited = mutableMapOf<Int, Boolean>()
    vertexes.forEach { visited[it] = false }
    while (!visited.all { it.value }) {
        val componentVertexes = TreeUtil.dfsWithoutEdge(this, vertexes.first { visited[it] == false }, -1)
        componentVertexes.forEach {
            visited[it] = true
        }
        components.add(componentVertexes)
    }
    return components.map { componentVertexes ->
        Graph(
            mapList.filter { componentVertexes.contains(it.key) }.map { entry ->
                entry.key to entry.value.filter { componentVertexes.contains(it) }
            }.toMap()
        )
    }
}

object TreeUtil {

    fun bfs(g: Graph, source: Int): List<Int> {
        val d = MutableList(g.n) { Int.MAX_VALUE }
        d[source] = 0
        val q = ArrayDeque<Int>()
        q.addLast(source)
        while (q.isNotEmpty()) {
            val u = q.removeLast()
            g.mapList[u].orEmpty().forEach { v ->
                if (d[v] == Int.MAX_VALUE) {
                    d[v] = d[u] + 1
                    q.addLast(v)
                }
            }
        }
        return d
    }

    fun getTreeCenterVertexes(g: Graph): Set<Int> {
        val actualVertexes = g.mapList.keys.toMutableSet()
        while (actualVertexes.size > 2) {
            val vertexesToRemove = mutableListOf<Int>()
            g.mapList.forEach { (vertex, vertexes) ->
                if (actualVertexes.contains(vertex)) {
                    if (vertexes.count { actualVertexes.contains(it) } == 1) {
                        vertexesToRemove.add(vertex)
                    }
                }
            }
            vertexesToRemove.forEach { vertex ->
                actualVertexes.remove(vertex)
            }
        }
        return actualVertexes
    }

    fun getTreeCenter(g: Graph): Int {
        val actualVertexes = getTreeCenterVertexes(g)

        if (actualVertexes.size == 1) {
            return actualVertexes.first()
        }

        val center1 = actualVertexes.first()
        val center2 = actualVertexes.last()
        val graph1Vertexes = dfsWithoutEdge(g, center1, center2)
        val graph2Vertexes = dfsWithoutEdge(g, center2, center1)

        if (graph1Vertexes.size < graph2Vertexes.size) {
            return center1
        }
        if (graph2Vertexes.size < graph1Vertexes.size) {
            return center2
        }

        val graph1 = g.filterVertexes { graph1Vertexes.contains(it) }

        val graph2 = g.filterVertexes { graph2Vertexes.contains(it) }

        return if (getCanonicalCode(graph1, center1).toCodeString() >= getCanonicalCode(
                graph2,
                center2
            ).toCodeString()
        ) {
            center1
        } else {
            center2
        }
    }

    fun dfsWithoutEdge(g: Graph, start: Int, skip: Int): List<Int> {
        val visited = mutableMapOf<Int, Boolean>()

        fun dfs(u: Int) {
            visited[u] = true
            g.mapList[u]?.forEach { vertex ->
                if (vertex != skip && visited[vertex] != true) {
                    dfs(vertex)
                }
            }
        }

        dfs(start)

        return visited.filter { it.value }.keys.toList()
    }

    fun getCanonicalCode(g: Graph, center: Int): List<Int> = try {
        val codes = g.mapList.map { entry -> entry.key to mutableListOf<Int>() }.toMap().toMutableMap()
        val leaves = g.getLeaves()
        leaves.forEach { vertex ->
            codes[vertex]?.add(1)
        }
        val levels = dijkstra(g, center)
        val used = mutableListOf<Int>()
        g.mapList.keys.forEach { i ->
            if (codes[i]?.isEmpty() == true && i != center) {
                used.add(i)
                codeHelper(g, center, codes, i, null, used, levels)
            }
        }
        var count = 1
        repeat(g.mapList[center]!!.size) { i ->
            count += codes[g.mapList[center]!![i]]?.get(0) ?: 0
        }
        codes[center]?.add(count)
        val h = mutableListOf<List<Int>>()
        repeat(g.mapList[center]!!.size) { i ->
            h.add(codes[g.mapList[center]?.get(i)]!!)
        }
        h.sortBy { it.toCodeString() }
        h.forEach { hi ->
            hi.forEach { hij ->
                codes[center]?.add(hij)
            }
        }
        val code = mutableListOf<Int>()
        codes[center]?.forEach {
            code.add(it)
        }
        code
    } catch (e: Exception) {
        throw e
    }

    private fun codeHelper(
        g: Graph,
        center: Int,
        codes: MutableMap<Int, MutableList<Int>>,
        start: Int,
        parent: Int?,
        used: MutableList<Int>,
        levels: Map<Int, Int>
    ) {
        g.mapList[start]?.forEach { v ->
            if (v != center && codes[v]?.size == 0 && v != parent && !used.contains(v) && levels[v]!! >= levels[start]!!) {
                codeHelper(g, center, codes, v, parent, used, levels)
            }
        }

        var count = 1
        g.mapList[start]?.forEach { v ->
            if (v != parent && v != center && levels[v]!! >= levels[start]!!) {
                count += codes[v]?.get(0)!!
            }
        }

        codes[start]?.add(count)

        val h = mutableListOf<List<Int>>()
        g.mapList[start]?.forEach { v ->
            if (v != parent && v != center && levels[v]!! >= levels[start]!!) {
                h.add(codes[v].orEmpty())
            }
        }
        h.sortBy { it.toCodeString() }
        h.forEach { hi ->
            hi.forEach { hij ->
                codes[start]?.add(hij)
            }
        }
    }

    private fun dijkstra(g: Graph, center: Int): Map<Int, Int> {
        val used = mutableSetOf<Int>()
        val d = g.mapList.map { entry -> entry.key to Int.MAX_VALUE }.toMap().toMutableMap()
        d[center] = 0
        repeat(g.mapList.size) {
            var v: Int? = null
            g.mapList.keys.forEach { j ->
                if (!used.contains(j) && (v == null || d[j]!! < d[v]!!)) {
                    v = j
                }
            }
            if (v == null) {
                return d
            }
            used.add(v!!)
            g.mapList[v]?.forEach { j ->
                if (d[v]!! + 1 < d[j]!!) {
                    d[j] = d[v]!! + 1
                }
            }
        }
        return d
    }
}

fun List<Int>.toCodeString(): String {
    return map { 'a' + it }.joinToString("")
}

fun Graph.getLeaves(): List<Int> {
    val leaves = mutableListOf<Int>()
    mapList.forEach { (vertex, vertexes) ->
        if (vertexes.size == 1) {
            leaves.add(vertex)
        }
    }
    return leaves
}

