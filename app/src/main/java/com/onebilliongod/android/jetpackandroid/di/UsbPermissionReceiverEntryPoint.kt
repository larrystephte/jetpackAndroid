package com.onebilliongod.android.jetpackandroid.di

import com.onebilliongod.android.jetpackandroid.data.socket.client.SerialClient
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface UsbPermissionReceiverEntryPoint {
    fun serialClient(): SerialClient
}