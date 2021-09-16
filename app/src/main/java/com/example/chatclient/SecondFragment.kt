package com.example.chatclient

import android.app.AlertDialog
import android.content.Context
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
import com.example.chatclient.databinding.ActivityMainBinding.inflate
import com.example.chatclient.databinding.ContentMainBinding.inflate

import com.example.chatclient.databinding.FragmentSecondBinding
import com.example.chatclient.databinding.FragmentSecondBinding.inflate
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.io.StringReader
import java.lang.Exception
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.delay
import okhttp3.internal.wait
import java.lang.Thread.sleep
import java.lang.reflect.Type
import android.content.DialogInterface

import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast

import android.app.Application
import android.text.Editable


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {
    private var friends: ArrayList<Friend>? = null
    private var adapter = friends?.let { FriendsAdapter(it) }

    enum class FRIENDACTION {ADD, REMOVE}

private var _binding: FragmentSecondBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

      _binding = FragmentSecondBinding.inflate(inflater, container, false)
      return binding.root
    }

    private fun showAddItemDialog(action : FRIENDACTION) {
        val editText = EditText(requireContext())

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT)
            editText.setLayoutParams(layoutParams)

        AlertDialog.Builder(requireContext())
            .setTitle("EditText Alert")
            .setMessage("Please input your name..")
            .setView(editText)
            .setPositiveButton("OK") { dialog, which ->
                Toast.makeText(requireContext(), "Your name is ${editText.text}",
                    Toast.LENGTH_LONG).show()
                if (action == FRIENDACTION.ADD) {
                    addFriend(editText.text.toString())
                } else {
                    removeFriend(editText.text.toString())
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(requireContext(), "Cancel is pressed", Toast.LENGTH_LONG).show()
            }
            .show()
    }

    private fun addFriend(friend: String) {
        val json = """
            {
                "Name": "$friend",
                "Presence": true
            }
            """.trimIndent()

        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        try {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val postRequest: Request = Request.Builder()
                    .url("https://${Global.serverIpAndPort}/friend?name=${Global.userName}")
                    .post(requestBody)
                    .build()

                val response = Global.client.newCall(postRequest).execute()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val index = adapter?.itemCount

        val newFriend = Friend (
            Name = friend,
            Presence = false
        )

        if (index != null) {
            friends?.add(index, newFriend)
        }

        if (index != null) {
            adapter?.notifyItemInserted(index)
        }
    }

    fun removeFriend(friend: String) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val getRequest: Request = Request.Builder()
            .url("https://${Global.serverIpAndPort}/friend?name=${Global.userName}")
            .build()

        var hasFinishedNetworkJob = false

        try {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val response = Global.client.newCall(getRequest).execute()

                println(response.request)

                val responseBodyString = response.body!!.string()
                println("response.body!!.string() looks like: $responseBodyString")

                try {
                    val jsonAdapterFriendArray: JsonAdapter<List<Friend>> =
                        Global.moshi.adapter(Global.type)

                    friends = jsonAdapterFriendArray.fromJson(responseBodyString) as ArrayList<Friend>?

                    adapter = friends?.let { FriendsAdapter(it) }
                }
                catch (e: Exception)
                {
                    println("#####$e")
                }

                hasFinishedNetworkJob = true;
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        while(!hasFinishedNetworkJob)
        {
            //Kotlin does not have a join thread
            sleep(500)
        }

        val recyclerID = view.findViewById<RecyclerView>(R.id.friendRecycler)

        recyclerID.adapter = adapter

        recyclerID.apply {
            layoutManager = LinearLayoutManager(activity)
        }

        Snackbar.make(
            view,
            "Loaded friend list!",
            Snackbar.LENGTH_SHORT
        ).show()

        binding.addFriendButton.setOnClickListener {
            showAddItemDialog(FRIENDACTION.ADD)
        }
        binding.removeFriendButton.setOnClickListener {
            showAddItemDialog(FRIENDACTION.REMOVE)
        }
    }
override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}