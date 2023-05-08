package com.example.diplomdemo.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomdemo.DemoCommunity
import com.example.diplomdemo.R
import com.example.diplomdemo.adapters.PeerItemAdapter
import com.example.diplomdemo.items.PeerItem
import nl.tudelft.ipv8.android.IPv8Android

class ListOfAvailablePeers : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_available_peers)

        val community = IPv8Android.getInstance().getOverlay<DemoCommunity>()!!
        var peers = community.getListOfAvailablePeers()
        val peerItemList = ArrayList<PeerItem>()
        peers.forEach{
            peerItemList.add(PeerItem(it.mid, it))
        }

        val recyclerView = findViewById<RecyclerView>(R.id.peers_recycler_view)
        val adapter = PeerItemAdapter(peerItemList)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val button = findViewById<Button>(R.id.btnUpdateListOfPeers)
        button.setOnClickListener{
            Log.d("Demo.button", "clicked")
            community.broadcastGreeting()
            peers = community.getListOfAvailablePeers()
            peerItemList.clear()
            peers.forEach{
                peerItemList.add(PeerItem(it.mid, it))
            }
            adapter.notifyDataSetChanged()
        }
    }
}