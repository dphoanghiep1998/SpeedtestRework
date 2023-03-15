package com.example.speedtest_rework

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.LogLevel
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.example.speedtest_rework.activities.MainActivity
import com.example.speedtest_rework.common.Configuration
import com.example.speedtest_rework.common.utils.AppSharePreference
import com.example.speedtest_rework.common.utils.NetworkUtils
import com.example.speedtest_rework.common.utils.buildMinVersionO
import com.example.speedtest_rework.services.AppForegroundService
import com.facebook.appevents.AppEventsLogger
import com.gianghv.libads.AppOpenAdManager
import com.gianghv.libads.InterstitialPreloadAdManager
import com.gianghv.libads.InterstitialSingleReqAdManager
import com.gianghv.libads.utils.Constants
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CustomApplication : Application(),Application.ActivityLifecycleCallbacks,
    LifecycleObserver {
     var currentActivity: Activity? = null
    private var appOpenAdsManager: AppOpenAdManager? = null
    var settingLanguageLocale = ""
    var shouldDestroyApp = false
    var showAdsClickBottomNav = false
    var nativeAD: NativeAd? = null

    companion object{
        lateinit var app:CustomApplication
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        AppSharePreference.getInstance(applicationContext)
        Configuration.getInstance()
        registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        AppForegroundService.getInstance()
        NetworkUtils(applicationContext)
        createChannelNotification()
        MobileAds.initialize(this) { MobileAds.setAppMuted(true) }
        val requestConfiguration =
            RequestConfiguration.Builder().setTestDeviceIds(Constants.testDevices()).build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        initAdjust()
        initFBApp()
        initApplovinMediation()
        initOpenAds()
    }


    private fun createChannelNotification() {
        if (buildMinVersionO()) {
            val channel = NotificationChannel(
                applicationContext.getString(R.string.channel_id),
                applicationContext.getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager: NotificationManager = getSystemService(NotificationManager::class.java)
            if (manager != null) {
                manager.createNotificationChannel(channel)
            }
        }
    }

    private fun initFBApp() {
//        AudienceNetworkInitializeHelper.initialize(applicationContext)
//        AppEventsLogger.activateApp(this)
    }

    private fun initApplovinMediation() {
        AppLovinSdk.getInstance(this).mediationProvider = AppLovinMediationProvider.MAX
        AppLovinSdk.getInstance(this).initializeSdk {}
    }

    private fun initAdjust() {
        val config = AdjustConfig(
            this, BuildConfig.adjust_token_key, AdjustConfig.ENVIRONMENT_PRODUCTION
        )
        config.setLogLevel(LogLevel.WARN)
        Adjust.onCreate(config)
        registerActivityLifecycleCallbacks(
            this
        )
    }

    private fun initOpenAds() {
        appOpenAdsManager = AppOpenAdManager(
            this, BuildConfig.open_app_id, BuildConfig.open_app_id, BuildConfig.open_app_id
        )
        appOpenAdsManager?.loadAd()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onMoveToForeground() {
        if (!InterstitialPreloadAdManager.isShowingAds && !InterstitialSingleReqAdManager.isShowingAds) {
            currentActivity?.let {
                if (currentActivity is MainActivity) {
                    appOpenAdsManager?.showAdIfAvailable(it)
                }
            }
        }
    }




    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityStarted(p0: Activity) {
    }

    override fun onActivityResumed(p0: Activity) {
        currentActivity = p0
        Adjust.onResume()
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(p0: Activity) {
        Adjust.onPause()
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
        currentActivity = null
    }
    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        newConfig?.let {
            val uiMode = it.uiMode
            it.setTo(baseContext.resources.configuration)
            it.uiMode = uiMode
        }
    }
}