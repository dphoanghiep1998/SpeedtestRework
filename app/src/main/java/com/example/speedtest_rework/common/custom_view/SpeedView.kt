package com.example.speedtest_rework.common.custom_view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.location.Location
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.speedtest_rework.R
import com.example.speedtest_rework.core.SpeedTest
import com.example.speedtest_rework.core.getIP.GetIP
import com.example.speedtest_rework.data.model.HistoryModel
import com.example.speedtest_rework.databinding.LayoutSpeedviewBinding
import java.util.*

class SpeedView : ConstraintLayout {
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var speedTest: SpeedTest
    private lateinit var binding : LayoutSpeedviewBinding
    private lateinit var getIP : GetIP
    constructor(context: Context) : super(context){
        binding = LayoutSpeedviewBinding.inflate(LayoutInflater.from(context),this)
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        initView()
    }

    fun loadServer() {
        Log.d("TAG", "loadServer: vao day")
        object : Thread() {
            override fun run() {
                try {
                    requireActivity().runOnUiThread(Runnable { showLoadingPanel() })
                    speedTest = SpeedTest()
                    getIP = GetIP()
                    getIP.run()
                    getIP.join()
                    val mapKey: HashMap<Int, String> = getIP.getMapKey()
                    val mapValue: HashMap<Int, List<String>> = getIP.getMapValue()
                    val selfLat: Double = getIP.getSelfLat()
                    val selfLon: Double = getIP.getSelfLon()
                    var tmp = 19349458.0
                    var findServerIndex = 0
                    for (index in mapKey.keys) {
                        if (tempBlackList.contains(mapValue[index]!![5])) {
                            continue
                        }
                        val source = Location("Source")
                        source.latitude = selfLat
                        source.longitude = selfLon
                        val ls = mapValue[index]!!
                        val dest = Location("Dest")
                        dest.latitude = ls[0].toDouble()
                        dest.longitude = ls[1].toDouble()
                        val distance = source.distanceTo(dest).toDouble()
                        if (tmp > distance) {
                            tmp = distance
                            findServerIndex = index
                        }
                    }
                    val info = mapValue[findServerIndex]
                    if (info == null) {
                        requireActivity().runOnUiThread(Runnable {
                            binding.tvIspName.setText("No connection")
                            hideLoadingPanel()
                            isLoadedServer = false
                        })
                        return
                    } else {
                        testPoint = TestPoint(
                            info[3],
                            "http://" + info[6],
                            "speedtest/",
                            "speedtest/upload",
                            ""
                        )
                        requireActivity().runOnUiThread(Runnable {
                            if (type == "wifi") {
                                wifi.setWifi_external_ip(getIP.getSelfIspIp())
                                wifi.setWifi_ISP(getIP.getSelfIsp())
                            } else if (type == "mobile") {
                                mobile.setMobile_external_ip(getIP.getSelfIspIp())
                                mobile.setMobile_isp(getIP.getSelfIsp())
                            }
                            binding.tvIspName.setText(getIP.getSelfIsp())
                            hideLoadingPanel()
                        })
                        isLoadedServer = true
                    }
                } catch (e: Exception) {
                    Log.d("TAG", "run: to Exception ")
                    e.printStackTrace()
                }
            }
        }.start()
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
                    if (type == "no connection" || !isLoadedServer) {
                        viewModel.setIsScanning(false)
                        Toast.makeText(requireContext(), "No connection", Toast.LENGTH_SHORT).show()
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
                        requireActivity().runOnUiThread { downloadView() }
                    }
                    requireActivity().runOnUiThread {
                        binding.tvSpeedValue.setText(format(dl.toFloat().toDouble()))
                        binding.speedView.speedTo(dl.toFloat())
                        if (progress >= 1) {
                            requireActivity().runOnUiThread {
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
                    requireActivity().runOnUiThread {
                        if (progress == 0.0) {
                            requireActivity().runOnUiThread { uploadView() }
                        }
                        binding.speedView.speedTo(ul.toFloat())
                        binding.tvSpeedValue.setText(format(ul.toFloat().toDouble()))
                        if (progress >= 1) {
                            binding.tvUploadValue.clearAnimation()
                            binding.tvUploadValue.setText(format(ul))
                            binding.speedView.speedTo(0f)
                            binding.speedView.setWithTremble(false)
                            binding.tvSpeedValue.text = 0.0.toString() + ""
                        }
                    }
                }

                override fun onPingJitterUpdate(ping: Double, jitter: Double, progress: Double) {
                    requireActivity().runOnUiThread {
                        binding.tvPingCount.setText(format(ping) + " ms")
                        binding.tvJitterCount.setText(format(jitter) + " ms")
                    }
                }

                override fun onEnd() {
//                    val intent = Intent(activity, ResultActivity::class.java)
                    requireActivity().runOnUiThread {
                        if (type == "wifi") {
                            val connectivityTestModel = HistoryModel(
                                binding.tvWifiName.text.toString(),
                                Date(),
                                binding.tvDownloadValue.text.toString(),
                                binding.tvUploadValue.text.toString(),
                                binding.tvPingCount.text.toString().replace(" ms", ""),
                                binding.tvJitterCount.text.toString().replace(" ms", ""),
                                binding.tvLossCount.text.toString().replace("%", ""),
                                null,
                                wifi, type
                            )
                            (requireActivity() as MainActivity).viewModel.insertResultTest(
                                connectivityTestModel
                            )
                            intent.putExtra("test_result", connectivityTestModel)
                            intent.putExtra("EXTRA_MESS_1", "from_scan_activity")
                        } else {
                            val connectivityTestModel = ConnectivityTestModel(
                                binding.tvWifiName.text.toString(),
                                Date(),
                                binding.tvDownloadValue.text.toString(),
                                binding.tvUploadValue.text.toString(),
                                binding.tvPingCount.text.toString().replace(" ms", ""),
                                binding.tvJitterCount.text.toString().replace(" ms", ""),
                                binding.tvLossCount.text.toString().replace("%", ""),
                                mobile,
                                null, type
                            )
                            viewModel.insertResultTest(
                                connectivityTestModel
                            )
                            intent.putExtra("test_result", connectivityTestModel)
                            intent.putExtra("EXTRA_MESS_1", "from_scan_activity")
                        }
                        viewModel.setIsScanning(false)
                        startActivityForResult(intent, 1)
                    }
                }

                override fun onCriticalFailure(err: String?) {
                    viewModel.setIsScanning(false)

                }
            })
        }
    }

    fun setupView() {
//    customSpeedview()

        // speedview
        binding.speedView.centerCircleColor = getColor(R.color.gray_400)
        val shader: Shader = LinearGradient(
            0f,
            0f,
            0f,
            binding.tvGo.lineHeight.toFloat(),
            getColor(R.color.gradient_green_start),
            getColor(R.color.gradient_green_end),
            Shader.TileMode.REPEAT
        )
        binding.tvGo.paint.shader = shader
        // Underline wifi name
        val wifi_name = binding.tvWifiName.text.toString()
        val underlineWifiName = SpannableString(wifi_name)
        underlineWifiName.setSpan(UnderlineSpan(), 0, wifi_name.length, 0)
        binding.tvWifiName.text = underlineWifiName
        // custom tick

        //onClick start scan
        binding.btnStart.setOnClickListener {
            viewModel.setIsScanning(true)
        }


        //animation expandview
        val anim = ValueAnimator.ofInt(binding.containerExpandView.measuredWidth, 600)
        anim.addUpdateListener { valueAnimator: ValueAnimator? -> binding.containerExpandView.layoutParams }

        //expandview
        binding.containerExpandView.setOnClickListener { view ->
            var isExpanded: Boolean = false
            TransitionManager.beginDelayedTransition(
                view as ViewGroup,
                AutoTransition()
            )
            val layoutParams = view.getLayoutParams()
            if (!isExpanded) {
                layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT
                view.setLayoutParams(layoutParams)
                isExpanded = true
            } else {
                layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                view.setLayoutParams(layoutParams)
                isExpanded = false
            }
        }
    }

    private fun downloadView() {
        val anim: Animation = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 100
        anim.startOffset = 50
        anim.repeatCount = Animation.INFINITE
        binding.speedView.indicatorLightColor = getColor(R.color.gradient_green_start)
        binding.speedView.setSpeedometerColor(getColor(R.color.gradient_green_start))
        binding.tvDownloadValue.setTextColor(getColor(R.color.gradient_green_start))
        binding.iconSpeedValue.setImageDrawable(getDrawable(R.drawable.ic_download))
        binding.tvDownloadValue.startAnimation(anim)
    }

    private fun uploadView() {
        val anim: Animation = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 100
        anim.startOffset = 50
        anim.repeatCount = Animation.INFINITE
        binding.iconSpeedValue.setImageDrawable(getDrawable(R.drawable.ic_upload))
        binding.speedView.indicatorLightColor = getColor(R.color.gradient_orange_start)
        binding.tvUploadValue.setTextColor(getColor(R.color.gradient_orange_start))
        binding.tvUploadValue.startAnimation(anim)
        binding.speedView.setSpeedometerColor(getColor(R.color.gradient_orange_start))
    }

    fun resetView() {
        if (speedTest != null) {
            speedTest.abort()
        }
        binding.speedView.setSpeedometerColor(getColor(R.color.gray_400))
        binding.speedView.speedTo(0f)
        binding.tvSpeedValue.text = "0"
        binding.tvDownloadValue.clearAnimation()
        binding.tvUploadValue.clearAnimation()
        binding.tvWifiName.isEnabled = true
        binding.tvDownloadValue.text = "0"
        binding.tvUploadValue.text = "0"
        binding.tvPingCount.text = "0"
        binding.tvJitterCount.text = "0"
        if (countDownTimer != null) {
            countDownTimer.cancel()
        }
        binding.tvDownloadValue.setTextColor(getColor(R.color.gray_400))
        binding.tvUploadValue.setTextColor(getColor(R.color.gray_400))
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
        binding.tvWifiName.isEnabled = true
    }


}
