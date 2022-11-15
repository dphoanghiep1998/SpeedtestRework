package com.example.speedtest_rework.data.repositories

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.speedtest_rework.R
import com.example.speedtest_rework.common.utils.AppSharePreference
import com.example.speedtest_rework.core.getIP.CurrentNetworkInfo
import com.example.speedtest_rework.data.model.HistoryModel
import com.example.speedtest_rework.data.services.AddressInfoRemoteService
import com.example.speedtest_rework.data.services.HistoryLocalService
import com.example.speedtest_rework.data.services.PrivilegedService
import com.example.speedtest_rework.di.IoDispatcher
import com.example.speedtest_rework.network.NetworkResult
import com.example.speedtest_rework.ui.data_usage.model.DataUsageModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject


class AppRepository @Inject constructor(
    private val addressInfoRemoteService: AddressInfoRemoteService,
    private val historyLocalService: HistoryLocalService,
    private val privilegeService: PrivilegedService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {


    suspend fun insertHistoryModel(model: HistoryModel) = withContext(dispatcher) {
        historyLocalService.historyDao.insertHistory(model.toHistoryEntity())
    }

    suspend fun deleteAllHistory() =
        withContext(dispatcher) {
            historyLocalService.historyDao.deleteAllHistory()
        }

    suspend fun deleteHistory(model: HistoryModel) = withContext(dispatcher) {
        historyLocalService.historyDao.deleteHistory(model.toHistoryEntity())
    }

    fun getAllHistory(): LiveData<List<HistoryModel>> =
        historyLocalService.historyDao.getListHistory()

    suspend fun getAddressInfo() = withContext(dispatcher) {
        when (val result = addressInfoRemoteService.getAddressInfoList()) {
            is NetworkResult.Error -> {
                Log.d("TAG", "getAddressInfo: " + result.exception)
                throw result.exception
            }
            is NetworkResult.Success -> {
                result.data
            }
        }
    }

    suspend fun getCurrentNetworkInfo(): CurrentNetworkInfo {
        var currentNetworkInfo: CurrentNetworkInfo
        withContext(dispatcher) {
            currentNetworkInfo = CurrentNetworkInfo().currentNetWorkInfo
            AppSharePreference.INSTANCE.saveIpRouter(R.string.ip_router,"https://www.google.com")
        }
        return currentNetworkInfo
    }

    @RequiresApi(Build.VERSION_CODES.M)
    suspend fun getDataUsageList(): List<DataUsageModel> {
        var mList:List<DataUsageModel>
        withContext(dispatcher){
            mList = privilegeService.getUsageData()
        }
        return mList
    }




}