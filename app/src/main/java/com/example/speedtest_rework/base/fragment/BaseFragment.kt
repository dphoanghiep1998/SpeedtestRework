package com.example.speedtest_rework.base.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.activity.BaseActivity
import com.example.speedtest_rework.base.viewmodel.BaseViewModel
import com.example.speedtest_rework.common.utils.EventObserver


open class BaseFragment : Fragment() {
    protected fun toastShort(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    protected fun toastLong(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    protected fun hideView(view: View) {
        view.visibility = View.GONE
    }

    protected fun showView(view: View) {
        view.visibility = android.view.View.VISIBLE

    }

    protected fun navigateToPage(fragmentId: Int, actionId: Int, bundle: Bundle? = null) {
        if (fragmentId == findNavController().currentDestination?.id) {
            if (bundle != null) {
                findNavController().navigate(actionId, bundle)
                return
            }
            findNavController().navigate(actionId)
        }


    }


    protected fun getColor(resId: Int): Int {
        return ContextCompat.getColor(requireContext(), resId)
    }

    protected fun getDrawable(resId: Int): Drawable? {
        return ContextCompat.getDrawable(requireContext(), resId)
    }


}