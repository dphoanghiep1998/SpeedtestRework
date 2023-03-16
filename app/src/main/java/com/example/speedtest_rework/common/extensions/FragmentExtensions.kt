package com.example.speedtest_rework.common.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.speedtest_rework.BuildConfig
import com.example.speedtest_rework.base.dialog.DialogLoadingInterAds
import com.example.speedtest_rework.common.utils.buildMinVersionM
import com.gianghv.libads.*


enum class NativeType {
    SPEED_TEST_RESULT, TOOL, RESULTS, BACK_EXIT, LANGUAGE
}

enum class InterAds {
    SPLASH, SPEED_TEST_RESULT, TOOLS_FUNCTION_BACK, SWITCH_TAB, PING_TEST_SUCCESS, SIGNAL_TEST_STOP
}


fun Fragment.navigateToPage(id: Int, actionId: Int, bundle: Bundle? = null) {
    if (findNavController().currentDestination?.id == id && isAdded) {
        findNavController().navigate(
            actionId, bundle
        )
        return
    }
}

fun Fragment.openLink(strUri: String?) {
    try {
        val uri = Uri.parse(strUri)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Fragment.changeBackPressCallBack(action: () -> Unit) {
    val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            action.invoke()
        }
    }
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
}

fun Fragment.setStatusColor(color: Int) {

    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

    requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

    requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), color)

}

fun hideSoftKeyboard(activity: Activity, view: View) {
    val inputMethodManager: InputMethodManager = activity.getSystemService(
        Activity.INPUT_METHOD_SERVICE
    ) as InputMethodManager
    if (inputMethodManager.isAcceptingText) {
        inputMethodManager.hideSoftInputFromWindow(
            activity.currentFocus?.windowToken, 0
        )
    }
    view.clearFocus()
}


fun showSoftKeyboard(activity: Activity, view: View) {
    val inputMethodManager: InputMethodManager = activity.getSystemService(
        Activity.INPUT_METHOD_SERVICE
    ) as InputMethodManager

    inputMethodManager.showSoftInput(
        view, 0
    )
}

@SuppressLint("ClickableViewAccessibility")
fun Fragment.setupUI(view: View) {
    // Set up touch listener for non-text box views to hide keyboard.
    if (view !is EditText) {
        view.setOnTouchListener { _, _ ->
            hideSoftKeyboard(requireActivity(), view)
            false
        }
    }

    //If a layout container, iterate over children and seed recursion.
    if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            val innerView = view.getChildAt(i)
            setupUI(innerView)
        }
    }
}


fun isInternetAvailable(context: Context): Boolean {
    if (buildMinVersionM()) {
        var result = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        cm?.run {
            this.getNetworkCapabilities(this.activeNetwork)?.run {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }
        }
        return result
    } else {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        if (netInfo != null) {
            val networkInfo = cm.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
        return false
    }
}

fun Fragment.showNativeAds(
    view: NativeAdMediumView?,
    view_small: NativeAdSmallView?,
    action: (() -> Unit)? = null,
    action_fail: (() -> Unit)? = null,
    type: NativeType
) {
    val mNativeAdManager: NativeAdsManager?
    when (type) {

        NativeType.BACK_EXIT -> {
            mNativeAdManager = NativeAdsManager(
                requireActivity(),
                BuildConfig.native_back_exit_id1,
                BuildConfig.native_back_exit_id2,
                BuildConfig.native_back_exit_id3,
            )
        }
        NativeType.TOOL -> {
            mNativeAdManager = NativeAdsManager(
                requireActivity(),
                BuildConfig.native_tool_id1,
                BuildConfig.native_tool_id2,
                BuildConfig.native_tool_id3,
            )
        }
        NativeType.LANGUAGE -> {
            mNativeAdManager = NativeAdsManager(
                requireActivity(),
                BuildConfig.native_languages_id1,
                BuildConfig.native_languages_id2,
                BuildConfig.native_languages_id3,
            )
        }

        NativeType.SPEED_TEST_RESULT -> {
            mNativeAdManager = NativeAdsManager(
                requireActivity(),
                BuildConfig.native_speedtest_result_id1,
                BuildConfig.native_speedtest_result_id2,
                BuildConfig.native_speedtest_result_id3,
            )
        }
        NativeType.RESULTS -> {
            mNativeAdManager = NativeAdsManager(
                requireActivity(),
                BuildConfig.native_result_id1,
                BuildConfig.native_result_id2,
                BuildConfig.native_result_id3,
            )
        }


    }
    view?.let {
        it.showShimmer(true)
        mNativeAdManager.loadAds(onLoadSuccess = { nativeAd ->
            it.visibility = View.VISIBLE
            action?.invoke()
            it.showShimmer(false)
            it.setNativeAd(nativeAd)
            it.isVisible = true
        }, onLoadFail = { _ ->
            action_fail?.invoke()
            it.errorShimmer()
            if (type == NativeType.TOOL) {
                it.visibility = View.INVISIBLE
            } else {
                it.visibility = View.GONE
            }
        })
    }

    view_small?.let {
        it.showShimmer(true)
        mNativeAdManager.loadAds(onLoadSuccess = { nativeAd ->
            it.visibility = View.VISIBLE
            action?.invoke()
            it.showShimmer(false)
            it.setNativeAd(nativeAd)
            it.isVisible = true
        }, onLoadFail = { _ ->
            it.errorShimmer()
            it.visibility = View.GONE
        })
    }

}

fun Fragment.showBannerAds(view: ViewGroup, action: (() -> Unit)? = null) {
    val adaptiveBannerManager = AdaptiveBannerManager(
        requireActivity(),
        BuildConfig.banner_home_id1,
        BuildConfig.banner_home_id2,
        BuildConfig.banner_home_id3,
    )
    if (AdaptiveBannerManager.isBannerLoaded) {
        adaptiveBannerManager.loadAdViewToParent(view)
        return
    }

    adaptiveBannerManager.loadBanner(view,
        onAdLoadFail = { action?.invoke() },
        onAdLoader = { action?.invoke() })
}

fun Fragment.showInterAds(
    action: () -> Unit, type: InterAds
) {
    if (!isAdded) {
        action.invoke()
        return
    }
    if (!isInternetAvailable(requireContext())) {
        action.invoke()
        return
    }
    val interstitialSingleReqAdManager: InterstitialSingleReqAdManager
    when (type) {
        InterAds.SPLASH -> {
            interstitialSingleReqAdManager = InterstitialSingleReqAdManager(
                requireActivity(),
                BuildConfig.inter_splash_id1,
                BuildConfig.inter_splash_id2,
                BuildConfig.inter_splash_id3,
            )
        }
        InterAds.SWITCH_TAB -> {
            interstitialSingleReqAdManager = InterstitialSingleReqAdManager(
                requireActivity(),
                BuildConfig.inter_switch_tab_id1,
                BuildConfig.inter_switch_tab_id2,
                BuildConfig.inter_switch_tab_id3,
            )
        }
        InterAds.PING_TEST_SUCCESS -> {
            interstitialSingleReqAdManager = InterstitialSingleReqAdManager(
                requireActivity(),
                BuildConfig.inter_pingtest_success_id1,
                BuildConfig.inter_pingtest_success_id2,
                BuildConfig.inter_pingtest_success_id3,
            )
        }

        InterAds.SIGNAL_TEST_STOP -> {
            interstitialSingleReqAdManager = InterstitialSingleReqAdManager(
                requireActivity(),
                BuildConfig.inter_wifi_signal_test_stop_id1,
                BuildConfig.inter_wifi_signal_test_stop_id2,
                BuildConfig.inter_wifi_signal_test_stop_id3,
            )
        }
        InterAds.TOOLS_FUNCTION_BACK -> {
            interstitialSingleReqAdManager = InterstitialSingleReqAdManager(
                requireActivity(),
                BuildConfig.inter_tools_function_back_id1,
                BuildConfig.inter_tools_function_back_id2,
                BuildConfig.inter_tools_function_back_id3,
            )
        }

        InterAds.SPEED_TEST_RESULT -> {
            interstitialSingleReqAdManager = InterstitialSingleReqAdManager(
                requireActivity(),
                BuildConfig.inter_speedtest_result_id1,
                BuildConfig.inter_speedtest_result_id2,
                BuildConfig.inter_speedtest_result_id3,
            )
        }


    }
    val dialogLoadingInterAds: DialogLoadingInterAds? = DialogLoadingInterAds(requireContext())
    dialogLoadingInterAds?.showDialog()
    InterstitialSingleReqAdManager.isShowingAds = true

    interstitialSingleReqAdManager.showAds(requireActivity(), onLoadAdSuccess = {
        dialogLoadingInterAds?.hideDialog()
    }, onAdClose = {
        InterstitialSingleReqAdManager.isShowingAds = false
        action()
        dialogLoadingInterAds?.hideDialog()
    }, onAdLoadFail = {
        InterstitialSingleReqAdManager.isShowingAds = false
        action()
        dialogLoadingInterAds?.hideDialog()
    })

}
