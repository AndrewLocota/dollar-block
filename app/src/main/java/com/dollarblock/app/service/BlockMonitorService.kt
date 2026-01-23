package com.dollarblock.app.service

import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.dollarblock.app.MainActivity
import com.dollarblock.app.R
import com.dollarblock.app.UnblockActivity
import com.dollarblock.app.data.AppDatabase
import kotlinx.coroutines.*

/**
 * Modern approach using UsageStatsManager instead of AccessibilityService.
 * This is what apps like Forest, Freedom, and AppBlock use.
 */
class BlockMonitorService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var monitoringJob: Job? = null
    private var blockedAppsObserverJob: Job? = null
    private var lastCheckedTime = System.currentTimeMillis()
    private var lastBlockedPackage: String? = null

    // Cached set of blocked package names - updated reactively via Flow
    private val blockedPackages = mutableSetOf<String>()

    companion object {
        const val CHANNEL_ID = "dollar_block_monitor"
        const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
            val intent = Intent(context, BlockMonitorService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, BlockMonitorService::class.java))
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        observeBlockedApps()
        startMonitoring()
    }

    private fun observeBlockedApps() {
        val database = AppDatabase.getDatabase(applicationContext)
        blockedAppsObserverJob = scope.launch {
            database.blockedAppDao().getAllBlockedApps().collect { apps ->
                synchronized(blockedPackages) {
                    blockedPackages.clear()
                    blockedPackages.addAll(apps.map { it.packageName })
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startMonitoring() {
        monitoringJob = scope.launch {
            while (isActive) {
                checkCurrentApp()
                delay(500) // Check every 500ms
            }
        }
    }

    private suspend fun checkCurrentApp() {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val currentTime = System.currentTimeMillis()

        // Get events in last second
        val events = usageStatsManager.queryEvents(lastCheckedTime, currentTime)
        lastCheckedTime = currentTime

        val event = UsageEvents.Event()
        var currentPackage: String? = null

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                currentPackage = event.packageName
            }
        }

        currentPackage?.let { packageName ->
            // Don't block ourselves or system apps
            if (packageName == this.packageName || packageName == lastBlockedPackage) {
                return
            }

            // Check if app is blocked (using cached set - no DB query!)
            val isBlocked = synchronized(blockedPackages) {
                blockedPackages.contains(packageName)
            }

            if (isBlocked) {
                lastBlockedPackage = packageName
                showBlockScreen(packageName)
            }
        }
    }

    private fun showBlockScreen(packageName: String) {
        val intent = Intent(this, UnblockActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            putExtra("blocked_package", packageName)
        }
        startActivity(intent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Dollar Block Monitor",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors blocked apps"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Dollar Block Active")
            .setContentText("Monitoring blocked apps")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        monitoringJob?.cancel()
        blockedAppsObserverJob?.cancel()
        scope.cancel()
    }
}
