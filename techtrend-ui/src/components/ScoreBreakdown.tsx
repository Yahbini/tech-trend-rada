"use client";

import { Tooltip, theme } from "antd";
import type { ScoreParts } from "@/lib/scoring";

const PART_LABELS: { key: keyof ScoreParts; label: string; color: string }[] = [
  { key: "velocity", label: "Mention growth", color: "#2f6fed" },
  { key: "sourceCount", label: "Independent sources", color: "#13a8a8" },
  { key: "categoryWeight", label: "Category weight", color: "#d48806" },
];

/** Stacked bar of the three weighted parts; full width = score 1.0. */
export default function ScoreBreakdown({ score, parts }: { score: number; parts: ScoreParts }) {
  const { token } = theme.useToken();

  const tooltip = (
    <div>
      {PART_LABELS.map((p) => (
        <div key={p.key}>
          <span style={{ color: p.color }}>■</span> {p.label}: {parts[p.key].toFixed(3)}
        </div>
      ))}
      <div style={{ marginTop: 4, fontWeight: 600 }}>Score: {score.toFixed(3)}</div>
    </div>
  );

  return (
    <Tooltip title={tooltip}>
      <div style={{ display: "flex", alignItems: "center", gap: 8, minWidth: 160 }}>
        <div
          style={{
            flex: 1,
            height: 10,
            borderRadius: 5,
            overflow: "hidden",
            display: "flex",
            background: token.colorFillSecondary,
          }}
        >
          {PART_LABELS.map((p) => (
            <div key={p.key} style={{ width: `${parts[p.key] * 100}%`, background: p.color }} />
          ))}
        </div>
        <span style={{ fontVariantNumeric: "tabular-nums", fontWeight: 600, width: 44, textAlign: "right" }}>
          {score.toFixed(3)}
        </span>
      </div>
    </Tooltip>
  );
}

export { PART_LABELS };
