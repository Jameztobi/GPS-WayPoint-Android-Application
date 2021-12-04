package com.example.assignment_3

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class CustomView(context: Context?, attribs: AttributeSet?) : View(context, attribs)  {
    // private fields of the class
    private var _context: Context? = context
    private var _attribs: AttributeSet? = null
    private var canvas_width: Int = 0
    private var canvas_height: Int = 0
    private var width: Float = 0.0f
    private var height: Float = 0.0f
    private var radius: Float =0.0f
    private var _canvas: Canvas? = null
    private lateinit var outerCircleColor: Paint
    private lateinit var innerCircleColor: Paint
    private lateinit var labelColor: Paint

    init{
        outerCircleColor= Paint(Paint.ANTI_ALIAS_FLAG)
        innerCircleColor = Paint(Paint.ANTI_ALIAS_FLAG)
        labelColor = Paint(Paint.ANTI_ALIAS_FLAG)
        labelColor.textSize=50f
        outerCircleColor.color=Color.BLACK
        innerCircleColor.color=Color.RED
        labelColor.color = Color.BLACK
        outerCircleColor.style=Paint.Style.STROKE
        outerCircleColor.strokeWidth=15f

    }

    override fun onDraw(canvas: Canvas?) {
        //get the width and height of the canvas which is available drawing area
        canvas_width = canvas!!.width
        canvas_height = canvas!!.height
        width = canvas_width-100f
        height = canvas_height/2f
        radius = width
        _canvas=canvas
        drawCompass()
        drawInnerCircle()
        drawSecondCircle()
        drawNorth()
        drawSouth()
        drawEast()
        drawWest()
        drawArrow(canvas_width/2, canvas_width/14, canvas_width/4)
        canvas.drawLine(canvas_width*0.50f, 250f, canvas_width*0.50f, 850f, labelColor)
        canvas.drawLine(250f, canvas_width*0.50f, 850f, canvas_width*0.50f, labelColor)
        super.onDraw(canvas)
    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var parent: Int = 0
        parent = if (widthMeasureSpec > heightMeasureSpec) {
            heightMeasureSpec
        } else {
            widthMeasureSpec
        }

        this.setMeasuredDimension(parent, parent)
    }

    private fun drawCompass() {
        _canvas?.save()
        _canvas?.translate(canvas_width/2f, canvas_height/2f)
        _canvas?.drawCircle(0.0f, 0.0f, canvas_width*0.40f, outerCircleColor)
        _canvas?.restore()
    }
    private fun drawInnerCircle() {
        _canvas?.save()
        _canvas?.translate(canvas_width/2f, canvas_height/2f)
        _canvas?.drawCircle(0.0f, 0.0f, canvas_width*0.025f, innerCircleColor)
        _canvas?.restore()
    }

    private fun drawSecondCircle() {
        _canvas?.save()
        _canvas?.translate(canvas_width/2f, canvas_height/2f)
        _canvas?.drawCircle(0.0f, 0.0f, canvas_width*0.25f, outerCircleColor)
        _canvas?.restore()
    }

    private fun drawNorth(){
        _canvas?.drawText("N", canvas_width/2f, canvas_height.toFloat()/4, labelColor)
    }

    private fun drawSouth(){
        _canvas?.drawText("S", canvas_width/2f, (canvas_height.toFloat()/2)*1.6f, labelColor)
    }

    private fun drawEast(){
        _canvas?.drawText("W", canvas_width*0.20f, canvas_height.toFloat()/2f, labelColor)
    }

    private fun drawWest(){
        _canvas?.drawText("E", canvas_width*0.80f, canvas_height.toFloat()/2f, labelColor)
    }

    private fun drawArrow(){
        _canvas?.save()
        _canvas?.translate(canvas_width/4.0f, 3.0f*canvas_height/4f)
        var a: Point = Point(-canvas_width/2, canvas_width/2)
        var b: Point = Point(canvas_width/2, canvas_width/2)
        var c: Point = Point(0, canvas_width/2)
        var path: Path = Path()
//        path.lineTo(a.x.toFloat(), a.y.toFloat())
        path.lineTo(b.x.toFloat(), b.y.toFloat())
        path.lineTo(c.x.toFloat(), c.y.toFloat())
        path.close()
        _canvas?.drawPath(path, labelColor)
        _canvas?.restore()
    }

    private fun drawArrow(x:Int, y:Int,width:Int){
        var halfwidth = width/8
        var path: Path = Path()
        path.moveTo(x.toFloat(), y-halfwidth.toFloat())
        path.lineTo(x - halfwidth.toFloat(), y+halfwidth.toFloat())
        path.lineTo(x + halfwidth.toFloat(), y+halfwidth.toFloat())
        path.lineTo(x.toFloat(), y-halfwidth.toFloat())
        path.close()
        _canvas?.drawPath(path, labelColor)

    }


}