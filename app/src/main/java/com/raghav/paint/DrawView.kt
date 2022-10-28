package com.raghav.paint

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs


class DrawView(c: Context, attributeSet: AttributeSet) : View(c, attributeSet) {

    private val TOUCH_TOLERANCE = 4f
    private var mX = 0f
    private var mY = 0f
    private lateinit var mPath: Path

    //the Paint class encapsulates the color and style information about
    //how to draw the geometries,text and bitmaps
    private var mPaint: Paint = Paint()

    //ArrayList to store all the strokes drawn by the user on the Canvas
    private val paths = mutableListOf<Stroke>()
    private var currentColor = 0
    private var strokeWidth = 0
    private lateinit var mBitmap: Bitmap
    private lateinit var mCanvas: Canvas
    private val mBitmapPaint = Paint(Paint.DITHER_FLAG)

    init {

        //the below methods smoothens the drawings of the user
        //the below methods smoothens the drawings of the user
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.color = Color.GREEN
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.ROUND
        //0xff=255 in decimal
        mPaint.alpha = 0xff
    }

    fun init(height: Int, width: Int) {
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap)

        //set an initial color of the brush
        currentColor = Color.GREEN
        //set an initial brush size
        strokeWidth = 20
    }

    //sets the current color of stroke
    fun setColor(color: Int) {
        currentColor = color
    }

    //sets the stroke width
    fun setStrokeWidth(width: Int) {
        strokeWidth = width
    }

    fun undo() {
        //check whether the List is empty or not
        //if empty, the remove method will return an error
        if (paths.size != 0) {
            paths.removeAt(paths.size - 1)
            invalidate()
        }
    }

    //this methods returns the current bitmap
    fun save(): Bitmap? {
        return mBitmap
    }

    //this is the main method where the actual drawing takes place
    override fun onDraw(canvas: Canvas) {
        //save the current state of the canvas before,
        //to draw the background of the canvas
        canvas.save()
        //DEFAULT color of the canvas
        val backgroundColor = Color.WHITE
        mCanvas!!.drawColor(backgroundColor)

        //now, we iterate over the list of paths and draw each path on the canvas
        for (fp in paths) {
            mPaint.color = fp.color
            mPaint.strokeWidth = fp.strokeWidth.toFloat()
            mCanvas.drawPath(fp.path, mPaint)
        }
        canvas.drawBitmap(mBitmap!!, 0f, 0f, mBitmapPaint)
        canvas.restore()
    }

    //the below methods manages the touch response of the user on the screen

    //the below methods manages the touch response of the user on the screen
    //firstly, we create a new Stroke and add it to the paths list
    private fun touchStart(x: Float, y: Float) {
        mPath = Path()
        val fp = Stroke(currentColor, strokeWidth, mPath)
        paths.add(fp)

        //finally remove any curve or line from the path
        mPath.reset()
        //this methods sets the starting point of the line being drawn
        mPath.moveTo(x, y)
        //we save the current coordinates of the finger
        mX = x
        mY = y
    }

    //in this method we check if the move of finger on the
    // screen is greater than the Tolerance we have previously defined,
    //then we call the quadTo() method which actually smooths the turns we create,
    //by calculating the mean position between the previous position and current position
    private fun touchMove(x: Float, y: Float) {
        val dx = abs(x - mX)
        val dy = abs(y - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
    }

    //at the end, we call the lineTo method which simply draws the line until
    //the end position
    private fun touchUp() {
        mPath.lineTo(mX, mY)
    }

    //the onTouchEvent() method provides us with the information about the type of motion
    //which has been taken place, and according to that we call our desired methods
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
                invalidate()
            }
        }
        return true
    }

}