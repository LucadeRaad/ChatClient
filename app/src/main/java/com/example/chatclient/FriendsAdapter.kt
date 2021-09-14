package com.example.chatclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class FriendsAdapter(private val onClick: (Friend) -> Unit) :
    ListAdapter<Friend, FriendsAdapter.FriendViewHolder>(FriendDiffCallback) {

    /* ViewHolder for Friend, takes in the inflated view and the onClick behavior. */
    class FriendViewHolder(itemView: View, val onClick: (Friend) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private var currentFriend: Friend? = null

        init {
            itemView.setOnClickListener {
                currentFriend?.let {
                    onClick(it)
                }
            }
        }

        /* Bind Friend name and image. */
        fun bind(friend: Friend) {
            currentFriend = friend
        }
    }

    /* Creates and inflates view and return FriendViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_second, parent, false)
        return FriendViewHolder(view, onClick)
    }

    /* Gets current Friend and uses it to bind view. */
    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val Friend = getItem(position)
        holder.bind(Friend)

    }
}

object FriendDiffCallback : DiffUtil.ItemCallback<Friend>() {
    override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
        return oldItem.Name == newItem.Name
    }
}
