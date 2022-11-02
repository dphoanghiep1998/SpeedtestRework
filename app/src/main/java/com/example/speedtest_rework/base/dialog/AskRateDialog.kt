package com.example.speedtest_rework.base.dialog

import android.R
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import com.example.speedtest_rework.databinding.BottomsheetDialogBinding

interface AskRateCallBack {
    fun onDeny()
    fun onAgree()
}

class AskRateDialog(context: Context, private val callback: AskRateCallBack) : Dialog(context) {

    private lateinit var binding: BottomsheetDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = BottomsheetDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        window?.setLayout(
            (context.resources.displayMetrics.widthPixels),
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(R.color.transparent)
        binding.containerMain.setPadding(
            0,
            toDp(16),
            0,
            if (getNavBarHeight() > 0) getNavBarHeight() else 16
        )
        binding.root.setOnClickListener {
            dismiss()
        }
        binding.btnAgree.setOnClickListener {
            callback.onAgree()
            dismiss()
        }
        binding.btnDeny.setOnClickListener {
            callback.onDeny()
            dismiss()
        }
    }

    private fun getNavBarHeight(): Int {
        val resources: Resources = context.resources
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }

    private fun toDp(sizeInDp: Int): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (sizeInDp * scale + 0.5f).toInt()
    }

}