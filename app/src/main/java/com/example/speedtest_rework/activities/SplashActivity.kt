package com.example.speedtest_rework.activities

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.DecelerateInterpolator
import com.example.speedtest_rework.activities.MainActivity
import com.example.speedtest_rework.base.activity.BaseActivity
import com.example.speedtest_rework.databinding.ActivitySplashBinding


class SplashActivity : BaseActivity() {
    private lateinit var binding : ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigateMain()

    }

    private fun navigateMain(){
        val animation: ObjectAnimator = ObjectAnimator.ofInt(binding.pbLoading, "progress", 0, 100)
        animation.duration = 3000

        animation.interpolator = DecelerateInterpolator()
        animation.start()
        Handler().postDelayed({
            val i = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(i)
            finish()
        }, 3500)
    }


}