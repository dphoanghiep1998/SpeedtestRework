package com.example.speedtest_rework.common.custom_view

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.speedtest_rework.R
import com.example.speedtest_rework.core.SpeedTest
import com.example.speedtest_rework.core.serverSelector.TestPoint
import com.example.speedtest_rework.databinding.LayoutSpeedviewBinding
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import java.util.*

class SpeedView(
    context: Context,
    attributeSet: AttributeSet
) : ConstraintLayout(context, attributeSet) {

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var speedTest: SpeedTest
    private lateinit var binding: LayoutSpeedviewBinding
    private lateinit var viewModel: SpeedTestViewModel
    private var type = "no connection"
    private var testPoint: TestPoint? = null

    init {
        val inflater = LayoutInflater.from(context)
        binding = LayoutSpeedviewBinding.inflate(inflater, this)
    }

    fun initData(testPoint: TestPoint,isScanning:Boolean) {
        this.testPoint = testPoint
    }

    private fun initView() {

    }

    fun prepareViewSpeedTest() {
        countDownTimer = object : CountDownTimer(4000, 1000) {
            override fun onTick(l: Long) {
                if (l <= 1000) {
                    YoYo.with(Techniques.FadeIn).onStart { animator: Animator? ->
                        binding.speedView.visibility = View.VISIBLE
                    }.playOn(binding.speedView)
                    YoYo.with(Techniques.FadeIn).onStart { animator: Animator? ->
                        binding.containerSpeed.visibility = View.VISIBLE
                    }.playOn(binding.containerSpeed)
                    YoYo.with(Techniques.FadeIn).onStart { animator: Animator? ->
                        binding.tvSpeedValue.visibility = View.VISIBLE
                    }.playOn(binding.tvSpeedValue)
                } else if (l <= 2000) {
                    if (type == "no connection") {
                        viewModel.setIsScanning(false)
                        Toast.makeText(context, "No connection", Toast.LENGTH_SHORT).show()
                        return
                    } else {
                        YoYo.with(Techniques.FadeOut).onEnd { animator: Animator? ->
                            binding.loading.visibility = View.GONE
                        }.playOn(binding.loading)
                        YoYo.with(Techniques.FadeOut).onEnd { animator: Animator? ->
                            binding.tvConnecting.visibility = View.GONE
                        }.playOn(binding.tvConnecting)
                        YoYo.with(Techniques.SlideInDown).onStart { animator: Animator? ->
                            binding.topView.visibility = View.VISIBLE
                        }.playOn(binding.topView)
                    }
                } else if (l <= 3000) {
                    YoYo.with(Techniques.FadeIn).onStart { animator: Animator? ->
                        binding.loading.visibility = View.VISIBLE
                    }.playOn(binding.loading)
                } else if (l <= 4000) {
                    binding.btnStart.isEnabled = false
                    YoYo.with(Techniques.FadeOut).playOn(binding.btnStart)
                    YoYo.with(Techniques.SlideOutLeft).playOn(binding.tvGo)
                    YoYo.with(Techniques.SlideInRight).onStart { animator: Animator? ->
                        binding.tvConnecting.visibility = View.VISIBLE
                    }.playOn(binding.tvConnecting)
                }
            }

            override fun onFinish() {
                runSpeedTest()
            }
        }
        countDownTimer.start()
    }

    fun runSpeedTest() {
        if (speedTest != null) {
            speedTest = SpeedTest()
            speedTest.addTestPoint(testPoint)
        } else {
            speedTest.addTestPoint(testPoint)
//            getConnection()
            speedTest.start(object :
                SpeedTest.SpeedtestHandler() {
                override fun onDownloadUpdate(dl: Double, progress: Double) {
                    if (progress == 0.0) {
                        (context as Activity).runOnUiThread { downloadView() }
                    }
                    (context as Activity).runOnUiThread {
                        binding.tvSpeedValue.setText(format(dl.toFloat().toDouble()))
                        binding.speedView.speedTo(dl.toFloat())
                        if (progress >= 1) {
                            (context as Activity).runOnUiThread {
                                binding.tvDownloadValue.clearAnimation()
                                binding.tvDownloadValue.setText(format(dl))
                                binding.tvSpeedValue.text = 0.0.toString() + ""
                            }
                            binding.speedView.speedTo(0f)
                            binding.speedView.stop()
                        }
                    }
                }

                override fun onUploadUpdate(ul: Double, progress: Double) {
                    (context as Activity).runOnUiThread {
                        if (progress == 0.0) {
                            (context as Activity).runOnUiThread { uploadView() }
                        }
                        binding.speedView.speedTo(ul.toFloat())
                        binding.tvSpeedValue.setText(format(ul.toFloat().toDouble()))
                        if (progress >= 1) {
                            binding.tvUploadValue.clearAnimation()
                            binding.tvUploadValue.setText(format(ul))
                            binding.speedView.speedTo(0f)
                            binding.speedView.withTremble = false
                            binding.tvSpeedValue.text = 0.0.toString() + ""
                        }
                    }
                }

                override fun onPingJitterUpdate(ping: Double, jitter: Double, progress: Double) {
                    (context as Activity).runOnUiThread {
                        binding.tvPingCount.setText(format(ping) + " ms")
                        binding.tvJitterCount.setText(format(jitter) + " ms")
                    }
                }

                override fun onEnd() {
//                    val intent = Intent(activity, ResultActivity::class.java)
//                    (context as Activity).runOnUiThread {
//                        if (type == "wifi") {
//                            val connectivityTestModel = HistoryModel(
//                                binding.tvWifiName.text.toString(),
//                                Date(),
//                                binding.tvDownloadValue.text.toString(),
//                                binding.tvUploadValue.text.toString(),
//                                binding.tvPingCount.text.toString().replace(" ms", ""),
//                                binding.tvJitterCount.text.toString().replace(" ms", ""),
//                                binding.tvLossCount.text.toString().replace("%", ""),
//                                null,
//                                wifi, type
//                            )
//                            (requireActivity() as MainActivity).viewModel.insertResultTest(
//                                connectivityTestModel
//                            )
//                            intent.putExtra("test_result", connectivityTestModel)
//                            intent.putExtra("EXTRA_MESS_1", "from_scan_activity")
//                        } else {
//                            val connectivityTestModel = ConnectivityTestModel(
//                                binding.tvWifiName.text.toString(),
//                                Date(),
//                                binding.tvDownloadValue.text.toString(),
//                                binding.tvUploadValue.text.toString(),
//                                binding.tvPingCount.text.toString().replace(" ms", ""),
//                                binding.tvJitterCount.text.toString().replace(" ms", ""),
//                                binding.tvLossCount.text.toString().replace("%", ""),
//                                mobile,
//                                null, type
//                            )
//                            viewModel.insertResultTest(
//                                connectivityTestModel
//                            )
////                            intent.putExtra("test_result", connectivityTestModel)
////                            intent.putExtra("EXTRA_MESS_1", "from_scan_activity")
//                        }
//                        viewModel.setIsScanning(false)
////                        startActivityForResult(intent, 1)
//                    }
                }

                override fun onCriticalFailure(err: String?) {
                    viewModel.setIsScanning(false)

                }
            })
        }
    }


    private fun downloadView() {
        val anim: Animation = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 100
        anim.startOffset = 50
        anim.repeatCount = Animation.INFINITE
        binding.speedView.indicatorLightColor = resources.getColor(R.color.gradient_green_start)
        binding.speedView.setSpeedometerColor(resources.getColor(R.color.gradient_green_start))
        binding.tvDownloadValue.setTextColor(resources.getColor(R.color.gradient_green_start))
        binding.iconSpeedValue.setImageDrawable(resources.getDrawable(R.drawable.ic_download))
        binding.tvDownloadValue.startAnimation(anim)
    }

    private fun uploadView() {
        val anim: Animation = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 100
        anim.startOffset = 50
        anim.repeatCount = Animation.INFINITE
        binding.iconSpeedValue.setImageDrawable(resources.getDrawable(R.drawable.ic_upload))
        binding.speedView.indicatorLightColor = resources.getColor(R.color.gradient_orange_start)
        binding.tvUploadValue.setTextColor(resources.getColor(R.color.gradient_orange_start))
        binding.tvUploadValue.startAnimation(anim)
        binding.speedView.setSpeedometerColor(resources.getColor(R.color.gradient_orange_start))
    }

    fun resetView() {
        if (speedTest != null) {
            speedTest.abort()
        }
        binding.speedView.setSpeedometerColor(resources.getColor(R.color.gray_400))
        binding.speedView.speedTo(0f)
        binding.tvSpeedValue.text = "0"
        binding.tvDownloadValue.clearAnimation()
        binding.tvUploadValue.clearAnimation()
        binding.tvDownloadValue.text = "0"
        binding.tvUploadValue.text = "0"
        binding.tvPingCount.text = "0"
        binding.tvJitterCount.text = "0"
        if (countDownTimer != null) {
            countDownTimer.cancel()
        }
        binding.tvDownloadValue.setTextColor(resources.getColor(R.color.gray_400))
        binding.tvUploadValue.setTextColor(resources.getColor(R.color.gray_400))
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

    private fun format(d: Double): String? {
        val l: Locale
        l = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales[0]
        } else {
            resources.configuration.locale
        }
        return if (d < 200) String.format(l, "%.2f", d) else "" + Math.round(d)
    }


}
