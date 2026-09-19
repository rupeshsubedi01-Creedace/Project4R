# Project 4R — Vercel Backend API

Free serverless backend for real Google Flights data.

## Deploy to Vercel (free)

### Step 1 — Go to vercel.com
1. Sign up / log in with GitHub
2. Tap **Add New Project**
3. Import **Project4R** repo
4. Set **Root Directory** to `backend`
5. Tap **Deploy** ✓

### Step 2 — Add SerpAPI key
1. In Vercel dashboard → your project → **Settings** → **Environment Variables**
2. Add:
   - Name: `SERPAPI_KEY`
   - Value: your SerpAPI key from serpapi.com
3. Tap **Save** → **Redeploy**

### Step 3 — Test it
Open in browser:
```
https://your-app.vercel.app/api/flights?origin=DXB&destination=KTM&date=2026-09-26
https://your-app.vercel.app/api/health
```

### Step 4 — Add your Vercel URL to Android app
Tell Cred your Vercel URL and it will be added to the Android code!

## API Reference

### GET /api/flights
| Param | Default | Example |
|-------|---------|---------|
| origin | DXB | DXB |
| destination | KTM | KTM, BOM, LHR |
| date | next Friday | 2026-09-26 |
| adults | 1 | 1 |

### Response
```json
{
  "origin": "DXB",
  "destination": "KTM",
  "date": "2026-09-26",
  "count": 5,
  "flights": [
    {
      "airlineName": "IndiGo",
      "priceAed": 590,
      "priceNpr": 21546,
      "duration": "6h 30m",
      "stops": "1 stop",
      "isBestDeal": true,
      "bookingLinks": ["IndiGo.com", "MakeMyTrip"],
      "source": "Google Flights (live)"
    }
  ]
}
```
