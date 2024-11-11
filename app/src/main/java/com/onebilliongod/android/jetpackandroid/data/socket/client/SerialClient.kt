package com.onebilliongod.android.jetpackandroid.data.socket.client

import android.util.Log
import android.hardware.usb.UsbDeviceConnection
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.util.SerialInputOutputManager
import java.io.IOException
import java.util.concurrent.Executors


class SerialClient(private val serialPort: UsbSerialPort?,
                   private val connection: UsbDeviceConnection?,
                   private val config: SerialConfig) {
    var isConnected = false
        private set

    private var usbIoManager: SerialInputOutputManager? = null
    private val executorService = Executors.newSingleThreadExecutor()

    fun connect() {
        try {
            serialPort?.open(connection)
            serialPort?.setParameters(
                config.baudRate,
                config.dataBits,
                config.stopBits,
                config.parity
            )
            isConnected = true
        } catch (e: IOException) {
            Log.e("SerialClient", "connect fail: ${e.message}")
            isConnected = false
        }
    }
    fun sendData(data: String) {
        if (!isConnected) {
            Log.e("SerialClient", "The serial port is not connected and data cannot be sent")
            return
        }

        try {
            val dataBytes = data.toByteArray()
            serialPort?.write(dataBytes, 1000)
        } catch (e: IOException) {
            Log.e("SerialClient", "serial send data fail: ${e.message}")
        }
    }

    fun startReading(listener: (String) -> Unit) {
        if (!isConnected) {
            Log.e("SerialClient", "The serial port is not connected and data cannot be read.")
            return
        }

        usbIoManager = SerialInputOutputManager(serialPort, object : SerialInputOutputManager.Listener {
            override fun onNewData(data: ByteArray) {
                listener(String(data))
            }

            override fun onRunError(e: Exception) {
                Log.e("SerialClient", "serial read data fail: ${e.message}")
            }
        })

        executorService.submit(usbIoManager)
    }

    fun close() {
        try {
            //
            usbIoManager?.stop()
            usbIoManager = null

            serialPort?.close()
            connection?.close()

            //
            executorService.shutdownNow()

            isConnected = false
        } catch (e: IOException) {
            Log.e("SerialClient", "Failed to close serial port: ${e.message}")
        }
    }
}