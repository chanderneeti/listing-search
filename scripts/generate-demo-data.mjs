// Generates backend/src/main/resources/data/demo_listings.json
// Run from the project root:  node scripts/generate-demo-data.mjs
// Fixed seed: the same file comes out every time.
import { mkdirSync, writeFileSync } from "node:fs";

const OUTPUT = "backend/src/main/resources/data/demo_listings.json";
const BASE_DATE = new Date("2026-11-10T00:00:00Z"); // listing dates count back from here

function mulberry32(seed) {
    return function () {
        seed = (seed + 0x6d2b79f5) | 0;
        let t = Math.imul(seed ^ (seed >>> 15), 1 | seed);
        t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t;
        return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
    };
}
const rand = mulberry32(42);
const pick = (items) => items[Math.floor(rand() * items.length)];
const between = (min, max) => min + Math.floor(rand() * (max - min + 1));
const daysAgo = (n) => new Date(BASE_DATE.getTime() - n * 86400000).toISOString().slice(0, 10);
const roundTo500 = (value) => Math.round(value / 500) * 500;

const cities = [
    { city: "Reston", zip: "20190", lat: 38.9586, lon: -77.357, base: 520000 },
    { city: "Fairfax", zip: "22030", lat: 38.8462, lon: -77.3064, base: 480000 },
    { city: "Vienna", zip: "22180", lat: 38.9012, lon: -77.2653, base: 650000 },
    { city: "Springfield", zip: "22150", lat: 38.7893, lon: -77.1873, base: 430000 },
    { city: "Manassas", zip: "20110", lat: 38.7509, lon: -77.4753, base: 380000 },
    { city: "Chantilly", zip: "20151", lat: 38.8943, lon: -77.4311, base: 560000 },
    { city: "Herndon", zip: "20170", lat: 38.9696, lon: -77.3861, base: 500000 },
    { city: "Alexandria", zip: "22304", lat: 38.8048, lon: -77.0469, base: 590000 },
];
const streets = ["Oak", "Maple", "Cedar", "Elm", "Pine", "Lake", "Hill", "Park", "Main", "Church"];
const suffixes = ["St", "Ave", "Rd", "Ln", "Ct"];
const longSuffix = { St: "Street", Ave: "Avenue", Rd: "Road", Ln: "Lane", Ct: "Court" };
const features = [
    "Bright rooms and updated kitchen.", "Fenced yard, close to schools.",
    "Walkable to shops and Metro.", "Quiet street with a large driveway.",
    "Renovated bathrooms, hardwood floors.", "Corner lot near parks and trails.",
    "New roof and fresh paint.", "Open floor plan with a sunny deck.",
];
const petNotes = [
    "Pets allowed.", "Pet friendly.", "Pets welcome.", "No pets.",
    "Pets not permitted.", "Pet restrictions apply.", "", "", "",
];

function status() {
    const r = rand();
    return r < 0.7 ? "active" : r < 0.85 ? "pending" : "sold";
}

function makeListing(source, id, c) {
    const bedrooms = between(1, 5);
    const unit = rand() < 0.3 ? `, Apt ${between(1, 9)}${pick(["A", "B", "C"])}` : "";
    return {
        id, source,
        address: `${between(100, 9899)} ${pick(streets)} ${pick(suffixes)}${unit}`,
        city: c.city, state: "VA", zip: c.zip,
        price: roundTo500(c.base * (0.6 + bedrooms * 0.18) * (0.9 + rand() * 0.2)),
        bedrooms,
        bathrooms: Math.min(bedrooms, between(1, 3)) + (rand() < 0.4 ? 0.5 : 0),
        sqft: bedrooms * 400 + between(0, 500),
        latitude: c.lat, longitude: c.lon,
        listedDate: daysAgo(between(0, 120)),
        status: status(),
        description: `${pick(features)} ${pick(petNotes)}`.trim(),
    };
}

const listings = [];

// 1. 120 listings from feed DEMO_A
for (let i = 1; i <= 120; i++) listings.push(makeListing("DEMO_A", `D${i}`, pick(cities)));

// 2. 20 near-duplicates of those listings, as another feed (DEMO_B) would report them
for (let i = 1; i <= 20; i++) {
    const original = listings[between(0, 119)];
    const dayShift = between(-3, 3);
    const date = new Date(original.listedDate + "T00:00:00Z");
    date.setUTCDate(date.getUTCDate() + dayShift);
    let address = original.address.replace("Apt", "Unit");
    for (const [short, long] of Object.entries(longSuffix)) {
        address = address.replace(new RegExp(` ${short}\\b`), ` ${long}`);
    }
    listings.push({
        ...original, id: `E${i}`, source: "DEMO_B", address,
        price: roundTo500(original.price * (0.995 + rand() * 0.01)),
        listedDate: date.toISOString().slice(0, 10),
        description: `${pick(features)} ${pick(petNotes)}`.trim(),
    });
}

// 3. Deliberate ties: 4 groups of 3 listings with the same city, price, date and description
for (let g = 1; g <= 4; g++) {
    const c = cities[g - 1];
    const shared = { price: 400000 + g * 25000, listedDate: daysAgo(10 * g) };
    for (let k = 1; k <= 3; k++) {
        const l = makeListing("DEMO_A", `T${g}${k}`, c);
        listings.push({ ...l, ...shared, bedrooms: 3, bathrooms: 2, sqft: 1500,
            status: "active", description: "Tie test home. Pets allowed." });
    }
}

mkdirSync("backend/src/main/resources/data", { recursive: true });
writeFileSync(OUTPUT, JSON.stringify(listings, null, 2) + "\n");
console.log(`Wrote ${listings.length} listings to ${OUTPUT}`);