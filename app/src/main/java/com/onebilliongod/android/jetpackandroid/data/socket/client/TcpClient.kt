package com.onebilliongod.android.jetpackandroid.data.socket.client

import android.util.Log
import com.onebilliongod.android.jetpackandroid.data.socket.parser.PacketParser
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.isClosed
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import io.ktor.utils.io.writeFully
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import java.nio.charset.Charset
import kotlin.coroutines.cancellation.CancellationException

/**
 * Managing tcp connect client.
 * PacketParser is a parser that parses data received from the device.
 */
class TcpClient(
    private val host: String,
    private val port: Int,
    val parser: PacketParser<List<Float>>
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val selectorManager = ActorSelectorManager(Dispatchers.IO)
    private var socket: Socket? = null
    private var input: ByteReadChannel? = null
    private var output: ByteWriteChannel? = null

    //use this channel to send data to the business layer
    val messageChannel = Channel<ByteArray>(Channel.UNLIMITED)
    //use this sendMessageChannel to  receive data from the business layer
    val sendMessageChannel = Channel<ByteArray>(Channel.UNLIMITED)

    private val MAX_RETRY_COUNT = 3
    private val RETRY_DELAY_MS = 100L

    private var receiveJob: Job? = null

    /**
     * Establish a TCP connection
     */
    fun connect() {
        if (socket == null || socket?.isClosed == true) {
            scope.launch {
                try {
                    socket = aSocket(selectorManager).tcp().connect(InetSocketAddress(host, port))
                    input = socket?.openReadChannel()
                    output = socket?.openWriteChannel(autoFlush = true)

                    input?.let { launch { receiveData(it) } }

                    output?.let { launch { sendData(it) } }

                    Log.i("TcpClient", "Connected to server $host:$port")
                } catch (e: Exception) {
                    Log.e("TcpClient", "Connection error: ${e.message}")
                    reconnect()
                }
            }
        }

    }

    //start receive job
//    fun startReceiving() {
//        if (receiveJob == null || receiveJob?.isCancelled == true) {
//            receiveJob = scope.launch {
//                input?.let { launch { receiveData(it) } }
//            }
//        }
//    }

    //stop receive
//    fun stopReceiving() {
//        receiveJob?.cancel()
//        receiveJob = null
//    }

    //Verify the connection validity
    private fun isConnected(): Boolean {
        return socket != null && socket?.isClosed == false
    }

    /**
     * Validate the receive data to check if the TCP data contains any packet sticking or other issues.
     */
    private suspend fun receiveData(input: ByteReadChannel) {
        Log.i("TcpClient", "start receive data")
        //Allocate a sufficiently large buffer to store the bytes read from the socket and handler packet sticking and partial packets.
        val buffer = ByteBuffer.allocate(1024 * 10)
        try {
            while (scope.isActive) {
                //Suspend until data is available to read
                input.awaitContent()

                //check if the channel is close
                if (input.isClosedForRead && input.availableForRead == 0) {
                    println("Connection closed by server")
                    break
                }

                //Read data from the channel
                val bytesRead = input.readAvailable(buffer)

                if (bytesRead > 0) {
                    //switch to read
                    buffer.flip()

                    //parse data from buffer
                    parseBuffer(buffer)

                    buffer.compact()
                } else if (bytesRead == -1) {
                    Log.i("TcpClient", "Connection closed by server")
                    break
                }

                // delay(1) //
            }
        } catch (e: CancellationException) {
            println("Receiving was cancelled.")
        }  catch (e: Exception) {
            Log.e("TcpClient", "Receive error: ${e.message}")
        } finally {
           // messageChannel.close()
        }
    }

    /**
     * Parses the data from the provided ByteBuffer and handles packet extraction.
     * The packet format is START SEQUENCE + LENGTH + DATA + END SEQUENCE
     * @param buffer The ByteBuffer containing data to be parsed.
     */
    private fun parseBuffer(buffer: ByteBuffer) {
        Log.i("TcpClient", "parseBuffer")
        while (true) {
            buffer.mark() // Mark the current position in the buffer

            //check if there is enough data to read the start sequence and Length field.
            if (buffer.remaining() < parser.startSequence.size + 1) {
                Log.i("TcpClient", "${buffer.remaining()} < ${parser.startSequence.size} + 1")
                buffer.reset()
                break
            }

            //Look for the start sequence.
            val startPosition = buffer.position()
            val startSequenceFound = findSequence(buffer, parser.startSequence)
            if (!startSequenceFound) {
                //If the start sequence is not found, skip one byte and continue searching.
                buffer.position(startPosition + 1)
                continue
            }

            //Read the length field, ensuring there is enough data remaining.
            if (buffer.remaining() < 1) {
                Log.i("TcpClient", "buffer.remaining() < 1")
                buffer.reset()
                break
            }

            // Read the length field, convert to an unsigned byte.
            val length = buffer.get().toInt() and 0xFF

            //Check if there is enough data to read the full packet, including the end sequence.
            val totalPacketSize = parser.startSequence.size + 1 + length + parser.endSequence.size
            if (buffer.remaining() + parser.startSequence.size + 1 < totalPacketSize) {
                Log.i("TcpClient", "buffer.remaining() + parser.startSequence.size + 1 < totalPacketSize")
                buffer.reset()
                break
            }

            // Extract the full packet, including start sequence, length, data, and end sequence.
            val packet = ByteArray(totalPacketSize)
            buffer.reset()
            buffer.get(packet)

            //Send the extracted packet to the message channel for further processing.
            Log.i("TcpClient", "messageChannel.trySend:${packet}")
            messageChannel.trySend(packet)
        }
    }

    /**
     * Finds a specific byte sequence within the ByteBuffer.
     */
    private fun findSequence(buffer: ByteBuffer, sequence: ByteArray): Boolean {
        for (i in sequence.indices) {
            // Check if there are enough remaining bytes in the buffer
            if (!buffer.hasRemaining()) {
                buffer.position(buffer.position() - i)
                return false
            }
            // If the current byte does not match the sequence, reset the buffer's position
            if (buffer.get() != sequence[i]) {
                buffer.position(buffer.position() - i - 1)
                return false
            }
        }
        return true
    }

    /**
     * Continuously sends data from the sendMessageChannel to the output channel.
     */
    private suspend fun sendData(output: ByteWriteChannel) {
        Log.i("TcpClient", "sendData")
        try {
            //// Continuously retrieve messages from sendMessageChannel and send them
            for (message in sendMessageChannel) {
                Log.i("TcpClient", "sendData-get-message,${message}")
                try {
                    // Write the message to the output channel
                    output.writeFully(message)
                    output.flush() // Ensure data is sent immediately
                    Log.i("TcpClient", "Data sent: ${message.contentToString()}")
                } catch (e: Exception) {
                    Log.e("TcpClient", "Error sending data: ${e.message}")
                    break
                }
            }
        } catch (e: Exception) {
            Log.e("TcpClient", "Send data loop error: ${e.message}")
        }
    }


    //dis connect
    fun disconnect() {
        scope.launch {
            try {
                socket?.close()
                Log.i("TcpClient", "Connection closed")
            } catch (e: Exception) {
                Log.e("TcpClient","Error closing connection: ${e.message}")
            }
        }
    }

    /**
     * Attempts to reconnect to the server with a retry mechanism.
     */
    private fun reconnect() {
        scope.launch {
            var retryCount = 0
            while (retryCount < MAX_RETRY_COUNT) {
                try {
                    Log.i("TcpClient", "Reconnecting... Attempt ${retryCount + 1}")
                    connect()  // Attempt to re-establish the connection
                    if (isConnected()) {
                        Log.i("TcpClient", "Reconnected successfully.")
                        break
                    }
                } catch (e: Exception) {
                    Log.e("TcpClient", "Reconnect failed: ${e.message}")
                }
                retryCount++
                delay(RETRY_DELAY_MS)
            }
        }
    }
}