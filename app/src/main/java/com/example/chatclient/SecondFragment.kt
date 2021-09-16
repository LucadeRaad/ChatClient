package com.example.chatclient

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatclient.databinding.FragmentSecondBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.lang.Exception
import com.squareup.moshi.JsonAdapter
import java.lang.Thread.sleep
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.squareup.moshi.Types
import okhttp3.internal.EMPTY_REQUEST
import java.lang.reflect.Type
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment(), FriendsAdapter.OnItemClickListener {
    private var friends: ArrayList<Friend>? = null
    private var adapter = friends?.let { FriendsAdapter(it, this@SecondFragment) }

    enum class FRIENDACTION {Add, Remove}

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
        editText.layoutParams = layoutParams

        AlertDialog.Builder(requireContext())
            .setTitle("$action a friend")
            .setMessage("Please input the friend's name:")
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                if (action == FRIENDACTION.Add) {
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
                "Presence": false
            }
            """.trimIndent()

        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        try {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val postRequest: Request = Request.Builder()
                    .url("https://${Global.serverIpAndPort}/friend?name=${Global.userName}")
                    .post(requestBody)
                    .build()

                Global.client.newCall(postRequest).execute()
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

        Toast.makeText(requireContext(), "$friend has been added",
            Toast.LENGTH_LONG).show()
    }

    private fun removeFriend(friend: String) {
        try {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val deleteRequest: Request = Request.Builder()
                    .url("https://${Global.serverIpAndPort}/friend?name=${Global.userName}&friend=$friend")
                    .delete(EMPTY_REQUEST)
                    .build()

                Global.client.newCall(deleteRequest).execute()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var index = 0
        for (item in friends!!) {
            if (item.Name == friend) {
                adapter?.notifyItemRemoved(index)

                friends!!.removeAt(index)

                Toast.makeText(requireContext(), "$friend has been removed",
                    Toast.LENGTH_LONG).show()

                return
            }
            index += 1
        }

        Toast.makeText(requireContext(), "Could not find $friend",
            Toast.LENGTH_LONG).show()
    }

    override fun onItemClick(position: Int) {
        //Toast.makeText(requireContext(), "Friend $position clicked", Toast.LENGTH_SHORT).show()
        val clickedFriend : Friend? = friends?.get(position)

        val selectedFriend = Bundle()

        if (clickedFriend != null) {
            selectedFriend.putString("Name", clickedFriend.Name)
            selectedFriend.putBoolean("Presence", clickedFriend.Presence)
        }

        findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment, selectedFriend)
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
                    val type: Type = Types.newParameterizedType(
                        MutableList::class.java,
                        Friend::class.java
                    )

                    val jsonAdapterFriendArray: JsonAdapter<List<Friend>> =
                        Global.moshi.adapter(type)

                    friends = jsonAdapterFriendArray.fromJson(responseBodyString) as ArrayList<Friend>?

                    adapter = friends?.let { FriendsAdapter(it, this@SecondFragment) }
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
            showAddItemDialog(FRIENDACTION.Add)
        }
        binding.removeFriendButton.setOnClickListener {
            showAddItemDialog(FRIENDACTION.Remove)
        }
    }
override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}