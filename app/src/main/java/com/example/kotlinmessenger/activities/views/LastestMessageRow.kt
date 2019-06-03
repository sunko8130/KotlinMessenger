package com.example.kotlinmessenger.activities.views

import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.activities.model.ChatMessage
import com.example.kotlinmessenger.activities.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.lastest_message_row.view.*

class LastestMessageRow(val chatMessage: ChatMessage) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.lastest_message_row
    }

    var chatPartnerUser: User? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.message_textview_latest_message.text = chatMessage.message
        val chatPartnerId: String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.username_textview_latest_message.text = chatPartnerUser?.username
                val uri = chatPartnerUser?.profileImageUrl
                val targetImageView = viewHolder.itemView.imageview_latest_message
                Picasso.get().load(uri).placeholder(R.drawable.placeholder).into(targetImageView)

            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
}