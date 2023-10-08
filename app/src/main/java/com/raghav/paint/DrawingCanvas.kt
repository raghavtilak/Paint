package com.raghav.paint

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.raghav.paint.ui.theme.PaintTheme
import com.raghav.paint.ui.theme.Pink80
import com.raghav.paint.util.ERROR_SAVING
import com.raghav.paint.util.captureCanvasScreenshot
import com.raghav.paint.util.saveBitmapToStorage

@Composable
fun DrawingCanvas(modifier: Modifier = Modifier) {
    val undoHistory = remember { mutableStateListOf<BrushStroke>() }
    val redoHistory = remember { mutableStateListOf<BrushStroke>() }

    var canvasWidth by remember { mutableStateOf(-1) }
    var canvasHeight by remember { mutableStateOf(-1) }

    val context = LocalContext.current
    var currentColor by remember { mutableStateOf(Color.Green) }
    var strokeColor by remember { mutableStateOf(Color.Green) }
    var canvasColor by remember { mutableStateOf(Color.White) }

    var isBrushThicknessSliderVisible by remember { mutableStateOf(false) }
    var isEraserThicknessSliderVisible by remember { mutableStateOf(false) }
    var brushThickness by remember { mutableStateOf(10f) }
    var eraserThickness by remember { mutableStateOf(10f) }
    var useEraser by remember { mutableStateOf(false) }

    var isSavingImage by remember { mutableStateOf(false) }

    val view = LocalView.current
    val density = LocalDensity.current.density

    val saveImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("image/*")
    ) {
        it?.let { uri ->
            val bitmap = captureCanvasScreenshot(view, density)
            saveBitmapToStorage(context, uri, bitmap)

        } ?: Toast.makeText(context, ERROR_SAVING, Toast.LENGTH_SHORT).show()

        isSavingImage = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        AnimatedVisibility(
            visible = !isSavingImage,
            enter = fadeIn(animationSpec = tween(durationMillis = 0)),
            exit = fadeOut(animationSpec = tween(delayMillis = 400))
        ) {

            Column {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(
                            Pink80,
                            RoundedCornerShape(bottomEndPercent = 65, bottomStartPercent = 65)
                        )
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Image(
                        painterResource(id = R.drawable.ic_undo),
                        contentDescription = "Undo",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                if (undoHistory.isNotEmpty()) {
                                    val lastStroke = undoHistory.removeLast()
                                    redoHistory.add(lastStroke)
                                }
                            }
                    )

                    Image(
                        painterResource(id = R.drawable.ic_floppy_disk),
                        contentDescription = "Save",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                isSavingImage = true
                                saveImageLauncher.launch("sample.png")
                            })

                    Image(
                        painterResource(id = R.drawable.ic_colorpicker),
                        contentDescription = "Color Picker",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                MaterialColorPickerDialog
                                    .Builder(context)
                                    .setTitle("Pick Theme")
                                    .setColorShape(ColorShape.SQAURE)
                                    .setColorSwatch(ColorSwatch._300)
                                    .setColorListener { color, colorHex ->
                                        Log.d("canvas", "$color $colorHex")
                                        currentColor = Color(color)
                                        strokeColor = currentColor
                                    }
                                    .show()
                            }
                    )

                    Image(
                        painterResource(id = R.drawable.ic_paint_brush),
                        contentDescription = "Brush",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                useEraser = false
                                currentColor = strokeColor
                                isEraserThicknessSliderVisible = false
                                isBrushThicknessSliderVisible = !isBrushThicknessSliderVisible
                            }
                    )

                    Image(
                        painterResource(id = R.drawable.ic_eraser),
                        contentDescription = "Eraser",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                useEraser = true
                                currentColor = canvasColor
                                isBrushThicknessSliderVisible = false
                                isEraserThicknessSliderVisible = !isEraserThicknessSliderVisible
                            }
                    )

                    Image(
                        painterResource(id = R.drawable.ic_undo),
                        contentDescription = "Redo",
                        modifier = Modifier
                            .size(32.dp)
                            .scale(scaleX = -1f, scaleY = 1f)
                            .clickable {
                                if (redoHistory.isNotEmpty()) {
                                    val latestStroke = redoHistory.removeLast()
                                    undoHistory.add(latestStroke)
                                }
                            }
                    )
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

                AnimatedVisibility(visible = isEraserThicknessSliderVisible) {
                    Slider(
                        value = eraserThickness,
                        onValueChange = { eraserThickness = it },
                        valueRange = 2f..70f,
                        steps = 98,
                        colors = SliderDefaults.colors(
                            activeTickColor = Color.Transparent,
                            inactiveTickColor = Color.Transparent
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            Log.d("canvas", "drag start")
                            val stroke = BrushStroke(
                                points = mutableStateListOf(it),
                                color = currentColor,
                                strokeWidth = if (!useEraser)
                                    brushThickness.toDp()
                                else
                                    eraserThickness.toDp()
                            )

                            undoHistory.add(stroke)
                            redoHistory.clear()
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        undoHistory[undoHistory.lastIndex].points.add(change.position)
                    }
                }) {
            canvasWidth = size.width.toInt()
            canvasHeight = size.height.toInt()

            undoHistory.forEach { stroke ->
                drawPath(
                    path = stroke.points.toPath(),
                    color = stroke.color,
                    style = Stroke(
                        width = stroke.strokeWidth.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }
        }
    }
}

fun List<Offset>.toPath(): Path {
    val path = Path()

    path.moveTo(this.first().x, this.first().y)
    this.drop(1).forEach { offset ->
        path.lineTo(offset.x, offset.y)
    }

    return path
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    PaintTheme {
        DrawingCanvas()
    }
}