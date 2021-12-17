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
    private var _radius: Float=0.0f
    private var _canvas: Canvas? = null
    private lateinit var outerCircleColor: Paint
    private lateinit var innerCircleColor: Paint
    private lateinit var pointerColor: Paint
    private lateinit var labelColor: Paint
    private lateinit var northColor:Paint
    private lateinit var waypointColor:Paint
    private lateinit var waypointColorSelected:Paint
    private var checker: Boolean = false
    private var value = 0.0f
    private var waypointValue = 0.0f
    private var meterArray:ArrayList<Float> = ArrayList()
    private var degreeArray:ArrayList<Float> = ArrayList()
    private var currentSelectedWayPointNum=-1

    init{
        outerCircleColor= Paint(Paint.ANTI_ALIAS_FLAG)
        innerCircleColor = Paint(Paint.ANTI_ALIAS_FLAG)
        pointerColor = Paint(Paint.ANTI_ALIAS_FLAG)
        labelColor = Paint(Paint.ANTI_ALIAS_FLAG)
        northColor = Paint(Paint.ANTI_ALIAS_FLAG)
        waypointColor = Paint(Paint.ANTI_ALIAS_FLAG)
        waypointColorSelected = Paint(Paint.ANTI_ALIAS_FLAG)

        outerCircleColor.color=Color.BLACK
        innerCircleColor.color=Color.RED
        labelColor.color = Color.BLACK
        pointerColor.color=Color.GREEN
        northColor.color = Color.RED
        waypointColor.color = Color.BLUE
        waypointColorSelected.color = Color.BLUE


        labelColor.textSize=50f
        northColor.textSize=50f
        outerCircleColor.style=Paint.Style.STROKE
        waypointColor.style=Paint.Style.STROKE
        outerCircleColor.strokeWidth=15f
        pointerColor.strokeWidth=15f
        waypointColor.strokeWidth=15f

    }

    override fun onDraw(canvas: Canvas?) {
        //get the width and height of the canvas which is available drawing area
        canvas_width = canvas!!.width
        canvas_height = canvas!!.height
        width = canvas_width-100f
        height = canvas_height/2f
        radius = width
        _radius= (canvas_width / 8.0f) / 4f
        _canvas=canvas

        drawCompass()
        drawInnerCircle()
        drawSecondCircle()
        drawNorth()
        drawSouth()
        drawEast()
        drawWest()
        drawArrow(canvas_width/2, canvas_width/14, canvas_width/4)
        drawPointer()
        canvas.drawLine(canvas_width*0.50f, 250f, canvas_width*0.50f, 850f, labelColor)
        canvas.drawLine(250f, canvas_width*0.50f, 850f, canvas_width*0.50f, labelColor)
        drawWayPoint()
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
        _canvas?.rotate(value)
        _canvas?.drawCircle(0.0f, 0.0f, canvas_width*0.40f, outerCircleColor)
        _canvas?.restore()
    }
    private fun drawInnerCircle() {
        _canvas?.save()
        _canvas?.translate(canvas_width/2f, canvas_height/2f)
        _canvas?.rotate(value)
        _canvas?.drawCircle(0.0f, 0.0f, canvas_width*0.025f, innerCircleColor)
        _canvas?.restore()
    }

    private fun drawSecondCircle() {
        _canvas?.save()
        _canvas?.translate(canvas_width/2f, canvas_height/2f)
        _canvas?.rotate(value)
        _canvas?.drawCircle(0.0f, 0.0f, canvas_width*0.25f, outerCircleColor)
        _canvas?.restore()
    }

    private fun drawCircle(radPoint: Float, degree: Float, color :Paint){
        _canvas?.save()
        _canvas?.translate(canvas_width/2f, canvas_height/2f)
        _canvas?.rotate(degree)
        _canvas?.drawCircle(radPoint, 0.0f, _radius, color)
        _canvas?.restore()
    }

    private fun drawNorth(){
        _canvas?.save()
        _canvas?.rotate(value, canvas_width/2f, canvas_height/2f)
        _canvas?.drawText("N", canvas_width/2f, canvas_width*0.20f, northColor)
        _canvas?.restore()
    }

    private fun drawSouth(){
        _canvas?.save()
        _canvas?.rotate(value, canvas_width/2f, canvas_height/2f)
        _canvas?.drawText("S", canvas_width/2f, (canvas_height.toFloat()/2)*1.65f, labelColor)
        _canvas?.restore()

    }

    private fun drawWest(){
        _canvas?.save()
        _canvas?.rotate(value, canvas_width/2f, canvas_height/2f)
        _canvas?.drawText("W", canvas_width*0.15f, canvas_height.toFloat()/2f, labelColor)
        _canvas?.restore()
    }

    private fun drawEast(){
        _canvas?.save()
        _canvas?.save()
        _canvas?.rotate(value, canvas_width/2f, canvas_height/2f)
        _canvas?.drawText("E", canvas_width*0.80f, canvas_height.toFloat()/2f, labelColor)
        _canvas?.restore()
    }

    private fun drawPointer(){
        _canvas?.save()
        _canvas?.rotate(waypointValue, canvas_width/2f, canvas_height/2f)
        _canvas?.drawLine(canvas_width/2f, canvas_height/2f, canvas_width/2f, canvas_height.toFloat()/4.5f, pointerColor)
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
        _canvas?.save()
        _canvas?.rotate(value, canvas_width/2f, canvas_height/2f)
        _canvas?.drawPath(path, labelColor)
        _canvas?.restore()
    }

    fun setDegree(x: Float){
        checker = true;
        value=360-x


    }

    fun setWayPointDegree(x: Float, tempKey: Int){
        waypointValue=360-x

    }

    fun setWayPointOnView(_metersArray: ArrayList<Float>, _degreeArray: ArrayList<Float>){
        meterArray.clear()
        degreeArray.clear()
        meterArray=_metersArray
        degreeArray=_degreeArray

    }

    fun clearWayPoints(){
        meterArray.clear()
        degreeArray.clear()
    }

    private fun getMetersArray(): ArrayList<Float> {
        return meterArray
    }

    private fun getDegreeArray(): ArrayList<Float> {
        return degreeArray
    }

    private fun drawWayPoint(){
        if (getDegreeArray()!=null && getMetersArray()!=null){
            for(i in 0 until getMetersArray().size){
                var temp_value = ((canvas_width*0.40f/500)*getMetersArray()[i])


                if(currentSelectedWayPointNum==-1){
                    drawCircle(temp_value, 360-getDegreeArray()[i], waypointColor)
                }
                else{
                    if(i==currentSelectedWayPointNum){
                        drawCircle(temp_value, 360-getDegreeArray()[i], waypointColorSelected)
                    }
                    else{
                        drawCircle(temp_value, 360-getDegreeArray()[i], waypointColor)
                    }
                }

            }
        }

    }
}