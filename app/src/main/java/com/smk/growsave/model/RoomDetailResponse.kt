package com.smk.growsave.model

import com.google.gson.annotations.SerializedName

/**
 * Model data type-safe untuk merepresentasikan response data room dari backend.
 * Mendukung pembacaan data baik secara langsung (untuk Admin) maupun nested (untuk User/Member).
 */
data class RoomDetailResponse(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("room_name")
    val roomNameDirect: String? = null,

    @SerializedName("room_code")
    val roomCodeDirect: String? = null,

    @SerializedName("status")
    val statusDirect: String? = null,

    @SerializedName("total_members")
    val totalMembers: Int? = null,

    @SerializedName("room")
    val roomNested: RoomInfo? = null
) {
    val roomName: String
        get() = roomNameDirect ?: roomNested?.roomName ?: ""

    val roomCode: String
        get() = roomCodeDirect ?: roomNested?.roomCode ?: ""

    val status: String
        get() = statusDirect ?: roomNested?.status ?: ""
}

data class RoomInfo(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("room_name")
    val roomName: String? = null,

    @SerializedName("room_code")
    val roomCode: String? = null,

    @SerializedName("status")
    val status: String? = null
)
