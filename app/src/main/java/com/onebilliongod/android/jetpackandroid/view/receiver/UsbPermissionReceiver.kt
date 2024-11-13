package com.onebilliongod.android.jetpackandroid.view.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.onebilliongod.android.jetpackandroid.data.socket.client.SerialClient
import com.onebilliongod.android.jetpackandroid.di.UsbPermissionReceiverEntryPoint
import dagger.hilt.android.EntryPointAccessors

const val ACTION_USB_PERMISSION = "com.onebilliongod.android.jetpackandroid.USB_PERMISSION"

class UsbPermissionReceiver : BroadcastReceiver() {

    private lateinit var serialClient: SerialClient

    override fun onReceive(context: Context, intent: Intent) {
        Log.i("UsbPermissionReceiver", "onReceive-UsbPermissionReceiver:${intent.action}")
        if (ACTION_USB_PERMISSION == intent.action) {
            synchronized(this) {
                //Manually obtain the SerialClient instance through EntryPoint
                serialClient = EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    UsbPermissionReceiverEntryPoint::class.java
                ).serialClient()

                val permission = serialClient.checkPermission()
                Log.i("UsbPermissionReceiver", "permission:${permission}")
                if (permission) {
                    Log.i("UsbPermissionReceiver", "it had permission then connect")
                    serialClient.connect()
                } else {
                    Log.w("UsbPermissionReceiver", "permission denied for device")
                }
            }
        }
    }
}