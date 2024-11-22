package com.onebilliongod.android.jetpackandroid.data.socket.parser

import java.nio.ByteBuffer
import kotlin.math.roundToInt

/**
 * Specific parser for Float data packets.
 */
class FloatPacketParser : PacketParser<List<Float>>() {
    override val startSequence = byteArrayOf(0xAA.toByte(), 0xFF.toByte(), 0x55.toByte())
    override val endSequence = byteArrayOf(0xBB.toByte(), 0xBB.toByte())

    /**
     * Parses the data section of the packet into a list of Floats.
     *
     * This method extracts 4-byte float values from the data section of the packet.
     *  Each 4-byte section is interpreted as a little-endian floating-point number.
     */
    override fun parseData(data: ByteArray): List<Float> {
        val floats = mutableListOf<Float>()
        val numOfData = data.size / 4

        for (i in 0 until numOfData) {
            //parse Float Data
            val floatValue = parseFloatData(i, data)

            // Extract the float value and format it to 2 decimal places.
            floats.add(floatValue)
        }


        return floats
    }

    private fun parseFloatData(offset: Int, data: ByteArray): Float {
        val number = (data[offset].toInt() and 0xFF) or
                ((data[offset + 1].toInt() and 0xFF) shl 8) or
                ((data[offset + 2].toInt() and 0xFF) shl 16) or
                ((data[offset + 3].toInt() and 0xFF) shl 24)
        val floatValue = Float.fromBits(number)
        return floatValue
    }

}