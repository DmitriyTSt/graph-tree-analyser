package ru.dmitriyt.graphtreeanalyser.presentation.input

import ru.dmitriyt.graphtreeanalyser.presentation.BlockingSolverInput

val input7 = BlockingSolverInput.DataInputProvider(
    graphs = """
            :FaYiL
            :FaYeL
            :FaYbL
            :FaXeW
            :FaXeL
            :FaXeG
            :FaXbK
            :FaXbG
            :FaWmL
            :FaWmG
            :FaGaG
        """.trimIndent().split("\n")
)