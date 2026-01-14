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

        return packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 } // Only user apps
            .filter { it.packageName != context.packageName } // Exclude ourselves
            .map { appInfo ->
                AppInfo(
                    packageName = appInfo.packageName,
                    appName = appInfo.loadLabel(packageManager).toString(),
                    icon = appInfo.loadIcon(packageManager),
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
