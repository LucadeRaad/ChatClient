package com.example.chatclient

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.Request
import java.lang.reflect.Type

val getRequest: Request = Request.Builder()
    .url("https://${Global.serverIpAndPort}/friend?name=${Global.userName}")
    .build()

class BackgroundFriendRequests : ViewModel() {
    private var friends: ArrayList<Friend>? = null

    fun fetchFriends() : Boolean {
        var hasFinishedRequest = false

        viewModelScope.launch {
            kotlin.runCatching {
                // coroutineScope is needed, else in case of any network error, it will crash
                coroutineScope {
                    val response = Global.client.newCall(getRequest).execute()

                    val responseBodyString = response.body!!.string()

                    val type: Type = Types.newParameterizedType(
                        MutableList::class.java,
                        Friend::class.java
                    )

                    val jsonAdapterFriendArray: JsonAdapter<List<Friend>> =
                        Global.moshi.adapter(type)

                    friends = jsonAdapterFriendArray.fromJson(responseBodyString) as ArrayList<Friend>?

                    hasFinishedRequest = true
                }
            }
        }

        while (!hasFinishedRequest)
        {
            // Do nothing and wait for data to be populated!
        }

        return true
    }

    fun getFriends(): ArrayList<Friend>? {
        return friends
    }
}