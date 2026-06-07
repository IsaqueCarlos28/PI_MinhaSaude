package com.example.medicoapplication.data.remote.message

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MinhaSaudeFirebaseMessagingService :
    FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d(
            "FCM",
            "Novo token: $token"
        )
    }

    override fun onMessageReceived(
        remoteMessage: RemoteMessage
    ) {

        Log.d(
            "FCM",
            "Mensagem recebida"
        )

        Log.d(
            "FCM",
            "Title: ${remoteMessage.notification?.title}"
        )

        Log.d(
            "FCM",
            "Body: ${remoteMessage.notification?.body}"
        )
    }
}