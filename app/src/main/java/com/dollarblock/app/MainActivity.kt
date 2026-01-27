package com.dollarblock.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.activity.compose.BackHandler
import com.dollarblock.app.service.SessionManager
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
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
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dollarblock.app.data.AppInfo
import com.dollarblock.app.ui.components.LiquidToggle
import com.dollarblock.app.ui.theme.DollarBlockTheme
import com.dollarblock.app.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fromWidget = intent.getBooleanExtra("from_widget", false)

        setContent {
            DollarBlockTheme {
                MainScreen(
                    viewModel = viewModel,
                    fromWidget = fromWidget,
                    onStartSession = {
                        android.util.Log.d("MainActivity", "START SESSION button clicked")

                        // Start 15-minute blocking session
                        SessionManager.startSession(this, durationMinutes = 15)

                        android.util.Log.d("MainActivity", "Session started, going to home screen")

                        // Go to home screen - don't just finish() or it shows app switcher
                        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_HOME)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(homeIntent)
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(androidx.compose.material.ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    fromWidget: Boolean = false,
    onStartSession: () -> Unit
) {
    val installedApps by viewModel.installedApps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Handle back button - just minimize app (go home)
    BackHandler {
        // Don't close app, just go to home screen
        // User can come back to adjust blocked apps
    }

    // Load apps lazily only once when screen appears
    LaunchedEffect(Unit) {
        viewModel.loadApps()
    }

    // Pull-to-refresh state
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = { viewModel.refreshApps() }
    )

    // Morphing animation state
    var isExpanded by remember { mutableStateOf(!fromWidget) }

    LaunchedEffect(fromWidget) {
        if (fromWidget) {
            kotlinx.coroutines.delay(50) // Small delay for smooth animation
            isExpanded = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(animationSpec = tween(400)) +
                    scaleIn(
                        initialScale = 0.85f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ),
            exit = fadeOut(animationSpec = tween(300))
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

            // Apps list with pull-to-refresh
            Box(
                modifier = Modifier
                    .weight(1f)
                    .pullRefresh(pullRefreshState)
            ) {
                if (isLoading && installedApps.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
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

                // Pull refresh indicator
                PullRefreshIndicator(
                    refreshing = isLoading,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    backgroundColor = Color.White.copy(alpha = 0.9f),
                    contentColor = Color.Black
                )
            }

            Spacer(Modifier.height(20.dp))

            // Start Session button
            val blockedCount = installedApps.count { it.isBlocked }
            Button(
                onClick = onStartSession,
                enabled = blockedCount > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    disabledContainerColor = Color.White.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    if (blockedCount > 0) "START 15 MIN SESSION" else "SELECT APPS FIRST",
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
}

@Composable
fun AppToggleItem(app: AppInfo, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            app.appName,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )

        // Liquid toggle switch - flowy, soft, premium
        LiquidToggle(
            checked = app.isBlocked,
            onCheckedChange = { onToggle() }
        )
    }
}
