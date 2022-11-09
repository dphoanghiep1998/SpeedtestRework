package com.example.speedtest_rework.ui.signal_test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.databinding.FragmentSignalTestBinding

class FragmentSignalTest : BaseFragment() {
    private lateinit var binding: FragmentSignalTestBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignalTestBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }
    private fun initView(){
        initButton()
    }

    private fun initButton() {
        binding.btnBack.setOnClickListener{
            findNavController().popBackStack()
        }
    }
}