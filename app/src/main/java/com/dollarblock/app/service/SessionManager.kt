package com.dollarblock.app.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.dollarblock.app.MainActivity
import com.dollarblock.app.R
import kotlinx.coroutines.*

/**
 * Manages blocking sessions with countdown notifications
 */
object SessionManager {
    private const val CHANNEL_ID = "dollar_block_session"
    private const val SESSION_NOTIFICATION_ID = 2001
    private const val PREFS_NAME = "session_prefs"
    private const val KEY_SESSION_END_TIME = "session_end_time"

    private var countdownJob: Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun startSession(context: Context, durationMinutes: Int = 15) {
        val endTime = System.currentTimeMillis() + (durationMinutes * 60 * 1000)

        // Save session end time
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_SESSION_END_TIME, endTime)
            .apply()

        createNotificationChannel(context)
        startCountdown(context, endTime)

        // Start blocking service
        BlockMonitorService.start(context)
    }

    fun endSession(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_SESSION_END_TIME)
            .apply()

        countdownJob?.cancel()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(SESSION_NOTIFICATION_ID)

        BlockMonitorService.stop(context)
    }

    fun isSessionActive(context: Context): Boolean {
        val endTime = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_SESSION_END_TIME, 0)
        return endTime > System.currentTimeMillis()
    }

    fun getRemainingMinutes(context: Context): Int {
        val endTime = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_SESSION_END_TIME, 0)
        val remaining = endTime - System.currentTimeMillis()
        return (remaining / 60000).toInt().coerceAtLeast(0)
    }

    private fun startCountdown(context: Context, endTime: Long) {
        countdownJob?.cancel()
        countdownJob = scope.launch {
            // Show notification immediately
            updateNotification(context, endTime)

            while (isActive && System.currentTimeMillis() < endTime) {
                delay(60000) // Update every minute
                updateNotification(context, endTime)
            }

            // Session ended
            endSession(context)
        }
    }

    private fun updateNotification(context: Context, endTime: Long) {
        val remaining = endTime - System.currentTimeMillis()
        val minutes = (remaining / 60000).toInt()

        if (minutes <= 0) {
            endSession(context)
            return
        }

        val notification = buildNotification(context, minutes)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(SESSION_NOTIFICATION_ID, notification)
    }

    private fun buildNotification(context: Context, minutesRemaining: Int): Notification {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Format time display: "15:00" for premium feel
        val timeText = if (minutesRemaining >= 10) {
            "$minutesRemaining:00"
        } else {
            "0$minutesRemaining:00"
        }

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("🔒 Dollar Block")
            .setContentText("$timeText")
            .setSubText("Session active")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setShowWhen(false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Focus Session Timer",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Shows countdown timer for blocking session"
                setShowBadge(true)
                enableLights(false)
                enableVibration(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
