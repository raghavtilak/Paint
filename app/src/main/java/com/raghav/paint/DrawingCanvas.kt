package com.raghav.paint

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.unit.dp
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.raghav.paint.ui.theme.PaintTheme
import com.raghav.paint.util.ERROR_SAVING

@Composable
fun DrawingCanvas(modifier: Modifier = Modifier) {
    val strokeHistory = remember { mutableStateListOf<BrushStroke>() }
    val redoStack = remember { mutableStateListOf<BrushStroke>() }
    var canvasWidth by remember { mutableStateOf(-1) }
    var canvasHeight by remember { mutableStateOf(-1) }
    val context = LocalContext.current
    var currentColor by remember { mutableStateOf(Color.Green) }

    var isBrushThicknessSliderVisible by remember { mutableStateOf(false) }
    var brushThickness by remember { mutableStateOf(10f) }

    val saveImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("image/*")
    ) {
        it?.let { uri ->
            val bitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888)
            repeat(10_00_000) {}
            val outputStream = context.contentResolver.openOutputStream(uri)
            outputStream?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
        } ?: Toast.makeText(context, ERROR_SAVING, Toast.LENGTH_SHORT).show()
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
                Log.d("canvas", "$canvasHeight $canvasWidth")
                saveImageLauncher.launch("sample.png")
            }) {
                Image(painterResource(id = R.drawable.ic_floppy_disk), contentDescription = "Save")
            }

            IconButton(onClick = {
                MaterialColorPickerDialog
                    .Builder(context)
                    .setTitle("Pick Theme")
                    .setColorShape(ColorShape.SQAURE)
                    .setColorSwatch(ColorSwatch._300)
                    .setColorListener { color, colorHex ->
                        Log.d("canvas", "$color $colorHex")
                        currentColor = Color(color)
                    }
                    .show()
            }) {
                Image(
                    painterResource(id = R.drawable.ic_colorpicker),
                    contentDescription = "Color Picker"
                )
            }

            IconButton(onClick = {
                isBrushThicknessSliderVisible = !isBrushThicknessSliderVisible
            }) {
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

        AnimatedVisibility(visible = isBrushThicknessSliderVisible) {
            Slider(
                value = brushThickness,
                onValueChange = { brushThickness = it },
                valueRange = 2f..70f,
                steps = 98,
                colors = SliderDefaults.colors(
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .pointerInput(true) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val stroke = BrushStroke(
                            startOffset = change.position - dragAmount,
                            endOffset = change.position,
                            color = currentColor,
                            strokeWidth = brushThickness.toDp()
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