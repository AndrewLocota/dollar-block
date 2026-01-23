package com.dollarblock.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

/**
 * Liquid-style toggle switch with smooth, flowy animation.
 * Inspired by premium iOS switches but more fluid.
 */
@Composable
fun LiquidToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Animate the thumb position smoothly
    val thumbPosition by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "thumbPosition"
    )

    // Animate the track color (subtle fade)
    val trackAlpha by animateFloatAsState(
        targetValue = if (checked) 0.5f else 0.2f,
        animationSpec = tween(300),
        label = "trackAlpha"
    )

    // Animate scale for press effect
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Canvas(
        modifier = modifier
            .size(width = 51.dp, height = 31.dp) // iOS-like proportions
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onCheckedChange(!checked) }
                )
            }
    ) {
        val width = size.width
        val height = size.height
        val cornerRadius = height / 2

        // Apply scale transformation
        val scaledWidth = width * scale
        val scaledHeight = height * scale
        val offsetX = (width - scaledWidth) / 2
        val offsetY = (height - scaledHeight) / 2

        // Draw track (background)
        val trackPath = Path().apply {
            addRoundRect(
                RoundRect(
                    left = offsetX,
                    top = offsetY,
                    right = offsetX + scaledWidth,
                    bottom = offsetY + scaledHeight,
                    cornerRadius = CornerRadius(cornerRadius * scale, cornerRadius * scale)
                )
            )
        }

        drawPath(
            path = trackPath,
            color = Color.White.copy(alpha = trackAlpha),
            style = Fill
        )

        // Calculate thumb position
        val thumbRadius = (scaledHeight / 2) * 0.8f // Slightly smaller than track
        val thumbStart = offsetX + thumbRadius + (scaledHeight - thumbRadius * 2) * 0.2f
        val thumbEnd = offsetX + scaledWidth - thumbRadius - (scaledHeight - thumbRadius * 2) * 0.2f
        val thumbX = thumbStart + (thumbEnd - thumbStart) * thumbPosition
        val thumbY = offsetY + scaledHeight / 2

        // Draw liquid blob (thumb) - pure white
        drawCircle(
            color = Color.White,
            radius = thumbRadius,
            center = Offset(thumbX, thumbY)
        )
    }
}
