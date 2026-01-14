package com.dollarblock.app

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.dollarblock.app.data.AppDatabase
import com.dollarblock.app.payment.PaymentManager
import com.dollarblock.app.payment.PaymentResult
import com.dollarblock.app.ui.theme.DollarBlockTheme
import kotlinx.coroutines.launch

class UnblockActivity : ComponentActivity() {
    private lateinit var paymentManager: PaymentManager
    private var blockedPackage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        blockedPackage = intent.getStringExtra("blocked_package")
        paymentManager = PaymentManager(this)

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
                    onCancel = { finish() },
                    onComplete = { finish() }
                )
            }
        }
    }

    private fun handlePayment(onResult: (Boolean) -> Unit) {
        val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)
        scope.launch {
            when (val result = paymentManager.processPayment()) {
                is PaymentResult.Success -> {
                    // Unblock the app
                    blockedPackage?.let { pkg ->
                        val database = AppDatabase.getDatabase(applicationContext)
                        database.blockedAppDao().deleteByPackageName(pkg)
                    }
                    onResult(true)
                }
                is PaymentResult.Error -> {
                    onResult(false)
                }
                is PaymentResult.Cancelled -> {
                    onResult(false)
                }
            }
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

    // Random taunt message - psychological warfare!
    val tauntMessages = remember {
        listOf(
            "Go ahead, waste your dollar 💸",
            "Really? Again? 🤨",
            "Your future self is judging you right now 👀",
            "Do it. We dare you. 😏",
            "Is this really worth $1? 🤔",
            "Still can't focus? 😴",
            "Breaking already? Weak. 💀",
            "Another distraction? Seriously? 🙄",
            "We knew you'd be back 🎯",
            "Discipline = 0 📉",
            "Just pay. We want your money 💰",
            "Your goals can wait, right? ⏰",
            "One more scroll won't hurt... or will it? 📱",
            "Champions don't need this app 🏆",
            "This is why you're not progressing 📊",
            "Do it. I dare you. 😈",
            "Still wasting time? Classic. ⌛",
            "Your competition is working right now 🚀",
            "Imagine explaining this to your therapist 🛋️"
        )
    }

    val randomTaunt = remember { tauntMessages.random() }

    // Post-payment shame messages - BRUTAL
    val shameMessages = remember {
        listOf(
            "Weak.",
            "Pathetic.",
            "That was easy.",
            "Zero discipline.",
            "Couldn't resist.",
            "We knew you'd fold.",
            "Disappointing.",
            "So predictable.",
            "Not surprised.",
            "Soft.",
            "Typical.",
            "Try harder next time.",
            "You'll be back."
        )
    }

    val randomShame = remember { shameMessages.random() }

    // Auto-close after showing shame
    LaunchedEffect(paymentState) {
        if (paymentState is PaymentState.Success) {
            delay(2500) // Show shame for 2.5 seconds
            onComplete()
        } else if (paymentState is PaymentState.Error) {
            delay(1000)
            onComplete()
        }
    }

    // Pulsating animation for the lock icon
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        // Main card with form
        AnimatedVisibility(
            visible = paymentState !is PaymentState.Success,
            exit = fadeOut(animationSpec = tween(500))
        ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Lock icon with animation
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                    modifier = Modifier
                        .size(80.dp)
                        .scale(scale)
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Title
                Text(
                    "App Blocked",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(Modifier.height(12.dp))

                // Taunt message - THE PSYCHOLOGICAL WEAPON
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        randomTaunt,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Subtitle
                Text(
                    "This app is currently blocked.\nPay $1 to unblock it.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(Modifier.height(24.dp))

                // Pay button - The star of the show!
                Button(
                    onClick = onPayment,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = paymentState == PaymentState.Initial,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    when (paymentState) {
                        PaymentState.Processing -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 3.dp
                            )
                        }
                        else -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "Pay ",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.3f)
                                ) {
                                    Text(
                                        " $1 ",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    " to Unblock",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Cancel button - Make this the hero action
                OutlinedButton(
                    onClick = onCancel,
                    enabled = paymentState == PaymentState.Initial,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        "Stay Strong - Go Back",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Motivational text
                Text(
                    "Your discipline is worth more than $1",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }
        }

        // POST-PAYMENT SHAME OVERLAY - THE FINAL BLOW
        AnimatedVisibility(
            visible = paymentState is PaymentState.Success,
            enter = fadeIn(animationSpec = tween(300)) + scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Checkmark animation - satisfying but short-lived
                    val checkScale by animateFloatAsState(
                        targetValue = if (paymentState is PaymentState.Success) 1f else 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "checkScale"
                    )

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(80.dp)
                            .scale(checkScale)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp)
                        )
                    }

                    Spacer(Modifier.height(40.dp))

                    // The shame message - elegant serif font, devastating words
                    Text(
                        randomShame,
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Light,
                        fontFamily = FontFamily.Serif,
                        color = Color.White.copy(alpha = 0.9f),
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
