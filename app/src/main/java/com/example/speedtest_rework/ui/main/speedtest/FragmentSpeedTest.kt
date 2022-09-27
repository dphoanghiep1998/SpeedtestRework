package com.example.speedtest_rework.ui.main.speedtest

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.NetworkUtils
import com.example.speedtest_rework.core.SpeedTest
import com.example.speedtest_rework.data.model.HistoryModel
import com.example.speedtest_rework.databinding.FragmentSpeedTestBinding
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import java.util.*


class FragmentSpeedTest : BaseFragment() {

    private lateinit var binding: FragmentSpeedTestBinding

    private val viewModel: SpeedTestViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpeedTestBinding.inflate(layoutInflater, container, false)
        return binding.root
    }




    private fun ObserveConnectivityChanged() {
        viewModel._isConnectivityChanged.observe(this, Observer {
            if (it) {
                NetworkUtils.isWifiConnected(requireContext())
            }
        })
    }

    private fun ObserveIsScanning() {
        viewModel._isScanning.observe(this, Observer {

        })
    }


    private fun format(d: Double): String? {
        val l: Locale
        l = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales[0]
        } else {
            resources.configuration.locale
        }
        return if (d < 200) String.format(l, "%.2f", d) else "" + Math.round(d)
    }


}