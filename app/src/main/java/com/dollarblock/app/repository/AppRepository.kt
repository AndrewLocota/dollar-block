package com.dollarblock.app.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.dollarblock.app.data.AppDatabase
import com.dollarblock.app.data.AppInfo
import com.dollarblock.app.data.BlockedApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class AppRepository(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val blockedAppDao = database.blockedAppDao()

    val blockedApps: Flow<List<BlockedApp>> = blockedAppDao.getAllBlockedApps()

    suspend fun getInstalledApps(): List<AppInfo> {
        val packageManager = context.packageManager
        val blockedPackages = blockedApps.firstOrNull()?.map { it.packageName }?.toSet() ?: emptySet()

        // Get all apps with launcher intent (apps user can actually open)
        // This includes apps on internal storage, SD card, and external storage
        val launcherIntent = android.content.Intent(android.content.Intent.ACTION_MAIN, null).apply {
            addCategory(android.content.Intent.CATEGORY_LAUNCHER)
        }

        val launchableApps = packageManager.queryIntentActivities(
            launcherIntent,
            PackageManager.MATCH_ALL or PackageManager.MATCH_DISABLED_COMPONENTS
        )
            .mapNotNull { it.activityInfo?.packageName }
            .toSet()

        // Get ALL installed apps (internal + external storage)
        return packageManager.getInstalledApplications(
            PackageManager.GET_META_DATA or PackageManager.MATCH_UNINSTALLED_PACKAGES
        )
            .filter { launchableApps.contains(it.packageName) } // Only apps with launcher
            .filter { it.packageName != context.packageName } // Exclude ourselves
            .map { appInfo ->
                AppInfo(
                    packageName = appInfo.packageName,
                    appName = appInfo.loadLabel(packageManager).toString(),
                    icon = null, // Icons not displayed - skip loading for performance
                    isBlocked = blockedPackages.contains(appInfo.packageName)
                )
            }
            .sortedBy { it.appName }
    }

    suspend fun blockApp(appInfo: AppInfo) {
        blockedAppDao.insertBlockedApp(
            BlockedApp(
                packageName = appInfo.packageName,
                appName = appInfo.appName
            )
        )
    }

    suspend fun unblockApp(packageName: String) {
        blockedAppDao.deleteByPackageName(packageName)
    }

    suspend fun isAppBlocked(packageName: String): Boolean {
        return blockedAppDao.isAppBlocked(packageName)
    }
}
