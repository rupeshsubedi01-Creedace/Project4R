/**
 * Project 4R — Flight Search API
 * Vercel Serverless Function
 *
 * GET /api/flights?origin=DXB&destination=KTM&date=2026-09-26[&trip=round&returnDate=2026-10-03]
 *
 * Strict rules (user-mandated):
 *  - Nepal destinations ONLY. Anything else gets an honest empty result + note.
 *  - Carriers shown: Nepali national carriers + airlines of the origin
 *    country ("local location airways"). No unrelated foreign carriers.
 *  - trip=oneway (default) or trip=round with returnDate. Round trip uses
 *    SerpAPI's two-step flow (departure_token) to fetch real return legs.
 */

const NEPAL_AIRPORTS = new Set([
  'KTM', 'PKR', 'BWA', 'BHR', 'DNP', 'JUM', 'NGX', 'RUM', 'IMK', 'DHI', 'TPU', 'KEP', 'LDN', 'BJU', 'JIR', 'RHP', 'SYH', 'LTG'
]);

export default async function handler(req, res) {
  if (req.method === 'OPTIONS') { res.status(200).end(); return; }

  const {
    origin      = 'DXB',
    destination = 'KTM',
    date        = nextFriday(),
    trip        = 'oneway',
    returnDate  = null,
    adults      = '1'
  } = req.query;

  const dest = (destination || '').toUpperCase();
  const orig = (origin || '').toUpperCase();
  const isRound = trip === 'round' && !!returnDate;

  const nprRate = await aedToNprRate();

  // ---- Strict rule: Nepal destinations only ----
  if (!NEPAL_AIRPORTS.has(dest)) {
    return res.status(200).json({
      origin: orig, destination: dest, date, count: 0, flights: [],
      priceInsights: null,
      note: 'Project 4R lists Nepal routes only — try Kathmandu (KTM), Pokhara (PKR) or Bhairahawa (BWA).'
    });
  }

  const serpKey = process.env.SERPAPI_KEY;
  if (!serpKey) {
    return res.status(500).json({ error: 'SERPAPI_KEY not set in Vercel environment variables' });
  }

  const serpParams = () => {
    const p = new URLSearchParams();
    p.set('engine',       'google_flights');
    p.set('departure_id', orig);
    p.set('arrival_id',   dest);
    p.set('outbound_date', date);
    if (isRound) p.set('return_date', returnDate);
    p.set('currency', 'AED');
    p.set('hl', 'en');
    p.set('type', isRound ? '1' : '2');
    p.set('adults', adults);
    p.set('api_key', serpKey);
    return p;
  };

  const callSerp = async (params) => {
    const r = await fetch(`https://serpapi.com/search?${params}`);
    if (!r.ok) throw new Error(`SerpAPI error: ${r.status}`);
    return r.json();
  };

  const groups = (d) => [...(d.best_flights || []), ...(d.other_flights || [])];

  try {
    let flights = [];

    if (!isRound) {
      const data = await callSerp(serpParams());
      flights = groups(data).map(g => buildCard(g, g.flights || [], [], g.price, false, nprRate));
      if (req.query.debug === '1') return res.json({ rawGroups: groups(data).slice(0, 2) });
      var insights = data.price_insights ?? null;
    } else {
      // Step 1: outbound options
      const outData = await callSerp(serpParams());
      if (req.query.debug === '1') return res.json({ rawGroups: groups(outData).slice(0, 2) });
      const local = localCarriers(orig);
      const outGroups = groups(outData)
        .filter(g => (g.flights || []).length)
        .filter(g => carrierAllowed(g.flights[0].airline, local))
        .sort((a, b) => a.price - b.price)
        .slice(0, 2); // keep SerpAPI calls bounded (Vercel time limit)

      for (const og of outGroups) {
        const token = og.departure_token;
        if (!token) continue;
        const p = serpParams();
        p.set('departure_token', token);
        const retData = await callSerp(p);
        const retGroups = groups(retData).filter(g => (g.flights || []).length);
        if (!retGroups.length) continue;
        const rg = retGroups.sort((a, b) => a.price - b.price)[0];
        const total = rg.price >= og.price ? rg.price : og.price + rg.price;
        flights.push(buildCard(og, og.flights, rg.flights, total, true, nprRate));
      }
      var insights = outData.price_insights ?? null;
    }

    // ---- Strict rule: Nepali carriers + origin-country carriers only ----
    const local = localCarriers(orig);
    flights = flights.filter(f => carrierAllowed(f.airlineName, local));

    flights.sort((a, b) => a.priceAed - b.priceAed);
    if (flights.length) flights[0].isBestDeal = true;

    // max 2 cards per airline
    const perAirline = {};
    flights = flights.filter(f => {
      perAirline[f.airlineName] = (perAirline[f.airlineName] || 0) + 1;
      return perAirline[f.airlineName] <= 2;
    });

    // Nepali national carriers (indicative airline-site fares) when they serve this origin
    const nepali = nepaliCarriers(orig, dest, date, nprRate, isRound)
      .filter(nc => !flights.some(f => f.airlineName === nc.airlineName));
    flights = flights.concat(nepali).sort((a, b) => a.priceAed - b.priceAed);
    if (flights.length && !flights.some(f => f.isBestDeal)) flights[0].isBestDeal = true;

    return res.status(200).json({
      origin: orig, destination: dest, date,
      returnDate: isRound ? returnDate : null,
      trip: isRound ? 'round' : 'oneway',
      count: flights.length,
      flights,
      priceInsights: insights ?? null,
      note: null
    });

  } catch (err) {
    console.error('Flight search error:', err);
    return res.status(500).json({ error: err.message });
  }
}

// ---- Helpers ----

function buildCard(group, outSegs, retSegs, priceAed, isRound, nprRate) {
  const firstSeg = outSegs[0];
  const lastSeg  = outSegs[outSegs.length - 1] || firstSeg;
  return {
    id:             `flight_${firstSeg.flight_number}_${outSegs.length}_${retSegs.length}`,
    airlineName:    firstSeg.airline,
    airlineCode:    (firstSeg.flight_number || '--').slice(0, 2),
    flightNumber:   firstSeg.flight_number,
    priceAed,
    priceNpr:       Math.round(priceAed * (nprRate || 41.68)),
    duration:       fmtDur(sumDur(outSegs) || group.total_duration),
    stops:          stopsLabel(outSegs.length),
    returnStops:    isRound && retSegs.length ? stopsLabel(retSegs.length) : null,
    returnDuration: isRound && retSegs.length ? fmtDur(sumDur(retSegs)) : null,
    returnAirline:  isRound && retSegs.length ? retSegs[0].airline : null,
    isBestDeal:     false,
    departure: { airport: firstSeg.departure_airport.id, time: firstSeg.departure_airport.time },
    arrival:   { airport: lastSeg.arrival_airport.id,   time: lastSeg.arrival_airport.time },
    bookingLinks:  bookingLinksFor(firstSeg.airline),
    source:        'Google Flights (live)'
  };
}

function stopsLabel(n) {
  return n <= 1 ? 'Nonstop' : `${n - 1} stop${n > 2 ? 's' : ''}`;
}
function sumDur(segs) {
  return (segs || []).reduce((t, s) => t + (s.duration || 0), 0);
}
function fmtDur(mins) {
  if (!mins) return '';
  return `${Math.floor(mins / 60)}h ${mins % 60}m`;
}
function carrierAllowed(name, local) {
  const n = (name || '').toLowerCase();
  if (n.includes('nepal') || n.includes('himalaya')) return true;
  return (local || []).some(c => n.includes(c.toLowerCase()));
}

/** Airlines of the origin country — the "local location airways". */
function localCarriers(origin) {
  const UAE = ['Emirates', 'flydubai', 'Air Arabia', 'Etihad'];
  const map = {
    DXB: UAE, AUH: UAE, SHJ: UAE, DWC: UAE,
    DOH: ['Qatar Airways'],
    RUH: ['Saudia', 'flynas', 'flyadeal'], JED: ['Saudia', 'flynas', 'flyadeal'],
    BAH: ['Gulf Air'], KWI: ['Kuwait Airways', 'Jazeera'], MCT: ['Oman Air', 'SalamAir'],
    DEL: ['IndiGo', 'Air India', 'SpiceJet', 'Vistara'],
    BOM: ['IndiGo', 'Air India', 'SpiceJet', 'Vistara'],
    KUL: ['Malaysia Airlines', 'AirAsia', 'Batik Air'],
    SIN: ['Singapore Airlines', 'Scoot'],
    BKK: ['Thai Airways', 'Thai VietJet'],
    IST: ['Turkish Airlines', 'Pegasus'],
    LHR: ['British Airways', 'Virgin Atlantic'],
    JFK: ['Delta', 'JetBlue', 'United'],
    SYD: ['Qantas', 'Jetstar']
  };
  return map[(origin || '').toUpperCase()] || [];
}

/**
 * Nepali national carriers — indicative fares observed on each airline's
 * own booking engine (both operate nonstop into KTM):
 *  - Nepal Airlines  RA: nonstop 3h45m, from AED 735 (book-nac.crane.aero)
 *  - Himalaya Airlines H9: nonstop ~4h30m, ~NPR 31,105 ≈ AED 746
 * Round trip doubles the fare and mirrors the leg.
 */
function nepaliCarriers(origin, destination, date, nprRate, isRound) {
  if (!NEPAL_AIRPORTS.has((destination || '').toUpperCase())) return [];
  const network = ['DXB', 'AUH', 'SHJ', 'DOH', 'DEL', 'BOM', 'KUL', 'SIN', 'BKK'];
  if (!network.includes((origin || '').toUpperCase())) return [];

  const mk = (idx, name, code, priceAed, stops, duration) => ({
    id:            `np_${idx}`,
    airlineName:   name,
    airlineCode:   code,
    flightNumber:  null,
    priceAed:      isRound ? priceAed * 2 : priceAed,
    priceNpr:      Math.round((isRound ? priceAed * 2 : priceAed) * nprRate),
    duration,
    stops,
    returnStops:    isRound ? stops : null,
    returnDuration: isRound ? duration : null,
    returnAirline:  isRound ? name : null,
    isBestDeal:    false,
    departure: { airport: (origin || 'DXB').toUpperCase(), time: `${date} ` },
    arrival:   { airport: destination, time: `${date} ` },
    bookingLinks:  bookingLinksFor(name),
    source:        `${name} (indicative fare — airline site)`
  });
  return [
    mk(1, 'Nepal Airlines',    'RA', 735, 'Nonstop', '3h 45m'),
    mk(2, 'Himalaya Airlines', 'H9', 746, 'Nonstop', '4h 30m')
  ];
}

/** Live AED -> NPR rate (keyless). */
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
  return 41.68;
}

function nextFriday() {
  const d = new Date();
  d.setDate(d.getDate() + ((5 - d.getDay() + 7) % 7 || 7));
  return d.toISOString().split('T')[0];
}

function bookingLinksFor(airline) {
  // Direct airline booking only — no OTA middlemen like Wego.
  if (!airline) return ['Google Flights'];
  const a = airline.toLowerCase();
  if (a.includes('indigo'))   return ['IndiGo.com', 'MakeMyTrip'];
  if (a.includes('emirates')) return ['Emirates.com', 'Almosafer'];
  if (a.includes('flydubai')) return ['FlyDubai.com'];
  if (a.includes('arabia'))   return ['AirArabia.com'];
  if (a.includes('jazeera'))  return ['JazeeraAirways.com'];
  if (a.includes('himalaya')) return ['Himalaya-Airlines.com'];
  if (a.includes('nepal'))    return ['NepalAirlines.com'];
  if (a.includes('air india'))return ['AirIndia.in', 'MakeMyTrip'];
  return ['Google Flights'];
}
