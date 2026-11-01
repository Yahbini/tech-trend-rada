"use client";

import { ConfigProvider, theme } from "antd";
import { useEffect, useState } from "react";

// Follows the OS light/dark setting; AntD needs the algorithm switched in JS, CSS alone is not enough.
export default function ThemeProvider({ children }: { children: React.ReactNode }) {
  const [dark, setDark] = useState(false);

  useEffect(() => {
    const query = window.matchMedia("(prefers-color-scheme: dark)");
    setDark(query.matches);
    const onChange = (e: MediaQueryListEvent) => setDark(e.matches);
    query.addEventListener("change", onChange);
    return () => query.removeEventListener("change", onChange);
  }, []);

  return (
    <ConfigProvider
      theme={{
        algorithm: dark ? theme.darkAlgorithm : theme.defaultAlgorithm,
        token: { colorPrimary: "#2f6fed", borderRadius: 8 },
      }}
    >
      {children}
    </ConfigProvider>
  );
}
