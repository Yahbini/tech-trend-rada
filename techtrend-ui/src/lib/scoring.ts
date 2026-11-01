import type { TopScore } from "@/types/score";

// Mirrors trend-scoring-service defaults (app.scoring.weights); display-only breakdown, the score itself comes from the API.
export const WEIGHTS = { velocity: 0.5, sourceCount: 0.3, categoryWeight: 0.2 } as const;

export interface ScoreParts {
  velocity: number;
  sourceCount: number;
  categoryWeight: number;
}

/** Min-max norm over the whole snapshot (same as the server, which normalizes over every technology). */
export function scoreParts(items: TopScore[]): Map<number, ScoreParts> {
  const norm = (values: number[]) => {
    const min = Math.min(...values);
    const max = Math.max(...values);
    return (x: number) => (max === min ? 0 : (x - min) / (max - min));
  };
  const velocityNorm = norm(items.map((i) => i.mentionVelocity));
  const sourceNorm = norm(items.map((i) => i.sourceCount));

  return new Map(
    items.map((i) => [
      i.technologyId,
      {
        velocity: WEIGHTS.velocity * velocityNorm(i.mentionVelocity),
        sourceCount: WEIGHTS.sourceCount * sourceNorm(i.sourceCount),
        categoryWeight: WEIGHTS.categoryWeight * i.categoryWeight,
      },
    ]),
  );
}
