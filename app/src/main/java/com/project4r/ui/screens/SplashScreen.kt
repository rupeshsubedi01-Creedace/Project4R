package com.project4r.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {

    var logoVisible by remember { mutableStateOf(false) }
    var textVisible by remember { mutableStateOf(false) }
    var tagVisible  by remember { mutableStateOf(false) }
    var dotsVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200);  logoVisible = true
        delay(400);  textVisible = true
        delay(300);  tagVisible  = true
        delay(400);  dotsVisible = true
        delay(1400); onFinished()
    }

    val logoScale by animateFloatAsState(
        targetValue   = if (logoVisible) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMedium
        ), label = "logoScale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue   = if (logoVisible) 1f else 0f,
        animationSpec = tween(400), label = "logoAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF052E16),
                        Color(0xFF14532D),
                        Color(0xFF16A34A)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        // Decorative circles
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = (-60).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.04f))
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-60).dp, y = 60.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f))
        )

        // Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Logo orb
            Box(
                modifier = Modifier
                    .scale(logoScale)
                    .alpha(logoAlpha)
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF4ADE80),
                                Color(0xFF16A34A)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("\u2708\uFE0F", fontSize = 48.sp)
            }

            Spacer(Modifier.height(28.dp))

            // App name + pills
            AnimatedVisibility(
                visible = textVisible,
                enter   = fadeIn(tween(500)) + slideInVertically(
                    initialOffsetY = { 40 },
                    animationSpec  = tween(500)
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Project 4R",
                        fontSize      = 38.sp,
                        fontWeight    = FontWeight.ExtraBold,
                        color         = Color.White,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Route", "Remind", "Review", "Reset").forEach { label ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    label,
                                    fontSize   = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Tagline
            AnimatedVisibility(
                visible = tagVisible,
                enter   = fadeIn(tween(600))
            ) {
                Text(
                    "\uD83C\uDDF3\uD83C\uDDF5 Built for Nepali expats in Dubai \uD83C\uDDE6\uD83C\uDDEA",
                    fontSize   = 13.sp,
                    color      = Color.White.copy(alpha = 0.75f),
                    fontWeight = FontWeight.Medium,
                    textAlign  = TextAlign.Center
                )
            }

            Spacer(Modifier.height(50.dp))

            // Loading dots
            AnimatedVisibility(
                visible = dotsVisible,
                enter   = fadeIn(tween(400))
            ) {
                LoadingDots()
            }
        }

        // Bottom — creator card
        AnimatedVisibility(
            visible  = tagVisible,
            enter    = fadeIn(tween(800)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Created by",
                        fontSize   = 10.sp,
                        color      = Color.White.copy(alpha = 0.55f),
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        "Rupesh Subedi",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        (0..2).forEach { i ->
            val alpha by infiniteTransition.animateFloat(
                initialValue  = 0.3f,
                targetValue   = 1f,
                animationSpec = infiniteRepeatable(
                    animation          = tween(500, easing = LinearEasing),
                    repeatMode         = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(i * 160)
                ),
                label = "dot$i"
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .alpha(alpha)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}
