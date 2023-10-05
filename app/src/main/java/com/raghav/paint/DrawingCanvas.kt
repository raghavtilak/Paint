package com.raghav.paint

import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.raghav.paint.ui.theme.PaintTheme
import com.raghav.paint.util.saveBitmapAsPNG

@Composable
fun DrawingCanvas(modifier: Modifier = Modifier) {
    val strokeHistory = remember { mutableStateListOf<BrushStroke>() }
    val redoStack = remember { mutableStateListOf<BrushStroke>() }
    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    var canvasWidth by remember { mutableStateOf(-1) }
    var canvasHeight by remember { mutableStateOf(-1) }
    val context = LocalContext.current

    val saveImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("image/*")
    ) {
        it?.let { uri ->
            saveBitmapAsPNG(context, bitmap, uri)
        } ?: Toast.makeText(
            context,
            "Your project was not saved!\nPlease try again!",
            Toast.LENGTH_SHORT
        ).show()
    }

    Column(modifier = modifier.fillMaxSize()) {

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            IconButton(onClick = {
                if (strokeHistory.isNotEmpty()) {
                    val lastStroke = strokeHistory.removeLast()
                    redoStack.add(lastStroke)
                }
            }) {
                Image(painterResource(id = R.drawable.ic_undo), contentDescription = "Undo")
            }

            IconButton(onClick = {
                saveImageLauncher.launch("image.png")
                bitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888)

            }) {
                Image(painterResource(id = R.drawable.ic_floppy_disk), contentDescription = "Save")
            }

            IconButton(onClick = {

            }) {
                Image(
                    painterResource(id = R.drawable.ic_colorpicker),
                    contentDescription = "Color Picker"
                )
            }

            IconButton(onClick = { /*TODO*/ }) {
                Image(painterResource(id = R.drawable.ic_paint_brush), contentDescription = "Brush")
            }

            IconButton(onClick = {
                if (redoStack.isNotEmpty()) {
                    val latestStroke = redoStack.removeLast()
                    strokeHistory.add(latestStroke)
                }
            }) {
                Image(
                    painterResource(id = R.drawable.ic_undo),
                    contentDescription = "Redo",
                    modifier = Modifier
                        .scale(scaleX = -1f, scaleY = 1f)
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

                    strokeHistory.add(stroke)
                    redoStack.clear()
                }
            }) {
            canvasWidth = size.width.toInt()
            canvasHeight = size.height.toInt()

            strokeHistory.forEach { stroke ->
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