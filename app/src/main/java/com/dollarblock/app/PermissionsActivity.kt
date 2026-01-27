package com.dollarblock.app

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dollarblock.app.service.BlockMonitorService
import com.dollarblock.app.ui.theme.DollarBlockTheme

class PermissionsActivity : ComponentActivity() {

    private val usageStatsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        checkPermissions()
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Continue even if notification permission denied
        // The app can still work, just notifications might not show
        checkPermissions()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DollarBlockTheme {
                var permissionsGranted by remember { mutableStateOf(false) }

                // Check permissions on every resume (when user comes back from settings)
                LaunchedEffect(Unit) {
                    permissionsGranted = hasUsageStatsPermission()
                }

                if (permissionsGranted) {
                    // Request notification permission FIRST before going to MainActivity
                    LaunchedEffect(Unit) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ContextCompat.checkSelfPermission(
                                    this@PermissionsActivity,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                        // Small delay to let notification permission request show
                        kotlinx.coroutines.delay(500)
                        BlockMonitorService.start(this@PermissionsActivity)
                        startActivity(Intent(this@PermissionsActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    PermissionsScreen(
                        onRequestPermissions = { requestUsageStatsPermission() },
                        onCheckAgain = {
                            permissionsGranted = hasUsageStatsPermission()
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Recreate to refresh permission state when coming back from settings
        recreate()
    }

    private fun checkPermissions() {
        if (hasUsageStatsPermission()) {
            BlockMonitorService.start(this)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                packageName
            )
        } else {
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun requestUsageStatsPermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        usageStatsLauncher.launch(intent)
    }
}

@Composable
fun PermissionsScreen(
    onRequestPermissions: () -> Unit,
    onCheckAgain: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.weight(0.2f))

            // Icon
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.1f),
                modifier = Modifier.size(80.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            // Title
            Text(
                "One Permission",
                fontSize = 40.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.Serif,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            // Explanation
            Text(
                "Dollar Block needs Usage Access to detect when you open blocked apps.\n\nThis is the same permission used by Screen Time and other focus apps.\n\nNo data leaves your device.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(Modifier.weight(1f))

            // Grant button
            Button(
                onClick = onRequestPermissions,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                )
            ) {
                Text(
                    "Grant Permission",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }

            Spacer(Modifier.height(16.dp))

            // Check again button
            TextButton(
                onClick = onCheckAgain,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    "I've Granted Permission - Check Again",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Spacer(Modifier.height(20.dp))

            // Info text
            Text(
                "Find \"Dollar Block\" in the list and enable it",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.4f),
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(60.dp))
        }
    }
}
