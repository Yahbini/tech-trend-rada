"use client";

import { Card, Col, Input, Row, Segmented, Space, Statistic, Table, Tag, Typography } from "antd";
import type { TableColumnsType } from "antd";
import { useMemo, useState } from "react";
import ScoreBreakdown from "@/components/ScoreBreakdown";
import HowScoreWorks from "@/components/HowScoreWorks";
import { scoreParts } from "@/lib/scoring";
import type { TopScore, TopSnapshot } from "@/types/score";

const ALL = "all";

const CATEGORY_COLORS: Record<string, string> = {
  language: "geekblue",
  database: "purple",
  framework: "blue",
  "ai-tool": "magenta",
  devops: "cyan",
  cloud: "green",
  messaging: "orange",
  testing: "lime",
  tool: "gold",
  other: "default",
};

export default function TrendingView({ snapshot }: { snapshot: TopSnapshot }) {
  const [category, setCategory] = useState<string>(ALL);
  const [query, setQuery] = useState("");

  // Breakdown must normalize over every technology, so compute it before filtering.
  const parts = useMemo(() => scoreParts(snapshot.items), [snapshot.items]);

  const categories = useMemo(() => {
    const present = new Set(snapshot.items.map((i) => i.category ?? "other"));
    return [ALL, ...Object.keys(CATEGORY_COLORS).filter((c) => present.has(c))];
  }, [snapshot.items]);

  const rows = useMemo(() => {
    const q = query.trim().toLowerCase();
    return snapshot.items.filter(
      (i) =>
        (category === ALL || (i.category ?? "other") === category) &&
        (q === "" || i.canonicalName.toLowerCase().includes(q)),
    );
  }, [snapshot.items, category, query]);

  const top = snapshot.items[0];
  const growing = snapshot.items.filter((i) => i.mentionVelocity > 0).length;

  const columns: TableColumnsType<TopScore> = [
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
      responsive: ["sm"],
      render: (c: string | null) => <Tag color={CATEGORY_COLORS[c ?? "other"]}>{c ?? "other"}</Tag>,
    },
    {
      title: "Score",
      dataIndex: "score",
      width: 240,
      render: (score: number, row) => <ScoreBreakdown score={score} parts={parts.get(row.technologyId)!} />,
    },
    {
      title: "Growth (7d)",
      dataIndex: "mentionVelocity",
      width: 110,
      align: "right",
      responsive: ["md"],
      render: (v: number) => (
        <Typography.Text type={v > 0 ? "success" : v < 0 ? "danger" : "secondary"}>
          {v > 0 ? "+" : ""}
          {Math.round(v * 100)}%
        </Typography.Text>
      ),
    },
    { title: "Sources", dataIndex: "sourceCount", width: 90, align: "right", responsive: ["md"] },
  ];

  return (
    <main style={{ maxWidth: 1080, margin: "0 auto", padding: "32px 16px 48px" }}>
      <Space direction="vertical" size={24} style={{ width: "100%" }}>
        <div>
          <Typography.Title level={2} style={{ marginBottom: 4 }}>
            Tech Trend Radar
          </Typography.Title>
          <Typography.Text type="secondary">
            Technologies ranked by a 7-day trend score, built from mentions on Hacker News, GitHub, dev.to and more.
          </Typography.Text>
        </div>

        <Row gutter={[16, 16]}>
          <Col xs={12} md={6}>
            <Card size="small">
              <Statistic title="Technologies tracked" value={snapshot.items.length} />
            </Card>
          </Col>
          <Col xs={12} md={6}>
            <Card size="small">
              <Statistic title="Growing this week" value={growing} />
            </Card>
          </Col>
          <Col xs={12} md={6}>
            <Card size="small">
              <Statistic title="Top trend" value={top?.canonicalName ?? "—"} valueStyle={{ fontSize: 20 }} />
            </Card>
          </Col>
          <Col xs={12} md={6}>
            <Card size="small">
              <Statistic
                title="Snapshot"
                value={new Date(snapshot.exportedAt).toISOString().slice(0, 10)}
                valueStyle={{ fontSize: 20 }}
              />
            </Card>
          </Col>
        </Row>

        <Card>
          <Space direction="vertical" size={16} style={{ width: "100%" }}>
            <Space wrap style={{ justifyContent: "space-between", width: "100%" }}>
              <div style={{ overflowX: "auto", maxWidth: "100%" }}>
                <Segmented options={categories} value={category} onChange={(v) => setCategory(String(v))} />
              </div>
              <Input.Search
                allowClear
                placeholder="Search technology"
                onChange={(e) => setQuery(e.target.value)}
                style={{ width: 220 }}
              />
            </Space>
            <Table<TopScore>
              rowKey="technologyId"
              columns={columns}
              dataSource={rows}
              size="middle"
              pagination={{ pageSize: 15, hideOnSinglePage: true }}
              scroll={{ x: "max-content" }}
            />
          </Space>
        </Card>

        <HowScoreWorks />

        <Typography.Text type="secondary" style={{ fontSize: 12 }}>
          Snapshot exported {new Date(snapshot.exportedAt).toUTCString()} from the live <code>GET /top</code> API.
        </Typography.Text>
      </Space>
    </main>
  );
}
