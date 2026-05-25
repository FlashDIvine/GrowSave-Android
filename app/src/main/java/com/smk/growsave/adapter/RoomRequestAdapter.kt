package com.smk.growsave.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smk.growsave.databinding.ItemRoomRequestBinding
import com.smk.growsave.model.RoomRequest

class RoomRequestAdapter(
    private var requests: List<RoomRequest> = emptyList(),
    private val onApproveClicked: (RoomRequest) -> Unit,
    private val onRejectClicked: (RoomRequest) -> Unit
) : RecyclerView.Adapter<RoomRequestAdapter.RoomRequestViewHolder>() {

    fun submitList(newList: List<RoomRequest>) {
        requests = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomRequestViewHolder {
        val binding = ItemRoomRequestBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RoomRequestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomRequestViewHolder, position: Int) {
        holder.bind(requests[position])
    }

    override fun getItemCount(): Int = requests.size

    inner class RoomRequestViewHolder(
        private val binding: ItemRoomRequestBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(request: RoomRequest) {
            android.util.Log.d("ROOM_REQUEST", request.toString())

            binding.tvName.text = request.user?.name ?: "Unknown User"
            binding.tvEmail.text = request.user?.email ?: "-"
            binding.tvRoomCode.text = request.roomCode ?: "-"

            val name = request.user?.name
            val initials = if (!name.isNullOrEmpty()) {
                name.take(1).uppercase()
            } else {
                "U"
            }
            binding.tvInitials.text = initials

            val status = request.status?.lowercase() ?: "pending"
            if (status == "approved") {
                binding.tvStatus.text = "APPROVED"
                binding.tvStatus.setBackgroundResource(com.smk.growsave.R.drawable.bg_badge_success)
                binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#0D7B43"))
                binding.btnApprove.visibility = android.view.View.GONE
                binding.btnReject.visibility = android.view.View.GONE
            } else {
                binding.tvStatus.text = "PENDING"
                binding.tvStatus.setBackgroundResource(com.smk.growsave.R.drawable.bg_badge_warning)
                binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#D97706"))
                binding.btnApprove.visibility = android.view.View.VISIBLE
                binding.btnReject.visibility = android.view.View.VISIBLE
            }

            binding.btnApprove.setOnClickListener {
                onApproveClicked(request)
            }

            binding.btnReject.setOnClickListener {
                onRejectClicked(request)
            }
        }
    }
}
