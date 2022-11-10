package com.example.speedtest_rework.common.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.speedtest_rework.R
import com.example.speedtest_rework.databinding.LayoutSignalMeterBinding

class SignalTest(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    private var binding: LayoutSignalMeterBinding
    private var currentTicks = listOf(0f, .25f, .5f, .75f, 1f)


    init {
        val inflater = LayoutInflater.from(context)
        binding = LayoutSignalMeterBinding.inflate(inflater, this)
        initView()
    }

    fun setValue(value: Float) {
        binding.speedView.speedTo(value)
        binding.tvSignalValue.text = value.toString()
        if (binding.tvDbm.visibility == GONE) {
            binding.tvDbm.visibility = visibility
        }

    }

    private fun initView() {
        binding.tvSignalValue.text = "- -"
        binding.tvDbm.visibility = GONE
        binding.speedView.setStrokeCapRound()

    }

    fun initSignalView() {
        binding.tvSignalValue.text = "- -"
        binding.tvDbm.visibility = GONE
        binding.speedView.ticks = currentTicks
        binding.speedView.maxSpeed = 0f
        binding.speedView.minSpeed = -80f
        binding.speedView.setStrokeCapRound()
        binding.speedView.setState("download")
        binding.speedView.speedTo(-80f)
    }

}