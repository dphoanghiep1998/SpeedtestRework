package com.example.speedtest_rework.ui.ping_test.advanced_ping_test

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.Log
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler


class BarChartCustomRender(
    chart: BarDataProvider?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?,
    private val colors: List<Int>
) : BarChartRenderer(chart, animator, viewPortHandler) {
    private val mPaint = Paint(1)
    private val textPaint = Paint(1)
    private lateinit var rect: RectF

    init {

        mPaint.style = Paint.Style.STROKE

        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 25f
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.typeface = Typeface.DEFAULT_BOLD


    }

    override fun drawValue(c: Canvas?, valueText: String?, x: Float, y: Float, color: Int) {
        return
    }



    override fun drawValues(c: Canvas?) {
        super.drawValues(c)
        if (mBarBuffers.isNotEmpty()) {

            val buffer = mBarBuffers[0]
            var left: Float
            var right: Float
            var top: Float
            var bottom: Float
            var j = 0
            var colorIndex = 0

            while (j < buffer.buffer.size * mAnimator.phaseX) {
                mPaint.color = colors[colorIndex]
                textPaint.color = colors[colorIndex]

                left = buffer.buffer[j]
                right = buffer.buffer[j + 2]
                top = buffer.buffer[j + 1]
                bottom = buffer.buffer[j + 3]

                val textValue = mChart.barData.getDataSetByIndex(0).valueFormatter.getBarLabel(
                    mChart.barData.getDataSetByIndex(0).getEntryForIndex(colorIndex)
                )

                val trueValue = textValue.replace(",",".").toFloat() - 10
                if (trueValue.toString() != "" && trueValue.toString() != "0.0" && trueValue.toString() != "0") {
                    rect = RectF(left - 10, top - 60, right + 10, top - 10)
                    c?.drawRoundRect(rect, 5f, 5f, mPaint)
//                    val number3digits:Double = String.format("%.2f", trueValue).toDouble()
                    c?.drawText(
                        trueValue.toString(), (left + right) / 2, top - 25, textPaint
                    )
                }else{
                    rect = RectF(left - 10, top - 60, right + 10, top - 10)
                    c?.drawRoundRect(rect, 5f, 5f, mPaint)
                    c?.drawText(
                        "NaN", (left + right) / 2, top - 25, textPaint
                    )
                }

                j += 4
                colorIndex++


            }
        }


    }


}