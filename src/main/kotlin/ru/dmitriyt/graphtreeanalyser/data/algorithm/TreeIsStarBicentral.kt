package ru.dmitriyt.graphtreeanalyser.data.algorithm

import ru.dmitriyt.graphtreeanalyser.domain.algorithm.GraphCondition
import ru.dmitriyt.graphtreeanalyser.presentation.and

data object TreeIsStarBicentral : GraphCondition by (ReducedTreePackEquals1 and TreeCenterSizeEquals2)