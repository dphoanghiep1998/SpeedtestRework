package com.example.speedtest_rework.common.utils

import android.app.Activity
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import com.example.speedtest_rework.CustomApplication
import com.example.speedtest_rework.common.custom_view.ConnectionType

val Activity.customApplication: CustomApplication
    get() = application as CustomApplication

fun View.clickWithDebounce(debounceTime: Long = 600L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

fun View.touchWithDebounce(
    debounceTime: Long = 500L,
    action_press: () -> Unit,
    action_release: () -> Unit,
    action_press_release: () -> Unit
) {
    this.setOnTouchListener(object : View.OnTouchListener {
        private var lastClickTime: Long = 0
        override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {

            when (p1?.action) {
                MotionEvent.ACTION_DOWN -> {
                    action_press()
                }
                MotionEvent.ACTION_UP -> {
                    action_press_release()
                    if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return true
                    else action_release()
                    lastClickTime = SystemClock.elapsedRealtime()

                    return true
                }
                MotionEvent.ACTION_CANCEL -> {
                    action_press_release()
                }
                MotionEvent.ACTION_OUTSIDE -> {
                    action_press_release()
                }
            }
            return true
        }
    })

}
