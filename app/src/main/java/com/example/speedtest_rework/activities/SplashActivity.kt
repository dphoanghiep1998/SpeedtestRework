package com.example.speedtest_rework.activities

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieDrawable
import com.example.speedtest_rework.BuildConfig
import com.example.speedtest_rework.CustomApplication
import com.example.speedtest_rework.base.activity.BaseActivity
import com.example.speedtest_rework.base.dialog.DialogLoadingInterAds
import com.example.speedtest_rework.common.extensions.isInternetAvailable
import com.example.speedtest_rework.common.utils.AppSharePreference
import com.example.speedtest_rework.databinding.ActivitySplashBinding
import com.gianghv.libads.InterstitialPreloadAdManager
import com.gianghv.libads.NativeAdsManager
import kotlinx.coroutines.launch


@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding
    private var interSplashAds: InterstitialPreloadAdManager? = null
    private var mNativeAdManager: NativeAdsManager? = null
    private var status = MutableLiveData(0)
    private val initDone = AppSharePreference.INSTANCE.getIsLangSet(false)
    private var dialogLoadingInterAds: DialogLoadingInterAds? = null
    private lateinit var app: CustomApplication


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as CustomApplication
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialogLoadingInterAds = DialogLoadingInterAds(this)
        initAds()
        observeStatusLoadingAds()
        handleAds()

    }

    private fun initAds() {
        interSplashAds = InterstitialPreloadAdManager(
            this,
            BuildConfig.inter_splash_id1,
            BuildConfig.inter_splash_id2,
            BuildConfig.inter_splash_id3,
        )

        mNativeAdManager = NativeAdsManager(
            this,
            BuildConfig.native_languages_id1,
            BuildConfig.native_languages_id2,
            BuildConfig.native_languages_id3,
        )


    }

    private fun observeStatusLoadingAds() {
        val callback = object : InterstitialPreloadAdManager.InterstitialAdListener {
            override fun onClose() {
                dialogLoadingInterAds?.hideDialog()
                navigateMain()
            }

            override fun onError() {
                dialogLoadingInterAds?.hideDialog()
                navigateMain()
            }
        }
        status.observe(this) {
            if (it >= 2) {
                if (interSplashAds?.loadAdsSuccess == true) {
                    handleAtLeast3Second(action = {
                        lifecycleScope.launch {
                            startActivity(
                                Intent(this@SplashActivity, MainActivity::class.java)
                            )
                            finish()
                            interSplashAds?.showAds(
                                this@SplashActivity, callback
                            )
                        }
                    })
                } else {
                    navigateMain()
                }

            } else if (initDone && it >= 1) {
                if (interSplashAds?.loadAdsSuccess == true) {

                    handleAtLeast3Second(action = {
                        lifecycleScope.launch {
                            startActivity(
                                Intent(this@SplashActivity, MainActivity::class.java)
                            )
                            finish()
                            interSplashAds?.showAds(
                                this@SplashActivity, callback
                            )
                        }
                    })
                } else {
                    navigateMain()
                }
            }
        }
    }

    private fun handleAds() {
        if (!isInternetAvailable(this)) {
            handleWhenAnimationDone(action = {
                navigateMain()
            })
        } else {
            if (initDone) {
                setLoadingSplash(60000)
                handleAtLeast3Second(action = {
                    loadSplashAds()
                })
            } else {
                setLoadingSplash(60000)
                handleAtLeast3Second(action = {
                    loadNativeAds()
                    loadSplashAds()
                })
            }
        }
    }

    private fun loadNativeAds() {
        mNativeAdManager?.loadAds(onLoadSuccess = {
            app.nativeAD = it
            status.postValue(status.value?.plus(1) ?: 0)
        }, onLoadFail = {
            status.postValue(status.value?.plus(1) ?: 0)
        })
    }

    private fun loadSplashAds() {
        interSplashAds?.loadAds(onAdLoadFail = {
            status.postValue(status.value?.plus(1) ?: 0)
            interSplashAds?.loadAdsSuccess = false
        }, onAdLoader = {
            dialogLoadingInterAds?.showDialog()
            status.postValue(status.value?.plus(1) ?: 0)
            interSplashAds?.loadAdsSuccess = true
        })
    }

    private fun handleWhenAnimationDone(action: () -> Unit) {
        val animation: ObjectAnimator = ObjectAnimator.ofInt(binding.pbLoading, "progress", 0, 100)
        animation.duration = 3000
        animation.interpolator = DecelerateInterpolator()
        animation.addListener(object : AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                action.invoke()
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }

        })
        animation.start()
    }

    private fun handleAtLeast3Second(action: () -> Unit) {
        Handler().postDelayed({
            action.invoke()
        }, 3000)
    }


    private fun setLoadingSplash(time: Long) {
        val animation: ObjectAnimator = ObjectAnimator.ofInt(binding.pbLoading, "progress", 0, 100)
        animation.duration = time

        animation.interpolator = DecelerateInterpolator()
        animation.start()
    }

    private fun navigateMain() {
//        val animation: ObjectAnimator = ObjectAnimator.ofInt(binding.pbLoading, "progress", 0, 100)
//        animation.duration = 3000
//
//        animation.interpolator = DecelerateInterpolator()
//        animation.start()
//        Handler().postDelayed({
//
//        }, 3500)
        val i = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(i)
        finish()
    }


}