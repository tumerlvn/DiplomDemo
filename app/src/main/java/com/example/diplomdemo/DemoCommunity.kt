package com.example.diplomdemo

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.diplomdemo.contacts.Contact
import com.example.diplomdemo.files.MyFile
import kotlinx.coroutines.withContext
import nl.tudelft.ipv8.Community
import nl.tudelft.ipv8.Overlay
import nl.tudelft.ipv8.Peer
import nl.tudelft.ipv8.messaging.Packet
import nl.tudelft.ipv8.util.toHex
import java.io.File
import java.io.FileOutputStream

class DemoCommunity(
    private val context: Context
) : Community() {
    override val serviceId = "02313685c1912a141279f8248fc8db5899c5df5a"

    private val availablePeers = HashSet<Peer>()

    fun getListOfAvailablePeers() : List<Peer> {
        val peers = ArrayList<Peer>()
        availablePeers.forEach{
            peers.add(it)
        }
        return peers
    }

    fun broadcastGreeting() {
        for (peer in getPeers()) {
//            Log.d("DemoCommunity", peer.toString())
            val packet = serializePacket(Companion.GREETING_ID, MyMessage("Hello!"))
            send(peer.address, packet)
            Log.d("Demo.broadcast", peer.mid)
        }
    }



    fun sendMessageToPeer(peer: Peer, str: String) {
        val packet = serializePacket(Companion.MESSAGE_ID, MyMessage(str))
        send(peer.address, packet)
    }

    fun sendMessageToContact(contact: Contact, str: String) {
        val mid = contact.publicKey.keyToHash().toHex()
        val peer = getPeers().find { it.mid == mid }

        if (peer != null) {
            val payload = MyMessage(str)

            val packet = serializePacket(
                Companion.MESSAGE_ID,
                payload
            )

            send(peer, packet)
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
        val (peer, payload) = packet.getAuthPayload(MyMessage.Deserializer)
        Log.d("Demo.onMessage", peer.mid + ": " + payload.message)
        Toast.makeText(context, payload.message, Toast.LENGTH_SHORT).show()
    }

    private fun onGreeting(packet: Packet) {
        val (peer, payload) = packet.getAuthPayload(MyMessage.Deserializer)
        val answer = serializePacket(Companion.GREETING_ANSWER_ID, MyMessage("Greeting answered!"))
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
        onFile(payload)
    }

    private fun onFile(payload: MyFile) {
        val path = "" + context.filesDir + "/" + payload.id
        Log.d("Demo.onFile", context.filesDir.toString())
        val file = File(path)
        if (!file.exists()) {
            val os = FileOutputStream(file)
            os.write(payload.data)
        }
    }

    companion object {
        private const val GREETING_ID = 0
        private const val MESSAGE_ID = 1
        private const val GREETING_ANSWER_ID = 2
        private const val FILE_ID = 3
    }

    class Factory(
        private val context: Context
    ) : Overlay.Factory<DemoCommunity>(DemoCommunity::class.java) {
        override fun create(): DemoCommunity {
            return DemoCommunity(context)
        }
    }
}