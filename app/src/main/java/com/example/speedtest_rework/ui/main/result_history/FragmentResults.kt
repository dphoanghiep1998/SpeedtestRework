package com.example.speedtest_rework.ui.main.result_history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.speedtest_rework.databinding.FragmentResultsBinding

class FragmentResults : Fragment() {
    private lateinit var binding: FragmentResultsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentResultsBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

}