import {Route, Routes} from "react-router-dom";
import Layout from "@layout/Layout";
import StoreProvider from "@store/StoreProvider";

const App = () => {
    return (
        <StoreProvider>
            <Routes>
                <Route path="/" element={<></>}>
                    <Route index element={<h1>BLANK</h1>}/>
                </Route>
            </Routes>
        </StoreProvider>
    );
};

export default App;
