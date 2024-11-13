package com.onebilliongod.android.jetpackandroid.data.socket.client

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.util.Log
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import com.hoho.android.usbserial.driver.Ch34xSerialDriver
import com.hoho.android.usbserial.driver.ProbeTable
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import com.onebilliongod.android.jetpackandroid.view.receiver.ACTION_USB_PERMISSION
import com.onebilliongod.android.jetpackandroid.view.receiver.UsbPermissionReceiver
import java.io.IOException
import java.util.concurrent.Executors

data class SerialConfig(
    val vendorId: Int,
    val productId: Int,
    val baudRate: Int,
    val dataBits: Int,
    val stopBits: Int,
    val parity: Int
)

class SerialClient(private val context: Context,
                   private val config: SerialConfig) {
    var isConnected = false
        private set

    private lateinit var usbManager: UsbManager
    private var serialPort: UsbSerialPort? = null
    private var connection: UsbDeviceConnection? = null
    var usbDevice: UsbDevice? =null

    private var usbIoManager: SerialInputOutputManager? = null
    private val executorService = Executors.newSingleThreadExecutor()


    fun probe() {
        Log.i("SerialClient", "probe serial device")
        usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        Log.i("SerialClient", "find usb device list:${usbManager.deviceList}")
        val customTable = ProbeTable()
        customTable.addProduct(config.vendorId, config.productId, Ch34xSerialDriver::class.java)
        val prober = UsbSerialProber(customTable)
        val availableDrivers = prober.findAllDrivers(usbManager)

        Log.i("SerialClient", "find Ch34xSerialDriver usb device list:${availableDrivers}")
        if (availableDrivers.isNotEmpty()) {
            val driver = availableDrivers[0]
            serialPort = driver.ports[0]
            usbDevice = driver.device
        } else {
            Log.w("SerialClient", "No matching USB serial device found")
        }
    }

    fun requestPermission() {
        val permissionIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, UsbPermissionReceiver::class.java).apply {
                action = ACTION_USB_PERMISSION
            },
            PendingIntent.FLAG_IMMUTABLE
        )
        usbManager.requestPermission(usbDevice, permissionIntent)
    }

    fun checkPermission(): Boolean {
        val isPermissionGranted = usbManager.hasPermission(usbDevice)
        Log.w("SerialClient", "USB permission : $isPermissionGranted")
        if (!isPermissionGranted) {
            Log.w("SerialClient", "USB permission required")
        }
        return isPermissionGranted
    }

    fun probeAndCheckPermission() {
        probe()
        if (usbDevice != null) {
            val permission = checkPermission()
            if (!permission) {
                requestPermission()
                //connect after permission
//                connect()
            }
        }
    }

     fun connect() {
         if (isConnected) {
             Log.i("SerialClient", "device had connected")
             return
         }

         val permission = checkPermission()
         if (!permission) {
             requestPermission()
             return
         }
        try {
            //open device if connection is null
            if (connection == null) {
                connection = usbManager.openDevice(usbDevice)
            }

            if (connection != null) {
                serialPort?.open(connection)
                serialPort?.setParameters(
                    config.baudRate,
                    config.dataBits,
                    config.stopBits,
                    config.parity
                )
                isConnected = true
                Log.i("SerialClient", "device is connected")
            } else {
                Log.e("SerialClient", "connection is null")
            }
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

    fun startReading(listener: (ByteArray) -> Unit) {
        if (!isConnected) {
            Log.e("SerialClient", "The serial port is not connected and data cannot be read.")
            return
        }

        usbIoManager = SerialInputOutputManager(serialPort, object : SerialInputOutputManager.Listener {
            override fun onNewData(data: ByteArray) {
                listener(data)
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

            executorService.shutdownNow()

            usbDevice = null
//            usbManager = null

            isConnected = false
        } catch (e: IOException) {
            Log.e("SerialClient", "Failed to close serial port: ${e.message}")
        }
    }
}