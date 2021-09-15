package com.example.chatclient

import android.app.Application
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.Type

class Global : Application() {
    companion object {
        @JvmField
        var userName = ""
        var serverIpAndPort = ""

        val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

        val type: Type = Types.newParameterizedType(
            MutableList::class.java,
            Friend::class.java
        )
    }
}