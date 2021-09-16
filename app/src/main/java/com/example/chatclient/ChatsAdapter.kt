package com.example.chatclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatsAdapter(private val chatList: List<Chat>) :
    RecyclerView.Adapter<ChatsAdapter.ChatViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_message,
            parent, false)
        return ChatViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val currentItem = chatList[position]

        holder.messageName.text = currentItem.Message

        holder.messageDate.text = currentItem.Date

        holder.messageRead.text = currentItem.Read.toString()
    }
    override fun getItemCount() = chatList.size
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val messageName: TextView = itemView.findViewById(R.id.friend_message)
        val messageDate: TextView = itemView.findViewById(R.id.message_date)
        val messageRead: TextView = itemView.findViewById(R.id.read_status)
    }
}