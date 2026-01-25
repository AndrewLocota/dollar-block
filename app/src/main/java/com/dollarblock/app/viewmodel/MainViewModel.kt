package com.dollarblock.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dollarblock.app.data.AppInfo
import com.dollarblock.app.data.BlockedApp
import com.dollarblock.app.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)

    // Cache of all installed apps (loaded once)
    private val _allInstalledApps = MutableStateFlow<List<AppInfo>>(emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Track if apps have been loaded
    private var appsLoaded = false

    // Blocked apps from database (reactive)
    private val blockedApps: Flow<List<BlockedApp>> = repository.blockedApps

    // Reactively combine installed apps with blocked status
    val installedApps: StateFlow<List<AppInfo>> = _allInstalledApps
        .combine(blockedApps) { apps, blocked ->
            val blockedPackages = blocked.map { it.packageName }.toSet()
            apps.map { app ->
                app.copy(isBlocked = blockedPackages.contains(app.packageName))
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun loadApps() {
        // Only load once - prevents double loading on init + resume
        if (appsLoaded) return

        viewModelScope.launch {
            _isLoading.value = true
            _allInstalledApps.value = repository.getInstalledApps()
            _isLoading.value = false
            appsLoaded = true
        }
    }

    // For pull-to-refresh - force reload
    fun refreshApps() {
        viewModelScope.launch {
            _isLoading.value = true
            _allInstalledApps.value = repository.getInstalledApps()
            _isLoading.value = false
        }
    }

    fun toggleAppBlock(appInfo: AppInfo) {
        viewModelScope.launch {
            if (appInfo.isBlocked) {
                repository.unblockApp(appInfo.packageName)
            } else {
                repository.blockApp(appInfo)
            }
            // No need to reload - UI updates reactively via Flow!
        }
    }
}
