package com.example.chatclient

import android.app.Application

class Global : Application() {
    companion object {
        @JvmField
        var userName = ""
        var serverIpAndPort = ""
    }
}