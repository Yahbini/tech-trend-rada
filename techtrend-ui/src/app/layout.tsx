import type { Metadata } from "next";
import { AntdRegistry } from "@ant-design/nextjs-registry";
import ThemeProvider from "@/components/ThemeProvider";
import "./globals.css";

export const metadata: Metadata = {
  title: "Tech Trend Radar",
  description: "Which technologies are trending this week, scored from Hacker News, GitHub, dev.to and more.",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body>
        <AntdRegistry>
          <ThemeProvider>{children}</ThemeProvider>
        </AntdRegistry>
      </body>
    </html>
  );
}
