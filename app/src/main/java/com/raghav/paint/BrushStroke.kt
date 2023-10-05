package com.raghav.paint

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class BrushStroke(
    val startOffset: Offset,
    val endOffset: Offset,
    val color: Color = Color.Green,
    val strokeWidth: Dp = 2.dp
)