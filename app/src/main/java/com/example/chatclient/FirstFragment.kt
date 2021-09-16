package com.example.chatclient

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.chatclient.databinding.FragmentFirstBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

import okhttp3.Response

import okhttp3.MediaType.Companion.toMediaTypeOrNull

import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

private var _binding: FragmentFirstBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

      _binding = FragmentFirstBinding.inflate(inflater, container, false)
      return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.Send.setOnClickListener {
            val json = """
            {
                "date": "2021-09-27T23:09:27.529507+00:00",
                "message": "fifth test",
                "author": "${Global.userName}",
                "recipient": "Jerry"
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

        binding.ReceiveChat.setOnClickListener {
            val client = OkHttpClient()

            val getRequest: Request = Request.Builder()
                .url("https://localhost:49153/chat")
                .build()

            client.newCall(getRequest).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    println(response.body!!.string())
                }
            })
        }
    }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}