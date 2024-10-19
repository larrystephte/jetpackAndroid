package com.techtrend.intelligent.chunli_clr.view.home.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import javax.inject.Inject

//Just Test TCP
@HiltViewModel
class TcpViewModel @Inject constructor(): ViewModel() {
    var data by mutableStateOf("Waiting for data...")
        private set

    private val selectorManager = ActorSelectorManager(Dispatchers.IO)

    fun connectToServer(host: String, port: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val socket = aSocket(selectorManager).tcp().connect(InetSocketAddress(host, port))
            socket.use {
                try {
                    val input = socket.openReadChannel()
                    val output = socket.openWriteChannel(autoFlush = true)

                    sendMessage(output, "Hello, Server")

                    receiveMessage(input)
                } catch (e: Exception) {
                    data = "Error: ${e.message}"
                }
            }
        }
    }

    private suspend fun sendMessage(output: ByteWriteChannel, message: String) {
        withContext(Dispatchers.IO) {
            output.writeFully(message.toByteArray(Charset.defaultCharset()))
        }
    }

    private suspend fun receiveMessage(input: ByteReadChannel) {
        val buffer = ByteBuffer.allocate(1024)
        while (true) {
            val bytesRead = input.readAvailable(buffer)
            if (bytesRead > 0) {
                buffer.flip()
                val message = Charset.defaultCharset().decode(buffer).toString()
                data = message
                buffer.clear()
            }
        }
    }

}