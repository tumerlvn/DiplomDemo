package com.example.diplomdemo.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomdemo.R
import com.example.diplomdemo.messages.DbMessage

class DbMessageAdapter : ListAdapter<DbMessage, DbMessageAdapter.DbMessageViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<DbMessage>() {
            override fun areItemsTheSame(oldItem: DbMessage, newItem: DbMessage): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DbMessage, newItem: DbMessage): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DbMessageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.db_message_item,
            parent, false)
//        viewHolder.itemView.setOnClickListener {
//            val position = viewHolder.adapterPosition
//            onItemClicked(getItem(position))
//        }
        return DbMessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DbMessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DbMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(dbMessage: DbMessage) {
            itemView.findViewById<TextView>(R.id.messageTV).text = dbMessage.message
            itemView.findViewById<TextView>(R.id.timeTV).text = dbMessage.timestamp.toString()
        }
    }
}