package com.onebilliongod.android.jetpackandroid.view.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.util.Log
import com.onebilliongod.android.jetpackandroid.data.socket.client.SerialClient
import com.onebilliongod.android.jetpackandroid.di.UsbPermissionReceiverEntryPoint
import dagger.hilt.android.EntryPointAccessors

class UsbDeviceReceiver : BroadcastReceiver() {
    private lateinit var serialClient: SerialClient

    override fun onReceive(context: Context, intent: Intent) {
        Log.i("UsbDeviceReceiver", "onReceive-UsbDeviceReceiver:${intent.action}")
        //Manually obtain the SerialClient instance through EntryPoint
        serialClient = EntryPointAccessors.fromApplication(
            context.applicationContext,
            UsbPermissionReceiverEntryPoint::class.java
        ).serialClient()

        when (intent.action) {
            UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
//                val usbDevice = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                Log.i("UsbDeviceReceiver", "USB device attached")
                serialClient.probeAndCheckPermission()
            }
            UsbManager.ACTION_USB_DEVICE_DETACHED -> {
//                val usbDevice = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                Log.i("UsbDeviceReceiver", "USB device detached")
                serialClient.close()
            }
        }
    }
}