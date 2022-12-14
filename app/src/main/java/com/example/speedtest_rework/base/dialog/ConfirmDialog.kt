package com.example.speedtest_rework.base.dialog

import android.R
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import com.example.speedtest_rework.common.utils.clickWithDebounce
import com.example.speedtest_rework.databinding.CustomDialogBoxBinding

class ConfirmDialog(
    context: Context, private val callback: ConfirmCallback?,
    private val title: String,
    private val message: String?,
    private val positiveButtonTitle: String?,
    private val negativeButtonTitle: String?
) : Dialog(context) {
    private lateinit var binding: CustomDialogBoxBinding

    interface ConfirmCallback {
        fun negativeAction()
        fun positiveAction()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = CustomDialogBoxBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawableResource(R.color.transparent)

        binding.tvTitle.text = title
        message?.let {
            binding.tvContent.text = it
        }
        binding.btnYes.clickWithDebounce {
            callback?.positiveAction()
            dismiss()
        }
        binding.btnNo.clickWithDebounce {
            callback?.negativeAction()
            dismiss()
        }
        positiveButtonTitle?.let {
            binding.btnYes.text = it
        }
        negativeButtonTitle?.let {
            binding.btnNo.text = it
        }

    }
}