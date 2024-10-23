package com.onebilliongod.android.jetpackandroid.data.socket.parser

/**
 * Specific parser for Float data packets.
 */
class FloatPacketParser : PacketParser<List<Float>>() {
    override val startSequence = byteArrayOf(0xAA.toByte(), 0xFF.toByte(), 0x55.toByte())
    override val endSequence = byteArrayOf(0xBB.toByte(), 0xBB.toByte(), 0x0D.toByte(), 0x0A.toByte())

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
            //  Calculate the number of float data entries based on the data length.
            val dataBytes = data.copyOfRange(i * 4, (i + 1) * 4)

            // Reverse the byte order to handle little-endian format.
            dataBytes.reverse()

            // Create a ByteBuffer and set it to little-endian order.
            val buffer = java.nio.ByteBuffer.wrap(dataBytes).order(java.nio.ByteOrder.LITTLE_ENDIAN)

            floats.add(buffer.float)
        }
        return floats
    }
}