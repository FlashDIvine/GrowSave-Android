package com.smk.growsave.model

import com.google.gson.annotations.SerializedName

/**
 * Data class untuk merepresentasikan entitas RoomMember (penghuni room).
 */
data class RoomMember(
    @SerializedName("id")
    val id: Int,

    @SerializedName("room_id")
    val roomId: Int,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("status")
    val status: String,

    @SerializedName("joined_at")
    val joinedAt: String?,

    @SerializedName("user")
    val user: User?
)
