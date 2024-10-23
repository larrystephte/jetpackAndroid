package com.techtrend.intelligent.chunli_clr.view.home.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onebilliongod.android.jetpackandroid.data.socket.client.TcpClient
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import javax.inject.Inject

//Just Test TCP
@HiltViewModel
class TcpViewModel @Inject constructor(private val tcpClient: TcpClient): ViewModel() {
    var data by mutableStateOf("Waiting for data...")
        private set

    init {
        connectToServer()
    }

    fun connectToServer() {
        tcpClient.start()

        viewModelScope.launch {
            receiveMessage()

//            delay(3000)
//            sendMessage("TEST")
        }

        viewModelScope.launch {
            Log.i("TcpViewModel", "start Test message")
            sendMessage("TEST")
        }

    }

    private suspend fun sendMessage(message: String) {
        Log.i("TcpViewModel", "Test message")
        tcpClient.sendMessageChannel.send(message.toByteArray())
    }

    private suspend fun receiveMessage() {
        try {
            for (message in tcpClient.messageChannel) {
                Log.i("TcpViewModel", "tcpClient-receive-message:${message.joinToString(separator = "") { byte ->
                    "%02X".format(byte)
                }}")
            }
        } catch (e: Exception) {
            Log.e("TcpViewModel","Send data loop error: ${e.message}")
        }
    }

}