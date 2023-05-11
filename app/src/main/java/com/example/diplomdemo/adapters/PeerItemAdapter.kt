package com.example.diplomdemo.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomdemo.items.PeerItem
import com.example.diplomdemo.R
import com.example.diplomdemo.contacts.ContactStore
import nl.tudelft.ipv8.Peer

class PeerItemAdapter(private val peerItemList: List<PeerItem>) : RecyclerView.Adapter<PeerItemAdapter.PeerItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeerItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.peer_item,
            parent, false)
        return PeerItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PeerItemViewHolder, position: Int) {
        val currentItem = peerItemList[position]
        holder.textView.text = currentItem.peerName
        holder.peer = currentItem.peer
    }

    override fun getItemCount() = peerItemList.size

    class PeerItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.peerNameTV)
        val addBtn: ImageButton = itemView.findViewById(R.id.peerAddBtn)
        lateinit var peer: Peer

        init {
            addBtn.setOnClickListener {

                val publicKey = peer.publicKey
                //Todo: сделать добавление контактов двусторонним, т.е. должен будет приходить
                // запрос на добавление, а ты должен на него отвечать да или нет

                ContactStore.getInstance(textView.context).addContact(publicKey, peer.mid)
                Log.d("Demo.addContact", "Button pressed")
            }
        }



    }
}