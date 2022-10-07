package com.example.speedtest_rework.ui.result_detail

import android.animation.ValueAnimator
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.dialog.ConfirmDialog
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.Constant
import com.example.speedtest_rework.common.DateTimeUtils
import com.example.speedtest_rework.data.model.HistoryModel
import com.example.speedtest_rework.databinding.FragmentDetailResultBinding
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel

class FragmentResultDetail : BaseFragment(), ConfirmDialog.ConfirmCallback {
    private lateinit var binding: FragmentDetailResultBinding
    private lateinit var testModel: HistoryModel
    private var fromSpeedTestFragment: Boolean? = false
    private val viewModel: SpeedTestViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailResultBinding.inflate(inflater, container, false)
        getDataFromBundle()
        initView()
        return binding.root
    }


    private fun getDataFromBundle() {
        val bundle: Bundle? = this.arguments
        if (bundle != null) {
            testModel = bundle.getParcelable("testModel")!!
            fromSpeedTestFragment = bundle.getBoolean("from_speedTest_fragment")
        }
    }

    private fun initView() {
        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }
        val progress =
            if (testModel.download >= 40) 100 else if (testModel.download < 40 && testModel.download >= 20) 50 else 0
        val anim = ValueAnimator.ofInt(0, progress)
        anim.duration = 600
        anim.addUpdateListener { animation: ValueAnimator ->
            val animProgress = animation.animatedValue as Int
            binding.pbSignal.progress = animProgress
        }
        anim.start()
        val status =
            if (testModel.download >= 40) getString(R.string.signal_strong) else if (testModel.download < 40 && testModel.download >= 20) getString(
                R.string.signal_normal
            ) else getString(R.string.signal_weak)
        binding.tvSignalStrength.text = status
        binding.pbSignal.isEnabled = false

        setSignalTextColors(if (testModel.download >= 40) 0 else if (testModel.download < 40 && testModel.download >= 20) 1 else 2)

        setProgressBarColor(
            binding.pbSignal,
            if (testModel.download >= 40) 0 else if (testModel.download < 40 && testModel.download >= 20) 1 else 2
        )
        binding.tvTime.text = DateTimeUtils.getDateConvertedToResult(testModel.time)
        binding.tvDownloadValue.text = testModel.download.toString()
        binding.tvUploadValue.text = testModel.upload.toString()
        binding.tvPingCount.text = testModel.ping.toString() + " ms"
        binding.tvJitterCount.text = testModel.jitter.toString() + " ms"
        binding.tvLossCount.text = testModel.loss.toString() + " %"
        binding.tvConnectNameValue.text = testModel.network
        binding.tvConnectNameValue.text = testModel.name_network
        binding.tvIspValue.text = testModel.isp
        binding.tvInternalIpValue.text = testModel.internalIP
        binding.tvExternalIpValue.text = testModel.externalIP

        binding.btnDelete.setOnClickListener {
            val customDialog = ConfirmDialog(
                requireContext(),
                this,
                "DELETE RESULT",
                "This result will be deleted from your history",
                "YES",
                "NO"
            )
            customDialog.show()
        }

        if (fromSpeedTestFragment == null || fromSpeedTestFragment == false) {
            binding.btnScanAgain.visibility = View.GONE
        } else {
            binding.btnScanAgain.visibility = View.VISIBLE
            binding.btnScanAgain.setOnClickListener {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    Constant.KEY_SCAN_AGAIN,
                    true
                )
                findNavController().popBackStack()
            }
        }
    }


    private fun setProgressBarColor(progressBar: SeekBar, state: Int) {
        when (state) {
            0 -> {
                progressBar.progressDrawable = getDrawable(R.drawable.seekbar_strong)
                progressBar.thumb = getDrawable(R.drawable.custom_thumb_strong)

            }
            1 -> {
                progressBar.progressDrawable = getDrawable(R.drawable.seekbar)
                progressBar.thumb = getDrawable(R.drawable.custom_thumb)


            }
            2 -> {
                progressBar.progressDrawable = getDrawable(R.drawable.seekbar_weak)
                progressBar.thumb = getDrawable(R.drawable.custom_thumb_weak)

            }
        }
    }

    private fun setSignalTextColors(state: Int) {
        when (state) {
            0 -> {
                binding.tvSignalStrength.setShader(
                    getColor(R.color.wifi_strong_start),
                    getColor(R.color.wifi_strong_end)
                )
            }
            1 -> {
                binding.tvSignalStrength.setShader(
                    intArrayOf(
                        getColor(R.color.wifi_normal_start),
                        getColor(R.color.wifi_normal_center),
                        getColor(R.color.wifi_normal_end)
                    )
                )
            }
            2 -> {
                binding.tvSignalStrength.setShader(
                    getColor(R.color.wifi_weak_start),
                    getColor(R.color.wifi_weak_end)
                )
            }
        }
    }

    override fun negativeAction() {
        TODO("Not yet implemented")
    }

    override fun positiveAction() {
        viewModel.deleteHistoryAction(testModel)
        findNavController().popBackStack()
    }


}