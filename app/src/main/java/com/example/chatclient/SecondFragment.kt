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

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

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

    private fun OkHttpClient.Builder.ignoreAllSSLErrors(): OkHttpClient.Builder {
        val naiveTrustManager = object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) = Unit
            override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) = Unit
        }

        val insecureSocketFactory = SSLContext.getInstance("TLSv1.2").apply {
            val trustAllCerts = arrayOf<TrustManager>(naiveTrustManager)
            init(null, trustAllCerts, SecureRandom())
        }.socketFactory

        sslSocketFactory(insecureSocketFactory, naiveTrustManager)
        hostnameVerifier(HostnameVerifier { _, _ -> true })
        return this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val client = OkHttpClient.Builder().apply {
            ignoreAllSSLErrors()
        }.build()

        val getRequest: Request = Request.Builder()
            .url("https://${Global.serverIpAndPort}/friend?name=${Global.userName}")
            .build()

        var friends: List<Friend>? = null

        var hasFinishedNetworkJob = false

        try {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val response = client.newCall(getRequest).execute()

                println(response.request)

                val responseBodyString = response.body!!.string()
                println("response.body!!.string() looks like: $responseBodyString")

                try {
                    val jsonAdapterFriendArray: JsonAdapter<List<Friend>> =
                        Global.moshi.adapter(Global.type)

                    friends = jsonAdapterFriendArray.fromJson(responseBodyString)
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

        recyclerID.adapter = friends?.let { FriendsAdapter(it) }

        recyclerID.apply {
            layoutManager = LinearLayoutManager(activity)
        }

        Snackbar.make(
            view,
            "Loaded friend list!",
            Snackbar.LENGTH_SHORT
        ).show()
    }
override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}