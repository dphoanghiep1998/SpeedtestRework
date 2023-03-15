package com.example.speedtest_rework.base.dialog

import android.R
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.speedtest_rework.BuildConfig
import com.example.speedtest_rework.common.extensions.NativeType
import com.example.speedtest_rework.common.utils.clickWithDebounce
import com.example.speedtest_rework.databinding.CustomDialogBoxBinding
import com.example.speedtest_rework.databinding.DialogExitAppBinding
import com.gianghv.libads.NativeAdMediumView
import com.gianghv.libads.NativeAdSmallView
import com.gianghv.libads.NativeAdsManager

class DialogExitApp(
    context: Context, private val callback: ExitCallback?,
) : Dialog(context) {
    private lateinit var binding: DialogExitAppBinding

    interface ExitCallback {
        fun exitAction()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogExitAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawableResource(R.color.transparent)

        showNativeAds(context,binding.nativeAdMediumView)
        binding.btnExit.clickWithDebounce {
            callback?.exitAction()
        }

    }

    fun showNativeAds(
        context: Context,
        view: NativeAdMediumView?,
    ) {
        val mNativeAdManager = NativeAdsManager(
            context,
            BuildConfig.native_back_exit_id1,
            BuildConfig.native_back_exit_id2,
            BuildConfig.native_back_exit_id3
        )
        view?.let {
            it.showShimmer(true)
            mNativeAdManager.loadAds(onLoadSuccess = { nativeAd ->
                it.visibility = View.VISIBLE
                it.showShimmer(false)
                it.setNativeAd(nativeAd)
                it.isVisible = true
            }, onLoadFail = { _ ->
                it.errorShimmer()
                it.visibility = View.GONE
            })
        }

    }
}
