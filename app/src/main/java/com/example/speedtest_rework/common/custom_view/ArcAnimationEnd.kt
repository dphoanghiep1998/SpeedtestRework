package com.example.speedtest_rework.common.custom_view

import android.view.animation.Animation
import android.view.animation.Transformation
import com.github.anastr.speedviewlib.PointerSpeedometer

class ArcAnimationEnd(pointerSpeedometer: PointerSpeedometer) : Animation() {
    private var pointerSpeedometer: PointerSpeedometer ? = null
    private val oldAngle: Float
    override fun applyTransformation(interpolatedTime: Float, transformation: Transformation) {
        val angle = 405 - (270 * interpolatedTime).toInt()
        pointerSpeedometer?.setEndDegree(angle)
        pointerSpeedometer?.requestLayout()
    }

    init {
        this.pointerSpeedometer= pointerSpeedometer
        oldAngle = pointerSpeedometer.getStartDegree().toFloat()
    }
}