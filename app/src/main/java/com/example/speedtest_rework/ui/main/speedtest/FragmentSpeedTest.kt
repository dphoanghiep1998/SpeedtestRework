package com.example.speedtest_rework.ui.main.speedtest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.speedtest_rework.databinding.FragmentSpeedTestBinding

class FragmentSpeedTest : Fragment() {
    private lateinit var binding: FragmentSpeedTestBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSpeedTestBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}