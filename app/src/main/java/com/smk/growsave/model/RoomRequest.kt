package com.smk.growsave.model

import com.google.gson.annotations.SerializedName

data class RoomRequest(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user")
    val user: User?,
    @SerializedName("room_code")
    val roomCode: String? = null,
    @SerializedName("status")
    val status: String?
)
