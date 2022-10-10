package com.example.speedtest_rework.ui.main.result_history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.ui.main.result_history.adapter.HistoryAdapter
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.dialog.ConfirmDialog
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.Constant
import com.example.speedtest_rework.data.model.HistoryModel
import com.example.speedtest_rework.databinding.FragmentResultsBinding
import com.example.speedtest_rework.ui.main.result_history.adapter.ResultTouchHelper
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel

class FragmentResults : BaseFragment(), ResultTouchHelper, ConfirmDialog.ConfirmCallback {
    private lateinit var binding: FragmentResultsBinding
    private val viewModel: SpeedTestViewModel by activityViewModels()
    private var adapter: HistoryAdapter = HistoryAdapter(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "onCreate: ")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("TAG", "onCreateView: ")
        binding = FragmentResultsBinding.inflate(inflater, container, false)
        rcvInit()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.btnDelete.setOnClickListener {
            val customDialog = ConfirmDialog(
                requireActivity(),
                this,
                getString(R.string.delete_all_title),
                getString(R.string.delete_all_content),
                getString(R.string.YES),
                getString(R.string.NO)
            )
            customDialog.show()
        }

    }


    private fun rcvInit() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rcvConnectTestResult.layoutManager = linearLayoutManager
        binding.rcvConnectTestResult.adapter = adapter
        viewModel.getListHistory().observe(viewLifecycleOwner) { list -> adapter.setData(list) }
    }


    override fun negativeAction() {
    }

    override fun positiveAction() {
        viewModel.deleteAllHistoryAction()

    }

    override fun onClickResultTest(historyModel: HistoryModel?) {
        val bundle = Bundle()
        bundle.putParcelable(Constant.KEY_TEST_MODEL, historyModel)
        findNavController().navigate(R.id.action_fragmentMain_to_fragmentResultDetail, bundle)
    }
}