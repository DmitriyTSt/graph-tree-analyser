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
        val type = getGraphType(graph)
        println("GRAPH_TYPE = $type")
        return type
    }

    private fun getGraphType(graph: Graph): Int {
        val graphs = getScSbSubGraphs(graph) ?: run {
            // получили больше двух компонент SC/SB
            Logger.e("получили больше двух компонент SC/SB")
            return 0
        }
        if (graphs.size == 2) {
            // нашли два каких то подграфа SC или SB
            val graph1 = graphs[0]
            val graph2 = graphs[1]
            if (TreeIsStarCentral.check(graph1) && TreeIsStarCentral.check(graph2)) {
                // два SC графа
                // проверим что пересекаются они только по цепи
                val commonVertexes = graph1.mapList.keys.toSet().intersect(graph2.mapList.keys.toSet())
                if (commonVertexes.size == 1) {
                    // это путь нулевой длины - точка пересечения двух SC
                    Logger.i("это путь нулевой длины - точка пересечения двух SC")
                    return 1
                } else if (commonVertexes.isNotEmpty()) {
                    // SC1 и SC2 с общей частью больше чем 1 вершина, и это по идее тип 3,
                    // потом надо будет доработать проверки исключения других графов, если будут
                    Logger.i("SC1 и SC2 с общей частью больше чем 1 вершина")
                    return 3
                } else {
                    // два SC соединены через цепь (цепь в них не входит по этой проге)
                    val overVertexes = graph.mapList.keys - graph1.mapList.keys - graph2.mapList.keys
                    if (isPath(graph, overVertexes)
                        && isPathConnectedToCenterOfSCB(graph, graph1, overVertexes)
                        && isPathConnectedToCenterOfSCB(graph, graph2, overVertexes)
                    ) {
                        Logger.i("два SC соединены через цепь")
                        return 1
                    } else {
                        val center1 = TreeUtil.getTreeCenterVertexes(graph1).first()
                        val center2 = TreeUtil.getTreeCenterVertexes(graph2).first()
                        if (graph.mapList[center1].orEmpty().contains(center2)) {
                            // два SC соединены просто друг с другом за вершины через нулевую цепь
                            Logger.i("два SC соединены просто друг с другом за вершины через нулевую цепь")
                            return 1
                        } else {
                            Logger.e("нашли два SC, но они не соединены через цепь (возможно нулевую)")
                            // нашли два SC, но они не соединены через цепь (возможно нулевую)
                        }
                    }
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
                    Logger.e("непонятная пока что штука - SB и SB, которые имеют разный центр, по идее, такого не может быть")
                    // непонятная пока что штука - SB и SB, которые имеют разный центр, по идее, такого не может быть
                }
            } else {
                // тут неправильно считаем графы, на примере n9/18
                Logger.e("совершенно непонятная штука, по идее такой не должно быть (SC и SB)")
                Logger.e("${getGraphStarType(graph1)} : ${graph1.mapList.keys} and ${getGraphStarType(graph2)} ${graph2.mapList.keys}")
                // совершенно непонятная штука, по идее такой не должно быть (SC и SB)
            }
        } else if (graphs.size == 1) {
            // может быть случай, когда к цепи крепится SC за середину, т.е. цепь это вырожденный случай второго SC (так как только один лист)
            val graph1 = graphs[0]
            val overVertexes = graph.mapList.keys - graph1.mapList.keys

            if (TreeIsStarCentral.check(graph1)
                && isPath(graph, overVertexes)
                && isPathConnectedToCenterOfSCB(graph, graph1, overVertexes)
            ) {
                Logger.i("к цепи крепится SC за середину")
                return 1
            } else {
                Logger.e("нашли 1 граф SC/SB, но это не SC/SB + P")
                // нашли 1 граф, но это не SC + P
            }
        } else {
            Logger.e("нашли > 2 графов, вообще непонятно что такое")
            // нашли > 2 графов, вообще непонятно что такое
        }
        return 0
    }

    /**
     * Получение графов SC/SB, которые имеются в графе [graph] с приведенной колодой 2 (по подобным листьями)
     * Идем и отсекаем листья другого подобия, пока не получим SC/SB
     */
    fun getScSbSubGraphs(graph: Graph): List<Graph>? {
        val leaveToSubgraphCode = mutableListOf<Pair<Int, AbstractGraphCode>>()
        graph.getLeaves().forEach { vertex ->
            val newGraph = graph.filterVertexes { it != vertex }
            leaveToSubgraphCode.add(vertex to newGraph.getAbstractCode())
        }
        val sameLeavesGroups = leaveToSubgraphCode.groupBy { it.second }.map { (_, value) -> value.map { it.first } }
        Logger.i("sameLeavesGroups:")
        if (sameLeavesGroups.size > 2) {
            // получили больше 2х подобных листьев, значит это не граф с количеством приведенной колоды 2
            return null
        }
        val graphs = sameLeavesGroups.mapNotNull { leavesGroup ->
            Logger.i("sameLeavesGroup $leavesGroup")
            // leavesGroup - вершины, при удалении которых получаются подобные графы
            // т.е. будем убирать все остальные листья, пока не получим дерево с количеством приведенной колоды 1
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

    fun getPathStartEnd(g: Graph, path: Set<Int>): Pair<Int, Int> {
        if (path.isEmpty()) {
            error("path is empty")
        }
        if (path.size == 1) return path.first() to path.first()
        val (start, end) = path.filter { vertex -> path.count { g.mapList[vertex]?.contains(it) == true } == 1 }
        return start to end
    }

    fun isPath(g: Graph, path: Set<Int>): Boolean {
        if (path.isEmpty()) return false
        if (path.size == 1) return true
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

    private fun isPathConnectedToCenterOfSCB(g: Graph, scb: Graph, path: Set<Int>): Boolean {
        val graph1Center = TreeUtil.getTreeCenterVertexes(scb).first()
        return getPathStartEnd(g, path).let { (pathStart, pathEnd) ->
            g.mapList[graph1Center].orEmpty().let { it.contains(pathStart) || it.contains(pathEnd) }
        }
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