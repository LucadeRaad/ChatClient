package com.example.chatclient

import android.content.Context
import android.content.res.Configuration
import androidx.startup.Initializer

class TimerInitializer :Initializer<ServerTimer> {
    override fun create(context: Context): ServerTimer {
        return ServerTimer()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}