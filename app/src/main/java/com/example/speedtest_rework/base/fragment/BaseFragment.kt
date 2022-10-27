package com.example.speedtest_rework.base.fragment

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.activity.BaseActivity
import com.example.speedtest_rework.base.viewmodel.BaseViewModel
import com.example.speedtest_rework.common.utils.EventObserver


open class BaseFragment : Fragment() {

    protected fun hideView(view: View) {
        view.visibility = View.GONE
    }

    protected fun showView(view: View) {
        view.visibility = android.view.View.VISIBLE

    }

    protected fun navigateToPage(actionId: Int) {
        findNavController().navigate(actionId)
    }

    protected fun showLoading(isShow: Boolean) {
        val activity = requireActivity()
        if (activity is BaseActivity) {
            activity.showLoading(isShow)
        }
    }

    protected fun getColor(resId: Int): Int {
        return ContextCompat.getColor(requireContext(), resId)
    }

    protected fun getDrawable(resId: Int): Drawable? {
        return ContextCompat.getDrawable(requireContext(), resId)
    }


    protected fun showErrorMessage(message: String) {
        val activity = requireActivity()
        if (activity is BaseActivity) {
            activity.showErrorDialog(message)
        }
    }

    protected fun showNotify(title: String?, message: String) {
        val activity = requireActivity()
        if (activity is BaseActivity) {
            activity.showNotifyDialog(title ?: getDefaultNotifyTitle(), message)
        }
    }

    protected fun showNotify(titleId: Int = R.string.default_notify_title, messageId: Int) {
        val activity = requireActivity()
        if (activity is BaseActivity) {
            activity.showNotifyDialog(titleId, messageId)
        }
    }


    protected fun registerObserverLoadingMoreEvent(
        viewModel: BaseViewModel,
        viewLifecycleOwner: LifecycleOwner
    ) {
        viewModel.isLoadingMore.observe(viewLifecycleOwner, EventObserver { isShow ->
            showLoadingMore(isShow)
        })
    }

    protected fun showLoadingMore(isShow: Boolean) {

    }


    private fun getDefaultNotifyTitle(): String {
        return getString(R.string.default_notify_title)
    }

    protected fun registerObserverLoadingEvent(
        viewModel: BaseViewModel,
        viewLifecycleOwner: LifecycleOwner
    ) {
        viewModel.isLoading.observe(viewLifecycleOwner, EventObserver { isShow ->
            showLoading(isShow)
        })
    }

}