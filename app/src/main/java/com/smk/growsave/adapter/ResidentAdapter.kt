package com.smk.growsave.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smk.growsave.databinding.ItemResidentBinding
import com.smk.growsave.model.RoomMember

class ResidentAdapter(
    private var residents: List<RoomMember> = emptyList()
) : RecyclerView.Adapter<ResidentAdapter.ResidentViewHolder>() {

    private var filteredList: List<RoomMember> = residents

    fun submitList(newList: List<RoomMember>) {
        residents = newList
        filteredList = newList
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            residents
        } else {
            residents.filter {
                val name = it.user?.name ?: ""
                val block = it.user?.houseBlock ?: ""
                val number = it.user?.houseNumber ?: ""
                name.contains(query, ignoreCase = true) ||
                        block.contains(query, ignoreCase = true) ||
                        number.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    fun getFilteredSize(): Int = filteredList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResidentViewHolder {
        val binding = ItemResidentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ResidentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResidentViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount(): Int = filteredList.size

    class ResidentViewHolder(
        private val binding: ItemResidentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(member: RoomMember) {
            val user = member.user
            val name = user?.name ?: "Unknown User"
            binding.tvName.text = name

            // Format address: e.g. "Blok B3 No. 12"
            val houseBlock = user?.houseBlock ?: ""
            val houseNumber = user?.houseNumber ?: ""
            val address = when {
                houseBlock.isNotEmpty() && houseNumber.isNotEmpty() -> "$houseBlock $houseNumber"
                houseBlock.isNotEmpty() -> houseBlock
                houseNumber.isNotEmpty() -> houseNumber
                else -> "-"
            }
            binding.tvBlock.text = address

            // Initials
            val initials = if (name.isNotEmpty()) {
                name.take(1).uppercase()
            } else {
                "U"
            }
            binding.tvInitials.text = initials
        }
    }
}
