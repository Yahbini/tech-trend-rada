// Exports GET /top once into public/data/top.json; the static page reads this file instead of a live backend.
import { mkdir, writeFile } from "node:fs/promises";

const apiUrl = process.env.API_URL || "http://localhost:18082";
const url = `${apiUrl}/top?limit=100`;
const target = new URL("../public/data/top.json", import.meta.url);

const response = await fetch(url);
if (!response.ok) {
  console.error(`GET ${url} -> HTTP ${response.status}: ${await response.text()}`);
  process.exit(1);
}
const items = await response.json();

await mkdir(new URL(".", target), { recursive: true });
await writeFile(target, JSON.stringify({ exportedAt: new Date().toISOString(), items }, null, 2) + "\n");
console.log(`Saved ${items.length} technologies from ${url}`);
