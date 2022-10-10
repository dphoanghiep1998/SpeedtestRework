package com.example.speedtest_rework.common.custom_view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.ColorFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
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
import com.example.speedtest_rework.common.Constant
import com.example.speedtest_rework.core.SpeedTest
import com.example.speedtest_rework.core.serverSelector.TestPoint
import com.example.speedtest_rework.data.model.HistoryModel
import com.example.speedtest_rework.databinding.LayoutSpeedviewBinding
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import kotlin.math.max
import kotlin.math.roundToInt


class SpeedView(
    context: Context,
    attributeSet: AttributeSet,
) : ConstraintLayout(context, attributeSet) {
    private var countDownTimer: CountDownTimer? = null
    private var speedTest: SpeedTest? = null
    private var binding: LayoutSpeedviewBinding
    private var testModel: HistoryModel? = null
    private var type = ConnectionType.UNKNOWN
    private var unitType = UnitType.MBPS
    private var testPoint: TestPoint? = null
    private var viewModel: SpeedTestViewModel? = null

    init {
        val inflater = LayoutInflater.from(context)
        binding = LayoutSpeedviewBinding.inflate(inflater, this)
        initView()
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
    }

    fun setData(testPoint: TestPoint, type: ConnectionType) {
        this.testPoint = testPoint
        this.type = type
        speedTest = SpeedTest()
        speedTest?.addTestPoint(testPoint)
    }

    fun setData(type: ConnectionType) {
        this.type = type
    }

    fun setData(type: UnitType) {
        unitType = type
    }

    fun setData(maxValue: Float) {
        //    var a = listOf(0f, 1 / 30f, 1 / 15f, 0.1f, 2 / 15f, 0.2f, 1 / 3f, 2 / 3f, 1f) 15k
//    var a = listOf(0f, .05f, .1f, .15f, .2f, .3f, .5f, .8f, 1f)  10k
//    var a = listOf(0f, .02f, .04f, .06f, .1f, .2f, .4f, .6f, 1f) 5k
//    var a = listOf(0f,.02f,.04f,.06f,.1f,.2f,.3f,.6f,1f) 50
//    var a = listOf(0f, .1f, .2f, .3f, .4f, .5f, .6f, .8f) 10
//    var a = listOf(0f, .01f, .02f, .05f, .1f, .2f, .3f, .6f, 1f) 500
//    var a = listOf(0f, .005f, .01f, .05f, .1f, .25f, .5f, .75f, 1f) 1000
//    var a = listOf(0f, .05f, .15f, .2f, .3f, .5f, .75f, 1f) 100
        binding.speedView.maxSpeed = maxValue
        when (maxValue) {
            100f -> {
                binding.speedView.ticks = listOf(0f, .05f, .1f, .15f, .2f, .3f, .5f, .75f, 1f)
            }
            500f -> {
                binding.speedView.ticks = listOf(0f, .01f, .02f, .05f, .1f, .2f, .3f, .6f, 1f)
            }
            1000f -> {
                binding.speedView.ticks = listOf(0f, .005f, .01f, .05f, .1f, .25f, .5f, .75f, 1f)
            }
            5000f -> {
                binding.speedView.ticks = listOf(0f, .02f, .04f, .06f, .1f, .2f, .4f, .6f, 1f)
            }
            10000f -> {
                binding.speedView.ticks = listOf(0f, .05f, .1f, .15f, .2f, .3f, .5f, .8f, 1f)
            }
            15000f -> {
                binding.speedView.ticks =
                    listOf(0f, 1 / 30f, 1 / 15f, 0.1f, 2 / 15f, 0.2f, 1 / 3f, 2 / 3f, 1f)
            }
            50f -> {
                binding.speedView.ticks = listOf(0f, .02f, .04f, .06f, .1f, .2f, .3f, .6f, 1f)
            }
            10f -> {
                binding.speedView.ticks = listOf(0f, .1f, .2f, .3f, .4f, .5f, .6f, .8f, 1f)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        binding.btnStart.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                when (p1?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        val filter = SimpleColorFilter(Color.WHITE)
                        val callback: LottieValueCallback<ColorFilter> = LottieValueCallback(filter)
                        binding.btnStart.addValueCallback(
                            KeyPath("**"),
                            LottieProperty.COLOR_FILTER,
                            callback
                        )

                    }
                    MotionEvent.ACTION_UP -> {
                        binding.btnStart.scaleX = 1.3f
                        binding.btnStart.scaleY = 1.3f
                        binding.btnStart.addValueCallback(
                            KeyPath("**"), LottieProperty.COLOR_FILTER
                        ) { null }
                        if (type == ConnectionType.UNKNOWN) {
                            Toast.makeText(context, "No connectivity!", Toast.LENGTH_SHORT).show()
                            return true
                        }
                        viewModel?.setIsScanning(true)
                        prepareViewSpeedTest()
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        binding.btnStart.scaleX = 1.3f
                        binding.btnStart.scaleY = 1.3f
                        binding.btnStart.addValueCallback(
                            KeyPath("**"), LottieProperty.COLOR_FILTER
                        ) { null }
                    }
                    MotionEvent.ACTION_OUTSIDE -> {
                        binding.btnStart.scaleX = 1.3f
                        binding.btnStart.scaleY = 1.3f
                        binding.btnStart.addValueCallback(
                            KeyPath("**"), LottieProperty.COLOR_FILTER
                        ) { null }
                    }
                }
                return true
            }

        })
        binding.speedView.isWithPointer = false
        binding.speedView.textSize = 40f
        binding.speedView.withTremble = false
    }

    private fun changeUnitType() {
        binding.tvDownloadCurrency.text = context.getString(unitType.unit)
        binding.tvUploadCurrency.text = context.getString(unitType.unit)
        binding.tvCurrency.text = context.getString(unitType.unit)
    }

    fun prepareViewSpeedTest() {
        changeUnitType()
        countDownTimer = object : CountDownTimer(4000, 1000) {
            override fun onTick(l: Long) {
                if (l <= 1000) {
                    val animation = ArcAnimation(binding.speedView)
                    animation.duration = 1000
                    animation.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {
                            binding.speedView.visibility = View.VISIBLE
                        }

                        override fun onAnimationEnd(animation: Animation) {}
                        override fun onAnimationRepeat(animation: Animation) {}
                    })
                    binding.speedView.startAnimation(animation)
//                    binding.speedView.showArc()
                    YoYo.with(Techniques.FadeIn).onStart {
                        binding.containerSpeed.visibility = View.VISIBLE
                    }.playOn(binding.containerSpeed)
                    YoYo.with(Techniques.FadeIn).onStart {
                        binding.tvSpeedValue.visibility = View.VISIBLE
                    }.playOn(binding.tvSpeedValue)
                } else if (l <= 2000) {
                    if (type == ConnectionType.UNKNOWN) {
                        Toast.makeText(context, "No connection", Toast.LENGTH_SHORT).show()
                        resetView()
                        return
                    } else {
                        YoYo.with(Techniques.FadeOut).onEnd {
                            binding.loading.visibility = View.GONE
                        }.playOn(binding.loading)
                        YoYo.with(Techniques.FadeOut).onEnd {
                            binding.tvConnecting.visibility = View.GONE
                        }.playOn(binding.tvConnecting)
                        YoYo.with(Techniques.SlideInDown).onStart {
                            binding.topView.visibility = View.VISIBLE
                        }.playOn(binding.topView)
                    }
                } else if (l <= 3000) {
                    YoYo.with(Techniques.FadeIn).onStart {
                        binding.loading.visibility = View.VISIBLE
                    }.playOn(binding.loading)
                } else if (l <= 4000) {
                    binding.btnStart.isEnabled = false
                    YoYo.with(Techniques.FadeOut).playOn(binding.btnStartContainer)
                    YoYo.with(Techniques.SlideInRight).onStart {
                        binding.tvConnecting.visibility = View.VISIBLE
                    }.playOn(binding.tvConnecting)
                }
            }

            override fun onFinish() {
                runSpeedTest()
            }
        }
        countDownTimer?.start()
    }

    fun runSpeedTest() {
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
                            testModel?.download = roundOffDecimal(convert(dl))
                        }
                        binding.speedView.speedTo(0f, 500L)
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
                        binding.speedView.speedTo(0f, 500L)
                        binding.speedView.withTremble = false
                        binding.tvSpeedValue.text = context.getString(R.string.zero_value)
                        testModel?.upload = roundOffDecimal(convert(ul))

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
                    viewModel?.insertNewHistoryAction(testModel!!)
                    viewModel?.setIsScanning(false)
                    binding.speedView.stop()
                    Handler(Looper.getMainLooper()).postDelayed({
                        val bundle = Bundle()
                        bundle.putParcelable(Constant.KEY_TEST_MODEL, testModel)
                        bundle.putBoolean(Constant.KEY_FROM_SPEED_TEST_FRAGMENT, true)
                        Navigation.findNavController(binding.root)
                            .navigate(R.id.action_fragmentMain_to_fragmentResultDetail, bundle)
                    }, 600L)
                }
            }


            override fun onCriticalFailure(err: String?) {
                (context as Activity).runOnUiThread {
                    viewModel?.setIsScanning(false)
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
        binding.speedView.setState("upload")

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
        binding.placeholderDownload.visibility = VISIBLE
        binding.placeholderUpload.visibility = VISIBLE
        binding.placeholderUpload.clearAnimation()
        binding.placeholderDownload.clearAnimation()
        binding.speedView.setState("download")
        binding.speedView.stop()
        binding.tvSpeedValue.text = context.getString(R.string.zero_value_dec)
        binding.tvDownloadValue.visibility = GONE
        binding.tvUploadValue.visibility = GONE
        binding.tvPingCount.text = context.getString(R.string.zero_value)
        binding.tvJitterCount.text = context.getString(R.string.zero_value)
        YoYo.with(Techniques.FadeOut).onStart {
            binding.topView.visibility = View.INVISIBLE
        }.playOn(binding.topView)
        YoYo.with(Techniques.FadeIn).playOn(binding.btnStartContainer)
        binding.btnStartContainer.visibility = View.VISIBLE
        binding.loading.visibility = View.GONE
        binding.tvConnecting.visibility = View.GONE
        binding.speedView.visibility = View.GONE
        binding.tvSpeedValue.visibility = View.GONE
        binding.containerSpeed.visibility = View.GONE
        binding.btnStart.isEnabled = true
    }

    private fun format(d: Double): String {
        return if (d < 200) String.format("%.2f", d) else "" + d.roundToInt()
    }

    fun roundOffDecimal(number: Double): Double {
        val number3digits: Double = (number * 1000.0).roundToInt() / 1000.0
        val number2digits: Double = (number3digits * 100.0).roundToInt() / 100.0
        return (number2digits * 10.0).roundToInt() / 10.0
    }

    private fun convertMbpsToMbs(value: Double): Double {
        return value * .125
    }

    private fun convertMbpsToKbs(value: Double): Double {
        return value * 125
    }

    fun convert(value: Double): Double {
        if (unitType == UnitType.MBS) {
            return convertMbpsToMbs(value)
        }
        if (unitType == UnitType.KBS) {
            return convertMbpsToKbs(value)
        }
        return value
    }

}
