package com.techtrend.intelligent.chunli_clr.view.home.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onebilliongod.android.jetpackandroid.data.socket.client.TcpClient
import com.onebilliongod.android.jetpackandroid.utils.HexUtil
import com.onebilliongod.android.jetpackandroid.view.home.ChartData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.writeFully
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import javax.inject.Inject
import kotlin.random.Random
import kotlin.time.TimeMark
import kotlin.time.TimeSource

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
                receiveMessage()
            }
        }
    }

    fun stop() {
        receiveJob?.cancel()
        receiveJob = null
        isGeneratingData = false
    }

    private suspend fun sendMessage(message: String) {
        Log.i("TcpViewModel", "Test message:${message}")
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
                //AAFF55 08 DDCCBBAA HHGGFFEE BBBB0D0A
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