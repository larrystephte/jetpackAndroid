package com.techtrend.intelligent.chunli_clr.view.home.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.ConnectedDatagramSocket
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

//Just Test UDP
@HiltViewModel
class UdpViewModel @Inject constructor(): ViewModel() {
    var data by mutableStateOf("Waiting for data...")
        private set

    private val selectorManager = ActorSelectorManager(Dispatchers.IO)
    private var socket: ConnectedDatagramSocket? = null

    fun connectToServer(host: String, port: Int) {
        println("connectToServer:${host}, ${port}")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                socket = aSocket(selectorManager).udp().connect(InetSocketAddress(host, port))
                println("connect sucess")
                sendMessage("init")
                receiveMessage()
            } catch (e: Exception) {
                e.printStackTrace()  // Logging the error for debugging
                data = "Connection failed: ${e.message}"
            }

        }
    }

    suspend fun receiveMessage() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                while (true) {
                    println("Start Received")
                    val datagram = socket?.receive() ?: break
                    println("Start Received TWO")
                    val message = datagram.packet.readBytes().decodeToString()
                    println("Received: $message from ${datagram.address}")
                    withContext(Dispatchers.Main) {
                        data = message
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    data = "Error receiving message: ${e.message}"
                }
            }
        }

    }

    private suspend fun sendMessage(message: String) {
        try {
            val sendData = message.toByteArray(Charset.defaultCharset())
            val datagram = Datagram(ByteReadPacket(sendData), socket?.remoteAddress ?: return)
            socket?.send(datagram)
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                data = "Error sending message: ${e.message}"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up the socket when ViewModel is destroyed
        socket?.close()
        selectorManager.close()
    }
}