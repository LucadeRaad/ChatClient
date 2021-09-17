package com.example.chatclient

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatclient.databinding.FragmentFirstBinding
import com.google.android.material.snackbar.Snackbar
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.Exception
import java.lang.reflect.Type

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    private var friendName = ""
    private var presence = false

    private var chats: ArrayList<Chat>? = null
    private var adapter = chats?.let { ChatsAdapter(it)}

private var _binding: FragmentFirstBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        friendName = arguments?.getString("Name").toString()
        presence = arguments?.getString("Presence").toBoolean()

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val getRequest: Request = Request.Builder()
            .url("https://${Global.serverIpAndPort}/chat?author=$friendName&recipient=${Global.userName}")
            .build()

        var hasFinishedNetworkJob = false

        try {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val response = Global.client.newCall(getRequest).execute()

                println(response.request)

                val responseBodyString = response.body!!.string()
                println("response.body!!.string() looks like: $responseBodyString")

                try {
                    val type: Type = Types.newParameterizedType(
                        MutableList::class.java,
                        Chat::class.java
                    )

                    val jsonAdapterChatArray: JsonAdapter<List<Chat>> =
                        Global.moshi.adapter(type)

                    chats = jsonAdapterChatArray.fromJson(responseBodyString) as ArrayList<Chat>?

                    adapter = chats?.let { ChatsAdapter(it) }
                }
                catch (e: Exception)
                {
                    println("#####$e")
                }

                hasFinishedNetworkJob = true
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        while(!hasFinishedNetworkJob)
        {
            //Kotlin does not have a join thread
            Thread.sleep(500)
        }

        val recyclerID = view.findViewById<RecyclerView>(R.id.chatRecycler)

        recyclerID.adapter = adapter

        recyclerID.apply {
            layoutManager = LinearLayoutManager(activity)
        }



        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.Send.setOnClickListener {
            val chatBox = view.findViewById<TextView>(R.id.chatBox)
            val message = chatBox.text.toString()

            if (message == "")
            {
                return@setOnClickListener
            }

            val json = """
            {
                "date": "2021-09-27T23:09:27.529507+00:00",
                "message": "$message",
                "author": "${Global.userName}",
                "recipient": "$friendName"
            }
            """.trimIndent()

            val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            try {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    val postRequest: Request = Request.Builder()
                        .url("https://" + Global.serverIpAndPort + "/chat")
                        .post(requestBody)
                        .build()

                    val response = Global.client.newCall(postRequest).execute()
                    Log.d("Debug", response.body!!.string())
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            Snackbar.make(
                view,
                "Sent a message!",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}