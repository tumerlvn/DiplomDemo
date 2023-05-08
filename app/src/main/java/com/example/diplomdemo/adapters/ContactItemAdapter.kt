package com.example.diplomdemo.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomdemo.R
import com.example.diplomdemo.contacts.Contact
import com.example.diplomdemo.contacts.ContactStore
import com.example.diplomdemo.items.ContactItem
import com.example.diplomdemo.items.PeerItem
import nl.tudelft.ipv8.Peer

class ContactItemAdapter(private val contactItemList: List<ContactItem>) : RecyclerView.Adapter<ContactItemAdapter.ContactItemViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.contact_item,
            parent, false)
        return ContactItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactItemViewHolder, position: Int) {
        val currentItem = contactItemList[position]
        holder.textView.text = currentItem.contactName
        holder.contact = currentItem.contact
    }

    override fun getItemCount() = contactItemList.size

    class ContactItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.contactNameTV)
        val dialBtn: ImageButton = itemView.findViewById(R.id.contactDialBtn)
        lateinit var contact: Contact

        init {
            dialBtn.setOnClickListener {

            }
        }



    }
}