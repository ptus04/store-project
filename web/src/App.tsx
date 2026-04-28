import { Route, Routes } from "react-router-dom";
import HomePage from "@/components/pages/HomePage";
import AdminDashboard from "@/components/pages/AdminDashboard";
import ProfilePage from "@/components/pages/ProfilePage";
import MainLayout from "@/components/ui/MainLayout";

const App = () => {
  return (
    <MainLayout>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/admin" element={<AdminDashboard />} />
        <Route path="/profile" element={<ProfilePage />} />
      </Routes>
    </MainLayout>
  );
};

export default App;
