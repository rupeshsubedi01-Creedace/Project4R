/**
 * Project 4R — Flight Search API
 * Vercel Serverless Function
 *
 * GET /api/flights?origin=DXB&destination=KTM&date=2026-09-26
 *
 * Uses SerpAPI Google Flights under the hood.
 * Set SERPAPI_KEY in Vercel Environment Variables.
 */

export default async function handler(req, res) {
  // CORS preflight
  if (req.method === 'OPTIONS') {
    res.status(200).end();
    return;
  }

  const {
    origin      = 'DXB',
    destination = 'KTM',
    date        = nextFriday(),
    adults      = '1'
  } = req.query;

  const serpKey = process.env.SERPAPI_KEY;

  // Live AED->NPR from the keyless ExchangeRate-API public mirror.
  const nprRate = await aedToNprRate();

  if (!serpKey) {
    return res.status(500).json({
      error: 'SERPAPI_KEY not set in Vercel environment variables'
    });
  }

  try {
    const serpUrl = new URL('https://serpapi.com/search');
    serpUrl.searchParams.set('engine',        'google_flights');
    serpUrl.searchParams.set('departure_id',  origin.toUpperCase());
    serpUrl.searchParams.set('arrival_id',    destination.toUpperCase());
    serpUrl.searchParams.set('outbound_date', date);
    serpUrl.searchParams.set('currency',      'AED');
    serpUrl.searchParams.set('hl',            'en');
    serpUrl.searchParams.set('type',          '2');   // one-way
    serpUrl.searchParams.set('adults',        adults);
    serpUrl.searchParams.set('api_key',       serpKey);

    const serpResp = await fetch(serpUrl.toString());
    if (!serpResp.ok) {
      throw new Error(`SerpAPI error: ${serpResp.status}`);
    }

    const data = await serpResp.json();

    // Combine best + other flights
    const allGroups = [
      ...(data.best_flights  || []),
      ...(data.other_flights || [])
    ];

    const flights = allGroups.map((group, idx) => {
      const firstSeg = group.flights[0];
      const lastSeg  = group.flights[group.flights.length - 1];
      const stops    = group.flights.length === 1
        ? 'Direct'
        : `${group.flights.length - 1} stop${group.flights.length > 2 ? 's' : ''}`;

      const hrs  = Math.floor(group.total_duration / 60);
      const mins = group.total_duration % 60;
      const priceAed = group.price;
      const priceNpr = Math.round(priceAed * nprRate);

      return {
        id:            `flight_${idx}`,
        airlineName:   firstSeg.airline,
        airlineCode:   firstSeg.flight_number?.slice(0, 2) ?? '--',
        flightNumber:  firstSeg.flight_number,
        priceAed,
        priceNpr,
        duration:      `${hrs}h ${mins}m`,
        stops,
        isBestDeal:    idx === 0,
        departure: {
          airport: firstSeg.departure_airport.id,
          time:    firstSeg.departure_airport.time
        },
        arrival: {
          airport: lastSeg.arrival_airport.id,
          time:    lastSeg.arrival_airport.time
        },
        bookingLinks:  bookingLinksFor(firstSeg.airline),
        source:        'Google Flights (live)'
      };
    }).sort((a, b) => a.priceAed - b.priceAed);

    return res.status(200).json({
      origin,
      destination,
      date,
      count:   flights.length,
      flights,
      priceInsights: data.price_insights ?? null
    });

  } catch (err) {
    console.error('Flight search error:', err);
    return res.status(500).json({ error: err.message });
  }
}

// ---- Helpers ----

/**
 * Live AED -> NPR rate (keyless, no env var needed).
 * Falls back to a recent snapshot if the rate service is unreachable.
 */
async function aedToNprRate() {
  try {
    const r = await fetch('https://open.er-api.com/v6/latest/AED');
    if (r.ok) {
      const d = await r.json();
      const npr = d && d.rates && d.rates.NPR;
      if (typeof npr === 'number' && npr > 1) return npr;
    }
  } catch (e) {
    console.error('NPR rate fetch failed, using fallback:', e);
  }
  return 41.68; // Sept-2026 snapshot fallback
}

function nextFriday() {
  const d = new Date();
  d.setDate(d.getDate() + ((5 - d.getDay() + 7) % 7 || 7));
  return d.toISOString().split('T')[0];
}

function bookingLinksFor(airline) {
  if (!airline) return ['Wego', 'Google Flights'];
  const a = airline.toLowerCase();
  if (a.includes('indigo'))   return ['IndiGo.com', 'MakeMyTrip'];
  if (a.includes('emirates')) return ['Emirates.com', 'Almosafer'];
  if (a.includes('flydubai')) return ['FlyDubai.com', 'Wego'];
  if (a.includes('arabia'))   return ['AirArabia.com', 'Wego'];
  if (a.includes('jazeera'))  return ['JazeeraAirways.com', 'Wego'];
  if (a.includes('nepal'))    return ['NepalAirlines.com', 'Wego'];
  if (a.includes('air india'))return ['AirIndia.in', 'MakeMyTrip'];
  return ['Wego', 'Google Flights'];
}
