// import { Route, Routes } from "react-router-dom";
//
// const App = () => {
//   return (
//     <Routes>
//       <Route path="/" element={<></>}>
//         <Route index element={<h1>BLANK</h1>} />
//       </Route>
//     </Routes>
//   );
// };
//
// export default App;

import { Navigate, Route, Routes } from "react-router-dom";
import Login from "@pages/login/Login";
import Dashboard from "@pages/dashboard/Dashboard";
import MainLayouts from "@layouts/MainLayouts.tsx";

// Kiểm tra user đã đăng nhập và có role ADMIN chưa
function isAuthenticated(): boolean {
    try {
        const raw = localStorage.getItem("user");
        if (!raw) return false;
        const user = JSON.parse(raw);
        return user?.role === "ADMIN";
    } catch {
        return false;
    }
}

// Bảo vệ route: chưa login → về /login
function PrivateRoute({ children }: { children: React.ReactNode }) {
    return isAuthenticated() ? <>{children}</> : <Navigate to="/login" replace />;
}

// Nếu đã login rồi mà vào /login → về trang chủ
function PublicRoute({ children }: { children: React.ReactNode }) {
    return isAuthenticated() ? <Navigate to="/" replace /> : <>{children}</>;
}

const App = () => {
    return (
        <Routes>
            <Route
                path="/login"
                element={
                    <PublicRoute>
                        <Login />
                    </PublicRoute>
                }
            />

            <Route
                element={
                    <PrivateRoute>
                        <MainLayouts />
                    </PrivateRoute>
                }
            >
                <Route index element={<Dashboard />} />
                <Route path="/" element={<Dashboard />} />
            </Route>

            {/* Mọi route không tồn tại → về trang chủ */}
            <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
    );
};

export default App;
