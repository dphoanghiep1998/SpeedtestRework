package com.example.speedtest_rework.ui.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.ui.main.result_history.FragmentResults

class ViewPagerAdapter(
    list: ArrayList<BaseFragment>,
    fm: FragmentManager, lifecycle: Lifecycle
) :
    FragmentStateAdapter(fm, lifecycle) {


    private val fragmentList: ArrayList<BaseFragment> = list
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}