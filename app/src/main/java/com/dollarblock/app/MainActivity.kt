package com.dollarblock.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dollarblock.app.data.AppInfo
import com.dollarblock.app.ui.theme.DollarBlockTheme
import com.dollarblock.app.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DollarBlockTheme {
                MainScreen(
                    viewModel = viewModel,
                    onOpenAccessibilitySettings = {
                        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadApps()
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onOpenAccessibilitySettings: () -> Unit
) {
    val installedApps by viewModel.installedApps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            Spacer(Modifier.height(60.dp))

            // Header with italic serif
            Text(
                "Choose your poison.",
                fontSize = 48.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.Serif,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = Color.White,
                lineHeight = 52.sp
            )

            Spacer(Modifier.height(12.dp))

            // Subtitle
            Text(
                "SELECT APPS TO BLOCK",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.5f),
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(40.dp))

            // Apps list
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(installedApps, key = { it.packageName }) { app ->
                        AppToggleItem(
                            app = app,
                            onToggle = { viewModel.toggleAppBlock(app) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Done button
            Button(
                onClick = onOpenAccessibilitySettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                )
            ) {
                Text(
                    "DONE",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    letterSpacing = 2.sp
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun AppToggleItem(app: AppInfo, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable(onClick = onToggle),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            app.appName,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White
        )

        // Toggle switch
        Switch(
            checked = app.isBlocked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color.White.copy(alpha = 0.5f),
                uncheckedThumbColor = Color.White.copy(alpha = 0.7f),
                uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
            )
        )
    }
}
