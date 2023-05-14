package com.example.diplomdemo.messages

import nl.tudelft.ipv8.keyvault.PublicKey
import java.util.*

data class DbMessage(
    /**
     * The unique message ID.
     */
    val id: String,

    /**
     * The message content.
     */
    val message: String,

    /**
     * The public key of the message sender.
     */
    val sender: PublicKey,

    /**
     * The public key of the message recipient.
     */
    val recipient: PublicKey,

    /**
     * True if we are the sender, false otherwise.
     */
    val outgoing: Boolean,

    /**
     * The timestamp when the message was sent/received.
     */
    val timestamp: Date,

)