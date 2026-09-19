package com.project4r.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project4r.data.UserPreferences
import com.project4r.ui.theme.Green600
import com.project4r.ui.theme.GreenCard

data class CountryOption(
    val name: String,
    val flag: String,
    val currency: String,
    val airportCode: String = ""
)

val workCountries = listOf(
    CountryOption("United Arab Emirates", "\uD83C\uDDE6\uD83C\uDDEA", "AED", "DXB"),
    CountryOption("Saudi Arabia",         "\uD83C\uDDF8\uD83C\uDDE6", "SAR", "RUH"),
    CountryOption("Qatar",                "\uD83C\uDDF6\uD83C\uDDE6", "QAR", "DOH"),
    CountryOption("Kuwait",              "\uD83C\uDDF0\uD83C\uDDFC", "KWD", "KWI"),
    CountryOption("United Kingdom",       "\uD83C\uDDEC\uD83C\uDDE7", "GBP", "LHR"),
    CountryOption("United States",        "\uD83C\uDDFA\uD83C\uDDF8", "USD", "JFK"),
    CountryOption("Australia",            "\uD83C\uDDE6\uD83C\uDDFA", "AUD", "SYD"),
    CountryOption("Canada",              "\uD83C\uDDE8\uD83C\uDDE6", "CAD", "YYZ"),
    CountryOption("Germany",             "\uD83C\uDDE9\uD83C\uDDEA", "EUR", "FRA"),
    CountryOption("Japan",               "\uD83C\uDDEF\uD83C\uDDF5", "JPY", "NRT"),
    CountryOption("South Korea",         "\uD83C\uDDF0\uD83C\uDDF7", "KRW", "ICN"),
    CountryOption("Malaysia",            "\uD83C\uDDF2\uD83C\uDDFE", "MYR", "KUL"),
    CountryOption("Singapore",           "\uD83C\uDDF8\uD83C\uDDEC", "SGD", "SIN"),
)

val homeCountries = listOf(
    CountryOption("Nepal",               "\uD83C\uDDF3\uD83C\uDDF5", "NPR", "KTM"),
    CountryOption("India",               "\uD83C\uDDEE\uD83C\uDDF3", "INR", "DEL"),
    CountryOption("Philippines",         "\uD83C\uDDF5\uD83C\uDDED", "PHP", "MNL"),
    CountryOption("Pakistan",            "\uD83C\uDDF5\uD83C\uDDF0", "PKR", "KHI"),
    CountryOption("Bangladesh",          "\uD83C\uDDE7\uD83C\uDDE9", "BDT", "DAC"),
    CountryOption("Sri Lanka",           "\uD83C\uDDF1\uD83C\uDDF0", "LKR", "CMB"),
    CountryOption("Indonesia",           "\uD83C\uDDEE\uD83C\uDDE9", "IDR", "CGK"),
    CountryOption("Egypt",               "\uD83C\uDDEA\uD83C\uDDEC", "EGP", "CAI"),
    CountryOption("Nigeria",             "\uD83C\uDDF3\uD83C\uDDEC", "NGN", "LOS"),
    CountryOption("Kenya",               "\uD83C\uDDF0\uD83C\uDDEA", "KES", "NBO"),
    CountryOption("Ethiopia",            "\uD83C\uDDEA\uD83C\uDDF9", "ETB", "ADD"),
    CountryOption("Ghana",               "\uD83C\uDDEC\uD83C\uDDED", "GHS", "ACC"),
    CountryOption("Mexico",              "\uD83C\uDDF2\uD83C\uDDFD", "MXN", "MEX"),
    CountryOption("Brazil",              "\uD83C\uDDE7\uD83C\uDDF7", "BRL", "GRU"),
)

@Composable
fun OnboardingScreen(
    prefs: UserPreferences,
    onFinished: () -> Unit
) {
    var step by remember { mutableStateOf(0) } // 0 = work country, 1 = home country
    var selectedWork by remember { mutableStateOf<CountryOption?>(null) }
    var selectedHome by remember { mutableStateOf<CountryOption?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF052E16), Color(0xFF15803D))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            // Step indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(2) { i ->
                    Box(
                        modifier = Modifier
                            .height(4.dp)
                            .width(if (i == step) 32.dp else 16.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (i <= step) Color.White
                                else Color.White.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                }, label = "step"
            ) { currentStep ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        if (currentStep == 0) "\uD83D\uDCBC" else "\uD83C\uDFE0",
                        fontSize = 48.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        if (currentStep == 0)
                            "Where do you work?"
                        else
                            "Where is home?",
                        fontSize   = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.White,
                        textAlign  = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        if (currentStep == 0)
                            "Select your current country"
                        else
                            "Select your home country",
                        fontSize  = 14.sp,
                        color     = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Country list
            val options = if (step == 0) workCountries else homeCountries
            val selected = if (step == 0) selectedWork else selectedHome

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp)),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(options) { country ->
                    val isSelected = selected?.name == country.name
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) Color.White
                                else Color.White.copy(alpha = 0.1f)
                            )
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) Green600 else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                if (step == 0) selectedWork = country
                                else selectedHome = country
                            }
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(country.flag, fontSize = 28.sp)
                            Column(Modifier.weight(1f)) {
                                Text(
                                    country.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize   = 15.sp,
                                    color      = if (isSelected) Color(0xFF111827) else Color.White
                                )
                                Text(
                                    country.currency,
                                    fontSize = 12.sp,
                                    color    = if (isSelected) Green600
                                              else Color.White.copy(alpha = 0.6f)
                                )
                            }
                            if (isSelected) {
                                Text("\u2713", fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Green600)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Continue / Done button
            Button(
                onClick = {
                    if (step == 0 && selectedWork != null) {
                        step = 1
                    } else if (step == 1 && selectedHome != null) {
                        // Save preferences
                        selectedWork?.let {
                            prefs.workCountryName  = it.name
                            prefs.workCurrencyCode = it.currency
                            prefs.workCurrencyFlag = it.flag
                        }
                        selectedHome?.let {
                            prefs.homeCountryName  = it.name
                            prefs.homeCurrencyCode = it.currency
                            prefs.homeCurrencyFlag = it.flag
                            prefs.homeAirportCode  = it.airportCode
                        }
                        prefs.onboardingDone = true
                        onFinished()
                    }
                },
                enabled = if (step == 0) selectedWork != null else selectedHome != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape  = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor   = Green600
                )
            ) {
                Text(
                    if (step == 0) "Continue \u2192" else "Let's Go! \uD83D\uDE80",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 16.sp
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
