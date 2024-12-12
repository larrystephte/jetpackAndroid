package com.onebilliongod.android.jetpackandroid.di

import android.content.Context
import com.hoho.android.usbserial.driver.UsbSerialPort
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
        vendorId = 6790,
        productId = 29987,
        baudRate = 115200,
        dataBits = 8,
        stopBits = UsbSerialPort.STOPBITS_1,
        parity = UsbSerialPort.PARITY_NONE
    )

    @Provides
    @Singleton
    fun provideSerialClient(@ApplicationContext context: Context): SerialClient {
        return SerialClient(context, config)
    }
}