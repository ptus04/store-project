import { useReducer } from "react";

// ─── State ────────────────────────────────────────────────────────────────────

type AppState = {
  error?: string;
  warning?: string;
  success?: string;
};

const initialState: AppState = {};

// ─── Actions ──────────────────────────────────────────────────────────────────

type AppAction =
  | { type: "SET_ERROR"; payload: string | undefined }
  | { type: "SET_WARNING"; payload: string | undefined }
  | { type: "SET_SUCCESS"; payload: string | undefined };

// ─── Reducer ──────────────────────────────────────────────────────────────────

const reducer = (state: AppState, action: AppAction): AppState => {
  switch (action.type) {
    case "SET_ERROR":
      return { ...state, error: action.payload };
    case "SET_WARNING":
      return { ...state, warning: action.payload };
    case "SET_SUCCESS":
      return { ...state, success: action.payload };
    default:
      return state;
  }
};

// ─── Hook ────────────────────────────────────────────────────────────────────

const useStore = () => {
  const [state, dispatch] = useReducer(reducer, initialState);
  return { state, dispatch };
};

export default useStore;
