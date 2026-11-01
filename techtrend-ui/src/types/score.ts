/** One item of GET /top (api-gateway TopScoreResponse). */
export interface TopScore {
  technologyId: number;
  canonicalName: string;
  category: string | null;
  score: number;
  mentionVelocity: number;
  sourceCount: number;
  categoryWeight: number;
  rank: number;
}

/** One item of GET /top/hourly (TopTechnologyResponse): mentions inside one 1h window. */
export interface HourlyTrend {
  technologyId: number;
  canonicalName: string;
  category: string | null;
  windowStart: string;
  mentionCount: number;
  mentionVelocity: number;
  rank: number;
}

/** One item of GET /search (SearchTechnologyResponse): the technology's own latest 1h window. */
export interface LastActive {
  technologyId: number;
  canonicalName: string;
  category: string | null;
  windowStart: string;
  mentionCount: number;
  mentionVelocity: number;
}

export interface TrendSnapshot {
  exportedAt: string;
  items: TopScore[];
  hourly: { windowStart: string | null; items: HourlyTrend[] };
  lastActive: Record<string, LastActive>;
}
