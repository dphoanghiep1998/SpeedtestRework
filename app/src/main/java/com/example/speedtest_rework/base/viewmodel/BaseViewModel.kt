package com.example.speedtest_rework.base.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.speedtest_rework.common.utils.Event
import com.example.speedtest_rework.network.BaseNetworkException
import com.example.speedtest_rework.network.NetworkErrorException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job

open class BaseViewModel : ViewModel() {

    val isError = MutableLiveData(false)
    var baseNetworkException = MutableLiveData<Event<BaseNetworkException>>()
        protected set

    var networkException = MutableLiveData<Event<NetworkErrorException>>()
        protected set


    var isLoading = MutableLiveData<Event<Boolean>>()
        protected set

    var onNavigateToPage = MutableLiveData<Event<Int>>()
        protected set

    var errorMessageResourceId = MutableLiveData<Event<Int>>()
        protected set

    var notifyMessageResourceId = MutableLiveData<Event<Int>>()
        protected set

    var isLoadingMore = MutableLiveData<Event<Boolean>>()
        protected set

    var parentJob: Job? = null
        protected set

    protected fun registerJobFinish(){
        parentJob?.invokeOnCompletion {
            showLoading(false)
        }
    }

    protected fun showError(messageId: Int) {
        errorMessageResourceId.postValue(Event(messageId))
    }

    protected fun showNotify(messageId: Int) {
        notifyMessageResourceId.postValue(Event(messageId))
    }


    protected fun showLoading(isShow: Boolean) {
        isLoading.postValue(Event(isShow))
    }

    protected fun showLoadingMore(isShow: Boolean){
        isLoadingMore.postValue(Event(isShow))
    }

    protected fun navigateToPage(actionId: Int) {
        onNavigateToPage.postValue(Event(actionId))
    }
    protected open fun parseErrorCallApi(e: Throwable) {
        isError.value = true
        when (e) {
            is BaseNetworkException -> {
                baseNetworkException.postValue(Event(e))
            }
            is NetworkErrorException -> {
                networkException.postValue(Event(e))
            }
            else -> {
                val unknowException = BaseNetworkException()
                unknowException.mainMessage = e.message ?: "Something went wrong"
                baseNetworkException.postValue(Event(unknowException))
            }
        }
    }
    protected fun addBaseNetworkException(exception: BaseNetworkException) {
        baseNetworkException.postValue(Event(exception))
    }

    protected fun addNetworkException(exception: NetworkErrorException) {
        networkException.postValue(Event(exception))
    }

    val handler = CoroutineExceptionHandler { _, exception ->
        parseErrorCallApi(exception)
    }

    open fun fetchData() {

    }

}