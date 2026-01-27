package com.dollarblock.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class SessionActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "END_SESSION" -> {
                SessionManager.endSession(context)
            }
        }
    }
}
