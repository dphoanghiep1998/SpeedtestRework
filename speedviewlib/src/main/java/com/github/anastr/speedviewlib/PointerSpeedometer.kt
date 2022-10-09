package com.github.anastr.speedviewlib

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import com.github.anastr.speedviewlib.components.Style
import com.github.anastr.speedviewlib.components.indicators.SpindleIndicator

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
open class PointerSpeedometer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : Speedometer(context, attrs, defStyleAttr) {

    private val speedometerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val blurPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val darkPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val pointerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pointerBackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedometerRect = RectF()
    private var speedometerColor = 0xFFEEEEEE.toInt()
    private var pointerColor = 0xFFFFFFFF.toInt()
    private var downloadColorsList = intArrayOf(0xFF00FACC.toInt(), 0xFF36E7E7.toInt())
    private var uploadColorsList = intArrayOf(0xFFFF981F.toInt(), 0xFFFF7F0A.toInt())
    private var state = "none"
    private var withPointer = true
    private var initDone = false
    private var rectF = RectF()

    /**
     * change the color of the center circle.
     */
    var centerCircleColor: Int
        get() = circlePaint.color
        set(centerCircleColor) {
            circlePaint.color = centerCircleColor
            if (isAttachedToWindow)
                invalidate()
        }

    /**
     * change the width of the center circle.
     */
    var centerCircleRadius = dpTOpx(12f)
        set(centerCircleRadius) {
            field = centerCircleRadius
            if (isAttachedToWindow)
                invalidate()
        }

    /**
     * enable to draw circle pointer on speedometer arc.
     *
     * this will not make any change for the Indicator.
     *
     * true: draw the pointer,
     * false: don't draw the pointer.
     */
    var isWithPointer: Boolean
        get() = withPointer
        set(withPointer) {
            this.withPointer = withPointer
            if (isAttachedToWindow)
                invalidate()
        }

    init {
        init()
        initAttributeSet(context, attrs)
    }

    override fun defaultGaugeValues() {
        super.speedometerWidth = dpTOpx(10f)
        super.textColor = 0xFFFFFFFF.toInt()
        super.speedTextColor = 0xFFFFFFFF.toInt()
        super.unitTextColor = 0xFFFFFFFF.toInt()
        super.speedTextSize = dpTOpx(24f)
        super.unitTextSize = dpTOpx(11f)
        super.speedTextTypeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    override fun defaultSpeedometerValues() {
        super.marksNumber = 8
        super.marksPadding = speedometerWidth + dpTOpx(12f)
        super.tickPadding = speedometerWidth + dpTOpx(10f)
        super.markStyle = Style.ROUND
        super.markHeight = dpTOpx(5f)
        super.markWidth = dpTOpx(2f)
        indicator = SpindleIndicator(context)
        indicator.apply {
            width = dpTOpx(16f)
            color = 0xFFFFFFFF.toInt()
        }
        super.backgroundCircleColor = 0xff48cce9.toInt()
    }

    private fun init() {
        speedometerPaint.style = Paint.Style.STROKE
        speedometerPaint.strokeCap = Paint.Cap.BUTT
        speedometerPaint.color = 0xFF00FACC.toInt()

        darkPaint.style = Paint.Style.STROKE
        darkPaint.strokeCap = Paint.Cap.BUTT
        darkPaint.color = 0xFF4E4B66.toInt()


        blurPaint.style = Paint.Style.STROKE
        blurPaint.strokeCap = Paint.Cap.BUTT
        blurPaint.maskFilter = BlurMaskFilter(0.4f * speedometerWidth, BlurMaskFilter.Blur.NORMAL)
        blurPaint.strokeWidth = 1.3f * speedometerWidth

        circlePaint.color = 0xFFFFFFFF.toInt()
        speedometerPaint.strokeWidth = speedometerWidth
        darkPaint.strokeWidth = speedometerWidth


        rectF = RectF(
            speedometerRect.left + 5f,
            speedometerRect.top + 5f,
            speedometerRect.right - 5f,
            speedometerRect.bottom - 5f
        )

    }

    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        if (attrs == null) {
            initAttributeValue()
            return
        }
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.PointerSpeedometer, 0, 0)

        speedometerColor =
            a.getColor(R.styleable.PointerSpeedometer_sv_speedometerColor, speedometerColor)
        pointerColor = a.getColor(R.styleable.PointerSpeedometer_sv_pointerColor, pointerColor)
        circlePaint.color =
            a.getColor(R.styleable.PointerSpeedometer_sv_centerCircleColor, circlePaint.color)
        centerCircleRadius =
            a.getDimension(R.styleable.SpeedView_sv_centerCircleRadius, centerCircleRadius)
        withPointer = a.getBoolean(R.styleable.PointerSpeedometer_sv_withPointer, withPointer)
        a.recycle()
        initAttributeValue()
    }

    private fun initAttributeValue() {
        pointerPaint.color = pointerColor
    }


    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)

        val risk = speedometerWidth * .5f + dpTOpx(8f) + padding.toFloat()
        speedometerRect.set(risk, risk, size - risk, size - risk)

        updateRadial()
        updateBackgroundBitmap()
    }

    fun showArc() {
        val offInt: ValueAnimator = ValueAnimator.ofInt(135, 405)
        offInt.duration = 500
        offInt.addUpdateListener {
            Log.d("TAG", "showArc: "+ it.animatedValue)
            setEndDegree(it.animatedValue as Int)
            invalidate()
            if(it.animatedValue == 405){
                showTicks()
            }
        }
        offInt.start()
    }
    fun showTicks(){

    }

    private fun initDraw() {
        if (initDone) return
        Log.d("TAG", "initDraw: ")
        speed = 0f
        invalidate()
        initDone = true


    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        initDraw()


        val position = getOffsetSpeed() * (getEndDegree() - getStartDegree())

        Log.d("onDraw", "onDraw: $position")

        canvas.drawArc(
            speedometerRect,
            getStartDegree().toFloat(),
            (getEndDegree() - getStartDegree()).toFloat(),
            false,
            darkPaint
        )

        canvas.drawArc(
            speedometerRect,
            getStartDegree().toFloat(),
            position,
            false,
            speedometerPaint
        )
        canvas.drawArc(
            rectF,
            getStartDegree().toFloat(),
            position,
            false,
            blurPaint
        )


        val c = centerCircleColor
        circlePaint.color =
            Color.argb((Color.alpha(c) * .5f).toInt(), Color.red(c), Color.green(c), Color.blue(c))
        canvas.drawCircle(size * .5f, size * .5f, centerCircleRadius + dpTOpx(4f), circlePaint)
        circlePaint.color = c
        canvas.drawCircle(size * .5f, size * .5f, centerCircleRadius, circlePaint)
        drawIndicator(canvas)
        drawTicks(canvas, getStartDegree() + position)
    }

    override fun updateBackgroundBitmap() {
        val c = createBackgroundBitmapCanvas()
        initDraw()

        drawMarks(c)

        if (tickNumber > 0)
            drawTicks(c, 0f)
        else
            drawDefMinMaxSpeedPosition(c)
    }

    fun setState(state: String) {
        this.state = state
        if (state == "upload") {
            val matrix = Matrix()
            matrix.setRotate(90f, speedometerRect.centerX(), speedometerRect.centerY())
            val sweepGradient = SweepGradient(width / 2f, width / 4f, uploadColorsList, null)
            sweepGradient.setLocalMatrix(matrix)
            blurPaint.shader = sweepGradient
            speedometerPaint.shader = SweepGradient(width / 2f, width / 4f, uploadColorsList, null);
        } else {
            val matrix = Matrix()
            matrix.setRotate(90f, speedometerRect.centerX(), speedometerRect.centerY())
            val sweepGradient = SweepGradient(width / 2f, width / 4f, downloadColorsList, null)
            sweepGradient.setLocalMatrix(matrix)
            blurPaint.shader = sweepGradient
            speedometerPaint.shader =
                SweepGradient(width / 2f, width / 4f, downloadColorsList, null);
        }
        invalidate()
    }

    private fun updateSweep(): SweepGradient {
        val startColor = Color.argb(
            150,
            Color.red(speedometerPaint.color),
            Color.green(speedometerPaint.color),
            Color.blue(speedometerPaint.color)
        )
        val color2 = Color.argb(
            220,
            Color.red(speedometerPaint.color),
            Color.green(speedometerPaint.color),
            Color.blue(speedometerPaint.color)
        )
        val color3 = Color.argb(
            70,
            Color.red(speedometerPaint.color),
            Color.green(speedometerPaint.color),
            Color.blue(speedometerPaint.color)
        )
        val endColor = Color.argb(
            15,
            Color.red(speedometerPaint.color),
            Color.green(speedometerPaint.color),
            Color.blue(speedometerPaint.color)
        )
        val position = getOffsetSpeed() * (getEndDegree() - getStartDegree()) / 360f
        val sweepGradient = SweepGradient(
            size * .5f,
            size * .5f,
            intArrayOf(startColor, color2, speedometerPaint.color, color3, color3, endColor),
            floatArrayOf(0f, position * .5f, position, position, .99f, 1f)
        )
        val matrix = Matrix()
        matrix.postRotate(getStartDegree().toFloat(), size * .5f, size * .5f)
        sweepGradient.setLocalMatrix(matrix)
        return sweepGradient
    }

    private fun updateRadial() {
        val centerColor = Color.argb(
            160,
            Color.red(pointerColor),
            Color.green(pointerColor),
            Color.blue(pointerColor)
        )
        val edgeColor = Color.argb(
            10,
            Color.red(pointerColor),
            Color.green(pointerColor),
            Color.blue(pointerColor)
        )
        val pointerGradient = RadialGradient(
            width * .5f,
            speedometerWidth * .5f + dpTOpx(8f) + padding.toFloat(),
            speedometerWidth * .5f + dpTOpx(8f),
            intArrayOf(centerColor, edgeColor),
            floatArrayOf(.4f, 1f),
            Shader.TileMode.CLAMP
        )
        pointerBackPaint.shader = pointerGradient
    }

    fun getSpeedometerColor(): Int {
        return speedometerColor
    }

    fun setSpeedometerColor(speedometerColor: Int) {
        this.speedometerPaint.color = speedometerColor
        invalidate()
    }

    fun getPointerColor(): Int {
        return pointerColor
    }

    fun setPointerColor(pointerColor: Int) {
        this.pointerColor = pointerColor
        pointerPaint.color = pointerColor
        updateRadial()
        if (isAttachedToWindow)
            invalidate()
    }
}
