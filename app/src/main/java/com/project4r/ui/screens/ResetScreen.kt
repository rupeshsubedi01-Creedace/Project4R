package com.project4r.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class WellnessOption(val icon: String, val label: String)

val wellnessOptions = listOf(
    WellnessOption("\uD83D\uDCE6", "Box Breathing"),
    WellnessOption("\u2708\uFE0F", "Flight Anxiety"),
    WellnessOption("\uD83C\uDF0D", "Jet Lag"),
    WellnessOption("\uD83E\uDEB7", "Pranayama"),
)

@Composable
fun ResetScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Orb
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("\uD83E\uDDD8", fontSize = 36.sp)
                    }
                    Spacer(Modifier.height(14.dp))
                    Text("Reset Your Mind",
                        fontWeight = FontWeight.ExtraBold, fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(
                        "Pre-flight calm \u00b7 Jet lag recovery \u00b7 Anxiety support",
                        fontSize = 12.sp, color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(18.dp))
                    Button(
                        onClick = { /* Start session */ },
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        Text("\u25B6 Start Session", fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        wellnessOptions.forEach { option ->
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(option.icon, fontSize = 20.sp)
                                    Spacer(Modifier.height(4.dp))
                                    Text(option.label, fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
