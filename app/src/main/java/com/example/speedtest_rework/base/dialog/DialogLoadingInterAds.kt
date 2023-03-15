package com.example.speedtest_rework.base.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import com.example.speedtest_rework.R

class DialogLoadingInterAds(context: Context): BackPressDialogCallBack {

    private var dialog: Dialog? = null

    init {
        dialog = Dialog(context)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.dialog_load_ads)
        val window = dialog?.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.CENTER
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        window.attributes = wlp
        dialog?.window!!.setBackgroundDrawableResource(R.color.white)
        dialog?.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT
        )
    }


    fun showDialog(){
        dialog?.show()
    }
    fun hideDialog(){
        dialog?.dismiss()
    }


    override fun shouldInterceptBackPress(): Boolean {
        return true
    }

    override fun onBackPressIntercepted() {
        //do nothing
    }


}