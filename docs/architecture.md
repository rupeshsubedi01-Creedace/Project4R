# Project 4R — Architecture

## MVVM + Repository Pattern

```
UI Layer          (Jetpack Compose screens)
     ↓ observes StateFlow
ViewModel Layer   (RouteViewModel, CurrencyViewModel)
     ↓ calls
Repository Layer  (FlightRepository, CurrencyRepository)
     ↓ calls
API / Data Layer  (AmadeusApi, CurrencyApi via Retrofit)
```

## Dependency Injection — Hilt
All repositories and API interfaces are injected via `@Singleton` Hilt bindings in `AppModule`.

## NLP Pipeline
```
User text input
     ↓
NLPParser.parse()
     ↓ returns ParsedIntent
ViewModel uses intent fields
     ↓
Repository calls API or returns cached data
```

## Dual Calendar
- All dates stored internally as **Gregorian**
- `BiksramSambat.toBS(date)` converts on display
- Dubai timezone: `Asia/Dubai` (GST = UTC+4)
- Nepal timezone: `Asia/Kathmandu` (NPT = UTC+5:45)

## API Keys
Loaded at build time from `local.properties` via `BuildConfig`.
Never committed to git.
