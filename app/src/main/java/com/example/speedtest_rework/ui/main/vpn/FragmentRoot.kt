package com.example.speedtest_rework.ui.main.vpn

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.speedtest_rework.R

class FragmentRoot : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_root, container, false)
    }


    override fun onResume() {

        super.onResume()
        val manager = requireActivity().supportFragmentManager
        manager.beginTransaction().add(R.id.root_fragment, FragmentVpn()).commit()
    }
}