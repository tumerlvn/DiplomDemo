package com.example.diplomdemo

import nl.tudelft.ipv8.messaging.Deserializable
import nl.tudelft.ipv8.messaging.Serializable
import nl.tudelft.ipv8.messaging.deserializeVarLen
import nl.tudelft.ipv8.messaging.serializeVarLen

class MyMessage(
    val id: String,
    val message: String
) : Serializable {
    override fun serialize(): ByteArray {
        return serializeVarLen(id.toByteArray()) +
                serializeVarLen(message.toByteArray())
    }

    companion object Deserializer : Deserializable<MyMessage> {
        override fun deserialize(buffer: ByteArray, offset: Int): Pair<MyMessage, Int> {
            var localOffset = offset
            val (id, idSize) = deserializeVarLen(buffer, localOffset)
            localOffset += idSize
            val (message, messageSize) = deserializeVarLen(buffer, localOffset)
            localOffset += messageSize

            return Pair(
                MyMessage(
                    id.toString(Charsets.UTF_8),
                    message.toString(Charsets.UTF_8)
                ),
                localOffset - offset
            )
        }
    }
}