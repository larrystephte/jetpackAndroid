package com.onebilliongod.android.jetpackandroid.view.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import com.onebilliongod.android.jetpackandroid.data.socket.client.SerialClient
import com.onebilliongod.android.jetpackandroid.di.UsbPermissionReceiverEntryPoint
import dagger.hilt.android.EntryPointAccessors

class UsbDeviceReceiver : BroadcastReceiver() {
    lateinit var serialClient: SerialClient

    override fun onReceive(context: Context, intent: Intent) {
        //Manually obtain the SerialClient instance through EntryPoint
        serialClient = EntryPointAccessors.fromApplication(
            context.applicationContext,
            UsbPermissionReceiverEntryPoint::class.java
        ).serialClient()

        when (intent.action) {
            UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                // 处理 USB 设备连接事件
                val usbDevice = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                Log.i("UsbDeviceReceiver", "USB device attached: $usbDevice")
                // 你可以在这里进行设备初始化、权限请求等操作
                serialClient.probeAndCheckPermission()
            }
            UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                // 处理 USB 设备断开事件
                val usbDevice = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                Log.i("UsbDeviceReceiver", "USB device detached: $usbDevice")
                // 这里可以进行清理操作，如关闭连接
                serialClient.close()
            }
        }
    }
}