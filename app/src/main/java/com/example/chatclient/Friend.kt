package com.example.chatclient

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)

data class Friend(
    @Json(name = "name") val Name: String,
    @Json(name = "presence") val Presence: Boolean
)
