package com.smk.growsave.model

import com.google.gson.annotations.SerializedName

/**
 * Model data untuk merepresentasikan satu entitas pengumuman.
 */
data class Announcement(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("image_url")
    val imageUrl: String?,

    @SerializedName("created_at")
    val createdAt: String
)
