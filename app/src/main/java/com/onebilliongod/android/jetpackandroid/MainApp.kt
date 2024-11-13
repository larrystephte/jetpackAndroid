package com.onebilliongod.android.jetpackandroid

import android.app.Application
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Log
import com.onebilliongod.android.jetpackandroid.data.socket.client.SerialClient
import com.onebilliongod.android.jetpackandroid.view.receiver.ACTION_USB_PERMISSION
import com.onebilliongod.android.jetpackandroid.view.receiver.UsbDeviceReceiver
import com.onebilliongod.android.jetpackandroid.view.receiver.UsbPermissionReceiver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * @HiltAndroidApp Use Hilt to manager dependency injection
 */
@HiltAndroidApp
class MainApp : Application() {

    //immediate init serialModule
    @Inject
    lateinit var serialClient: SerialClient

    override fun onCreate() {
        super.onCreate()

        registerUsbDeviceReceiver()

        serialClient.probeAndCheckPermission()
    }

    private fun registerUsbDeviceReceiver() {
        Log.i("Application", "registerUsbDeviceReceiver:${Build.VERSION.SDK_INT}")
        val usbReceiver = UsbDeviceReceiver()

        val intentFilter = IntentFilter()
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)

        registerReceiver(usbReceiver, intentFilter)
    }

    private fun registerPermissionReceiver() {
        Log.i("Application", "start registerReceiver:${Build.VERSION.SDK_INT}")
        val usbPermissionReceiver = UsbPermissionReceiver()
        val filter = IntentFilter(ACTION_USB_PERMISSION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.registerReceiver(usbPermissionReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            this.registerReceiver(usbPermissionReceiver, filter)
        }
    }


}