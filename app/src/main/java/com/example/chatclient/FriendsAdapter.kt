package com.example.chatclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FriendsAdapter(private val friendList: List<Friend>) : RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item,
        parent, false)

        return FriendsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val currentItem = friendList[position]

        holder.imageView.setImageResource(when(currentItem.Presence) {
            true -> 0
            false -> 0
        })

        holder.friendName.text = currentItem.Name
        
        holder.friendStatus.text = when(currentItem.Presence) {
            true -> "online"
            false -> "offline"
        }
    }

    override fun getItemCount() = friendList.size

    class FriendsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_view)
        val friendName: TextView = itemView.findViewById(R.id.friend_name)
        val friendStatus: TextView = itemView.findViewById(R.id.friend_status)
    }
}
