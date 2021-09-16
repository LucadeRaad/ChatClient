package com.example.chatclient

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)

data class Chat(
    @Json(name = "date") val Date: String,
    @Json(name = "message") val Message: String,
    @Json(name = "author") val Author: String,
    @Json(name = "recipient") val Recipient: String,
    @Json(name = "read") val Read: Boolean
)