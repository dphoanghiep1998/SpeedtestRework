package com.example.speedtest_rework.base.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import com.example.speedtest_rework.R
import com.example.speedtest_rework.databinding.DialogRateBinding

interface RateCallBack {
    fun onClickRateUs(star: Int)
}

class RateDialog(context: Context, private val callback: RateCallBack) : Dialog(context) {
    private lateinit var binding: DialogRateBinding
    private var star = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogRateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        initFirst()
        binding.btnRate.setOnClickListener {
            callback.onClickRateUs(star)
            dismiss()
        }
    }

    private fun initFirst() {
        binding.btnRate.isEnabled = false
        binding.btnRate.setTextColor(Color.parseColor("#6611111F"))
        var count = 0
        val countDownTimer = object : CountDownTimer(3000, 300) {
            override fun onTick(p0: Long) {
                count++
                when (count) {
                    1 -> {
                        binding.star1.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_rate_app_active
                            )
                        )
                        binding.imvExpressive.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_1_star
                            )
                        )
                        binding.tvExpressive.text = context.getString(R.string.expressive_bad)
                    }
                    2 -> {
                        binding.star2.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_rate_app_active
                            )
                        )
                        binding.imvExpressive.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_2_star
                            )
                        )
                        binding.tvExpressive.text = context.getString(R.string.expressive_bad)
                    }
                    3 -> {
                        binding.star3.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_rate_app_active
                            )
                        )
                        binding.imvExpressive.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_3_star
                            )
                        )
                        binding.tvExpressive.text = context.getString(R.string.expressive_normal)

                    }
                    4 -> {
                        binding.star4.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_rate_app_active
                            )
                        )
                        binding.imvExpressive.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_4_star
                            )
                        )
                        binding.tvExpressive.text = context.getString(R.string.expressive_good)

                    }
                    5 -> {
                        binding.star5.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_rate_app_active
                            )
                        )
                        binding.imvExpressive.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_5_star
                            )
                        )
                        binding.tvExpressive.text = context.getString(R.string.expressive_best)

                    }
                    6 -> {
                        binding.star5.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_rate_app
                            )
                        )
                        binding.imvExpressive.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_4_star
                            )
                        )
                        binding.tvExpressive.text = context.getString(R.string.expressive_good)

                    }
                    7 -> {
                        binding.star4.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_rate_app
                            )
                        )
                        binding.imvExpressive.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_3_star
                            )
                        )
                        binding.tvExpressive.text = context.getString(R.string.expressive_normal)

                    }
                    8 -> {
                        binding.star3.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_rate_app
                            )
                        )
                        binding.imvExpressive.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_2_star
                            )
                        )
                        binding.tvExpressive.text = context.getString(R.string.expressive_bad)

                    }
                    9 -> {
                        binding.star2.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_rate_app
                            )
                        )
                        binding.imvExpressive.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_1_star
                            )
                        )
                        binding.tvExpressive.text = context.getString(R.string.expressive_bad)
                    }
                    10 -> {
                        binding.star1.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_rate_app
                            )
                        )
                        binding.imvExpressive.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_5_star
                            )
                        )
                        binding.tvExpressive.text = context.getString(R.string.expressive_holder)

                    }
                }
            }

            override fun onFinish() {
                binding.btnRate.isEnabled = true
                binding.btnRate.setTextColor(ContextCompat.getColor(context, R.color.gray_700))
            }
        }
        countDownTimer.start()

        val groupStar =
            listOf(binding.star1, binding.star2, binding.star3, binding.star4, binding.star5)
        val expressive = listOf(
            R.drawable.ic_1_star,
            R.drawable.ic_2_star,
            R.drawable.ic_3_star,
            R.drawable.ic_4_star,
            R.drawable.ic_5_star
        )
        val textExpressive = listOf(
            R.string.expressive_bad,
            R.string.expressive_bad,
            R.string.expressive_normal,
            R.string.expressive_good,
            R.string.expressive_best
        )
        groupStar.forEachIndexed { index, item ->
            kotlin.run {
                item.setOnClickListener {
                    star = index + 1
                    countDownTimer.cancel()
                    binding.btnRate.isEnabled = true
                    binding.btnRate.setTextColor(ContextCompat.getColor(context, R.color.gray_700))
                    binding.imvExpressive.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            expressive[index]
                        )
                    )

                    binding.tvExpressive.text = context.getString(textExpressive[index])
                    val subStar = groupStar.slice(0..index)
                    if (index < groupStar.size - 1) {
                        val subStarInActive = groupStar.slice(index + 1..4)
                        subStarInActive.forEach { item ->
                            kotlin.run {
                                item.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.ic_rate_app
                                    )
                                )
                            }
                        }
                    }
                    subStar.forEach { item ->
                        kotlin.run {
                            item.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.ic_rate_app_active
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}