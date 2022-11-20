package com.example.speedtest_rework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.speedtest_rework.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FragmentSignalTestViewModel @Inject constructor():BaseViewModel() {

    private var listSignalLocation = MutableLiveData<MutableList<Pair<String, String>>>(
        mutableListOf()
    )
    val mListSignalLocation: LiveData<MutableList<Pair<String, String>>>
        get() = listSignalLocation

    fun setListSignalLocation(list: MutableList<Pair<String, String>>) {
        listSignalLocation.value = list
    }

    private var isSignalScanning = MutableLiveData(false)
    val signalScanning: LiveData<Boolean>
        get() = isSignalScanning
    fun setSignalScanning(status: Boolean) {
        isSignalScanning.value = status
    }

}