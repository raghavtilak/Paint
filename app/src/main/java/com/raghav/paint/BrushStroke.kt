package com.raghav.paint

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class BrushStroke(
    val points: SnapshotStateList<Offset>,
    val color: Color = Color.Green,
    val strokeWidth: Dp = 2.dp
)