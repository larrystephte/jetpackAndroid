package com.onebilliongod.android.jetpackandroid.utils

import java.nio.ByteBuffer

object HexUtil {
    fun convertStringToHexString(msg: String): String {
        //convert a string to its hexadecimal representation
        return msg.toByteArray().joinToString("") { "%02X".format(it) }
    }

    fun hexStringToByteArray(hexString: String): ByteArray {
        // Convert a hexadecimal string to a byte array
        return hexString.chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }

    fun lengthHexStr(hexString: String): String {
        // Calculate the total length of the data packet, including header and footer
        val length = hexString.length / 2 + 8 // Each two characters represent one byte, add header and footer length
        return String.format("%02X", length).uppercase()
    }


    fun floatConvertHex(value: Float) {
        // Convert a float value to its hexadecimal representation
        val buffer = ByteBuffer.allocate(4)
        buffer.putFloat(value)
        val byteArray = buffer.array()

        val hexString = byteArray.joinToString(" ") { "%02X".format(it) }
        println("hexString:${hexString}") // Output: 41 2C CC CD
    }

    fun hexConvertFloat() {
        // Define a hexadecimal byte array (handle little-endian or big-endian as per original format)
        val dataBytes = byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x90.toByte(), 0x40.toByte())

        //Reverse the byte order to handle little-endian format
        dataBytes.reverse()

        // Wrap the data using ByteBuffer and set byte order to big-endian
        val buffer = ByteBuffer.wrap(dataBytes).order(java.nio.ByteOrder.BIG_ENDIAN)

        // Read the float value from the buffer
        val floatValue = buffer.float

        // Output the float value
        println("floatValue:${floatValue}") //
    }
}