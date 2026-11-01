// Exports /top, /top/hourly and per-technology /search once into public/data/snapshot.json;
// the static page reads this file instead of a live backend.
import { mkdir, writeFile } from "node:fs/promises";

const apiUrl = process.env.API_URL || "http://localhost:18082";
const target = new URL("../public/data/snapshot.json", import.meta.url);

// Retry 503 with a pause: right after trend-scoring restarts, the gateway's gRPC channel is still in reconnect backoff.
async function get(path) {
  for (let attempt = 1; ; attempt++) {
    const response = await fetch(apiUrl + path);
    if (response.ok) return response.json();
    if (attempt === 3 || response.status !== 503) {
      throw new Error(`GET ${path} -> HTTP ${response.status}: ${await response.text()}`);
    }
    await new Promise((resolve) => setTimeout(resolve, 2000));
  }
}

try {
  const items = await get("/top?limit=100");
  const hourlyItems = await get("/top/hourly?limit=20");

  // /search matches by name, so keep only the hit for the same technology id.
  const lastActive = {};
  for (const tech of items) {
    const hits = await get(`/search?q=${encodeURIComponent(tech.canonicalName)}&limit=10`);
    const own = hits.find((h) => h.technologyId === tech.technologyId);
    if (own) lastActive[tech.technologyId] = own;
  }

  const snapshot = {
    exportedAt: new Date().toISOString(),
    items,
    hourly: { windowStart: hourlyItems[0]?.windowStart ?? null, items: hourlyItems },
    lastActive,
  };
  await mkdir(new URL(".", target), { recursive: true });
  await writeFile(target, JSON.stringify(snapshot, null, 2) + "\n");
  console.log(
    `Saved ${items.length} scores, ${hourlyItems.length} hourly items, ${Object.keys(lastActive).length} last-active entries`,
  );
} catch (e) {
  console.error(e.message);
  process.exit(1);
}
