import { Outlet } from "react-router-dom";
import SideNavBar from "../components/SideNavBar";

export default function MainLayouts() {
  return (
    <div className="bg-surface dark:bg-surface-container-lowest min-h-screen">
      <SideNavBar />

      <main className="min-h-screen p-6 md:ml-64">
        <Outlet />
      </main>
    </div>
  );
}
