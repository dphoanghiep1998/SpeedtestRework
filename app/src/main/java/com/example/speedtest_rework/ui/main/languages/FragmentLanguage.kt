package com.example.speedtest_rework.ui.main.languages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.utils.AppSharePreference.Companion.INSTANCE
import com.example.speedtest_rework.databinding.FragmentLanguageBinding
import java.util.*

class FragmentLanguage : BaseFragment(), TouchLanguageListener {
    private lateinit var binding: FragmentLanguageBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLanguageBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        val adapter = LanguageAdapter(requireContext(), this)
        adapter.setCurrentLanguage(getCurrentLanguage())
        binding.rcvLanguage.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvLanguage.adapter = adapter

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getCurrentLanguage(): String {
        return INSTANCE.getSavedLanguage(
            R.string.key_language,
            Locale.getDefault().language
        )
    }

    override fun onClickLanguage(locale: Locale) {
        INSTANCE.saveLanguage(R.string.key_language, locale.language)
    }

}