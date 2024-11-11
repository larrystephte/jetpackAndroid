package com.onebilliongod.android.jetpackandroid.di

import android.content.Context
import android.hardware.usb.UsbManager
import android.util.Log
import com.hoho.android.usbserial.driver.Ch34xSerialDriver
import com.hoho.android.usbserial.driver.ProbeTable
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.onebilliongod.android.jetpackandroid.data.socket.client.SerialClient
import com.onebilliongod.android.jetpackandroid.data.socket.client.SerialConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SerialModule {
    private val config = SerialConfig(
        vendorId = 0x6790,
        productId = 0x29987,
        baudRate = 9600,
        dataBits = 8,
        stopBits = UsbSerialPort.STOPBITS_1,
        parity = UsbSerialPort.PARITY_NONE
    )

    @Provides
    @Singleton
    fun provideSerialClient(@ApplicationContext context: Context): SerialClient {
        Log.i("SerialModule", "start init serial client")
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        Log.i("SerialModule", "find usb device list:${usbManager.deviceList}")
        val customTable = ProbeTable()
        customTable.addProduct(config.vendorId, config.productId, Ch34xSerialDriver::class.java)
//        customTable.addProduct(0x1234, 0x0002, FtdiSerialDriver::class.java)

        val prober = UsbSerialProber(customTable)
        val availableDrivers = prober.findAllDrivers(usbManager)

        Log.i("SerialModule", "find Ch34xSerialDriver usb device list:${availableDrivers}")
        return if (availableDrivers.isNotEmpty()) {
            val driver = availableDrivers[0]
            val connection = usbManager.openDevice(driver.device)
            if (connection == null) {
                Log.w("SerialModule", "USB permission required")
                SerialClient(null, null, config)
            } else {

                val serialPort = driver.ports[0]
                val serialClient = SerialClient(serialPort, connection, config)
                //auto connect
                serialClient.connect()
                Log.i("SerialModule", "Ch34xSerial device was connected")
                return serialClient
            }
        } else {
            Log.w("SerialModule", "No matching USB serial device found")
            SerialClient(null, null, config)
        }
    }
}