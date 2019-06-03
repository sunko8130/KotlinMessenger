package com.example.kotlinmessenger.activities.messenges

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.activities.model.ChatMessage
import com.example.kotlinmessenger.activities.model.User
import com.example.kotlinmessenger.activities.views.ChatFromItem
import com.example.kotlinmessenger.activities.views.ChatToItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "Chat Log"
    }

    private var toUser: User? = null

    val adapter = GroupAdapter<ViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        recyclerview_chat_log.adapter = adapter

        toUser = intent.getParcelableExtra(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser?.username

        listenForMessages()

        send_button_chat_log.setOnClickListener {
            performChatMessage()
        }

    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/users_messages/$fromId/$toId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = LastestMessagesActivity.currentUser ?: return
                        adapter.add(ChatFromItem(chatMessage.message, currentUser))
                    } else {
                        adapter.add(ChatToItem(chatMessage.message, toUser!!))

                    }
                }

                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    private fun performChatMessage() {
        val message = edittext_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid
        if (fromId == null) return
        val ref = FirebaseDatabase.getInstance().getReference("/users_messages/$fromId/$toId").push()
        val toRef = FirebaseDatabase.getInstance().getReference("/users_messages/$toId/$fromId").push()
        val chatMessage = ChatMessage(ref.key!!, message, fromId, toId, System.currentTimeMillis() / 1000)
        ref.setValue(chatMessage)
            .addOnSuccessListener {
                Log.e(TAG, "send messages to firebase database")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }
            .addOnFailureListener {

            }
        toRef.setValue(chatMessage)
        val lastestMessagesRef = FirebaseDatabase.getInstance().getReference("/lastest_messages/$fromId/$toId")
        lastestMessagesRef.setValue(chatMessage)
        val lastestMessagesToRef = FirebaseDatabase.getInstance().getReference("/lastest_messages/$toId/$fromId")
        lastestMessagesToRef.setValue(chatMessage)
    }
}


