import { Route, Routes } from "react-router-dom";

const App = () => {
  return (
    <Routes>
      <Route path="/" element={<></>}>
        <Route index element={<h1>BLANK</h1>} />
      </Route>
    </Routes>
  );
};

export default App;
