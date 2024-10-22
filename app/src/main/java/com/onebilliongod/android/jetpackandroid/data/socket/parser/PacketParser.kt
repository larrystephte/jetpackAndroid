package com.onebilliongod.android.jetpackandroid.data.socket.parser

/**
 * Abstract class for parsing packets.
 * This class provides a general framework for parsing packets that consist of a
 * start sequence, data, and an end sequence. Subclasses need to define how the data
 * portion of the packet is parsed by implementing the `parseData` method.
 */
abstract class PacketParser<T> {
    //The byte sequence indicating the start of the packet.
    abstract val startSequence: ByteArray

    //The byte sequence indicating the end of the packet.
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
    fun parsePacket(packet: ByteArray): T? {
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
            // If the start sequence is not found, return null.
            return null
        }

        //  Read the length of the data section.
        if (index >= packet.size) return null
        val length = packet[index].toInt() and 0xFF
        index++

        // Ensure that there is enough data for the length specified.
        val dataEndIndex = index + length
        if (dataEndIndex + endSequence.size > packet.size) return null

        // Extract the data section from the packet.
        val dataContent = packet.copyOfRange(index, dataEndIndex)
        index = dataEndIndex

        // Validate the end sequence to ensure packet integrity.
        val packetEnd = packet.copyOfRange(index, index + endSequence.size)
        if (!packetEnd.contentEquals(endSequence)) return null

        // Parse and return the data content using the abstract `parseData` method.
        return parseData(dataContent)
    }
}