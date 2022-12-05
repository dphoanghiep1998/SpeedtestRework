package com.example.speedtest_rework.common.utils

import android.app.Activity
import android.os.SystemClock
import android.view.View
import com.example.speedtest_rework.CustomApplication

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
