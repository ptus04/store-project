import { useNavigate, useLocation, Link } from "react-router-dom";

// Đọc thông tin user từ localStorage
function getUserInfo() {
  try {
    const raw = localStorage.getItem("user");
    if (!raw) return { name: "Admin", role: "ADMIN", avatar: null };
    return JSON.parse(raw) as {
      name: string;
      role: string;
      avatar: string | null;
    };
  } catch {
    return { name: "Admin", role: "ADMIN", avatar: null };
  }
}

export default function SideNavBar() {
  const navigate = useNavigate();
  const location = useLocation();
  const user = getUserInfo();
  const currentPath = location.pathname;

  function handleLogout() {
    localStorage.removeItem("user");
    navigate("/login");
  }

  return (
    <nav className="py-unit border-outline-variant bg-surface dark:bg-surface-container-lowest fixed top-0 left-0 z-50 hidden h-screen w-64 flex-col border-r md:flex">
      {/* Brand/Header Area */}
      <div className="border-outline-variant mb-4 flex flex-col items-start border-b px-4 py-6">
        <img
          alt="SLY Logo"
          className="mb-4 h-10 object-contain"
          src="https://lh3.googleusercontent.com/aida-public/AB6AXuC3Ug5Cspk24gYZjhxUTV-eG8Gko9CGFpdXFN4GYMuLY3iV3Duq0ztpyQNJvbbVPwSYlDx5s4C6w04HZmHk6ZiKnBbNT1RZpECrgkS5OXgfsZlBT_rts2ec-xTIczahVkg-hXhGSMDefdGKO1qP2Bet7Ok0XZm3yXyr77JzyDrrQu9am82mf-xUit3HnW-LGmw-4vvHXWfQMJEFx8I-uKaYtGrRw_7AeFlbd3NQSLyUF800Pdaimvx9PIBSPEpCYYd9_Xp0NdXSjPI"
        />

        <div className="flex items-center gap-3">
          <div className="bg-surface-container-high border-outline-variant flex h-10 w-10 items-center justify-center overflow-hidden rounded-full border">
            {user.avatar ? (
              <img
                src={user.avatar}
                alt={user.name}
                className="h-full w-full object-cover"
              />
            ) : (
              <span
                className="material-symbols-outlined text-primary"
                data-icon="person"
              >
                person
              </span>
            )}
          </div>

          <div>
            {/* Hiển thị họ tên thật của user */}
            <h2 className="text-label-sm font-label-sm text-primary">
              {user.name}
            </h2>
            {/* Hiển thị role của user */}
            <p className="text-secondary mt-1 text-[10px] tracking-widest uppercase">
              {user.role}
            </p>
          </div>
        </div>
      </div>

      {/* Navigation Links */}
      <div className="flex flex-1 flex-col gap-1 px-2">
        {/* Active Item: Dashboard */}
        <Link
          to="/"
          className={`flex items-center gap-3 border-l-4 px-4 py-3 transition-colors duration-200 ${
            currentPath === "/" ||
            currentPath === "" ||
            currentPath === "/dashboard"
              ? "bg-primary dark:bg-primary-fixed text-on-primary dark:text-on-primary-fixed border-primary font-medium"
              : "text-secondary dark:text-secondary-fixed-dim hover:bg-surface-container-high dark:hover:bg-surface-container-highest border-transparent"
          }`}
        >
          <span className="material-symbols-outlined" data-icon="dashboard">
            dashboard
          </span>
          <span className="text-label-sm font-label-sm">Dashboard</span>
        </Link>

        {/* Chat Hỗ Trợ */}
        <Link
          to="/chat"
          className={`flex items-center gap-3 border-l-4 px-4 py-3 transition-colors duration-200 ${
            currentPath === "/chat"
              ? "bg-primary dark:bg-primary-fixed text-on-primary dark:text-on-primary-fixed border-primary font-medium"
              : "text-secondary dark:text-secondary-fixed-dim hover:bg-surface-container-high dark:hover:bg-surface-container-highest border-transparent"
          }`}
        >
          <span className="material-symbols-outlined" data-icon="chat">
            chat
          </span>
          <span className="text-label-sm font-label-sm">Chat Hỗ Trợ</span>
        </Link>

        {/* Inactive Items */}
        <Link
          to="#"
          className="text-secondary dark:text-secondary-fixed-dim hover:bg-surface-container-high dark:hover:bg-surface-container-highest flex items-center gap-3 border-l-4 border-transparent px-4 py-3 transition-colors duration-200"
        >
          <span className="material-symbols-outlined" data-icon="badge">
            badge
          </span>
          <span className="text-label-sm font-label-sm">Nhân Viên</span>
        </Link>

        <Link
          to="#"
          className="text-secondary dark:text-secondary-fixed-dim hover:bg-surface-container-high dark:hover:bg-surface-container-highest flex items-center gap-3 border-l-4 border-transparent px-4 py-3 transition-colors duration-200"
        >
          <span className="material-symbols-outlined" data-icon="group">
            group
          </span>
          <span className="text-label-sm font-label-sm">Khách hàng</span>
        </Link>
      </div>

      {/* Settings & Logout */}
      <div className="border-outline-variant mt-auto border-t p-4">
        <Link
          to="#"
          className="text-secondary dark:text-secondary-fixed-dim hover:bg-surface-container-high dark:hover:bg-surface-container-highest flex items-center gap-3 border-l-4 border-transparent px-4 py-3 transition-colors duration-200"
        >
          <span className="material-symbols-outlined" data-icon="settings">
            settings
          </span>
          <span className="text-label-sm font-label-sm">Settings</span>
        </Link>

        {/* Nút Đăng xuất - luôn hiển thị */}
        <button
          onClick={handleLogout}
          className="flex w-full cursor-pointer items-center gap-3 px-4 py-3 text-red-500 transition-colors duration-200 hover:bg-red-50 dark:hover:bg-red-950/20"
        >
          <span className="material-symbols-outlined text-[20px]">logout</span>
          <span className="text-label-sm font-label-sm">Đăng xuất</span>
        </button>
      </div>
    </nav>
  );
}
