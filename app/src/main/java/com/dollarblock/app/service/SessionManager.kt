package com.dollarblock.app.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
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
        Log.d("SessionManager", "Starting $durationMinutes minute session")
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
        Log.d("SessionManager", "Session started, notification should be visible")
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
        Log.d("SessionManager", "Starting countdown, end time: $endTime")
        countdownJob?.cancel()
        countdownJob = scope.launch {
            // Show notification immediately
            Log.d("SessionManager", "Posting initial notification")
            updateNotification(context, endTime)

            while (isActive && System.currentTimeMillis() < endTime) {
                delay(60000) // Update every minute
                updateNotification(context, endTime)
            }

            // Session ended
            Log.d("SessionManager", "Countdown finished, ending session")
            endSession(context)
        }
    }

    private fun updateNotification(context: Context, endTime: Long) {
        val remaining = endTime - System.currentTimeMillis()
        val minutes = (remaining / 60000).toInt()

        if (minutes <= 0) {
            Log.d("SessionManager", "Session ended")
            endSession(context)
            return
        }

        Log.d("SessionManager", "Updating notification: $minutes minutes remaining")
        val notification = buildNotification(context, minutes)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        Log.d("SessionManager", "Posting notification to system (ID: $SESSION_NOTIFICATION_ID)")
        notificationManager.notify(SESSION_NOTIFICATION_ID, notification)
        Log.d("SessionManager", "Notification posted successfully")
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

        // End session action
        val endSessionIntent = Intent(context, SessionActionReceiver::class.java).apply {
            action = "END_SESSION"
        }
        val endSessionPendingIntent = PendingIntent.getBroadcast(
            context, 1, endSessionIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("🔒 Focus Session")
            .setContentText(timeText)
            .setSubText("Tap to manage")
            .setContentIntent(pendingIntent)
            .setOngoing(true)  // Makes it permanent
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setShowWhen(false)
            .setAutoCancel(false)  // Don't dismiss on tap
            .setOnlyAlertOnce(true)  // Only alert on first show
            .setSound(null)  // No sound for countdown updates
            .setColorized(true)
            .setColor(0xFF000000.toInt())  // Black color
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "End",
                endSessionPendingIntent
            )
            .build()
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Focus Session Timer",
                NotificationManager.IMPORTANCE_HIGH  // HIGH importance for heads-up
            ).apply {
                description = "Shows countdown timer for blocking session"
                setShowBadge(true)
                enableLights(false)
                enableVibration(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setSound(null, null)  // No sound for updates
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            Log.d("SessionManager", "Notification channel created with IMPORTANCE_HIGH")
        }
    }
}
