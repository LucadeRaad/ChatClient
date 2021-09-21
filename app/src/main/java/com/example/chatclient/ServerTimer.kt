package com.example.chatclient
import java.util.*

const val FIFTEEN_SECONDS = 15.toLong()

class ServerTimer {

    init {
        val timer = Timer("Server Relay", true)

        timer.schedule(ServerTask(), FIFTEEN_SECONDS)
    }
}