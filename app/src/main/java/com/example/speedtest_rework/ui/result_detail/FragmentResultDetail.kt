package com.example.speedtest_rework.ui.result_detail

import android.animation.ValueAnimator
import android.graphics.PorterDuff
import android.graphics.drawable.ClipDrawable
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

        binding.pbSignal.isEnabled = false
        binding.tvSignalStrength.setTextColor(
            if (testModel.download >= 40) getColor(R.color.signal_good) else if (testModel.download < 40 && testModel.download >= 20) getColor(
                R.color.signal_normal
            ) else getColor(R.color.signal_poor)
        )
        setProgressBarColor(
            binding.pbSignal,
            if (testModel.download >= 40) getColor(R.color.signal_good) else if (testModel.download < 40 && testModel.download >= 20) getColor(
                R.color.signal_normal
            ) else getColor(R.color.signal_poor)
        )
        binding.tvTime.text = DateTimeUtils.getDateConvertedToResult(testModel.time)
        binding.tvSignalStrength.text = status
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
                findNavController().previousBackStackEntry?.savedStateHandle?.set(Constant.KEY_SCAN_AGAIN,true)
                findNavController().popBackStack()
            }
        }
    }


    private fun setProgressBarColor(progressBar: SeekBar, newColor: Int) {
        val ld = progressBar.progressDrawable as LayerDrawable
        val d1 = ld.findDrawableByLayerId(android.R.id.progress) as ClipDrawable
        d1.setColorFilter(newColor, PorterDuff.Mode.SRC_IN)
        val thumb = getDrawable(R.drawable.custom_thumb) as LayerDrawable
        val bgThumb = thumb.findDrawableByLayerId(R.id.bg_thumb)
        bgThumb.setColorFilter(newColor, PorterDuff.Mode.SRC_IN)
        progressBar.thumb = thumb
    }

    override fun negativeAction() {
        TODO("Not yet implemented")
    }

    override fun positiveAction() {
        viewModel.deleteHistoryAction(testModel)
        findNavController().popBackStack()
    }


}