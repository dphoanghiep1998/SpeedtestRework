package com.example.speedtest_rework.common.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.speedtest_rework.databinding.ExpandViewBinding

class ExpandView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    private var binding: ExpandViewBinding

    init {
        binding = ExpandViewBinding.inflate(LayoutInflater.from(context), this)
    }
}