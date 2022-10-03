package com.example.speedtest_rework.common.custom_view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.speedtest_rework.R
import com.example.speedtest_rework.common.Constant
import com.example.speedtest_rework.core.SpeedTest
import com.example.speedtest_rework.core.serverSelector.TestPoint
import com.example.speedtest_rework.data.model.HistoryModel
import com.example.speedtest_rework.databinding.LayoutSpeedviewBinding
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import kotlin.math.roundToInt

class SpeedView(
    context: Context,
    attributeSet: AttributeSet,
) : ConstraintLayout(context, attributeSet) {
    private var countDownTimer: CountDownTimer? = null
    private var speedTest: SpeedTest? = null
    private var binding: LayoutSpeedviewBinding
    private var testModel: HistoryModel? = null
    private var type = "no_connection"
    private var testPoint: TestPoint? = null
    private var viewModel: SpeedTestViewModel? = null

    init {
        val inflater = LayoutInflater.from(context)
        binding = LayoutSpeedviewBinding.inflate(inflater, this)
        initView()
    }

    fun setData(
        testPoint: TestPoint,
        type: String,
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

    fun setData(testPoint: TestPoint, type: String) {
        this.testPoint = testPoint
        this.type = type
        speedTest = SpeedTest()
        speedTest?.addTestPoint(testPoint)
    }
    fun setData(type: String){
        this.type = type
    }


    private fun initView() {
        binding.btnStart.setOnClickListener {
            Log.d("TAG", "type: $type")
            if (type == "no_connection") {
                Toast.makeText(context, "No connectivity!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel?.setIsScanning(true)
            prepareViewSpeedTest()
        }
        binding.speedView.isWithPointer = false
    }

    fun prepareViewSpeedTest() {
        countDownTimer = object : CountDownTimer(4000, 1000) {
            override fun onTick(l: Long) {
                if (l <= 1000) {
                    YoYo.with(Techniques.FadeIn).onStart {
                        binding.speedView.visibility = View.VISIBLE
                    }.playOn(binding.speedView)
                    YoYo.with(Techniques.FadeIn).onStart {
                        binding.containerSpeed.visibility = View.VISIBLE
                    }.playOn(binding.containerSpeed)
                    YoYo.with(Techniques.FadeIn).onStart {
                        binding.tvSpeedValue.visibility = View.VISIBLE
                    }.playOn(binding.tvSpeedValue)
                } else if (l <= 2000) {
                    if (type == "no_connection") {
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
                    YoYo.with(Techniques.FadeOut).playOn(binding.btnStart)
                    YoYo.with(Techniques.SlideOutLeft).playOn(binding.tvGo)
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
                    binding.tvSpeedValue.text = format(dl.toFloat().toDouble())
                    binding.speedView.speedTo(dl.toFloat())
                    if (progress >= 1) {
                        (context as Activity).runOnUiThread {
                            binding.tvDownloadValue.clearAnimation()
                            binding.tvDownloadValue.text = format(dl)
                            binding.tvSpeedValue.text = 0.0.toString() + ""
                        }
                        binding.speedView.speedTo(0f)
                        binding.speedView.stop()
                    }
                    testModel?.download = roundOffDecimal(dl)

                }

            }

            override fun onUploadUpdate(ul: Double, progress: Double) {
                (context as Activity).runOnUiThread {
                    if (progress == 0.0) {
                        uploadView()
                    }
                    binding.speedView.speedTo(ul.toFloat())
                    binding.tvSpeedValue.text = format(ul.toFloat().toDouble())
                    if (progress >= 1) {
                        binding.tvUploadValue.clearAnimation()
                        binding.tvUploadValue.text = format(ul)
                        binding.speedView.speedTo(0f)
                        binding.speedView.withTremble = false
                        binding.tvSpeedValue.text = 0.0.toString() + ""
                    }
                    testModel?.upload = roundOffDecimal(ul)

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
                    val bundle = Bundle()
                    bundle.putParcelable(Constant.KEY_TEST_MODEL, testModel)
                    bundle.putBoolean(Constant.KEY_FROM_SPEED_TEST_FRAGMENT, true)
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.action_fragmentMain_to_fragmentResultDetail, bundle)
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
        binding.speedView.indicatorLightColor =
            ContextCompat.getColor(context, R.color.gradient_green_start)
        binding.speedView.setSpeedometerColor(
            ContextCompat.getColor(
                context,
                R.color.gradient_green_start
            )
        )
        binding.tvDownloadValue.setTextColor(
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
        binding.tvDownloadValue.startAnimation(anim)
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
        binding.tvUploadValue.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.gradient_orange_start
            )
        )
        binding.tvUploadValue.startAnimation(anim)
        binding.speedView.setSpeedometerColor(
            ContextCompat.getColor(
                context,
                R.color.gradient_orange_start
            )
        )
    }

    fun resetView() {
        if (speedTest != null) {
            speedTest?.abort()
        }
        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }
        binding.speedView.setSpeedometerColor(ContextCompat.getColor(context, R.color.gray_400))
        binding.speedView.speedTo(0f)
        binding.tvSpeedValue.text = "0"
        binding.tvDownloadValue.clearAnimation()
        binding.tvUploadValue.clearAnimation()
        binding.tvDownloadValue.text = "0"
        binding.tvUploadValue.text = "0"
        binding.tvPingCount.text = "0"
        binding.tvJitterCount.text = "0"
        binding.tvDownloadValue.setTextColor(ContextCompat.getColor(context, R.color.gray_400))
        binding.tvUploadValue.setTextColor(ContextCompat.getColor(context, R.color.gray_400))
        YoYo.with(Techniques.FadeOut).onStart {
            binding.topView.visibility = View.GONE
        }.playOn(binding.topView)
        YoYo.with(Techniques.FadeIn).playOn(binding.btnStart)
        YoYo.with(Techniques.FadeIn).playOn(binding.tvGo)
        binding.tvGo.visibility = View.VISIBLE
        binding.btnStart.visibility = View.VISIBLE
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


}
