package com.example.chatclient

import java.util.*

class ServerTask : TimerTask() {
    override fun run() {
        println("Timer task has been executed!")
    }
}