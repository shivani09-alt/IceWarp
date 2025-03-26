package com.example.icewarp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.icewarp.R
import com.example.icewarp.data.Channel

class ChannelListRecyclerView(private var channelList: List<Channel>) :
    RecyclerView.Adapter<ChannelListRecyclerView.ChannelViewHolder>() {

    class ChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val channelName: TextView = itemView.findViewById(R.id.channelName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_channel, parent, false)
        return ChannelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        val channel = channelList[position]
        holder.channelName.text = channel.name
    }

    override fun getItemCount(): Int = channelList.size

    // Method to update the list
    fun updateList(newList: List<Channel>) {
        channelList = newList
        notifyDataSetChanged()
    }
}
