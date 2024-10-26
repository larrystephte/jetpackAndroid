package com.onebilliongod.android.jetpackandroid.view.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onebilliongod.android.jetpackandroid.data.socket.client.TcpClient
import com.onebilliongod.android.jetpackandroid.utils.HexUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.TimeMark
import kotlin.time.TimeSource

data class ChartData(
    val x : Int,
    val y : Float,
    val y2 : Float,
    val y3 : Float,
)
//Just Test TCP
@HiltViewModel
class TcpViewModel @Inject constructor(private val tcpClient: TcpClient): ViewModel() {
    var isGeneratingData by mutableStateOf(false)
        private set

    //StateFlow to provide chart data to the UI in a reactive manner
    private val startData = ChartData(0, 0f, 0f, 0f)
    private val _chartData = MutableStateFlow(startData)
    val chartData: StateFlow<ChartData> = _chartData

    private var startTime: TimeMark? = null

    private var receiveJob: Job? = null

    init {
//        connect()
    }

    fun connect() {
        tcpClient.connect()
    }

    fun start() {
        if (isGeneratingData) return

        isGeneratingData = true

        if (startTime == null) {
            startTime = TimeSource.Monotonic.markNow()
        }

        if (receiveJob == null || receiveJob?.isCancelled == true) {
            receiveJob = viewModelScope.launch {
//                tcpClient.messageChannel = Channel<ByteArray>(Channel.UNLIMITED)
                receiveMessage()
            }
        }
    }

    fun stop() {
        receiveJob?.cancel()
        receiveJob = null
        isGeneratingData = false

//        tcpClient.messageChannel.close()
    }

    fun disconnection() {
        startTime = null
        tcpClient.disconnect()

        stop()
        _chartData.value = startData
    }

    private suspend fun sendMessage(message: String) {
        val messageByte = command(message)
        tcpClient.sendMessageChannel.send(messageByte)
    }

    private fun command(msg: String): ByteArray {
        val hexStr = HexUtil.convertStringToHexString(msg)
        val lengthHexStr = HexUtil.lengthHexStr(hexStr)
        val msgStr = "AAFF55" + lengthHexStr + hexStr + "FFFFBBBB"
        Log.i("TcpViewModel", "msg: $msg; command: $msgStr")
        return HexUtil.hexStringToByteArray(msgStr)
    }

    private suspend fun receiveMessage() {
        try {
            for (message in tcpClient.messageChannel) {
                //AAFF55 0C 000020C1 00005041 00009040 BBBB
                val data = tcpClient.parser.parsePacket(message)
                val t = startTime?.elapsedNow()!!.inWholeMilliseconds / 1000f
                if (!data.isNullOrEmpty()) {
                    _chartData.value = ChartData(
                        x = t.toInt(),
                        y = data[0],
                        y2 = data[1],
                        y3 = data[2]
                    )
                }
                Log.i("TcpViewModel", "data:${data}")
            }
        } catch (e: Exception) {
            Log.e("TcpViewModel","Send data loop error: ${e.message}")
        }
    }

}