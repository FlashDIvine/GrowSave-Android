package com.smk.growsave.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smk.growsave.databinding.ItemAnnouncementBinding
import com.smk.growsave.model.Announcement

/**
 * AnnouncementAdapter menghubungkan data pengumuman ke dalam RecyclerView.
 */
class AnnouncementAdapter(
    private var announcements: List<Announcement> = emptyList(),
    private val isAdmin: Boolean = false,
    private val onDeleteClick: ((Announcement) -> Unit)? = null
) : RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder>() {

    /**
     * Memperbarui data list pengumuman di adapter.
     */
    fun submitList(newList: List<Announcement>) {
        announcements = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        val binding = ItemAnnouncementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AnnouncementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        holder.bind(announcements[position], isAdmin, onDeleteClick)
    }

    override fun getItemCount(): Int = announcements.size

    class AnnouncementViewHolder(
        private val binding: ItemAnnouncementBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            announcement: Announcement,
            isAdmin: Boolean,
            onDeleteClick: ((Announcement) -> Unit)?
        ) {
            binding.tvTitle.text = announcement.title
            binding.tvContent.text = announcement.content
            binding.tvDate.text = announcement.createdAt

            if (isAdmin) {
                binding.btnDelete.visibility = View.VISIBLE
                binding.btnDelete.setOnClickListener {
                    onDeleteClick?.invoke(announcement)
                }
            } else {
                binding.btnDelete.visibility = View.GONE
            }

            if (!announcement.imageUrl.isNullOrEmpty()) {
                binding.ivAnnouncement.visibility = View.VISIBLE
                // Memuat gambar dari URL menggunakan Glide secara asinkronus
                Glide.with(binding.root.context)
                    .load(announcement.imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery) // Ikon galeri bawaan Android sebagai placeholder
                    .error(android.R.drawable.ic_menu_report_image) // Ikon error bawaan jika gagal memuat gambar
                    .into(binding.ivAnnouncement)
            } else {
                binding.ivAnnouncement.visibility = View.GONE
            }
        }
    }
}
