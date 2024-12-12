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
     * sample: AAFF5588554EF0406504D440C4D36840FCA467407F91CD400000000000000000BEDA1241CA54524036D7E93F4102203E1860094100000000000000009F2CE13F656AC83F6201953F3633C63FC4184D3F0000000000000000AF2D973F833FEB3ED37C203E96229D3D7C7F843E0000000000000000000000000000000000000000000000000000BBBB
     */
    override fun parseData(data: ByteArray): List<Float> {
        var offset = 0

        //check the data size
        if (data.size < 108 + 4 + (3 * 4) + 2) {
            throw IllegalArgumentException("data length is not enough")
        }

        val floats = mutableListOf<Float>()

        // 1. The first 108 bytes are parsed into one decimal number every 4 bytes, resulting in a total of 27 numbers.
        for (i in 0 until 28) {
            val floatValue = parseFloatData(offset, data)
            floats.add(floatValue)
            offset += 4
        }

        // 2. The next 4 bytes  are pared into one number per byte, resulting in a total of 4 numbers.
        for (i in 0 until 4) {
            val number = data[offset].toInt() and 0xFF
            floats.add(number.toFloat())
            offset += 1
        }

        // 3. Then there are 3 numbers, each 4 bytes in size.
        for (i in 0 until 3) {
            val floatValue = parseFloatData(offset, data)
            floats.add(floatValue)
            offset += 4
        }

        // 4.The end are 2 numbers, each 1 bytes in size.
        for (i in 0 until 2) {
            val number = data[offset].toInt() and 0xFF
            floats.add(number.toFloat())
            offset += 1
        }
        return floats
    }

    /**
     * Parses 4 bytes from a ByteArray and converts them into a Float value.
     *
     *  This function takes a 4-byte segment starting from the specified offset in the
     *   `data` ByteArray and interprets these bytes as a single 32-bit integer, which
     *   is then converted to a float using `Float.fromBits()`. The byte order is assumed
     *    to be little-endian (least significant byte first).
     */
    fun parseFloatData(offset: Int, data: ByteArray): Float {
        // Combine 4 bytes into a 32-bit integer (little-endian byte order)
        val number = (data[offset].toInt() and 0xFF) or
                ((data[offset + 1].toInt() and 0xFF) shl 8) or
                ((data[offset + 2].toInt() and 0xFF) shl 16) or
                ((data[offset + 3].toInt() and 0xFF) shl 24)

        // Convert the 32-bit integer to a Float using the bitwise representation
        val floatValue = Float.fromBits(number)
        return floatValue
    }
}