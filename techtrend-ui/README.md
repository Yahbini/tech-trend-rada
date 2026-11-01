# techtrend-ui

Trending page (Next.js + Ant Design), exported as a static site. It reads a JSON snapshot of `GET /top` instead of calling the backend live, so it can be hosted for free.

## Commands (pnpm only)

| Command | What it does |
|---|---|
| `pnpm install` | Install deps (shared store `D:\workspace\dev-common\pnpm-store`) |
| `pnpm snapshot` | Call `GET /top?limit=100` on the gateway (`API_URL`, default `http://localhost:18082`) and write `public/data/top.json`. trend-scoring + api-gateway must be running |
| `pnpm dev` | Dev server at http://localhost:3000 |
| `pnpm build` | Static export to `out/` (set `NEXT_PUBLIC_BASE_PATH` when hosting under a sub-path, e.g. GitHub Pages) |

## Structure

- `src/app` — layout + the single page
- `src/components` — `TrendingView` (filter, table), `ScoreBreakdown` (stacked bar of the 3 weighted parts), `HowScoreWorks`, `ThemeProvider` (follows OS light/dark)
- `src/lib/scoring.ts` — recomputes the 3 parts for display; weights mirror `trend-scoring-service` (`app.scoring.weights`), update both if they change
- `scripts/snapshot.mjs` — exports the snapshot
