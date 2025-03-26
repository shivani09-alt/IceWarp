package com.example.icewarp.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.icewarp.R
import com.example.icewarp.databinding.ItemGroupHeaderBinding
import kotlin.math.truncate

class GroupAdapter(private val groupedChannels: List<GroupedChannels>) :
    RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = ItemGroupHeaderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groupedChannels[position]
        holder.bind(group)
    }

    override fun getItemCount(): Int = groupedChannels.size

    class GroupViewHolder(private val binding: ItemGroupHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        private var isExpanded = true

        fun bind(group: GroupedChannels) {
            binding.groupHeader.text = group.groupFolderName
            binding.childRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
            binding.childRecyclerView.adapter = ChannelListRecyclerView(group.channels)
            binding.conatiner.setOnClickListener {

                binding.childRecyclerView.visibility = if (isExpanded) {
                    binding.icon.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
                    View.GONE
                } else {
                    binding.icon.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
                    View.VISIBLE
                }
                isExpanded = !isExpanded

            }

        }
    }
}
