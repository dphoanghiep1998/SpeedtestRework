package com.github.anastr.speedviewlib.components.indicators

import android.content.Context
import android.graphics.*
import android.opengl.ETC1.getHeight




/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */

class KiteIndicator(context: Context) : Indicator<KiteIndicator>(context) {

    private val indicatorPath = Path()
    private var bottomY: Float = 0.toFloat()

    init {
        width = dpTOpx(12f)
    }

    override fun getBottom(): Float {
        return bottomY
    }

    override fun draw(canvas: Canvas) {
        indicatorPaint.shader = LinearGradient(

            getCenterX() - (width-10) / 2,
            getViewSize()  / 2+30,
            getCenterX() - 10f,
            getViewSize() * 0.25f,
            0xFF1A1B2F.toInt(),
            Color.WHITE,
            Shader.TileMode.CLAMP
        )
        canvas.drawPath(indicatorPath, indicatorPaint)
    }

    override fun updateIndicator() {
        indicatorPath.reset()
        indicatorPath.moveTo(getCenterX() - 10f, getViewSize() * 0.25f)
        indicatorPath.lineTo(getCenterX() + 10f, getViewSize() * 0.25f)
        indicatorPath.lineTo(getCenterX() + (width + 10) / 2, getViewSize() / 2 +30)
        indicatorPath.lineTo(getCenterX() - (width-10) / 2, getViewSize()  / 2+30)
        indicatorPaint.color = color
    }

    override fun setWithEffects(withEffects: Boolean) {
        if (withEffects && !speedometer!!.isInEditMode)
            indicatorPaint.maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.SOLID)
        else
            indicatorPaint.maskFilter = null
    }
}
