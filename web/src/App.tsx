import { Route, Routes } from "react-router-dom";
import HomePage from "@/components/pages/HomePage";

const App = () => {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
    </Routes>
  );
};

export default App;
