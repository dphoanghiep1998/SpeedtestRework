package com.example.speedtest_rework.base.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import com.example.speedtest_rework.databinding.DialogSignalInfoBinding
import com.example.speedtest_rework.databinding.PingPopupInfoBinding

class PingInfoPopup(context: Context) : Dialog(context) {
    private lateinit var binding:PingPopupInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = PingPopupInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}