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

export interface TopSnapshot {
  exportedAt: string;
  items: TopScore[];
}
