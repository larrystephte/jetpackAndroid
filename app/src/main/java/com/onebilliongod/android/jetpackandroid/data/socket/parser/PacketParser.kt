package com.onebilliongod.android.jetpackandroid.data.socket.parser

/**
 * Abstract class for parsing packets.
 */
abstract class PacketParser<T> {
    abstract val startSequence: ByteArray
    abstract val endSequence: ByteArray

    // Abstract method to parse the data section of the packet.
    abstract fun parseData(data: ByteArray): T

    /**
     * Main method to parse a complete packet.
     *
     * This method extracts and validates the packet based on the defined start
     * and end sequences. It ensures that the packet has the correct structure
     * (i.e., start sequence, data, and end sequence) and invokes `parseData` to
     * process the data portion. If the packet format is invalid or incomplete,
     * it returns null.
     */
    fun  parsePacket(packet: ByteArray): T? {
        var index = 0

        //Search for the start sequence within the packet.
        while (index <= packet.size - startSequence.size) {
            if (packet.copyOfRange(index, index + startSequence.size).contentEquals(startSequence)) {
                index += startSequence.size
                break
            }
            index++
        }

        if (index > packet.size - startSequence.size) {
            return null
        }

        //  Read the length of the data section.
        if (index >= packet.size) return null
        val length = packet[index].toInt() and 0xFF
        index++

        if (length != packet.size) {
            return null
        }

        // Ensure that there is enough data for the length specified.
        val dataEndIndex = length - 2
        if (dataEndIndex + endSequence.size > packet.size) return null

        val dataContent = packet.copyOfRange(index, dataEndIndex)
        index = dataEndIndex

        val packetEnd = packet.copyOfRange(index, index + endSequence.size)
        if (!packetEnd.contentEquals(endSequence)) return null

//        Log.i("PacketParser", "dataContent: ${dataContent.size}")
        return parseData(dataContent)
    }
}