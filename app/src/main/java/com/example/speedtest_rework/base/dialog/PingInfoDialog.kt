package com.example.speedtest_rework.base.dialog

import android.R
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.example.speedtest_rework.common.utils.clickWithDebounce
import com.example.speedtest_rework.databinding.DialogPingInfoBinding

interface PingInfoCallBack {
    fun onOk()
}

class PingInfoDialog(
    context: Context,
    private val url: String,
    private val packetLoss: String?,
    private val packetSent: String?,
    private val packetReceive: String?,
    private val minLatency: String?,
    private val avgLatency: String?,
    private val maxLatency: String?,
) : Dialog(context) {
    private lateinit var binding: DialogPingInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogPingInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawableResource(R.color.transparent)
        if (packetReceive == "0") {
            binding.tvTitle.text = "Fail to connect to \n${url}"
            binding.tvMs1.visibility = View.GONE
            binding.tvMs2.visibility = View.GONE
            binding.tvMs3.visibility = View.GONE
        } else {
            binding.tvTitle.text = "Success to connect to \n${url}"
            binding.tvMs1.visibility = View.VISIBLE
            binding.tvMs2.visibility = View.VISIBLE
            binding.tvMs3.visibility = View.VISIBLE

        }
        binding.tvPacketLossValue.text = packetLoss.toString()
        binding.tvPacketReceivedValue.text = packetReceive
        binding.tvPacketSentValue.text = packetSent
        binding.tvAvgLatencyValue.text = avgLatency
        binding.tvMinLatencyValue.text = minLatency
        binding.tvMaxLatencyValue.text = maxLatency


        binding.tvOk.clickWithDebounce {
            dismiss()
        }
        binding.root.clickWithDebounce {
            dismiss()
        }
    }
}