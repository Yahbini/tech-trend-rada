import type { NextConfig } from "next";

// Static export: the page reads a JSON snapshot of GET /top, so it can be hosted for free without a backend.
const nextConfig: NextConfig = {
  output: "export",
  basePath: process.env.NEXT_PUBLIC_BASE_PATH || "",
  images: { unoptimized: true },
};

export default nextConfig;
