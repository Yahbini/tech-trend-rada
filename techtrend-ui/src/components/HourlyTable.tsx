"use client";

import { Empty, Table, Tag, Typography } from "antd";
import type { TableColumnsType } from "antd";
import { CATEGORY_COLORS, formatGrowth, formatWindow } from "@/lib/format";
import type { HourlyTrend } from "@/types/score";

/** Ranking inside the latest closed 1h window (GET /top/hourly), by mention count. */
export default function HourlyTable({ windowStart, items }: { windowStart: string | null; items: HourlyTrend[] }) {
  const columns: TableColumnsType<HourlyTrend> = [
    { title: "#", dataIndex: "rank", width: 56, align: "right" },
    {
      title: "Technology",
      dataIndex: "canonicalName",
      render: (name: string) => <Typography.Text strong>{name}</Typography.Text>,
    },
    {
      title: "Category",
      dataIndex: "category",
      width: 120,
      render: (c: string | null) => <Tag color={CATEGORY_COLORS[c ?? "other"]}>{c ?? "other"}</Tag>,
    },
    { title: "Mentions", dataIndex: "mentionCount", width: 100, align: "right" },
    {
      title: "vs. previous hour",
      dataIndex: "mentionVelocity",
      width: 150,
      align: "right",
      render: (v: number | null) => (v == null ? "—" : formatGrowth(v)),
    },
  ];

  return (
    <>
      <Typography.Paragraph type="secondary">
        Mentions inside the latest closed 1-hour window: <Typography.Text strong>{formatWindow(windowStart)}</Typography.Text>.
        Hourly data only exists while crawlers are running, so this window can be older than the snapshot.
      </Typography.Paragraph>
      <Table<HourlyTrend>
        rowKey="technologyId"
        columns={columns}
        dataSource={items}
        size="middle"
        pagination={false}
        scroll={{ x: "max-content" }}
        locale={{ emptyText: <Empty description="No hourly window in this snapshot" /> }}
      />
    </>
  );
}
