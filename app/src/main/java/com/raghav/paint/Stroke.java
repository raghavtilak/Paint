package com.raghav.paint;

import android.graphics.Path;

public class Stroke {

    //color of the stroke
    public int color;
    //width of the stroke
    public int strokeWidth;
    //a Path object to represent the path drawn
    public Path path;

    //constructor to initialise the attributes
    public Stroke(int color, int strokeWidth, Path path) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }
}