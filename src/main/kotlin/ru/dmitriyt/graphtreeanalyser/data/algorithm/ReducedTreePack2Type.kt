package ru.dmitriyt.graphtreeanalyser.data.algorithm

import ru.dmitriyt.graphtreeanalyser.data.AbstractGraphCode
import ru.dmitriyt.graphtreeanalyser.data.Graph
import ru.dmitriyt.graphtreeanalyser.data.TreeUtil
import ru.dmitriyt.graphtreeanalyser.data.getAbstractCode
import ru.dmitriyt.graphtreeanalyser.data.getLeaves
import ru.dmitriyt.graphtreeanalyser.domain.Logger
import ru.dmitriyt.graphtreeanalyser.domain.algorithm.GraphInvariant

data object ReducedTreePack2Type : GraphInvariant {
    override fun solve(graph: Graph): Int {
        val graphs = getProbSubGraphs(graph)
        if (graphs.size == 2) {
            // нашли два каких то подграфа SC или SB
            val graph1 = graphs[0]
            val graph2 = graphs[1]
            if (TreeIsStarCentral.check(graph1) && TreeIsStarCentral.check(graph2)) {
                // два SC графа
                // проверим что пересекаются они только по цепи
                val commonVertexes = graph1.mapList.keys.toSet().intersect(graph2.mapList.keys.toSet())
                if (commonVertexes.size == 1) {
                    // это путь нулевой длины - точка пересечения двух SC, значит это ТИП 1
                    return 1
                } else if (commonVertexes.isNotEmpty()) {
                    if (isPath(graph1, commonVertexes) && isPath(graph2, commonVertexes)) {
                        return 1
                    } else {
                        // непонятная пока что штука - SC и SC, которые имеют общую часть - не путь (похоже на тип 3)
                        return 3
                    }
                } else {
                    Logger.i("очень интересно, но тут два SC без общих частей, либо ошибка проги, либо до сюда не дойдет")
                    // очень интересно, но тут два SC без общих частей, либо ошибка проги, либо до сюда не дойдет
                }
            } else if (TreeIsStarBicentral.check(graph1) && TreeIsStarBicentral.check(graph2)) {
                // два BC графа
                val center1 = TreeUtil.getTreeCenterVertexes(graph1)
                val center2 = TreeUtil.getTreeCenterVertexes(graph2)
                if (center1 == center2) {
                    // а тут есть варианты, мы нашли SB и SB с одинаковым центром, но вдруг у них еще есть другая общая часть
                    val commonVertexes = graph1.mapList.keys.toSet().intersect(graph2.mapList.keys.toSet())
                    if (commonVertexes.size > 2) {
                        // по идее тут общая часть больше чем две вершины центральных, но больше ничего не проверял по типу 4
                        return 4
                    } else {
                        return 2
                    }
                } else {
                    Logger.i("непонятная пока что штука - SB и SB, которые имеют разный центр, по идее, такого не может быть")
                    // непонятная пока что штука - SB и SB, которые имеют разный центр, по идее, такого не может быть
                }
            } else {
                // тут неправильно считаем графы, на примере n9/18
                Logger.i("совершенно непонятная штука, по идее такой не должно быть (SC и SB)")
                Logger.i("${getGraphStarType(graph1)} : ${graph1.mapList.keys} and ${getGraphStarType(graph2)} ${graph2.mapList.keys}")
                // совершенно непонятная штука, по идее такой не должно быть (SC и SB)
            }
        } else {
            Logger.i("нашли НЕ 2 графа, вообще непонятно что такое (другие левые графы не SC или SB может быть)")
            // нашли НЕ 2 графа, вообще непонятно что такое (другие левые графы не SC или SB может быть)
        }
        return 0
    }

    fun getProbSubGraphs(graph: Graph): List<Graph> {
        val leaveToSubgraphCode = mutableListOf<Pair<Int, AbstractGraphCode>>()
        graph.getLeaves().forEach { vertex ->
            val newGraph = graph.filterVertexes { it != vertex }
            leaveToSubgraphCode.add(vertex to newGraph.getAbstractCode())
        }
        val sameLeavesGroups = leaveToSubgraphCode.groupBy { it.second }.map { (_, value) -> value.map { it.first } }
        Logger.i("sameLeavesGroups:")
        val graphs = sameLeavesGroups.mapNotNull { leavesGroup ->
            Logger.i("sameLeavesGroup $leavesGroup")
            // leavesGroup - вершины, при удалении которых получаются подобные графы
            // т.е. будем убирать все остальные листья, пока не получим дерево с количеством приведенной колоды 2
            var currentGraph = graph.copy()
            var otherLeaves = currentGraph.getLeaves().toSet() - leavesGroup.toSet()
            var forceStop = false
            while (
                !(ReducedTreePackEquals1.check(currentGraph) && // ищем граф SC или SB
                    currentGraph.getLeaves().toSet() == leavesGroup.toSet()) && // чтобы листья были только те, что в группе
                otherLeaves.isNotEmpty() && // пока есть что откусывать
                !forceStop
            ) {
                val oldGraph = currentGraph
                currentGraph = currentGraph.filterVertexes { !otherLeaves.contains(it) }
                forceStop = oldGraph == currentGraph // остановимся, если граф не поменялся
                otherLeaves = currentGraph.getLeaves().toSet() - leavesGroup.toSet()
            }
            // нашли граф SC или SB, (если нет, то потом разберемся с этим кейсом
            currentGraph.takeIf { ReducedTreePackEquals1.check(currentGraph) }
        }
        return graphs
    }

    fun isPath(g: Graph, path: Set<Int>): Boolean {
        val mutablePath = path.toMutableSet()
        val startVertex = path.find { vertex -> path.count { g.mapList[vertex]?.contains(it) == true } == 1 }
        var currentVertex = startVertex
        mutablePath.remove(currentVertex)
        while (currentVertex != null) {
            currentVertex = g.mapList[currentVertex].orEmpty().find { mutablePath.contains(it) }
            mutablePath.remove(currentVertex)
        }
        return mutablePath.isEmpty()
    }

    private fun getGraphStarType(graph: Graph): String {
        if (TreeIsStarCentral.check(graph)) {
            return "SC"
        } else if (TreeIsStarBicentral.check(graph)) {
            return "SB"
        } else {
            return "ER"
        }
    }
}