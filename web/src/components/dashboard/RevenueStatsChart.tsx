import { useState, useCallback, useEffect } from "react";
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";

// ─── Types ────────────────────────────────────────────────────────────────────

interface DailyRevenueStat {
  date: string;
  revenue: number;
}

interface TooltipPayload {
  value: number;
}

interface CustomTooltipProps {
  active?: boolean;
  payload?: TooltipPayload[];
  label?: string;
}

// ─── Constants ────────────────────────────────────────────────────────────────

const MONTHS = [
  "Tháng 1",
  "Tháng 2",
  "Tháng 3",
  "Tháng 4",
  "Tháng 5",
  "Tháng 6",
  "Tháng 7",
  "Tháng 8",
  "Tháng 9",
  "Tháng 10",
  "Tháng 11",
  "Tháng 12",
];

const YEARS = [2024, 2025, 2026];

// ─── Formatter ────────────────────────────────────────────────────────────────

function formatVND(value: number): string {
  if (value >= 1_000_000) return `${(value / 1_000_000).toFixed(1)}M`;
  if (value >= 1_000) return `${(value / 1_000).toFixed(0)}K`;
  return `${value}`;
}

function formatFullVND(value: number): string {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(value);
}

// ─── Custom Tooltip ───────────────────────────────────────────────────────────

function CustomTooltip({ active, payload, label }: CustomTooltipProps) {
  if (!active || !payload?.length) return null;
  return (
    <div className="rounded border border-gray-200 bg-white px-3 py-2 shadow-md">
      <p className="text-xs text-gray-500">Ngày {label}</p>
      <p className="text-sm font-semibold text-[#28a745]">
        {formatFullVND(payload[0].value)}
      </p>
    </div>
  );
}

// ─── Main Component ───────────────────────────────────────────────────────────

export default function RevenueStatsChart() {
  const currentDate = new Date();
  const [selectedMonth, setSelectedMonth] = useState(currentDate.getMonth());
  const [selectedYear, setSelectedYear] = useState(currentDate.getFullYear());
  const [data, setData] = useState<DailyRevenueStat[]>([]);
  const [totalRevenue, setTotalRevenue] = useState(0);
  const [loading, setLoading] = useState(false);

  const API_URL = import.meta.env.VITE_API_URL;
  const token = localStorage.getItem("token");

  const fetchStats = useCallback(async () => {
    setLoading(true);
    try {
      const month = selectedMonth + 1;
      const res = await fetch(
        `${API_URL}/api/admin/dashboard/revenue-stats?month=${month}&year=${selectedYear}`,
        { headers: { Authorization: `Bearer ${token}` } },
      );
      if (!res.ok) throw new Error("Lỗi khi tải dữ liệu");
      const json: DailyRevenueStat[] = await res.json();
      setData(json);
      setTotalRevenue(json.reduce((sum, item) => sum + item.revenue, 0));
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [selectedMonth, selectedYear]);

  useEffect(() => {
    fetchStats();
  }, []);

  return (
    <div className="border-outline-variant hover:border-primary border bg-white p-12 transition-colors duration-300">
      {/* ── Header ── */}
      <div className="mb-6 flex flex-col items-start justify-between gap-4 md:flex-row md:items-center">
        <div>
          <h3 className="font-title-md text-title-md text-primary tracking-wider uppercase">
            THỐNG KÊ DOANH THU
          </h3>
          {!loading && totalRevenue > 0 && (
            <p className="mt-1 text-sm text-gray-500">
              Tổng tháng:{" "}
              <span className="font-semibold text-[#28a745]">
                {formatFullVND(totalRevenue)}
              </span>
            </p>
          )}
        </div>

        <div className="flex w-full items-center gap-2 md:w-auto">
          <select
            value={selectedMonth}
            onChange={(e) => setSelectedMonth(Number(e.target.value))}
            className="border-outline-variant text-body-base focus:ring-primary min-w-[120px] border px-4 py-2 outline-none focus:ring-2"
          >
            {MONTHS.map((label, idx) => (
              <option key={idx} value={idx}>
                {label}
              </option>
            ))}
          </select>

          <select
            value={selectedYear}
            onChange={(e) => setSelectedYear(Number(e.target.value))}
            className="border-outline-variant text-body-base focus:ring-primary min-w-[100px] border px-4 py-2 outline-none focus:ring-2"
          >
            {YEARS.map((y) => (
              <option key={y} value={y}>
                {y}
              </option>
            ))}
          </select>

          <button
            onClick={fetchStats}
            disabled={loading}
            className="bg-[#28a745] px-6 py-2 font-bold text-white transition-colors duration-200 hover:bg-[#218838] disabled:opacity-60"
          >
            {loading ? "Đang tải..." : "Thống Kê"}
          </button>
        </div>
      </div>

      {/* ── Chart ── */}
      {loading ? (
        <div className="flex h-[320px] items-center justify-center text-gray-400">
          Đang tải dữ liệu...
        </div>
      ) : (
        <ResponsiveContainer width="100%" height={320}>
          <AreaChart
            data={data}
            margin={{ top: 10, right: 10, left: 10, bottom: 0 }}
          >
            <defs>
              <linearGradient id="revenueGradient" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor="#28a745" stopOpacity={0.18} />
                <stop offset="95%" stopColor="#28a745" stopOpacity={0} />
              </linearGradient>
            </defs>

            <CartesianGrid
              strokeDasharray="4 4"
              vertical={false}
              stroke="#e5e7eb"
            />

            <XAxis
              dataKey="date"
              tick={{ fontSize: 11, fill: "#9ca3af" }}
              axisLine={false}
              tickLine={false}
            />

            <YAxis
              tick={{ fontSize: 11, fill: "#9ca3af" }}
              axisLine={false}
              tickLine={false}
              width={50}
              tickFormatter={formatVND}
            />

            <Tooltip content={<CustomTooltip />} />

            <Area
              type="monotone"
              dataKey="revenue"
              stroke="#28a745"
              strokeWidth={2.5}
              fill="url(#revenueGradient)"
              dot={{ r: 3, fill: "#fff", stroke: "#28a745", strokeWidth: 2 }}
              activeDot={{ r: 5, fill: "#28a745" }}
            />
          </AreaChart>
        </ResponsiveContainer>
      )}
    </div>
  );
}
