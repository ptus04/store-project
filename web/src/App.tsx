import { Route, Routes, Outlet } from "react-router-dom";
import ProductDetailPage from "@components/pages/ProductDetailPage.tsx";
import HomePage from "@components/pages/HomePage.tsx";

const App = () => {
  return (
    <Routes>
      <Route path="/" element={<Outlet />}>
        <Route index element={<HomePage />} />
        <Route path="products">
          <Route path=":id" element={<ProductDetailPage />} />
        </Route>
      </Route>
    </Routes>
  );
};

export default App;
