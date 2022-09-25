package com.example.speedtest_rework.ui.main.vpn

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.speedtest_rework.R
import com.example.speedtest_rework.activities.MainActivity
import com.example.speedtest_rework.databinding.FragmentVpnBinding

class FragmentVpn : Fragment() {

   private lateinit var binding: FragmentVpnBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVpnBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.containerInfor.setOnClickListener {
            if (binding.containerHidden.visibility === View.VISIBLE) {
                TransitionManager.beginDelayedTransition(
                    binding.containerInfor,
                    AutoTransition()
                )
                binding.containerHidden.visibility = View.GONE
                binding.imvArrowDown2.rotation = 0f
            } else {
                TransitionManager.beginDelayedTransition(
                    binding.containerInfor,
                    AutoTransition()
                )
                binding.containerHidden.visibility = View.VISIBLE
                binding.imvArrowDown2.rotation = 180f
            }
        }
        binding.containerSelector.setOnClickListener { view ->
            TransitionManager.beginDelayedTransition(
                view as ViewGroup,
                AutoTransition()
            )
            val fragmentManager =
                requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.root_fragment, FragmentChangeLocation(), null).addToBackStack("vpn")
                .commit()
//            (requireActivity() as MainActivity).showBackBtn()
        }
    }
}