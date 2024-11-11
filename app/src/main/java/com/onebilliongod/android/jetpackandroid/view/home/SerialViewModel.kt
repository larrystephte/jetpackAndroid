package com.onebilliongod.android.jetpackandroid.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.onebilliongod.android.jetpackandroid.data.socket.client.SerialClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SerialViewModel @Inject constructor(private val serialClient: SerialClient?) : ViewModel() {
    private val _receivedData = MutableLiveData<String>()
    val receivedData: LiveData<String> get() = _receivedData

    fun start() {
        //send start command
        val startCommand = "START"
        serialClient?.sendData(startCommand)
    }

    fun sendConfigurationData(configData: String) {
        //send config data
        serialClient?.sendData(configData)
    }

    fun startReadingSerialData() {
        serialClient?.startReading { data ->
            _receivedData.postValue(data)
        }
    }

}