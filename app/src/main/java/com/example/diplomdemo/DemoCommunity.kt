package com.example.diplomdemo

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import android.widget.Toast
import com.example.diplomdemo.contacts.Contact
import com.example.diplomdemo.files.MyFile
import com.example.diplomdemo.messages.DbMessage
import com.example.diplomdemo.messages.DbMessageStore
import kotlinx.coroutines.withContext
import nl.tudelft.ipv8.Community
import nl.tudelft.ipv8.Overlay
import nl.tudelft.ipv8.Peer
import nl.tudelft.ipv8.keyvault.PublicKey
import nl.tudelft.ipv8.messaging.Packet
import nl.tudelft.ipv8.util.toHex
import java.io.File
import java.io.FileOutputStream
import java.util.Date
import java.util.UUID

class DemoCommunity(
    private val dbMessageStore: DbMessageStore,
    private val context: Context
) : Community() {
    override val serviceId = "02313685c1912a141279f8248fc8db5899c5df5a"

    private val availablePeers = HashSet<Peer>()

    lateinit var file: File

    fun getListOfAvailablePeers() : List<Peer> {
        val peers = ArrayList<Peer>()
        availablePeers.forEach{
            peers.add(it)
        }
        return peers
    }

    fun broadcastGreeting() {
        for (peer in getPeers()) {
            val packet = serializePacket(Companion.GREETING_ID, MyMessage("0","Hello!"))
            send(peer.address, packet)
            Log.d("Demo.broadcast", peer.mid)
        }
    }

//    fun sendMessageToPeer(peer: Peer, str: String) {
//        val packet = serializePacket(Companion.MESSAGE_ID, MyMessage(str))
//        send(peer.address, packet)
//    }

    fun sendMessageToContact(contact: Contact, str: String) {
        val dbMessage = createOutgoingDbMessage(str, contact.publicKey)
        dbMessageStore.addMessage(dbMessage)

        sendMessageToContact(dbMessage)
    }
    fun sendMessageToContact(dbMessage: DbMessage) {
        val mid = dbMessage.recipient.keyToHash().toHex()
        val peer = getPeers().find { it.mid == mid }

        if (peer != null) {
            val payload = MyMessage(dbMessage.id, dbMessage.message)

            val packet = serializePacket(
                Companion.MESSAGE_ID,
                payload
            )

            send(peer, packet)
            //writeAsActivityOfUser("sendMessageToContact()", peer.mid)
        } else {
            Log.d("PeerChat", "Peer $mid not online")
        }
    }

    fun sendFileToContact(contact: Contact, file: File) {
        val mid = contact.publicKey.keyToHash().toHex()
        val peer = getPeers().find { it.mid == mid }

        if (peer != null) {
            // Todo: добавить в file функцию equals() для нормального хэша
            val payload = MyFile(Integer.toHexString(file.readBytes().hashCode()), file.readBytes());

            val packet = serializePacket(
                Companion.FILE_ID,
                payload
            )

            send(peer, packet)
            //writeAsActivityOfUser("sendFileToContact()", peer.mid)
        } else {
            Log.d("PeerChat", "Peer $mid not online")
        }
    }

    init {
        messageHandlers[GREETING_ID] = ::onGreeting
        messageHandlers[MESSAGE_ID] = ::onMessage
        messageHandlers[GREETING_ANSWER_ID] = ::onGreetingAnswered
        messageHandlers[FILE_ID] = ::onFilePayload
    }

    private fun onMessage(packet: Packet) {
        val ti = TimeInformer()
        val (peer, payload) = packet.getAuthPayload(MyMessage.Deserializer)
        Log.d("Demo.onMessage", peer.mid + ": " + payload.message + ". Time: " + ti.getTime().toString())

        val dbMessage = createIncomingDbMessage(peer, payload)

        try {
            dbMessageStore.addMessage(dbMessage)
        } catch (e: SQLiteConstraintException) {
            e.printStackTrace()
        }

        //Toast.makeText(context, payload.message, Toast.LENGTH_SHORT).show()
    }

    private fun onGreeting(packet: Packet) {
        val (peer, payload) = packet.getAuthPayload(MyMessage.Deserializer)
        val answer = serializePacket(Companion.GREETING_ANSWER_ID, MyMessage("1","Greeting answered!"))
        send(peer.address, answer)
        Log.d("Demo.onGreeting", peer.mid + ": " + payload.message)
    }

    private fun onGreetingAnswered(packet: Packet) {
        val (peer, payload) = packet.getAuthPayload(MyMessage.Deserializer)
        availablePeers.add(peer)
        Log.d("Demo.onAnswered", peer.mid + ": " + payload.message)
    }

    private fun onFilePayload(packet: Packet) {
        val (peer, payload) = packet.getAuthPayload(MyFile.Deserializer)
        Log.d("Demo.onFilePayload", peer.mid + ": " + payload.id)
        onFile(payload, peer.mid)
    }

    private fun onFile(payload: MyFile, contactId: String) {
        val path = "" + context.filesDir + "/contactId/" + payload.id
        Log.d("Demo.onFile", context.filesDir.toString())
        val file = File(path)
        if (!file.exists()) {
            val os = FileOutputStream(file)
            os.write(payload.data)
        }
    }

    fun writeAsActivityOfUser(action: String, text: String) {
        Thread {
            val timeStamp = TimeInformer().getTime().toString()
            // Todo: метка времени должна приходить вместе с подписью сервера времени
            //       в нашем случае эту роль будет выполнять абонент с которым общается
            //       наш человек
            file.appendText("$timeStamp - $action: $text\n")
        }.start()
    }

    private fun createOutgoingDbMessage(
        message: String,
        recipient: PublicKey
    ): DbMessage {
        val id = UUID.randomUUID().toString()
        return DbMessage(
            id,
            message,
            myPeer.publicKey,
            recipient,
            true,
            Date()
        )
    }

    private fun createIncomingDbMessage(peer: Peer, message: MyMessage): DbMessage {
        return DbMessage(
            message.id,
            message.message,
            peer.publicKey,
            myPeer.publicKey,
            false,
            Date()
        )
    }

    companion object {
        private const val GREETING_ID = 0
        private const val MESSAGE_ID = 1
        private const val GREETING_ANSWER_ID = 2
        private const val FILE_ID = 3
    }

    class Factory(
        private val dbMessageStore: DbMessageStore,
        private val context: Context
    ) : Overlay.Factory<DemoCommunity>(DemoCommunity::class.java) {
        override fun create(): DemoCommunity {
            val path = context.filesDir
            val letDirectory = File(path, "DemoDirectory")
            letDirectory.mkdirs()
            val demoCommunity = DemoCommunity(dbMessageStore, context)
            demoCommunity.file = File(letDirectory, "UserActivity.txt")
            return demoCommunity
        }
    }
}