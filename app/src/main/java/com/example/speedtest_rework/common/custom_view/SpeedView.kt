package com.example.speedtest_rework.common.custom_view

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.ColorFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.speedtest_rework.R
import com.example.speedtest_rework.common.utils.Constant
import com.example.speedtest_rework.common.utils.NetworkUtils
import com.example.speedtest_rework.common.utils.buildMinVersionQ
import com.example.speedtest_rework.common.utils.format
import com.example.speedtest_rework.common.utils.roundOffDecimal
import com.example.speedtest_rework.core.SpeedTest
import com.example.speedtest_rework.core.serverSelector.TestPoint
import com.example.speedtest_rework.data.model.HistoryModel
import com.example.speedtest_rework.databinding.LayoutSpeedviewBinding
import com.example.speedtest_rework.viewmodel.ScanStatus
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel


class SpeedView(
    context: Context,
    attributeSet: AttributeSet,
) : ConstraintLayout(context, attributeSet) {
    private var countDownTimer: CountDownTimer? = null
    private var speedTest: SpeedTest? = null
    private var binding: LayoutSpeedviewBinding
    private var testModel: HistoryModel? = null
    private var type = ConnectionType.UNKNOWN
    private var testPoint: TestPoint? = null
    private var viewModel: SpeedTestViewModel? = null

    private lateinit var btnStartComposeShow: YoYo.AnimationComposer
    private lateinit var btnStartComposeHide: YoYo.AnimationComposer
    private lateinit var tvConnectingShow: YoYo.AnimationComposer
    private lateinit var tvConnectingHide: YoYo.AnimationComposer
    private lateinit var btnStartStringShow: YoYo.YoYoString

    private var currentTicks = listOf<Float>()

    init {
        val inflater = LayoutInflater.from(context)
        binding = LayoutSpeedviewBinding.inflate(inflater, this)
        initAnimation()
        initView()
    }

    private fun initAnimation() {
        btnStartComposeShow = YoYo.with(Techniques.FadeIn)
            .onStart {
                binding.btnStartContainer.visibility = View.VISIBLE
            }

        btnStartComposeHide =
            YoYo.with(Techniques.FadeOut).onEnd {
                binding.btnStartContainer.visibility = View.GONE
            }.onCancel {
                binding.btnStartContainer.visibility = View.VISIBLE
            }
        btnStartStringShow = btnStartComposeShow.playOn(binding.btnStartContainer)
        btnStartComposeShow.playOn(binding.tvGo)


        tvConnectingShow = YoYo.with(Techniques.SlideInRight).onStart {
            binding.tvConnecting.visibility = View.VISIBLE
        }
        tvConnectingHide =
            YoYo.with(Techniques.FadeOut).onEnd { binding.tvConnecting.visibility = View.GONE }
    }


    fun setData(
        testPoint: TestPoint,
        type: ConnectionType,
        testModel: HistoryModel,
        viewModel: SpeedTestViewModel
    ) {
        this.testPoint = testPoint
        this.type = type
        this.testModel = testModel
        this.viewModel = viewModel
        speedTest = SpeedTest()
        speedTest?.addTestPoint(testPoint)
        if (type == ConnectionType.UNKNOWN) {
            binding.containerNoNetwork.visibility = View.VISIBLE
            binding.tvGo.visibility = View.GONE
        } else {
            binding.containerNoNetwork.visibility = View.GONE
            binding.tvGo.visibility = View.VISIBLE
        }
    }

    fun setData(testPoint: TestPoint, type: ConnectionType) {
        this.testPoint = testPoint
        this.type = type
        speedTest = SpeedTest()
        speedTest?.addTestPoint(testPoint)
        if (type == ConnectionType.UNKNOWN) {
            binding.containerNoNetwork.visibility = View.VISIBLE
            binding.tvGo.visibility = View.GONE
        } else {
            binding.containerNoNetwork.visibility = View.GONE
            binding.tvGo.visibility = View.VISIBLE
        }
    }

    fun setData(type: ConnectionType) {
        this.type = type
        if (type == ConnectionType.UNKNOWN) {
            binding.containerNoNetwork.visibility = View.VISIBLE
            binding.tvGo.visibility = View.GONE
        } else {
            binding.containerNoNetwork.visibility = View.GONE
            binding.tvGo.visibility = View.VISIBLE
        }
    }


    fun setData(maxValue: Float) {
        binding.speedView.ticks = listOf()
        binding.speedView.maxSpeed = maxValue
        when (maxValue) {
            100f -> {
                currentTicks = listOf(0f, .05f, .1f, .15f, .2f, .3f, .5f, .75f, 1f)
            }
            500f -> {
                currentTicks = listOf(0f, .01f, .02f, .05f, .1f, .2f, .3f, .6f, 1f)
            }
            1000f -> {
                currentTicks = listOf(0f, .005f, .01f, .05f, .1f, .25f, .5f, .75f, 1f)
            }
            5000f -> {
                currentTicks = listOf(0f, .02f, .04f, .06f, .1f, .2f, .4f, .6f, 1f)
            }
            10000f -> {
                currentTicks = listOf(0f, .05f, .1f, .15f, .2f, .3f, .5f, .8f, 1f)
            }
            15000f -> {
                currentTicks =
                    listOf(0f, 1 / 30f, 1 / 15f, 0.1f, 2 / 15f, 0.2f, 1 / 3f, 2 / 3f, 1f)
            }
            50f -> {
                currentTicks = listOf(0f, .02f, .04f, .06f, .1f, .2f, .3f, .6f, 1f)
            }
            10f -> {
                currentTicks = listOf(0f, .1f, .2f, .3f, .4f, .5f, .6f, .8f, 1f)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        binding.loading.speed = 1f
        binding.loading.addAnimatorListener(object : Animator.AnimatorListener {

            override fun onAnimationStart(p0: Animator) {
                showLoading()
            }

            override fun onAnimationEnd(p0: Animator) {
                hideLoading()
                hideTvConnecting()
                showSpeedView()
                showContainerSpeed()
                showTvSpeed()
            }

            override fun onAnimationCancel(p0: Animator) {
                showBtnStart()
            }

            override fun onAnimationRepeat(p0: Animator) {
            }


        })
        binding.btnStart.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                when (p1?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        changeColorWhenPressDown()
                    }
                    MotionEvent.ACTION_UP -> {
                        changeColorWhenRelease()
                        if (type == ConnectionType.UNKNOWN) {
                            openConnectivitySetting()
                            return true
                        }
                        viewModel?.setScanStatus(ScanStatus.SCANNING)
                        prepareViewSpeedTest()
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        changeColorWhenRelease()
                    }
                    MotionEvent.ACTION_OUTSIDE -> {
                        changeColorWhenRelease()
                    }
                }
                return true
            }

        })
        binding.speedView.textSize = 40f
        binding.speedView.withTremble = false
    }

    private fun openConnectivitySetting() {
        if (buildMinVersionQ())
            context.startActivity(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))
        else
            context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
    }

    private fun changeColorWhenPressDown() {
        val filter = SimpleColorFilter(Color.WHITE)
        val callback: LottieValueCallback<ColorFilter> = LottieValueCallback(filter)
        binding.btnStart.addValueCallback(
            KeyPath("**"),
            LottieProperty.COLOR_FILTER,
            callback
        )
        binding.tvGo.reset()
    }

    private fun changeColorWhenRelease() {
        binding.btnStart.addValueCallback(
            KeyPath("**"), LottieProperty.COLOR_FILTER
        ) { null }
        binding.tvGo.setShader(
            ContextCompat.getColor(context, R.color.gradient_green_end),
            ContextCompat.getColor(context, R.color.gradient_green_start)
        )
        binding.tvGo.setTextColor(ContextCompat.getColor(context, R.color.gradient_green_start))
    }

    private fun changeUnitType() {
        binding.tvDownloadCurrency.text = context.getString(viewModel?.unitType?.value!!.unit)
        binding.tvUploadCurrency.text = context.getString(viewModel?.unitType?.value!!.unit)
        binding.tvCurrency.text = context.getString(viewModel?.unitType?.value!!.unit)
    }

    fun prepareViewSpeedTest() {
        keepScreenOn = true
        binding.placeholderDownload.visibility = VISIBLE
        binding.placeholderUpload.visibility = VISIBLE
        binding.tvDownloadValue.visibility = GONE
        binding.tvUploadValue.visibility = GONE
        binding.speedView.setInitDone(false)

        changeUnitType()
        var count = 0
        countDownTimer = object : CountDownTimer(4000, 1000) {
            override fun onTick(l: Long) {
                count++
                if (count == 3) {
                    if (type == ConnectionType.UNKNOWN) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.no_connection),
                            Toast.LENGTH_SHORT
                        ).show()
                        resetView()
                        return
                    } else {
                        if (binding.topView.visibility != VISIBLE) {
                            showTopView()
                            TransitionManager.beginDelayedTransition(
                                binding.testBackground,
                                AutoTransition()
                            )
                        }
                    }
                } else if (count == 2) {
                    showLoading()
                } else if (count == 1) {
                    hideBtnStart()
                    showTvConnecting()
                }
            }

            override fun onFinish() {
                runSpeedTest()
            }
        }
        countDownTimer?.start()
    }

    private fun showSpeedView() {

        binding.speedView.clearAnimation()

        val animation = ArcAnimation(binding.speedView)
        animation.duration = 400
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                binding.speedView.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation) {
                var count = 0
                val countDownTimer = object : CountDownTimer(1000, 50) {
                    override fun onTick(p0: Long) {
                        count++
                        if (count <= currentTicks.size)
                            binding.speedView.ticks = currentTicks.subList(0, count)
                    }

                    override fun onFinish() {
                        binding.speedView.setInitDone(true)
                    }

                }
                countDownTimer.start()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        binding.speedView.startAnimation(animation)
    }

    private fun showContainerSpeed() {
        YoYo.with(Techniques.FadeIn).onStart {
            binding.containerSpeed.visibility = View.VISIBLE
        }.playOn(binding.containerSpeed)
    }

    private fun hideContainerSpeed() {
        binding.containerSpeed.visibility = View.GONE
    }

    private fun showTvSpeed() {
        YoYo.with(Techniques.FadeIn).onStart {
            binding.tvSpeedValue.visibility = View.VISIBLE
        }.playOn(binding.tvSpeedValue)
    }

    private fun hideLoading() {
        binding.loading.visibility = View.GONE

    }

    private fun showLoading() {
        if (!binding.loading.isAnimating) {
            binding.loading.playAnimation()
        }
        binding.loading.visibility = View.VISIBLE
    }

    private fun hideBtnStart() {
        binding.btnStart.isEnabled = false
        btnStartComposeHide.playOn(binding.btnStartContainer)
    }

    private fun showBtnStart() {
        binding.btnStart.isEnabled = true
        val btnStartComposeHideString = btnStartComposeHide.playOn(binding.btnStartContainer)
        if (btnStartComposeHideString.isStarted || btnStartComposeHideString.isRunning) {
            btnStartComposeHideString.stop()
        }
        btnStartComposeShow.playOn(binding.btnStartContainer)
    }

    private fun showTvConnecting(): YoYo.YoYoString {
        return YoYo.with(Techniques.SlideInRight).onStart {
            binding.tvConnecting.visibility = View.VISIBLE
        }.playOn(binding.tvConnecting)
    }

    private fun hideTvConnecting(): YoYo.YoYoString {
        return YoYo.with(Techniques.FadeOut).onEnd {
            binding.tvConnecting.visibility = View.GONE
        }.playOn(binding.tvConnecting)
    }

    private fun hideTopView(): YoYo.YoYoString {
        return YoYo.with(Techniques.SlideOutUp).onStart {
            binding.topView.visibility = View.GONE
        }.playOn(binding.topView)
    }

    private fun showTopView(): YoYo.YoYoString {
        return YoYo.with(Techniques.SlideInDown).onStart {
            binding.topView.visibility = View.VISIBLE
        }.playOn(binding.topView)

    }

    fun runSpeedTest() {
        speedTest = SpeedTest()
        speedTest?.addTestPoint(testPoint)
        speedTest?.start(object :
            SpeedTest.SpeedtestHandler() {

            override fun onDownloadUpdate(dl: Double, progress: Double) {
                if (progress == 0.0) {
                    (context as Activity).runOnUiThread { downloadView() }
                }
                (context as Activity).runOnUiThread {
                    binding.tvSpeedValue.text = format(convert(dl))
                    binding.speedView.speedTo(convert(dl).toFloat())
                    if (progress >= 1) {
                        (context as Activity).runOnUiThread {
                            binding.placeholderDownload.clearAnimation()
                            binding.placeholderDownload.visibility = GONE
                            binding.tvDownloadValue.visibility = VISIBLE
                            binding.tvDownloadValue.text = format(convert(dl))
                            binding.tvSpeedValue.text = context.getString(R.string.zero_value)
                            testModel?.download = roundOffDecimal(dl)
                        }
                        binding.speedView.speedTo(0f, 200L)
                    }
                }
            }

            override fun onUploadUpdate(ul: Double, progress: Double) {
                (context as Activity).runOnUiThread {
                    if (progress == 0.0) {
                        uploadView()
                    }
                    binding.speedView.speedTo(convert(ul).toFloat())
                    binding.tvSpeedValue.text = format(convert(ul))
                    if (progress >= 1) {
                        binding.placeholderUpload.visibility = GONE
                        binding.placeholderUpload.clearAnimation()
                        binding.tvUploadValue.text = format(convert(ul))
                        binding.tvUploadValue.visibility = View.VISIBLE
                        binding.speedView.speedTo(0f, 500L)
                        binding.speedView.withTremble = false
                        binding.tvSpeedValue.text = context.getString(R.string.zero_value)
                        testModel?.upload = roundOffDecimal(ul)

                    }
                }
            }

            override fun onPingJitterUpdate(ping: Double, jitter: Double, progress: Double) {
                (context as Activity).runOnUiThread {
                    binding.tvPingCount.text = format(ping) + " ms"
                    binding.tvJitterCount.text = format(jitter) + " ms"
                    testModel?.ping = roundOffDecimal(ping)
                    testModel?.jitter = roundOffDecimal(jitter)
                }
            }

            override fun onEnd() {
                (context as Activity).runOnUiThread {
                    testModel?.name_network = NetworkUtils.getNameWifi(context)
                    testModel?.time = System.currentTimeMillis()

                    viewModel?.insertNewHistoryAction(testModel!!)
                    viewModel?.setScanStatus(ScanStatus.DONE)
                    binding.speedView.stop()

                    val bundle = Bundle()
                    bundle.putParcelable(Constant.KEY_TEST_MODEL, testModel)

                    bundle.putBoolean(Constant.KEY_FROM_SPEED_TEST_FRAGMENT, true)

                    Navigation.findNavController(binding.root)
                        .navigate(R.id.action_fragmentMain_to_fragmentResultDetail, bundle)

                }
            }


            override fun onCriticalFailure(err: String?) {
                (context as Activity).runOnUiThread {
                    viewModel?.setScanStatus(ScanStatus.HARD_RESET)
                }
            }
        })
    }


    private fun downloadView() {
        val anim: Animation = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 100
        anim.startOffset = 50
        anim.repeatCount = Animation.INFINITE
        binding.speedView.setState("download")
        binding.speedView.setSpeedometerColor(
            ContextCompat.getColor(
                context,
                R.color.gradient_green_start
            )
        )
        binding.iconSpeedValue.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_download
            )
        )
        binding.placeholderDownload.startAnimation(anim)
    }

    private fun uploadView() {
        val anim: Animation = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 100
        anim.startOffset = 50
        anim.repeatCount = Animation.INFINITE
        binding.iconSpeedValue.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_upload
            )
        )
        binding.speedView.indicatorLightColor =
            ContextCompat.getColor(context, R.color.gradient_orange_start)
        binding.placeholderUpload.startAnimation(anim)
        binding.speedView.setSpeedometerColor(
            ContextCompat.getColor(
                context,
                R.color.gradient_orange_start
            )
        )
        Handler(Looper.getMainLooper()).postDelayed({
            binding.speedView.setState("upload")
        }, 200L)


    }

    fun onScanningDone() {
        binding.iconSpeedValue.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_download
            )
        )
        binding.speedView.ticks = listOf()
        binding.speedView.setInitDone(false)
        binding.speedView.stop()
        showBtnStart()
        binding.placeholderUpload.clearAnimation()
        binding.placeholderDownload.clearAnimation()
        binding.btnStart.isEnabled = true
        binding.tvSpeedValue.text = context.getString(R.string.zero_value_dec)
        binding.tvSpeedValue.visibility = View.GONE
        hideContainerSpeed()
        binding.speedView.visibility = GONE

    }

    fun resetView() {
        if (speedTest != null) {
            speedTest?.abort()
        }
        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }
        binding.iconSpeedValue.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_download
            )
        )
        binding.speedView.ticks = listOf()
        binding.speedView.setInitDone(false)
        binding.speedView.stop()
        hideTopView()
        binding.loading.cancelAnimation()
        binding.containerSpeed.visibility = GONE
        binding.speedView.visibility = GONE
        binding.tvDownloadValue.visibility = GONE
        binding.placeholderDownload.visibility = VISIBLE
        binding.placeholderDownload.clearAnimation()

        binding.tvUploadValue.visibility = GONE
        binding.placeholderUpload.visibility = VISIBLE
        binding.placeholderUpload.clearAnimation()

        binding.tvSpeedValue.text = context.getString(R.string.zero_value_dec)
        binding.tvSpeedValue.visibility = View.GONE

        binding.tvPingCount.text = context.getString(R.string.zero_value)
        binding.tvJitterCount.text = context.getString(R.string.zero_value)

        binding.loading.visibility = GONE
        binding.tvConnecting.visibility = GONE

        showBtnStart()
    }


    private fun convertMbpsToMbs(value: Double): Double {
        return value * .125
    }

    private fun convertMbpsToKbs(value: Double): Double {
        return value * 125
    }

    private fun convert(value: Double): Double {
        if (viewModel?.unitType?.value == UnitType.MBS) {
            return convertMbpsToMbs(value)
        }
        if (viewModel?.unitType?.value == UnitType.KBS) {
            return convertMbpsToKbs(value)
        }
        return value
    }

}
