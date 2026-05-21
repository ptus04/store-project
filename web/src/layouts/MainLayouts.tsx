import { Outlet } from "react-router-dom";
import SideNavBar from "../components/SideNavBar";
import ChatSupportWidget from "../components/chat/ChatSupportWidget";

export default function MainLayouts() {
  return (
    <div className="bg-surface dark:bg-surface-container-lowest min-h-screen">
      <SideNavBar />

      <main className="min-h-screen p-6 md:ml-64">
        <Outlet />
      </main>

      {/* Global floating chat support widget for staff */}
      <ChatSupportWidget />
    </div>
  );
}
