package com.example.chatclient

import com.beust.klaxon.Json

data class Friend(
    @Json(index = 1) val Name: String,
    @Json(index = 2) val Presence: Boolean
)
