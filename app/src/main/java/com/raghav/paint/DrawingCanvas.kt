package com.raghav.paint

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.raghav.paint.ui.theme.PaintTheme

@Composable
fun DrawingCanvas(modifier: Modifier = Modifier) {
    val history = remember { mutableStateListOf<BrushStroke>() }

    Column(modifier = Modifier.fillMaxSize()) {

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            IconButton(onClick = { /*TODO*/ }) {
                Image(painterResource(id = R.drawable.ic_undo), contentDescription = "Undo")
            }

            IconButton(onClick = { /*TODO*/ }) {
                Image(painterResource(id = R.drawable.ic_floppy_disk), contentDescription = "Save")
            }

            IconButton(onClick = { /*TODO*/ }) {
                Image(
                    painterResource(id = R.drawable.ic_colorpicker),
                    contentDescription = "Color Picker"
                )
            }

            IconButton(onClick = { /*TODO*/ }) {
                Image(painterResource(id = R.drawable.ic_paint_brush), contentDescription = "Brush")
            }

            IconButton(onClick = { /*TODO*/ }) {
                Image(
                    painterResource(id = R.drawable.ic_undo),
                    contentDescription = "Redo",
                    modifier = Modifier
                        .scale(scaleX = -1f, scaleY = 1f)
                        .clickable { }
                )
            }
        }

        Canvas(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(true) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val stroke = BrushStroke(
                        startOffset = change.position - dragAmount,
                        endOffset = change.position
                    )

                    history.add(stroke)
                }
            }) {
            history.forEach { stroke ->
                drawLine(
                    start = stroke.startOffset,
                    end = stroke.endOffset,
                    color = stroke.color,
                    strokeWidth = stroke.strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    PaintTheme {
        DrawingCanvas()
    }
}