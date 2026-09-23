package com.project4r.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Reset — a real, working box-breathing session (4-4-4-4).
 * No mock content: the orb, phases and cycle counter are live.
 */
@Composable
fun ResetScreen() {
    var running  by remember { mutableStateOf(false) }
    var phaseIdx by remember { mutableStateOf(-1) }
    var cycles   by remember { mutableStateOf(0) }

    val phases = listOf("Inhale", "Hold", "Exhale", "Hold")
    val inflated = running && (phaseIdx == 0 || phaseIdx == 1)

    val scale by animateFloatAsState(
        targetValue = when {
            !running -> 0.55f
            inflated -> 1.0f
            else     -> 0.62f
        },
        animationSpec = tween(durationMillis = 3500, easing = FastOutSlowInEasing),
        label = "breath"
    )

    LaunchedEffect(running) {
        if (!running) {
            phaseIdx = -1
            return@LaunchedEffect
        }
        cycles = 0
        while (true) {
            for (i in 0..3) {
                phaseIdx = i
                delay(4000)
            }
            cycles += 1
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Reset", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp,
            color = Color(0xFF111827))
        Text("Box breathing 4\u00b74\u00b74\u00b74 — pre-flight calm",
            fontSize = 13.sp, color = Color(0xFF6B7280))

        Spacer(Modifier.height(40.dp))

        Box(
            modifier = Modifier.size(220.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer { scaleX = scale; scaleY = scale }
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (!running) "\uD83E\uDDD8"
                    else phases[phaseIdx.coerceIn(0, 3)],
                    fontSize = if (running) 22.sp else 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            if (running) "Cycle ${cycles + 1}" else "Ready when you are",
            fontSize = 13.sp,
            color = Color(0xFF6B7280)
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { running = !running },
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text(
                if (running) "\u25A0 Stop Session" else "\u25B6 Start Session",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
