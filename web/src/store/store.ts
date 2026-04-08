export type State = {
  isExpanded: boolean;
  success?: string;
  warning?: string;
  error?: string;
};

export const initialState: State = {
  isExpanded: false,
};

export type Action =
  | { type: "SET_NAV_BAR_STATE"; payload: boolean }
  | { type: "SET_SUCCESS"; payload: string | undefined }
  | { type: "SET_WARNING"; payload: string | undefined }
  | { type: "SET_ERROR"; payload: string | undefined };

export const reducer = (state: State, action: Action): State => {
  switch (action.type) {
    case "SET_NAV_BAR_STATE":
      return { ...state, isExpanded: action.payload };
    case "SET_SUCCESS":
      return { ...state, success: action.payload };
    case "SET_WARNING":
      return { ...state, warning: action.payload };
    case "SET_ERROR":
      return { ...state, error: action.payload };

    default:
      return state;
  }
};
