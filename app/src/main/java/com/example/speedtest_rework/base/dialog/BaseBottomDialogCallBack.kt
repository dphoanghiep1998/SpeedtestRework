package com.example.speedtest_rework.base.dialog

import android.app.Dialog
import android.content.Context

interface BackPressBottomSheetDialogCallback {
    fun shouldInterceptBackPress(): Boolean
    fun onBackPressIntercepted()
}

class BaseBottomDialogCallBack(context: Context,
                       private val callback: BackPressBottomSheetDialogCallback) :
    Dialog(context) {

    override fun onBackPressed() {
        if (callback.shouldInterceptBackPress()) callback.onBackPressIntercepted()
        else super.onBackPressed()
    }
}