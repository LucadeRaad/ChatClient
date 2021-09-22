package com.example.chatclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatsAdapter(private var chatList: List<Chat>) :
    RecyclerView.Adapter<ChatsAdapter.ChatViewHolder>() {

    fun setChats(newChats: List<Chat>) {
        chatList = newChats
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ChatViewHolder {

        val itemView =  when (viewType == 1) {
            true -> LayoutInflater.from(parent.context).inflate(R.layout.recycler_message_right,
                parent, false)

            false -> LayoutInflater.from(parent.context).inflate(R.layout.recycler_message,
                    parent, false)
        }


        return ChatViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val currentItem = chatList[position]

        holder.messageName.text = currentItem.Message

        holder.messageDate.text = currentItem.Date

        holder.messageRead.text = currentItem.Read.toString()
    }

    // Returns 1 if item is sent from author, 0 if from recipient
    override fun getItemViewType(position: Int): Int {
        return (chatList[position].Author == Global.userName).compareTo(false)
    }

    override fun getItemCount() = chatList.size

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val messageName: TextView = itemView.findViewById(R.id.friend_message)
        val messageDate: TextView = itemView.findViewById(R.id.message_date)
        val messageRead: TextView = itemView.findViewById(R.id.read_status)
    }


}