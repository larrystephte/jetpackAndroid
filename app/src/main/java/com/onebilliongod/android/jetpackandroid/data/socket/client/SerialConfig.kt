package com.onebilliongod.android.jetpackandroid.data.socket.client

data class SerialConfig(
    val vendorId: Int,
    val productId: Int,
    val baudRate: Int,
    val dataBits: Int,
    val stopBits: Int,
    val parity: Int
)