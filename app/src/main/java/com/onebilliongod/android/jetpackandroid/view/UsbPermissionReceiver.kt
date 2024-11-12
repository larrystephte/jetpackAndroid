package com.onebilliongod.android.jetpackandroid.view

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.onebilliongod.android.jetpackandroid.data.socket.client.SerialClient
import javax.inject.Inject



const val ACTION_USB_PERMISSION = "com.onebilliongod.android.jetpackandroid.USB_PERMISSION"

//val usbPermissionReceiver = object : BroadcastReceiver() {
//    private val TAG = "BroadcastReceiver"
//
//    override fun onReceive(context: Context, intent: Intent) {
//        Log.i(TAG, "onReceive-UsbPermissionReceiver:${intent.action}")
//        if (ACTION_USB_PERMISSION == intent.action) {
//            synchronized(this) {
//                val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
//                Log.i(TAG, "UsbDevice:${device}")
//                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
//                    device?.apply {
//                        // call method to set up device communication
//                    }
//                } else {
//                    Log.d(TAG, "permission denied for device $device")
//                }
//            }
//        }
//    }
//}
class UsbPermissionReceiver : BroadcastReceiver() {
//    @Inject
//    lateinit var serialClient: SerialClient

    override fun onReceive(context: Context, intent: Intent) {
        Log.i("UsbPermissionReceiver", "onReceive-UsbPermissionReceiver:${intent.action}")
        if (ACTION_USB_PERMISSION == intent.action) {
            synchronized(this) {

                val device :UsbDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                } else {
                    intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                }
                Log.i("UsbPermissionReceiver", "UsbDevice:${device}")
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    device?.let {
                        Log.i("UsbPermissionReceiver", "$device had permission")
//                        serialClient.scan()
                    }
                } else {
                    Log.w("UsbPermissionReceiver", "permission denied for device $device")
                }
            }
        }
    }
}