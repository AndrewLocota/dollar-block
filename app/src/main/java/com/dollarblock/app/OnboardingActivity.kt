package com.dollarblock.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.dollarblock.app.ui.theme.DollarBlockTheme

private const val PREFS_NAME = "dollar_block_prefs"
private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
private const val TAG = "DollarBlock"

class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "OnboardingActivity onCreate started")

        // Skip onboarding if already completed
        if (hasCompletedOnboarding()) {
            Log.d(TAG, "Onboarding already completed, going to MainActivity")
            startMainActivity()
            return
        }

        Log.d(TAG, "Showing onboarding screen")
        setContent {
            DollarBlockTheme {
                OnboardingScreen(
                    onContinue = {
                        Log.d(TAG, "User clicked continue")
                        markOnboardingComplete()
                        startMainActivity()
                    }
                )
            }
        }
        Log.d(TAG, "setContent completed")
    }

    private fun hasCompletedOnboarding(): Boolean {
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    private fun markOnboardingComplete() {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ONBOARDING_COMPLETED, true)
            .apply()
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

@Composable
fun OnboardingScreen(onContinue: () -> Unit) {
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
            Spacer(Modifier.weight(0.3f))

            // Main message
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Discipline is",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = FontFamily.Serif,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 48.sp
                )
                Text(
                    "cheap.",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = FontFamily.Serif,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 48.sp
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    "Distraction",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.Serif,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 52.sp
                )
                Text(
                    "costs $1.",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.Serif,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 52.sp
                )
            }

            Spacer(Modifier.height(60.dp))

            // Three steps
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                StepItem("1.", "Choose apps.")
                StepItem("2.", "Pay $1 to open.")
                StepItem("3.", "Build focus.")
            }

            Spacer(Modifier.weight(1f))

            // CTA Button
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Link Card & Start Blocking",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "→",
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Bottom text
            Text(
                "COMMIT TO THE COST",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.3f),
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(60.dp))
        }
    }
}

@Composable
fun StepItem(number: String, text: String) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Text(
            number,
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            color = Color.White.copy(alpha = 0.4f),
            modifier = Modifier.width(60.dp)
        )
        Text(
            text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White
        )
    }
}
