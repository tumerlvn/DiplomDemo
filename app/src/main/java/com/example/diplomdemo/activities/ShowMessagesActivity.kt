package com.example.diplomdemo.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomdemo.DemoCommunity
import com.example.diplomdemo.R
import com.example.diplomdemo.adapters.DbMessageAdapter
import com.example.diplomdemo.contacts.Contact
import com.example.diplomdemo.messages.DbMessageStore
import nl.tudelft.ipv8.android.IPv8Android

class ShowMessagesActivity : AppCompatActivity() {

    val dbMessageStore = DbMessageStore.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_outgoing_messages)

        val currentJsonContact = intent.getByteArrayExtra("contact") as ByteArray
        val (currentContact, _) = Contact.deserialize(currentJsonContact, 0)

        val recyclerView = findViewById<RecyclerView>(R.id.dbMessagesRV)
        val adapter = DbMessageAdapter()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        if (intent.getBooleanExtra("outgoing", true)) {
            adapter.submitList(
                dbMessageStore.getAllMessagesFromTo(
                    IPv8Android.getInstance().getOverlay<DemoCommunity>()!!.myPeer.publicKey,
                    currentContact.publicKey
                )
            )
        } else {
            adapter.submitList(
                dbMessageStore.getAllMessagesFromTo(
                    currentContact.publicKey,
                    IPv8Android.getInstance().getOverlay<DemoCommunity>()!!.myPeer.publicKey
                )
            )
        }



    }
}