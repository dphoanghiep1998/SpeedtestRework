package com.gianghv.libads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.gianghv.libads.utils.Constants
import com.gianghv.libads.utils.Utils
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class RewardAdsManager(
    private val context: Context,
    private val mIDReward01: String,
    private val mIDReward02: String,
    private val mIDReward03: String
) {
    private var rewardedAd: RewardedAd? = null
    var handler: Handler? = null
    var runable: Runnable? = null


    fun showAds(
        activity: Activity,
        onLoadAdSuccess: (() -> Unit)? = null,
        onAdClose: (() -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null
    ) {
        val requestConfiguration = RequestConfiguration.Builder().build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        if (!Utils.isOnline(context)) {
            onAdLoadFail?.invoke()
            return
        }

        handler = Handler(Looper.getMainLooper())
        runable = Runnable {
            if (handler != null) {
                onAdLoadFail?.invoke()
            }
        }
        handler?.postDelayed(runable!!, Constants.TIME_OUT)

        loadAds(onAdLoader = {
            onLoadAdSuccess?.invoke()
            showRewardAds(activity, object : OnShowRewardAdListener {
                override fun onShowRewardSuccess() {
                    onAdClose?.invoke()
                }
            })
        }, onAdLoadFail)
    }

    fun loadAds(
        onAdLoader: (() -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null
    ) {
        requestAdsPrepare(mIDReward01, onAdLoader, onAdLoadFail = {
            requestAdsPrepare(mIDReward02, onAdLoader, onAdLoadFail = {
                requestAdsPrepare(mIDReward03, onAdLoader, onAdLoadFail = {
                    if (handler == null) {
                        onAdLoadFail?.invoke()
                    }
                    runable?.let { handler?.removeCallbacks(it) }
                    handler = null
                })
            })
        })
    }

    private fun requestAdsPrepare(
        idAds: String,
        onLoadAdSuccess: (() -> Unit)? = null,
        adLoader: (() -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null
    ) {
        RewardedAd.load(
            context,
            idAds,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(p0: RewardedAd) {
                    rewardedAd = p0
                    runable?.let { handler?.removeCallbacks(it) }
                    handler = null
                    onLoadAdSuccess?.invoke()
                    adLoader?.invoke()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    onAdLoadFail?.invoke()
                }
            })
    }

    fun showRewardAds(activity: Activity, onShowRewardAdListener: OnShowRewardAdListener) {
        if (rewardedAd != null) {
            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdShowedFullScreenContent() {
                        runable?.let { handler?.removeCallbacks(it) }
                        handler = null
                        // Code to be invoked when the ad showed full screen content.
                    }

                    override fun onAdDismissedFullScreenContent() {
                        rewardedAd = null
                        onShowRewardAdListener.onShowRewardSuccess()
                        // Code to be invoked when the ad dismissed full screen content.
                    }
                }
            rewardedAd?.fullScreenContentCallback = fullScreenContentCallback
            rewardedAd?.setOnPaidEventListener {
                Utils.postRevenueAdjust(it, rewardedAd?.adUnitId)
            }
            rewardedAd?.show(
                activity
            ) {

            }
        } else {
            loadAds()
            onShowRewardAdListener.onShowRewardSuccess()
        }
    }

    interface OnShowRewardAdListener {
        fun onShowRewardSuccess()
    }
}