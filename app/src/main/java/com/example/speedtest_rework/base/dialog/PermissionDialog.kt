package com.example.speedtest_rework.base.dialog

import android.R
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import com.example.speedtest_rework.common.utils.clickWithDebounce
import com.example.speedtest_rework.databinding.CustomDialogBoxBinding
import com.example.speedtest_rework.databinding.DialogPermissionBinding

class PermissionDialog(
    context: Context, private val callback: ConfirmCallback?,

    ) : Dialog(context) {
    private lateinit var binding: DialogPermissionBinding

    interface ConfirmCallback {
        fun negativeAction()
        fun positiveAction()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawableResource(R.color.transparent)

        binding.btnAllow.clickWithDebounce {
            callback?.positiveAction()
            dismiss()
        }
        binding.btnDeny.clickWithDebounce {
            callback?.negativeAction()
            dismiss()
        }
        binding.btnClose.clickWithDebounce {
            callback?.negativeAction()
            dismiss()
        }

    }
}