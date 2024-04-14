package ru.dmitriyt.graphtreeanalyser.presentation

import ru.dmitriyt.graphtreeanalyser.data.GraphCache
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.cos
import kotlin.math.sin

/**
 * Класс для создания изображения графа
 */
class GraphDrawer(
    /** Путь сохранения изображений */
    private val path: String = PATH,
    /** Подписывать ли граф */
    private val hasTitle: Boolean = true,
    /** Размер изображения */
    private val imageSize: Int = IMAGE_SIZE,
    /** Радиус вершины */
    private val vertexRadius: Int = VERTEX_RADIUS,
    /** Радиус графа */
    private val graphRadius: Int = GRAPH_RADIUS,
) {
    companion object {
        const val IMAGE_SIZE = 300
        const val VERTEX_RADIUS = 20
        const val GRAPH_RADIUS = 100
        const val PATH = "graphs/"
        private const val EXTENSION = "png"
    }

    /**
     * Создать и сохранить изображение графа
     * @param graph6 - граф в формате graph6
     */
    fun drawImage(graph6: String) {
        val graph = GraphCache[graph6]
        val n = graph.n
        val image = BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB)
        val graphics = image.createGraphics()
        graphics.apply {
            paint = Color.WHITE
            fillRect(0, 0, imageSize, imageSize)
            paint = Color.BLACK
            val vertexCount = graph.n
            val dAngle = 360 / vertexCount
            var angle = 0
            if (hasTitle) {
                font = Font(null, Font.BOLD, 14)
                drawString(graph6, 0, 16)
            }
            // рисование ребер
            repeat(graph.n) { i ->
                repeat(graph.n) { j ->
                    if (graph.mapList[i].orEmpty().contains(j)) {
                        val (x1, y1) = getVertexCenter(i, dAngle)
                        val (x2, y2) = getVertexCenter(j, dAngle)
                        drawEdge(this, x1, y1, x2, y2)
                    }
                }
            }
            // рисование вершин
            font = Font(null, Font.BOLD, 24)
            repeat(graph.n) { vertex ->
                drawVertex(
                    this,
                    vertex,
                    imageSize / 2 + (graphRadius * sin(Math.toRadians(angle.toDouble()))).toInt(),
                    imageSize / 2 + (graphRadius * cos(Math.toRadians(angle.toDouble()))).toInt()
                )
                angle += dAngle
            }

            dispose()
        }

        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdir()
        }
        val dirByN = File(dir, "n$n")
        if (!dirByN.exists()) {
            dirByN.mkdir()
        }
        val number = dirByN.listFiles()?.size
        ImageIO.write(image, EXTENSION, File(dirByN, "$number.$EXTENSION"))
    }

    private fun getVertexCenter(vertex: Int, dAngle: Int): Pair<Int, Int> {
        val angle = dAngle * vertex
        val x = imageSize / 2 + (graphRadius * sin(Math.toRadians(angle.toDouble()))).toInt()
        val y = imageSize / 2 + (graphRadius * cos(Math.toRadians(angle.toDouble()))).toInt()
        return x to y
    }

    private fun drawVertex(graphics: Graphics2D, vertex: Int, centerX: Int, centerY: Int) {
        graphics.stroke = BasicStroke(2f)
        graphics.paint = Color.LIGHT_GRAY
        graphics.fillOval(
            centerX - vertexRadius,
            centerY - vertexRadius,
            2 * vertexRadius,
            2 * vertexRadius
        )
        graphics.paint = Color.BLACK
        graphics.drawOval(
            centerX - vertexRadius,
            centerY - vertexRadius,
            2 * vertexRadius,
            2 * vertexRadius
        )
        graphics.drawCenteredString(
            vertex.toString(),
            Rectangle(
                centerX - vertexRadius,
                centerY - vertexRadius,
                2 * vertexRadius,
                2 * vertexRadius
            )
        )
    }

    private fun drawEdge(graphics: Graphics2D, v1x: Int, v1y: Int, v2x: Int, v2y: Int) {
        graphics.paint = Color.BLACK
        graphics.stroke = BasicStroke(4f)
        graphics.drawLine(v1x, v1y, v2x, v2y)
    }

    private fun Graphics.drawCenteredString(text: String, rect: Rectangle) {
        val metrics = getFontMetrics(font)
        val x = rect.x + (rect.width - metrics.stringWidth(text)) / 2
        val y = rect.y + (rect.height - metrics.height) / 2 + metrics.ascent
        drawString(text, x, y)
    }
}
