export const CATEGORY_COLORS: Record<string, string> = {
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

/** "2026-10-19T11:00:00Z" -> "2026-10-19 11:00 UTC" (windows are UTC hours). */
export function formatWindow(iso: string | null | undefined): string {
  if (!iso) return "—";
  return iso.slice(0, 16).replace("T", " ") + " UTC";
}

export function formatGrowth(velocity: number): string {
  return `${velocity > 0 ? "+" : ""}${Math.round(velocity * 100)}%`;
}

/** "2026-10-19T11:00:00Z" -> "2026-10-19 18:00 (UTC+7)"; fixed offset so build machine timezone does not matter. */
export function formatWindowVn(iso: string | null | undefined): string {
  if (!iso) return "—";
  const vn = new Date(new Date(iso).getTime() + 7 * 3600 * 1000).toISOString();
  return vn.slice(0, 16).replace("T", " ") + " (UTC+7)";
}
