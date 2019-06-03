package com.example.kotlinmessenger.activities.model

class ChatMessage(val id:String,val message:String,val fromId:String,val toId:String,val timeStamp:Long){
    constructor():this("","","","",-1)
}