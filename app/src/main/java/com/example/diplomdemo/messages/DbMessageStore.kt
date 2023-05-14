package com.example.diplomdemo.messages

import android.content.Context
import android.graphics.BitmapFactory
import android.os.IBinder.DeathRecipient
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import nl.tudelft.ipv8.keyvault.PublicKey
import nl.tudelft.ipv8.keyvault.defaultCryptoProvider
import nl.tudelft.ipv8.util.hexToBytes
import com.example.diplomdemo.sqldelight.Database
import com.example.diplomdemo.contacts.Contact
import com.example.diplomdemo.contacts.ContactStore
import com.example.diplomdemo.messages.DbMessage
import java.util.*

class DbMessageStore(context: Context) {
    private val driver = AndroidSqliteDriver(Database.Schema, context, "dbmessage.db")
    private val database = Database(driver)
    val contactsStore = ContactStore.getInstance(context)

    private val messageMapper =
        { id: String, message: String, senderPk: ByteArray, receipientPk: ByteArray, outgoing: Long, timestamp: Long ->
            DbMessage(
                id,
                message,
                defaultCryptoProvider.keyFromPublicBin(senderPk),
                defaultCryptoProvider.keyFromPublicBin(receipientPk),
                outgoing == 1L,
                Date(timestamp)
            )
        }

    fun getAllMessages(): Flow<List<DbMessage>> {
        return database.dbMessageQueries.getAll(messageMapper).asFlow().mapToList()
    }

    fun getMessageById(id: String): DbMessage? {
        return database.dbMessageQueries.getMessageById(id, messageMapper).executeAsOneOrNull()
    }


//    /**
//     * Get the list of last messages by public key, filtered if archived, blocked or not
//     */
//    fun getLastMessages(
//        isRecent: Boolean,
//        isArchive: Boolean,
//        isBlocked: Boolean
//    ): Flow<List<DbMessage>> {
//        return combine(
//            contactsStore.getContacts(), getAllMessages(), getAllContactState()
//        ) { _, messages, state ->
//            messages.asSequence().sortedByDescending {
//                it.timestamp.time
//            }.distinctBy {
//                if (it.outgoing) it.recipient else it.sender
//            }.filter { message ->
//                state.filter {
//                    (it.publicKey == if (message.outgoing) message.recipient else message.sender)
//                }.let { stateOfContact ->
//                    when {
//                        isRecent -> stateOfContact.isEmpty() || stateOfContact.any { !it.isArchived && !it.isBlocked }
//                        isArchive -> stateOfContact.any { it.isArchived && !it.isBlocked }
//                        isBlocked -> stateOfContact.any { it.isBlocked }
//                        else -> false
//                    }
//                }
//            }.toList()
//        }
//    }

    fun getContactsWithLastMessages(): Flow<List<Pair<Contact, DbMessage?>>> {
        return combine(contactsStore.getContacts(), getAllMessages()) { contacts, messages ->
            val notContacts =
                messages.asSequence().filter { !it.outgoing }.map { it.sender }.distinct()
                    .filter { publicKey -> contacts.find { it.publicKey == publicKey } == null }
                    .map { Contact("", it) }.toList()

            (contacts + notContacts).map { contact ->
                val lastMessage = messages.findLast {
                    it.recipient == contact.publicKey || it.sender == contact.publicKey
                }
                Pair(contact, lastMessage)
            }
        }
    }

    fun addMessage(message: DbMessage) {
        database.dbMessageQueries.addMessage(
            message.id,
            message.message,
            message.sender.keyToBin(),
            message.recipient.keyToBin(),
            if (message.outgoing) 1L else 0L,
            message.timestamp.time
        )
    }

    fun getAllByPublicKey(publicKey: PublicKey): Flow<List<DbMessage>> {
        val publicKeyBin = publicKey.keyToBin()
        return database.dbMessageQueries.getAllByPublicKey(
            publicKeyBin, publicKeyBin, messageMapper
        ).asFlow().mapToList()
    }

    fun getAllSentByPublicKeyToMe(publicKey: PublicKey): List<DbMessage> {
        return database.dbMessageQueries.getAllByPublicKey(
            publicKey.keyToBin(), publicKey.keyToBin(), messageMapper
        ).executeAsList()
    }

    fun getAllOutgoingByPublicKey(publicKey: PublicKey): List<DbMessage> {
        return database.dbMessageQueries.getAllOutgoingByPublicKey(
            publicKey.keyToBin(), messageMapper
        ).executeAsList()
    }

    fun getAllIncomingByPublicKey(publicKey: PublicKey): List<DbMessage> {
        return database.dbMessageQueries.getAllIncomingByPublicKey(
            publicKey.keyToBin(), messageMapper
        ).executeAsList()
    }

    fun getAllMessagesFromTo(senderKey: PublicKey, recipientKey: PublicKey): List<DbMessage> {
        return database.dbMessageQueries.getAllMessagesFromTo(
            senderKey.keyToBin(), recipientKey.keyToBin(), messageMapper
        ).executeAsList()
    }

    fun deleteMessagesOfPublicKey(publicKey: PublicKey) {
        database.dbMessageQueries.deleteMessagesOfPublicKey(
            publicKey.keyToBin(), publicKey.keyToBin()
        )
    }


    companion object {
        private lateinit var instance: DbMessageStore
        fun getInstance(context: Context): DbMessageStore {
            if (!::instance.isInitialized) {
                instance = DbMessageStore(context)
            }
            return instance
        }
    }
}