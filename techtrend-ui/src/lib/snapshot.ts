import data from "../../public/data/top.json";
import type { TopSnapshot } from "@/types/score";

// Bundled at build time, so the exported page needs no fetch and no backend.
export const snapshot = data as TopSnapshot;
