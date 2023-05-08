package com.example.diplomdemo

import android.util.Log
import nl.tudelft.ipv8.Community
import nl.tudelft.ipv8.Peer
import nl.tudelft.ipv8.messaging.Packet

class DemoCommunity : Community() {
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

    init {
        messageHandlers[GREETING_ID] = ::onGreeting
        messageHandlers[MESSAGE_ID] = ::onMessage
        messageHandlers[GREETING_ANSWER_ID] = ::onGreetingAnswered
    }

    private fun onMessage(packet: Packet) {
        val (peer, payload) = packet.getAuthPayload(MyMessage.Deserializer)
        Log.d("DemoCommunity", peer.mid + ": " + payload.message)
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

    companion object {
        private const val GREETING_ID = 0
        private const val MESSAGE_ID = 1
        private const val GREETING_ANSWER_ID = 2
    }
}