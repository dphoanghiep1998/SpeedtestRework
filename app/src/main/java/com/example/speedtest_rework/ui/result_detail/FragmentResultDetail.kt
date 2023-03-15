package com.example.speedtest_rework.ui.result_detail

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.dialog.*
import com.example.speedtest_rework.common.custom_view.UnitType
import com.example.speedtest_rework.common.extensions.NativeType
import com.example.speedtest_rework.common.extensions.showInterAds
import com.example.speedtest_rework.common.extensions.showNativeAds
import com.example.speedtest_rework.common.utils.Constant
import com.example.speedtest_rework.common.utils.DateTimeUtils
import com.example.speedtest_rework.common.utils.clickWithDebounce
import com.example.speedtest_rework.common.utils.format
import com.example.speedtest_rework.data.database.file_provider.DatabaseHelper
import com.example.speedtest_rework.data.model.HistoryModel
import com.example.speedtest_rework.databinding.FragmentDetailResultBinding
import com.example.speedtest_rework.viewmodel.ScanStatus
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel

class FragmentResultDetail : DialogFragment(), ConfirmDialog.ConfirmCallback, AskRateCallBack,
    RateCallBack {
    private lateinit var binding: FragmentDetailResultBinding
    private var testModel: HistoryModel = HistoryModel()
    private var fromSpeedTestFragment: Boolean = false
    private val viewModel: SpeedTestViewModel by activityViewModels()
    private var askRateDialog: AskRateDialog? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = ConstraintLayout(requireContext())
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(getColor(R.color.background_main)))
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailResultBinding.inflate(inflater, container, false)
        getDataFromBundle()
        changeBackPressCallBack()
        observeUnitType()
        askRateDialog = AskRateDialog(requireContext(), this)
        initView()
        showNativeAds(binding.nativeAdMediumView,null,null,null,NativeType.SPEED_TEST_RESULT )
        viewModel.speedTestDone = true
        return binding.root
    }


    private fun handleShowRate() {
        if (!viewModel.userActionRate) {
            val askRateDialog = AskRateDialog(requireContext(), this)
            Handler(Looper.getMainLooper()).postDelayed({
                askRateDialog.show()
            }, 1000)
        }
    }

    private fun observeUnitType() {
        viewModel.unitType.observe(viewLifecycleOwner) {
            binding.tvDownloadCurrency.text = getString(it.unit)
            binding.tvUploadCurrency.text = getString(it.unit)
        }
    }

    private fun getColor(resId: Int): Int {
        return ContextCompat.getColor(requireContext(), resId)
    }

    private fun getDataFromBundle() {
        val bundle: Bundle? = this.arguments
        if (bundle != null) {
            testModel = bundle.getParcelable(Constant.KEY_TEST_MODEL)!!
            fromSpeedTestFragment = bundle.getBoolean(Constant.KEY_FROM_SPEED_TEST_FRAGMENT, false)
            if (fromSpeedTestFragment) {
                handleShowRate()
            }
        }
    }

    private fun initView() {

        binding.btnBack.clickWithDebounce {
            findNavController().popBackStack()
        }
        binding.btnShare.clickWithDebounce {
            exportFile()
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
        binding.tvDownloadValue.text = format(convert(testModel.download))
        binding.tvUploadValue.text = format(convert(testModel.upload))
        binding.tvPingCount.text = "${testModel.ping} ms"
        binding.tvJitterCount.text = "${testModel.jitter} ms"
        binding.tvLossCount.text = "${testModel.loss} %"
        binding.tvConnectNameValue.text = testModel.name_network
        binding.tvIspValue.text = testModel.isp
        binding.tvInternalIpValue.text = testModel.internalIP
        binding.tvExternalIpValue.text = testModel.externalIP

        binding.btnDelete.clickWithDebounce {
            val customDialog = ConfirmDialog(
                requireContext(),
                this,
                getString(R.string.delete_title),
                getString(R.string.delete_content),
                getString(R.string.YES),
                getString(R.string.NO)
            )
            customDialog.show()
        }

        if (!fromSpeedTestFragment) {
            binding.btnScanAgain.visibility = View.GONE
            binding.btnClose.visibility = View.GONE
        } else {

            binding.btnScanAgain.visibility = View.VISIBLE
            binding.btnClose.visibility = View.VISIBLE

            binding.btnScanAgain.clickWithDebounce {
                findNavController().popBackStack()
                viewModel.setScanStatus(ScanStatus.SCANNING)
            }

            binding.btnClose.clickWithDebounce {
                findNavController().popBackStack()
                viewModel.setScanStatus(ScanStatus.HARD_RESET)

            }
        }
    }

    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun setProgressBarColor(progressBar: SeekBar, state: Int) {
        when (state) {
            0 -> {
                progressBar.progressDrawable =
                    ContextCompat.getDrawable(requireContext(), R.drawable.seekbar_strong)
                progressBar.thumb =
                    ContextCompat.getDrawable(requireContext(), R.drawable.custom_thumb_strong)

            }
            1 -> {
                progressBar.progressDrawable =
                    ContextCompat.getDrawable(requireContext(), R.drawable.seekbar)
                progressBar.thumb =
                    ContextCompat.getDrawable(requireContext(), R.drawable.custom_thumb)


            }
            2 -> {
                progressBar.progressDrawable =
                    ContextCompat.getDrawable(requireContext(), R.drawable.seekbar_weak)
                progressBar.thumb =
                    ContextCompat.getDrawable(requireContext(), R.drawable.custom_thumb_weak)

            }
        }
    }

    private fun setSignalTextColors(state: Int) {
        when (state) {
            0 -> {
                binding.tvSignalStrength.setShader(
                    ContextCompat.getColor(requireContext(), R.color.wifi_strong_start),
                    ContextCompat.getColor(requireContext(), R.color.wifi_strong_end)
                )
            }
            1 -> {
                binding.tvSignalStrength.setShader(
                    intArrayOf(
                        ContextCompat.getColor(requireContext(), R.color.wifi_normal_start),
                        ContextCompat.getColor(requireContext(), R.color.wifi_normal_center),
                        ContextCompat.getColor(requireContext(), R.color.wifi_normal_end)
                    )
                )
            }
            2 -> {
                binding.tvSignalStrength.setShader(
                    ContextCompat.getColor(requireContext(), R.color.wifi_weak_start),
                    ContextCompat.getColor(requireContext(), R.color.wifi_weak_end)
                )
            }
        }
    }

    override fun negativeAction() {
        //Do nothing
    }

    override fun positiveAction() {
        viewModel.deleteHistoryAction(testModel)
        findNavController().popBackStack()
    }

    private fun convertMbpsToMbs(value: Double): Double {
        return value * .125
    }

    private fun convertMbpsToKbs(value: Double): Double {
        return value * 125
    }

    private fun convert(value: Double): Double {
        if (viewModel.unitType.value == UnitType.MBS) {
            return convertMbpsToMbs(value)
        }
        if (viewModel.unitType.value == UnitType.KBS) {
            return convertMbpsToKbs(value)
        }
        return value
    }

    override fun onDeny() {
        viewModel.userActionRate = true
    }

    override fun onAgree() {
        val rateDialog = RateDialog(requireContext(), this)
        rateDialog.show()
    }

    override fun onClickRateUs(star: Int) {
        viewModel.userActionRate = true
        if (star < 4) {
            return
        }
        openLink(Constant.URL_APP)
    }

    private fun openLink(strUri: String?) {
        try {
            val uri = Uri.parse(strUri)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun exportFile() {
        val databaseHelper = DatabaseHelper(requireContext(), viewModel.unitType.value!!)
        databaseHelper.exportDatabaseToCSVFile(testModel)
    }

    override fun onDestroy() {
        super.onDestroy()
        askRateDialog?.dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        askRateDialog?.dismiss()
    }


}