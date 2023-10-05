package com.raghav.paint.xml

import android.graphics.Path

data class Stroke(

    //color of the stroke
    var color: Int,
    //width of the stroke
    var strokeWidth: Int,
    //a Path object to represent the path drawn
    var path: Path
)