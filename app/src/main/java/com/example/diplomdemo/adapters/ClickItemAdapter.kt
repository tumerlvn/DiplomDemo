package com.example.diplomdemo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomdemo.items.ClickItem
import com.example.diplomdemo.R

class ClickItemAdapter(private val clickItemList: List<ClickItem>) : RecyclerView.Adapter<ClickItemAdapter.ClickItemViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {

        fun onItemClick(position: Int)

    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClickItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.click_item,
        parent, false)
        return ClickItemViewHolder(itemView,mListener)
    }

    override fun onBindViewHolder(holder: ClickItemViewHolder, position: Int) {
        val currentItem = clickItemList[position]
        holder.imageView.setImageResource(currentItem.imageResource)
        holder.textView.text = currentItem.text
    }

    override fun getItemCount() = clickItemList.size

    class ClickItemViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView: TextView = itemView.findViewById(R.id.textView)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}