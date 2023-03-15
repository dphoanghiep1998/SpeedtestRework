package com.example.speedtest_rework.base.dialog

import android.app.Dialog
import android.content.Context

interface BackPressDialogCallBack {
    fun shouldInterceptBackPress(): Boolean
    fun onBackPressIntercepted()
}

open class DialogCallBack(context: Context,
                          private val callback: BackPressDialogCallBack
) :
    Dialog(context) {

    override fun onBackPressed() {
        if (callback.shouldInterceptBackPress()) callback.onBackPressIntercepted()
        else super.onBackPressed()
    }
}