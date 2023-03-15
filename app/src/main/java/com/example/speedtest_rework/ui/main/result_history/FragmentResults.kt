package com.example.speedtest_rework.ui.main.result_history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.CustomApplication
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.dialog.ConfirmDialog
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.extensions.InterAds
import com.example.speedtest_rework.common.extensions.showInterAds
import com.example.speedtest_rework.common.utils.Constant
import com.example.speedtest_rework.common.utils.clickWithDebounce
import com.example.speedtest_rework.data.model.HistoryModel
import com.example.speedtest_rework.databinding.FragmentResultsBinding
import com.example.speedtest_rework.ui.main.result_history.adapter.HistoryAdapter
import com.example.speedtest_rework.ui.main.result_history.adapter.ResultTouchHelper
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel

class FragmentResults(private val onStartClickedListener: OnStartClickedListener) : BaseFragment(),
    ResultTouchHelper, ConfirmDialog.ConfirmCallback {
    private lateinit var binding: FragmentResultsBinding
    private val viewModel: SpeedTestViewModel by activityViewModels()
    private var adapter: HistoryAdapter = HistoryAdapter(this)
    private lateinit var app: CustomApplication

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultsBinding.inflate(inflater, container, false)
        observeUnitType()
        rcvInit()
        app = requireActivity().application as CustomApplication

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }
    private fun showAdsBottomNav() {
        if (!app.showAdsClickBottomNav) {
            showInterAds(action = {
                app.showAdsClickBottomNav = true
            }, InterAds.SWITCH_TAB)
        }
    }

    private fun initView() {
        binding.btnDelete.clickWithDebounce {
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
        binding.btnTestAgain.clickWithDebounce {
            onStartClickedListener.onStartClicked()
        }

    }

    private fun observeUnitType() {
        viewModel.unitType.observe(viewLifecycleOwner) {
            adapter.setData(it)
            binding.tvDownloadCurrency.text = getString(it.unit)
            binding.tvUploadCurrency.text = getString(it.unit)
        }
    }


    private fun rcvInit() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rcvConnectTestResult.layoutManager = linearLayoutManager
        binding.rcvConnectTestResult.adapter = adapter
        binding.rcvConnectTestResult.isMotionEventSplittingEnabled = false


        viewModel.getListHistory().observe(viewLifecycleOwner) { list ->
            if (list.isEmpty()) {
                binding.containerEmpty.visibility = View.VISIBLE
                binding.btnDelete.clickWithDebounce {
                    toastShort(getString(R.string.no_list_found))
                }
            } else {
                binding.containerEmpty.visibility = View.GONE
                binding.btnDelete.clickWithDebounce {
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
            adapter.setData(list.toMutableList())
        }
    }


    override fun negativeAction() {
    }

    override fun positiveAction() {
        viewModel.deleteAllHistoryAction()

    }

    override fun onClickResultTest(historyModel: HistoryModel?) {
        val bundle = Bundle()
        bundle.putParcelable(Constant.KEY_TEST_MODEL, historyModel)
        navigateToPage(R.id.fragmentMain,R.id.action_fragmentMain_to_fragmentResultDetail, bundle)
    }

    interface OnStartClickedListener {
        fun onStartClicked()
    }
}