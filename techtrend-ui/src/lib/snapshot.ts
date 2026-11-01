import data from "../../public/data/snapshot.json";
import type { TrendSnapshot } from "@/types/score";

// Bundled at build time, so the exported page needs no fetch and no backend.
export const snapshot = data as TrendSnapshot;
