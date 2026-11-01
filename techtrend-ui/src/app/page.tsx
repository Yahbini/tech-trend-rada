import TrendingView from "@/components/TrendingView";
import { snapshot } from "@/lib/snapshot";

export default function TrendingPage() {
  return <TrendingView snapshot={snapshot} />;
}
