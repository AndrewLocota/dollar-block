package com.dollarblock.app.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.dollarblock.app.UnblockActivity
import com.dollarblock.app.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AppBlockService : AccessibilityService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var lastBlockedPackage: String? = null
    private var lastBlockTime: Long = 0
    private val blockCooldown = 2000L // 2 seconds cooldown

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return

            // Ignore our own package and system UI
            if (packageName == this.packageName ||
                packageName == "com.android.systemui" ||
                packageName == "android") {
                return
            }

            // Cooldown to prevent repeated triggers
            val currentTime = System.currentTimeMillis()
            if (packageName == lastBlockedPackage &&
                (currentTime - lastBlockTime) < blockCooldown) {
                return
            }

            // Check if app is blocked
            serviceScope.launch {
                val database = AppDatabase.getDatabase(applicationContext)
                val isBlocked = database.blockedAppDao().isAppBlocked(packageName)

                if (isBlocked) {
                    lastBlockedPackage = packageName
                    lastBlockTime = currentTime

                    // Launch unblock activity
                    val intent = Intent(this@AppBlockService, UnblockActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        putExtra("blocked_package", packageName)
                    }
                    startActivity(intent)

                    // Go back to home screen
                    performGlobalAction(GLOBAL_ACTION_HOME)
                }
            }
        }
    }

    override fun onInterrupt() {
        // Service interrupted
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
