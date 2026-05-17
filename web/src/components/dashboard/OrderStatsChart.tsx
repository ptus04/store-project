// export default function OrderStatsChart() {
//   return (
//     <div className="border-outline-variant p-card-padding hover:border-primary border bg-white transition-colors duration-300">
//       <div className="mb-10 flex flex-col items-start justify-between gap-4 md:flex-row md:items-center">
//         <h3 className="font-title-md text-title-md text-primary tracking-wider uppercase">
//           THỐNG KÊ ĐƠN HÀNG
//         </h3>
//         <div className="gap-stack-sm flex w-full items-center md:w-auto">
//           <select className="border-outline-variant text-body-base focus:ring-primary min-w-[120px] border px-4 py-2 outline-none focus:ring-2">
//             <option>Tháng 6</option>
//             <option>Tháng 5</option>
//           </select>
//           <select className="border-outline-variant text-body-base focus:ring-primary min-w-[100px] border px-4 py-2 outline-none focus:ring-2">
//             <option>2026</option>
//             <option>2025</option>
//           </select>
//           <button className="bg-[#28a745] px-6 py-2 font-bold text-white transition-colors duration-200 hover:bg-[#218838]">
//             Thống Kê
//           </button>
//         </div>
//       </div>
//
//       {/* CHART AREA */}
//       <div className="relative h-[400px] w-full pt-10">
//         {/* Y Axis Labels */}
//         <div className="text-label-caps text-secondary absolute top-10 bottom-10 left-0 flex h-full min-w-[40px] flex-col justify-between pr-4 text-right font-semibold">
//           <span>200</span>
//           <span>160</span>
//           <span>120</span>
//           <span>80</span>
//           <span>40</span>
//           <span>0</span>
//         </div>
//
//         {/* Grid & Line Chart Container */}
//         <div className="chart-container border-outline relative ml-[60px] h-full border-b">
//           {/* SVG Line Chart */}
//           <svg
//             className="preserve-3d h-full w-full overflow-visible"
//             preserveAspectRatio="none"
//             viewBox="0 0 1000 320"
//           >
//             {/* Gradient Area under line */}
//             <defs>
//               <linearGradient id="areaGradient" x1="0" x2="0" y1="0" y2="1">
//                 <stop offset="0%" stopColor="#3498db" stopOpacity="0.1" />
//                 <stop offset="100%" stopColor="#3498db" stopOpacity="0" />
//               </linearGradient>
//             </defs>
//             <path
//               d="M 0 100 Q 20 220 40 210 Q 60 180 80 140 Q 100 80 120 180 Q 140 220 160 210 Q 180 200 200 80 Q 220 120 240 140 Q 260 150 280 140 Q 300 130 320 120 Q 340 210 360 200 Q 380 180 400 120 Q 420 80 440 120 Q 460 140 480 30 Q 500 180 520 200 Q 540 280 560 320 L 1000 320 L 0 320 Z"
//               fill="url(#areaGradient)"
//             />
//             {/* Main Data Line */}
//             <path
//               d="M 0 100 Q 20 220 40 210 Q 60 180 80 140 Q 100 80 120 180 Q 140 220 160 210 Q 180 200 200 80 Q 220 120 240 140 Q 260 150 280 140 Q 300 130 320 120 Q 340 210 360 200 Q 380 180 400 120 Q 420 80 440 120 Q 460 140 480 30 Q 500 180 520 200 Q 540 280 560 320 H 1000"
//               fill="none"
//               stroke="#3498db"
//               strokeLinecap="round"
//               strokeLinejoin="round"
//               strokeWidth="4"
//             />
//             {/* Data Points */}
//             <circle
//               cx="0"
//               cy="100"
//               r="4"
//               fill="white"
//               stroke="#3498db"
//               strokeWidth="2"
//             />
//             <circle
//               cx="40"
//               cy="210"
//               r="4"
//               fill="white"
//               stroke="#3498db"
//               strokeWidth="2"
//             />
//             <circle
//               cx="80"
//               cy="140"
//               r="4"
//               fill="white"
//               stroke="#3498db"
//               strokeWidth="2"
//             />
//             <circle
//               cx="120"
//               cy="180"
//               r="4"
//               fill="white"
//               stroke="#3498db"
//               strokeWidth="2"
//             />
//             <circle
//               cx="200"
//               cy="80"
//               r="4"
//               fill="white"
//               stroke="#3498db"
//               strokeWidth="2"
//             />
//             <circle
//               cx="480"
//               cy="30"
//               r="4"
//               fill="white"
//               stroke="#3498db"
//               strokeWidth="2"
//             />
//           </svg>
//         </div>
//
//         {/* X Axis Labels */}
//         <div className="ml-[60px] flex justify-between overflow-x-auto pt-4">
//           <span className="font-label-caps text-secondary min-w-[20px] text-[10px]">
//             D1
//           </span>
//           <span className="font-label-caps text-secondary min-w-[20px] text-[10px]">
//             D3
//           </span>
//           <span className="font-label-caps text-secondary min-w-[20px] text-[10px]">
//             D5
//           </span>
//           <span className="font-label-caps text-secondary min-w-[20px] text-[10px]">
//             D7
//           </span>
//           <span className="font-label-caps text-secondary min-w-[20px] text-[10px]">
//             D9
//           </span>
//           <span className="font-label-caps text-secondary min-w-[20px] text-[10px]">
//             D11
//           </span>
//           <span className="font-label-caps text-secondary min-w-[20px] text-[10px]">
//             D13
//           </span>
//           <span className="font-label-caps text-secondary min-w-[20px] text-[10px]">
//             D15
//           </span>
//           <span className="font-label-caps text-secondary min-w-[20px] text-[10px]">
//             D17
//           </span>
//           <span className="font-label-caps text-secondary min-w-[20px] text-[10px]">
//             D19
//           </span>
//           <span className="font-label-caps text-secondary min-w-[20px] text-[10px]">
//             D21
//           </span>
//           <span className="font-label-caps text-secondary min-w-[20px] text-[10px]">
//             D23
//           </span>
//           <span className="font-label-caps text-secondary min-w-[20px] text-[10px]">
//             D25
//           </span>
//           <span className="font-label-caps text-secondary min-w-[20px] text-[10px]">
//             D27
//           </span>
//           <span className="font-label-caps text-secondary min-w-[20px] text-[10px]">
//             D29
//           </span>
//           <span className="font-label-caps text-secondary min-w-[20px] text-[10px]">
//             D31
//           </span>
//         </div>
//       </div>
//     </div>
//   );
// }
// import { useState, useCallback } from "react";
// import {
//   AreaChart,
//   Area,
//   XAxis,
//   YAxis,
//   CartesianGrid,
//   Tooltip,
//   ResponsiveContainer,
// } from "recharts";
//
// // ─── Types ────────────────────────────────────────────────────────────────────
//
// interface DailyOrderStat {
//   date: string; // "01" → "31"
//   orders: number;
// }
//
// interface TooltipPayload {
//   value: number;
// }
//
// interface CustomTooltipProps {
//   active?: boolean;
//   payload?: TooltipPayload[];
//   label?: string;
// }
//
// // ─── Mock data (thay bằng data thật từ API) ───────────────────────────────────
//
// const MOCK_DATA: DailyOrderStat[] = [
//   { date: "01", orders: 150 },
//   { date: "03", orders: 85 },
//   { date: "05", orders: 140 },
//   { date: "07", orders: 160 },
//   { date: "09", orders: 80 },
//   { date: "11", orders: 130 },
//   { date: "13", orders: 125 },
//   { date: "15", orders: 145 },
//   { date: "17", orders: 185 },
//   { date: "19", orders: 10 },
//   { date: "21", orders: 8 },
//   { date: "23", orders: 12 },
//   { date: "25", orders: 15 },
//   { date: "27", orders: 10 },
//   { date: "29", orders: 8 },
//   { date: "31", orders: 12 },
// ];
//
// const MONTHS = [
//   "Tháng 1",
//   "Tháng 2",
//   "Tháng 3",
//   "Tháng 4",
//   "Tháng 5",
//   "Tháng 6",
//   "Tháng 7",
//   "Tháng 8",
//   "Tháng 9",
//   "Tháng 10",
//   "Tháng 11",
//   "Tháng 12",
// ];
//
// const YEARS = [2024, 2025, 2026];
//
// // ─── Custom Tooltip ────────────────────────────────────────────────────────────
//
// function CustomTooltip({ active, payload, label }: CustomTooltipProps) {
//   if (!active || !payload?.length) return null;
//
//   return (
//     <div className="rounded border border-gray-200 bg-white px-3 py-2 shadow-md">
//       <p className="text-xs text-gray-500">Ngày {label}</p>
//       <p className="text-sm font-semibold text-[#28a745]">
//         {payload[0].value} đơn
//       </p>
//     </div>
//   );
// }
//
// // ─── Main Component ────────────────────────────────────────────────────────────
//
// export default function OrderStatsChart() {
//   const currentDate = new Date();
//   const [selectedMonth, setSelectedMonth] = useState(currentDate.getMonth()); // 0-indexed
//   const [selectedYear, setSelectedYear] = useState(currentDate.getFullYear());
//   const [data, setData] = useState<DailyOrderStat[]>(MOCK_DATA);
//   const [loading, setLoading] = useState(false);
//
//   // Gọi API thật — thay URL theo project của bạn
//   const fetchStats = useCallback(async () => {
//     setLoading(true);
//     try {
//       const month = selectedMonth + 1; // API nhận 1-indexed
//       const res = await fetch(
//         `/api/admin/dashboard/order-stats?month=${month}&year=${selectedYear}`,
//         {
//           method: "GET",
//           credentials: "include",
//         },
//       );
//       if (!res.ok) throw new Error("Lỗi khi tải dữ liệu");
//       const json: DailyOrderStat[] = await res.json();
//       setData(json);
//     } catch (err) {
//       console.error(err);
//       // Giữ mock data nếu API lỗi (dev mode)
//     } finally {
//       setLoading(false);
//     }
//   }, [selectedMonth, selectedYear]);
//
//   return (
//     <div className="border-outline-variant p-card-padding hover:border-primary border bg-white transition-colors duration-300 p-12">
//       {/* ── Header ── */}
//       <div className="mb-10 flex flex-col items-start justify-between gap-4 md:flex-row md:items-center">
//         <h3 className="font-title-md text-title-md text-primary tracking-wider uppercase">
//           THỐNG KÊ ĐƠN HÀNG
//         </h3>
//
//         <div className="gap-stack-sm flex w-full items-center md:w-auto">
//           {/* Chọn tháng */}
//           <select
//             value={selectedMonth}
//             onChange={(e) => setSelectedMonth(Number(e.target.value))}
//             className="border-outline-variant text-body-base focus:ring-primary min-w-[120px] border px-4 py-2 outline-none focus:ring-2"
//           >
//             {MONTHS.map((label, idx) => (
//               <option key={idx} value={idx}>
//                 {label}
//               </option>
//             ))}
//           </select>
//
//           {/* Chọn năm */}
//           <select
//             value={selectedYear}
//             onChange={(e) => setSelectedYear(Number(e.target.value))}
//             className="border-outline-variant text-body-base focus:ring-primary min-w-[100px] border px-4 py-2 outline-none focus:ring-2"
//           >
//             {YEARS.map((y) => (
//               <option key={y} value={y}>
//                 {y}
//               </option>
//             ))}
//           </select>
//
//           {/* Nút thống kê */}
//           <button
//             onClick={fetchStats}
//             disabled={loading}
//             className="bg-[#28a745] px-6 py-2 font-bold text-white transition-colors duration-200 hover:bg-[#218838] disabled:opacity-60"
//           >
//             {loading ? "Đang tải..." : "Thống Kê"}
//           </button>
//         </div>
//       </div>
//
//       {/* ── Recharts Area Chart ── */}
//       <ResponsiveContainer width="100%" height={320}>
//         <AreaChart
//           data={data}
//           margin={{ top: 10, right: 10, left: 0, bottom: 0 }}
//         >
//           {/* Gradient fill */}
//           <defs>
//             <linearGradient id="orderGradient" x1="0" y1="0" x2="0" y2="1">
//               <stop offset="5%" stopColor="#3b9de8" stopOpacity={0.18} />
//               <stop offset="95%" stopColor="#3b9de8" stopOpacity={0} />
//             </linearGradient>
//           </defs>
//
//           {/* Grid ngang */}
//           <CartesianGrid
//             strokeDasharray="4 4"
//             vertical={false}
//             stroke="#e5e7eb"
//           />
//
//           {/* Trục X */}
//           <XAxis
//             dataKey="date"
//             tick={{ fontSize: 11, fill: "#9ca3af" }}
//             axisLine={false}
//             tickLine={false}
//             tickFormatter={(v) => `${v}`}
//           />
//
//           {/* Trục Y */}
//           <YAxis
//             tick={{ fontSize: 11, fill: "#9ca3af" }}
//             axisLine={false}
//             tickLine={false}
//             width={36}
//           />
//
//           {/* Tooltip */}
//           <Tooltip content={<CustomTooltip />} />
//
//           {/* Area + Line */}
//           <Area
//             type="monotone"
//             dataKey="orders"
//             stroke="#3b9de8"
//             strokeWidth={2.5}
//             fill="url(#orderGradient)"
//             dot={{ r: 3, fill: "#fff", stroke: "#3b9de8", strokeWidth: 2 }}
//             activeDot={{ r: 5, fill: "#3b9de8" }}
//           />
//         </AreaChart>
//       </ResponsiveContainer>
//     </div>
//   );
// }
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

interface DailyOrderStat {
  date: string;
  orders: number;
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

// ─── Custom Tooltip ───────────────────────────────────────────────────────────

function CustomTooltip({ active, payload, label }: CustomTooltipProps) {
  if (!active || !payload?.length) return null;

  return (
    <div className="rounded border border-gray-200 bg-white px-3 py-2 shadow-md">
      <p className="text-xs text-gray-500">Ngày {label}</p>
      <p className="text-sm font-semibold text-[#28a745]">
        {payload[0].value} đơn
      </p>
    </div>
  );
}

// ─── Main Component ───────────────────────────────────────────────────────────

export default function OrderStatsChart() {
  const currentDate = new Date();
  const [selectedMonth, setSelectedMonth] = useState(currentDate.getMonth()); // 0-indexed
  const [selectedYear, setSelectedYear] = useState(currentDate.getFullYear());
  const [data, setData] = useState<DailyOrderStat[]>([]);
  const [loading, setLoading] = useState(false);
  const API_URL = import.meta.env.VITE_API_URL;
  const token = localStorage.getItem("token");

  const fetchStats = useCallback(async () => {
    setLoading(true);
    try {
      const month = selectedMonth + 1; // API nhận 1-indexed
      const res = await fetch(
        `${API_URL}/api/admin/dashboard/order-stats?month=${month}&year=${selectedYear}`,
        { headers: { Authorization: `Bearer ${token}` } },
      );
      if (!res.ok) throw new Error("Lỗi khi tải dữ liệu");
      const json: DailyOrderStat[] = await res.json();
      setData(json);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [selectedMonth, selectedYear]);

  // Tự động load khi component mount
  useEffect(() => {
    fetchStats();
  }, []);

  return (
    <div className="border-outline-variant hover:border-primary border bg-white p-12 transition-colors duration-300">
      {/* ── Header ── */}
      <div className="mb-10 flex flex-col items-start justify-between gap-4 md:flex-row md:items-center">
        <h3 className="font-title-md text-title-md text-primary tracking-wider uppercase">
          THỐNG KÊ ĐƠN HÀNG
        </h3>

        <div className="gap-stack-sm flex w-full items-center gap-2 md:w-auto">
          {/* Chọn tháng */}
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

          {/* Chọn năm */}
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

          {/* Nút thống kê */}
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
            margin={{ top: 10, right: 10, left: 0, bottom: 0 }}
          >
            <defs>
              <linearGradient id="orderGradient" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor="#3b9de8" stopOpacity={0.18} />
                <stop offset="95%" stopColor="#3b9de8" stopOpacity={0} />
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
              width={36}
            />

            <Tooltip content={<CustomTooltip />} />

            <Area
              type="monotone"
              dataKey="orders"
              stroke="#3b9de8"
              strokeWidth={2.5}
              fill="url(#orderGradient)"
              dot={{ r: 3, fill: "#fff", stroke: "#3b9de8", strokeWidth: 2 }}
              activeDot={{ r: 5, fill: "#3b9de8" }}
            />
          </AreaChart>
        </ResponsiveContainer>
      )}
    </div>
  );
}