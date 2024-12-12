package com.onebilliongod.android.jetpackandroid.view.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onebilliongod.android.jetpackandroid.data.socket.client.SerialClient
import com.onebilliongod.android.jetpackandroid.data.socket.parser.FloatPacketParser
import com.onebilliongod.android.jetpackandroid.utils.DatabaseUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.TimeMark
import kotlin.time.TimeSource


@HiltViewModel
class SerialViewModel @Inject constructor(private val serialClient: SerialClient,
                                          private val databaseUtil: DatabaseUtil) : ViewModel() {
    private val parser = FloatPacketParser()

    var isGeneratingData by mutableStateOf(false)
        private set

    //StateFlow to provide chart data to the UI in a reactive manner
    private val startData = ChartData(0, 0f, 0f, 0f)
    private val _chartData = MutableStateFlow(startData)
    val chartData: StateFlow<ChartData> = _chartData

    private var startTime: TimeMark? = null

    private var receiveJob: Job? = null

    fun connect() {
        serialClient.connect()
    }

    fun test() {
        databaseUtil.backupDatabase()
    }

    fun start() {
        //send start command
        val startCommand = "START\n"
        serialClient.sendData(startCommand)

        if (isGeneratingData) return

        isGeneratingData = true

        if (startTime == null) {
            startTime = TimeSource.Monotonic.markNow()
        }

        if (receiveJob == null || receiveJob?.isCancelled == true) {
            receiveJob = viewModelScope.launch {
                receivedData()
            }
        }
    }

    fun stop() {
        stopCommand()

//        receiveJob?.cancel()
//        receiveJob = null
        isGeneratingData = false
    }

    fun disconnection() {
        startTime = null
        serialClient.close()

        stop()
        _chartData.value = startData
    }

    fun save() {
        sendConfigurationData("SETTING\n")
    }

    fun stopCommand() {
        val cmd = "STOP"
        serialClient.sendData(cmd)
    }

    fun sendConfigurationData(configData: String) {
        //send config data
        serialClient.sendData(configData)
    }

    private suspend fun receivedData() {
        //use flow to control the backPressing
        serialClient.receivedData
            .map { data ->
                val t = startTime?.elapsedNow()!!.inWholeMilliseconds / 1000f
                ChartData(
                    x = t.toInt(),
                    y = data[0],
                    y2 = data[1],
                    y3 = data[2]
                )
            }
            .buffer(capacity = 100)
            .sample(200L)
            .collect {
                _chartData.value = it
                Log.i("SerialViewModel", "data:${it}")
            }
    }

}