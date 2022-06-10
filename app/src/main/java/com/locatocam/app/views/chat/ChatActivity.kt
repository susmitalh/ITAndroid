package com.locatocam.app.views.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.locatocam.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.pusher.client.PusherOptions

import com.pusher.client.Pusher
import com.pusher.client.channel.Channel
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState

import com.pusher.client.connection.ConnectionStateChange
import java.lang.Exception


class ChatActivity : AppCompatActivity() {

    lateinit var options:PusherOptions
    lateinit var pusher:Pusher
    //lateinit var binding: ActivityChatBinding
    lateinit var channel: Channel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       /* binding= ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)*/

        options = PusherOptions().setCluster("ap2")
        pusher = Pusher("b6d002c0a247a206d4be", options)

        CoroutineScope(Dispatchers.IO).launch {
            pusher.connect(object : ConnectionEventListener {
                override fun onConnectionStateChange(change: ConnectionStateChange) {
                    println(
                        "State changed to " + change.currentState +
                                " from " + change.previousState
                    )

                }

                override fun onError(message: String?, code: String?, e: Exception?) {
                    println("There was a problem connecting!")
                }
            }, ConnectionState.ALL)


             channel = pusher.subscribe("my-channel")
        }
    }
}