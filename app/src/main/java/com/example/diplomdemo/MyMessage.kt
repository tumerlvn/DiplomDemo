package com.example.diplomdemo

import nl.tudelft.ipv8.messaging.Deserializable
import nl.tudelft.ipv8.messaging.Serializable

class MyMessage(val message: String) : Serializable {
    override fun serialize(): ByteArray {
        return message.toByteArray()
    }

    companion object Deserializer : Deserializable<MyMessage> {
        override fun deserialize(buffer: ByteArray, offset: Int): Pair<MyMessage, Int> {
            var tmp = buffer.toString(Charsets.UTF_8).substring(offset)
            return Pair(MyMessage(tmp), buffer.size)
        }
    }
}