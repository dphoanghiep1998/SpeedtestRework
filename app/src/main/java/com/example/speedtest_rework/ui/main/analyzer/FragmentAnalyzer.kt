package com.example.speedtest_rework.ui.main.analyzer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.speedtest_rework.databinding.FragmentAnalyzerBinding


class FragmentAnalyzer : Fragment() {
private lateinit var binding : FragmentAnalyzerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAnalyzerBinding.inflate(inflater,container,false)
        return binding.root
    }


}
