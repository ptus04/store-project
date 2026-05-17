import { useEffect, useState } from "react";
import OrderStatsChart from "@components/dashboard/OrderStatsChart.tsx";

interface DashboardStats {
  totalCustomers: number;
  totalEmployees: number;
}

export default function Dashboard() {
  const [stats, setStats] = useState<DashboardStats>({
    totalCustomers: 0,
    totalEmployees: 0,
  });
  const [loading, setLoading] = useState(true);
  const API_URL = import.meta.env.VITE_API_URL;
  const token = localStorage.getItem("token");

  useEffect(() => {
    async function fetchStats() {
      try {
        const response = await fetch(`${API_URL}/api/admin/dashboard/stats`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (!response.ok) {
          console.error("Không thể tải dashboard stats:", response.status);
          return;
        }

        const data: DashboardStats = await response.json();
        setStats(data);
      } catch (err) {
        console.error("Không thể tải dashboard stats:", err);
      } finally {
        setLoading(false);
      }
    }

    fetchStats();
  }, []);
  return (
    <main className="p-gutter bg-background flex-1 overflow-y-auto">
      <div className="max-w-container-max mx-auto space-y-8 pb-12">
        {/* Page Header */}
        <div className="border-outline-variant flex flex-col justify-between gap-4 border-b pb-4 sm:flex-row sm:items-end">
          <div>
            <h2 className="text-headline-md font-headline-md text-primary tracking-tight">
              Dashboard Overview
            </h2>
            <p className="text-body-md font-body-md text-secondary mt-1">
              High-level metrics and performance data.
            </p>
          </div>
        </div>

        {/* Stats Row */}
        <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
          {/* Employee Card */}
          <div className="bg-surface-container-lowest border-outline-variant group hover:border-primary relative flex flex-col justify-between border p-8 transition-colors">
            <div className="mb-12 flex items-start justify-between">
              <h3 className="text-label-sm font-label-sm text-secondary tracking-widest uppercase">
                Nhân viên
              </h3>
              <div className="bg-surface-container flex h-10 w-10 items-center justify-center rounded-full">
                <span
                  className="material-symbols-outlined text-primary"
                  data-icon="badge"
                >
                  badge
                </span>
              </div>
            </div>

            <div>
              <div className="text-display font-display text-primary mb-4 leading-none">
                {loading ? (
                  <span className="inline-block h-12 w-24 animate-pulse rounded bg-gray-200" />
                ) : (
                  stats.totalEmployees.toLocaleString()
                )}
              </div>
            </div>

            <div className="group-hover:border-primary absolute right-0 bottom-0 h-8 w-8 border-r-2 border-b-2 border-transparent transition-colors" />
          </div>

          {/* Customer Card */}
          <div className="bg-surface-container-lowest border-outline-variant group hover:border-primary relative flex flex-col justify-between border p-8 transition-colors">
            <div className="mb-12 flex items-start justify-between">
              <h3 className="text-label-sm font-label-sm text-secondary tracking-widest uppercase">
                Khách hàng
              </h3>
              <div className="bg-surface-container flex h-10 w-10 items-center justify-center rounded-full">
                <span
                  className="material-symbols-outlined text-primary"
                  data-icon="group"
                >
                  group
                </span>
              </div>
            </div>

            <div>
              <div className="text-display font-display text-primary mb-4 leading-none">
                {loading ? (
                  <span className="inline-block h-12 w-24 animate-pulse rounded bg-gray-200" />
                ) : (
                  stats.totalCustomers.toLocaleString()
                )}
              </div>
            </div>

            <div className="group-hover:border-primary absolute right-0 bottom-0 h-8 w-8 border-r-2 border-b-2 border-transparent transition-colors" />
          </div>
        </div>

        <OrderStatsChart />
      </div>
    </main>
  );
}
