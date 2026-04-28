import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Loading from "@components/ui/Loading";
import { useAuth } from "@/utils/AuthContext";

type ActivePage = "dashboard";

const AdminDashboard = () => {
  const { user, isLoading: authLoading, logout } = useAuth();
  const [activePage] = useState<ActivePage>("dashboard");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    if (!authLoading) {
      if (!user) {
        setError("Vui lòng đăng nhập");
      } else if (user.role === "CUSTOMER") {
        setError("Bạn không có quyền truy cập trang này");
      } else {
        setLoading(false);
      }
    }
  }, [user, authLoading]);

  if (loading || authLoading) return <Loading isLoading={true} />;

  if (error) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gray-50">
        <div className="rounded-2xl bg-white p-8 text-center shadow-xl">
          <h2 className="mb-4 text-2xl font-bold text-red-500">{error}</h2>
          <button
            onClick={() => window.location.href = "http://localhost:8080/dang-nhap"}
            className="rounded-md bg-black px-6 py-2 text-white"
          >
            Đến trang đăng nhập
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen bg-gray-50">
      {/* ── Sidebar ── */}
      <aside className="flex w-64 flex-col bg-black p-6 text-white">
        <h2 className="mb-8 text-2xl font-black italic">SLY ADMIN</h2>
        <nav className="flex flex-col gap-2">
          <button
            onClick={() => navigate("/admin")}
            className={`flex items-center gap-3 rounded-lg px-3 py-2 font-bold transition-all ${
              activePage === "dashboard" ? "bg-red-500 text-white" : "text-gray-400 hover:text-white"
            }`}
          >
            <i className="fa fa-dashboard w-4 text-center" /> Dashboard
          </button>
          <button
            onClick={() => navigate("/profile")}
            className="flex items-center gap-3 rounded-lg px-3 py-2 transition-all text-gray-400 hover:text-white"
          >
            <i className="fa fa-user w-4 text-center" /> Tài khoản
          </button>
          <button className="flex items-center gap-3 rounded-lg px-3 py-2 text-gray-400 transition-all hover:text-white">
            <i className="fa fa-shopping-cart w-4 text-center" /> Đơn hàng
          </button>
          <button className="flex items-center gap-3 rounded-lg px-3 py-2 text-gray-400 transition-all hover:text-white">
            <i className="fa fa-cube w-4 text-center" /> Sản phẩm
          </button>
          <button className="flex items-center gap-3 rounded-lg px-3 py-2 text-gray-400 transition-all hover:text-white">
            <i className="fa fa-users w-4 text-center" /> Khách hàng
          </button>
          <hr className="my-3 border-gray-800" />
          <button
            onClick={logout}
            className="flex w-full items-center gap-3 rounded-lg px-3 py-2 transition-all text-red-400 hover:text-red-300"
          >
            <i className="fa fa-sign-out w-4 text-center" /> Đăng xuất
          </button>
        </nav>
      </aside>

      {/* ── Main ── */}
      <main className="flex-1 overflow-y-auto p-8">
        {/* Header */}
        <header className="mb-8 flex items-center justify-between">
          <h1 className="text-3xl font-black text-gray-900">
            Dashboard
          </h1>
          <div className="flex items-center gap-4">
            <span className="text-sm font-bold text-gray-500">
              Xin chào, {user?.name}
            </span>
            <button
              onClick={() => navigate("/profile")}
              className="flex h-10 w-10 items-center justify-center rounded-full bg-red-500 font-bold text-white shadow-lg transition-all hover:bg-red-600"
            >
              {user?.name.charAt(0)}
            </button>
          </div>
        </header>

        {/* ── Dashboard Content ── */}
        <div className="mb-8 grid grid-cols-1 gap-6 md:grid-cols-3">
          <div className="rounded-2xl border border-gray-100 bg-white p-6 shadow-sm">
            <p className="text-sm font-bold uppercase text-gray-400">Doanh thu hôm nay</p>
            <p className="mt-2 text-3xl font-black">12,500,000đ</p>
          </div>
          <div className="rounded-2xl border border-gray-100 bg-white p-6 shadow-sm">
            <p className="text-sm font-bold uppercase text-gray-400">Đơn hàng mới</p>
            <p className="mt-2 text-3xl font-black">48</p>
          </div>
          <div className="rounded-2xl border border-gray-100 bg-white p-6 shadow-sm">
            <p className="text-sm font-bold uppercase text-gray-400">Sản phẩm sắp hết</p>
            <p className="mt-2 text-3xl font-black">15</p>
          </div>
        </div>
        <div className="rounded-2xl border border-gray-100 bg-white p-8 shadow-sm">
          <h3 className="mb-6 text-xl font-black">Đơn hàng gần đây</h3>
          <div className="flex h-64 items-center justify-center rounded-xl border-2 border-dashed border-gray-100">
            <p className="italic text-gray-400">Dữ liệu đơn hàng đang được cập nhật...</p>
          </div>
        </div>
      </main>
    </div>
  );
};

export default AdminDashboard;
