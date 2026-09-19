# ✈️ Project 4R — AI-Powered Travel App

> **Route · Remind · Review · Reset**

An AI-powered travel companion for Dubai-based Nepali expatriates, built with Kotlin + Jetpack Compose.

---

## 🌟 Features

| Module | Description |
|--------|-------------|
| ✈️ **Route** | NLP flight search + real-time prices + booking links |
| 💱 **Currency** | Live AED→NPR/USD/INR/EUR rates via ExchangeRate-API |
| 🔔 **Remind** | Dual-calendar alerts (Gregorian + Bikram Sambat) |
| 📊 **Review** | Price history, trend charts, AI booking insights |
| 🧘 **Reset** | Travel wellness — box breathing, jet lag, Pranayama |

---

## 🧠 NLP Backbone

Users speak or type naturally — no forms:
- *"Cheapest flight to Kathmandu next Friday under AED 700"*
- *"Alert me when IndiGo drops below AED 500"*
- *"Convert 590 AED to NPR"*
- *"Remind me about Dashain 3 weeks before"*

---

## 🔧 Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose (White & Green design system)
- **Architecture:** MVVM + Repository pattern
- **Flight API:** [Amadeus](https://developers.amadeus.com) — free tier
- **Currency API:** [ExchangeRate-API](https://exchangerate-api.com) — free tier (1,500 req/month)
- **Calendar:** Gregorian + Bikram Sambat dual-engine
- **Timezone:** Asia/Dubai (GST UTC+4) ↔ Asia/Kathmandu (NPT UTC+5:45)
- **Storage:** Room DB (offline cache) + Firebase Firestore
- **Auth:** Google Sign-In / Phone OTP

---

## 📱 Sample Data

| Airline | Price | NPR | Book At |
|---------|-------|-----|---------|
| IndiGo ⭐ | AED 590 | NPR 21,546 | IndiGo.com / MakeMyTrip |
| FlyDubai | AED 710 | NPR 25,929 | FlyDubai.com / Wego |
| Emirates | AED 850 | NPR 31,042 | Emirates.com / Almosafer |
| Air Arabia | AED 920 | NPR 33,598 | AirArabia.com / Wego |

Arrival shown in **Bikram Sambat**: 3 Asoj 2083 · NPT time

---

## 🔑 Setup

1. Clone the repo:
```bash
git clone https://github.com/rupeshsubedi01-Creedace/Project4R.git
```
2. Add API keys to `local.properties`:
```
AMADEUS_CLIENT_ID=your_amadeus_key
AMADEUS_CLIENT_SECRET=your_amadeus_secret
EXCHANGE_API_KEY=your_exchangerate_api_key
```
3. Open in Android Studio → Run on device or emulator

---

## 🗺️ Roadmap

- [x] Phase 1: Route + Currency (MVP)
- [ ] Phase 2: Remind (BS calendar + alerts)
- [ ] Phase 3: Review (analytics + history)
- [ ] Phase 4: Reset (wellness sessions)
- [ ] Phase 5: Cross-module integration
- [ ] Phase 6: iOS port

---

## 👤 Author

**Rupesh Subedi** · Dubai, UAE

---

*Powered by Amadeus API · ExchangeRate-API · NLP Engine · Bikram Sambat · Jetpack Compose*
