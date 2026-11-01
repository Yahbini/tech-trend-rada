"use client";

import { Descriptions, Typography } from "antd";
import { formatGrowth, formatWindow } from "@/lib/format";
import type { LastActive } from "@/types/score";

/** Expanded row: the technology's own latest 1h window, taken from GET /search at snapshot time. */
export default function LastActivePanel({ lastActive }: { lastActive?: LastActive }) {
  if (!lastActive) {
    return <Typography.Text type="secondary">No hourly activity recorded for this technology yet.</Typography.Text>;
  }
  return (
    <Descriptions size="small" column={{ xs: 1, sm: 3 }} title="Last active hour">
      <Descriptions.Item label="Window">{formatWindow(lastActive.windowStart)}</Descriptions.Item>
      <Descriptions.Item label="Mentions">{lastActive.mentionCount}</Descriptions.Item>
      <Descriptions.Item label="vs. previous hour">
        {lastActive.mentionVelocity == null ? "—" : formatGrowth(lastActive.mentionVelocity)}
      </Descriptions.Item>
    </Descriptions>
  );
}
