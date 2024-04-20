package ru.dmitriyt.graphtreeanalyser.data.algorithm

import ru.dmitriyt.graphtreeanalyser.domain.algorithm.GraphCondition
import ru.dmitriyt.graphtreeanalyser.presentation.and

data object TreeIsStarCentral : GraphCondition by (ReducedTreePackEquals1 and TreeCenterSizeEquals1)