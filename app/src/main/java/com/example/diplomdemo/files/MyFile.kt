package com.example.diplomdemo.files

import nl.tudelft.ipv8.messaging.Deserializable
import nl.tudelft.ipv8.messaging.Serializable
import nl.tudelft.ipv8.messaging.deserializeVarLen
import nl.tudelft.ipv8.messaging.serializeVarLen


data class MyFile (
    val id: String, // hashCode
    val data: ByteArray
) : Serializable {
    override fun serialize(): ByteArray {
        return serializeVarLen(id.toByteArray()) +
                serializeVarLen(data)
    }

    companion object Deserializer : Deserializable<MyFile> {
        override fun deserialize(buffer: ByteArray, offset: Int): Pair<MyFile, Int> {
            var localOffset = offset
            val (id, idSize) = deserializeVarLen(buffer, localOffset)
            localOffset += idSize
            val (data, dataSize) = deserializeVarLen(buffer, localOffset)
            localOffset += dataSize
            return Pair(
                MyFile(
                    id.toString(Charsets.UTF_8),
                    data
                ),
                localOffset - offset
            )
        }
    }
}