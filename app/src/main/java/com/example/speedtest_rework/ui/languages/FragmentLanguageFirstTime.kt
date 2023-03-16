package com.example.speedtest_rework.ui.languages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.CustomApplication
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.extensions.NativeType
import com.example.speedtest_rework.common.extensions.showNativeAds
import com.example.speedtest_rework.common.utils.AppSharePreference.Companion.INSTANCE
import com.example.speedtest_rework.common.utils.clickWithDebounce
import com.example.speedtest_rework.databinding.FragmentLanguageFirstTimeBinding
import com.example.speedtest_rework.ui.main.languages.LanguageAdapter
import com.example.speedtest_rework.ui.main.languages.TouchLanguageListener
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import java.util.*

class FragmentLanguageFirstTime : BaseFragment(), TouchLanguageListener {
    private var currentLanguage = ""
    private lateinit var binding: FragmentLanguageFirstTimeBinding
    private val viewModel: SpeedTestViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLanguageFirstTimeBinding.inflate(inflater, container, false)
        handleLanguageFirstSet()
        changeBackPressCallBack()
        observeNativeAds()
        initView()
        return binding.root
    }

    private fun handleLanguageFirstSet() {
        if (INSTANCE.getIsLangSet(false)) {
            navigateToPage(
                R.id.fragmentLanguageFirstTime,
                R.id.action_fragmentLanguageFirstTime_to_fragmentLocation
            )
        }
    }

    private fun observeNativeAds() {
        val app = requireActivity().application as CustomApplication
        if (app.nativeAD != null) {
            binding.nativeAdMediumView.showShimmer(true)
            binding.nativeAdMediumView.visibility = View.VISIBLE
            binding.nativeAdMediumView.showShimmer(false)
            binding.nativeAdMediumView.setNativeAd(app.nativeAD!!)
            binding.nativeAdMediumView.isVisible = true
        } else {
            showNativeAds(
                binding.nativeAdMediumView, null, null, null, NativeType.LANGUAGE
            )
        }
    }

    private fun initView() {
        val adapter = LanguageAdapter(requireContext(), this)
        adapter.setCurrentLanguage(getCurrentLanguage())
        binding.rcvLanguage.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvLanguage.adapter = adapter

        binding.btnCheck.clickWithDebounce {
                INSTANCE.saveIsLangSet(true)
                INSTANCE.saveLanguage(currentLanguage)
                startActivity(requireActivity().intent)
                requireActivity().finish()
        }
    }

    private fun getCurrentLanguage(): String {
        return INSTANCE.getSavedLanguage(Locale.getDefault().language)
    }

    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onClickLanguage(locale: Locale) {
        currentLanguage = locale.language
    }

}