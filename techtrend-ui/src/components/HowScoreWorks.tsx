"use client";

import { Card, Col, Row, Typography } from "antd";
import { PART_LABELS } from "@/components/ScoreBreakdown";
import { WEIGHTS } from "@/lib/scoring";

const PART_TEXT: Record<string, string> = {
  velocity: "How fast mentions grew vs. the previous 7 days.",
  sourceCount: "How many different sites mention it, so one noisy source can't fake a trend.",
  categoryWeight: "A small boost by category (e.g. frameworks over generic tools).",
};

const PIPELINE = ["n8n crawlers", "Kafka + Avro", "Kafka Streams", "MySQL", "trend-scoring (gRPC)", "API gateway /top"];

export default function HowScoreWorks() {
  return (
    <Card title="How the score works">
      <Typography.Paragraph>
        <code>
          score = {WEIGHTS.velocity}·norm(growth) + {WEIGHTS.sourceCount}·norm(sources) + {WEIGHTS.categoryWeight}
          ·categoryWeight
        </code>
        <br />
        <Typography.Text type="secondary">
          norm(x) = (x − min) / (max − min) across all technologies, so the score stays between 0 and 1.
        </Typography.Text>
      </Typography.Paragraph>
      <Row gutter={[16, 12]}>
        {PART_LABELS.map((p) => (
          <Col xs={24} md={8} key={p.key}>
            <Typography.Text strong>
              <span style={{ color: p.color }}>■</span> {p.label} ({WEIGHTS[p.key] * 100}%)
            </Typography.Text>
            <br />
            <Typography.Text type="secondary">{PART_TEXT[p.key]}</Typography.Text>
          </Col>
        ))}
      </Row>
      <Typography.Paragraph type="secondary" style={{ marginTop: 16, marginBottom: 0 }}>
        Pipeline: {PIPELINE.join(" → ")} → this page.
      </Typography.Paragraph>
    </Card>
  );
}
