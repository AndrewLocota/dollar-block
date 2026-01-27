package com.dollarblock.app

import android.os.Bundle
import android.content.Intent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.dollarblock.app.data.AppDatabase
import com.dollarblock.app.ui.theme.DollarBlockTheme
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.lifecycle.lifecycleScope
import androidx.compose.ui.platform.LocalView
import android.view.SoundEffectConstants
import android.view.HapticFeedbackConstants

class UnblockActivity : ComponentActivity() {
    private var blockedPackage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make fullscreen - immersive mode
        enableEdgeToEdge()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        blockedPackage = intent.getStringExtra("blocked_package")

        setContent {
            DollarBlockTheme {
                var paymentState by remember { mutableStateOf<PaymentState>(PaymentState.Initial) }

                UnblockScreen(
                    paymentState = paymentState,
                    onPayment = {
                        paymentState = PaymentState.Processing
                        handlePayment { success ->
                            paymentState = if (success) PaymentState.Success else PaymentState.Error
                        }
                    },
                    onCancel = {
                        // Go to home screen - close blocked app
                        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_HOME)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(homeIntent)
                        finish()
                    },
                    onComplete = { finish() }
                )
            }
        }
    }

    private fun handlePayment(onResult: (Boolean) -> Unit) {
        lifecycleScope.launch {
            // Simulate payment processing
            delay(1000)

            // For demo: always succeed
            // NOTE: App stays blocked during session - we DON'T delete it
            // Payment just lets you use it temporarily this one time
            onResult(true)
        }
    }
}

sealed class PaymentState {
    object Initial : PaymentState()
    object Processing : PaymentState()
    object Success : PaymentState()
    object Error : PaymentState()
}

@Composable
fun UnblockScreen(
    paymentState: PaymentState,
    onPayment: () -> Unit,
    onCancel: () -> Unit,
    onComplete: () -> Unit
) {
    val view = LocalView.current

    // Pre-payment taunts - sophisticated and cutting - NEW RANDOM TAUNT EVERY TIME
    val tauntMessages = listOf(
        "We want you to pay.\nGive in." to "Is scrolling really worth a dollar?",
        "Do it.\nWe dare you." to "Your discipline is worth less than $1?",
        "Breaking already?\nTypical." to "This is why you're not progressing.",
        "Your goals can wait.\nRight?" to "One more scroll won't hurt...",
        "Champions don't cave.\nYou will." to "We knew you'd be back.",
        "Discipline is hard.\nGiving up is easy." to "Which will you choose?",
        "Your future self\nis watching." to "Make them proud or disappoint them.",
        "Still can't resist?\nWeak." to "This addiction costs $1 per hit.",
        "The choice is yours.\nMake it count." to "Distraction or discipline?",
        "We profit from\nyour weakness." to "Thank you for your contribution."
    )

    // Pick random taunt on EVERY composition (truly random every time)
    val (tauntMain, tauntSub) = tauntMessages.random()

    // Post-payment shame messages - one word, devastating - RANDOM EVERY TIME
    val shameMessages = listOf(
        "Weak." to "PREDICTABLE OUTCOME.",
        "Pathetic." to "EXPECTED BEHAVIOR.",
        "Soft." to "ZERO DISCIPLINE.",
        "Typical." to "NO SELF-CONTROL.",
        "Disappointing." to "INEVITABLE RESULT.",
        "Predictable." to "ADDICTION CONFIRMED.",
        "Spineless." to "RESISTANCE: NONE."
    )

    // Pick random shame message on EVERY composition
    val (shameMain, shameSub) = shameMessages.random()

    // Countdown timer for unlock progress
    var countdown by remember { mutableStateOf(25) }

    LaunchedEffect(paymentState) {
        if (paymentState is PaymentState.Success) {
            // Countdown from 25 to 0 over 2.5 seconds
            repeat(25) {
                delay(100)
                countdown = 25 - it - 1
            }
            onComplete()
        } else if (paymentState is PaymentState.Error) {
            delay(1000)
            onComplete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // PRE-PAYMENT SCREEN
        AnimatedVisibility(
            visible = paymentState !is PaymentState.Success,
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Lock icon at top
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "LOCKED",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.8f),
                        letterSpacing = 2.sp
                    )
                }

                Spacer(Modifier.weight(1f))

                // Main taunt message - SERIF FONT
                val lines = tauntMain.split("\n")
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    lines.forEachIndexed { index, line ->
                        Text(
                            line,
                            fontSize = if (index == 1) 52.sp else 48.sp,
                            fontWeight = if (index == 1) FontWeight.Normal else FontWeight.Light,
                            fontFamily = FontFamily.Serif,
                            fontStyle = if (index == 1) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            lineHeight = 56.sp
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Subtitle question
                Text(
                    tauntSub,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                Spacer(Modifier.weight(1f))

                // Morphing pay button: pill → circle spinner → checkmark
                MorphingPayButton(
                    paymentState = paymentState,
                    onPayment = onPayment
                )

                Spacer(Modifier.height(100.dp))

                // Bottom "stay focused" text
                Text(
                    "I'LL STAY FOCUSED",
                    modifier = Modifier.clickable(onClick = {
                        // Haptic feedback - strong vibration for staying focused
                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        // Sound effect
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        onCancel()
                    }),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.4f),
                    letterSpacing = 2.sp
                )

                Spacer(Modifier.height(60.dp))
            }
        }

        // POST-PAYMENT SHAME OVERLAY
        AnimatedVisibility(
            visible = paymentState is PaymentState.Success,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(Modifier.weight(0.3f))

                    // Shame message - large serif
                    Text(
                        shameMain,
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Light,
                        fontFamily = FontFamily.Serif,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    // Subtitle in caps
                    Text(
                        shameSub,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.5f),
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(60.dp))

                    // Checkmark with animation
                    val checkScale by animateFloatAsState(
                        targetValue = if (paymentState is PaymentState.Success) 1f else 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "checkScale"
                    )

                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier
                            .size(100.dp)
                            .scale(checkScale)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(40.dp))

                    // Dollar amount
                    Text(
                        "-$1 deducted.",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.weight(1f))

                    // Progress bar and status
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "UNLOCKING\nINSTAGRAM...",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.8f),
                                letterSpacing = 1.sp,
                                lineHeight = 16.sp
                            )
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    "Redirecting in",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                                Text(
                                    "${countdown}s",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Progress bar
                        val progress = (25 - countdown) / 25f
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progress)
                                    .height(2.dp)
                                    .background(Color.White.copy(alpha = 0.6f))
                            )
                        }
                    }

                    Spacer(Modifier.height(60.dp))
                }
            }
        }
    }
}

@Composable
fun MorphingPayButton(
    paymentState: PaymentState,
    onPayment: () -> Unit
) {
    val view = LocalView.current

    // Animate button width: full width → 64dp circle
    val buttonWidth by animateDpAsState(
        targetValue = if (paymentState == PaymentState.Initial) 1000.dp else 64.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "buttonWidth"
    )

    // Animate corner radius for morph effect
    val cornerRadius by animateDpAsState(
        targetValue = 32.dp, // Always fully rounded
        animationSpec = tween(300),
        label = "cornerRadius"
    )

    // Animate text alpha
    val textAlpha by animateFloatAsState(
        targetValue = if (paymentState == PaymentState.Initial) 1f else 0f,
        animationSpec = tween(200),
        label = "textAlpha"
    )

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                onPayment()
            },
            modifier = Modifier
                .width(buttonWidth.coerceAtMost(1000.dp))
                .height(64.dp),
            enabled = paymentState == PaymentState.Initial,
            shape = RoundedCornerShape(cornerRadius),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                disabledContainerColor = Color.White
            ),
            contentPadding = PaddingValues(0.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Text content
                Text(
                    "Pay $1 to Unblock",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black.copy(alpha = textAlpha),
                    modifier = Modifier.graphicsLayer { alpha = textAlpha }
                )

                // Spinner during processing
                androidx.compose.animation.AnimatedVisibility(
                    visible = paymentState == PaymentState.Processing,
                    enter = fadeIn(tween(300)) + scaleIn(tween(300)),
                    exit = fadeOut(tween(200))
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color.Black,
                        strokeWidth = 3.dp
                    )
                }
            }
        }
    }
}
