import { useState, ReactNode } from "react";
import Header from "./Header";
import Navbar from "./Navbar";
import Footer from "./Footer";

type MainLayoutProps = {
  children: ReactNode;
};

const MainLayout = ({ children }: MainLayoutProps) => {
  const [isNavOpen, setIsNavOpen] = useState(false);

  return (
    <div className="min-h-screen flex flex-col">
      <Header onMenuClick={() => setIsNavOpen(true)} />
      <Navbar isOpen={isNavOpen} onClose={() => setIsNavOpen(false)} />
      <main className="flex-grow">
        {children}
      </main>
      <Footer />
    </div>
  );
};

export default MainLayout;
