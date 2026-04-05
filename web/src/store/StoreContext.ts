import { createContext, type Dispatch } from "react";
import { type Action, initialState, type State } from "./store";

type StoreContextType = {
  state: State;
  dispatch: Dispatch<Action>;
};

export const StoreContext = createContext<StoreContextType>({
  state: initialState,
  dispatch: () => undefined,
});
