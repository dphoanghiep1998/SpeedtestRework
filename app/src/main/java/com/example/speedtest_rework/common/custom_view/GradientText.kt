package com.example.speedtest_rework.common.custom_view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.speedtest_rework.R


open class GradientText(context: Context, attributeSet: AttributeSet?) :
    AppCompatTextView(context, attributeSet) {
    private val mContext: Context
    private val textPaint: Paint
    private var firstColor: Int = 0xFFFFFFFF.toInt()
    private var secondColor: Int = 0xFFFFFFFF.toInt()

    init {
        mContext = context
        textPaint = this.paint

        attributeSet?.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.GradientText)
            try {
                firstColor =
                    attributes.getColor(R.styleable.GradientText_colorFirst, 0xFFFFFFFF.toInt())
                secondColor =
                    attributes.getColor(R.styleable.GradientText_colorSecond, 0xFFFFFFFF.toInt())
                setShader(firstColor, secondColor)
                this.setTextColor(firstColor)
            } finally {
                attributes.recycle()
            }


        }
    }

    fun setShader(colorFirst: Int, colorSecond: Int) {
        val shader: Shader = LinearGradient(
            0f,
            0f,
            0f,
            this.lineHeight.toFloat(),
            colorFirst,
            colorSecond,
            Shader.TileMode.CLAMP
        )
        textPaint.shader = shader
    }

    fun setShader(colorList: IntArray) {
        val shader: Shader = LinearGradient(
            0f,
            0f,
            0f,
            this.lineHeight.toFloat(),
            colorList,
            null,
            Shader.TileMode.CLAMP
        )
        textPaint.shader = shader
    }

    fun disableGradient() {
        this.setTextColor(Color.WHITE)
        textPaint.shader = null
    }

    fun enabledGradient() {
        val shader: Shader = LinearGradient(
            0f,
            0f,
            0f,
            this.lineHeight.toFloat(),
            firstColor,
            secondColor,
            Shader.TileMode.CLAMP
        )
        textPaint.shader = shader
        this.setTextColor(firstColor)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

}