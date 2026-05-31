export type OrderStatusEnum =
  | "UNPAID"
  | "PAID"
  | "PACKAGING"
  | "SHIPPING"
  | "COMPLETED"
  | "CANCELLED"
  | "REFUNDED";

export type OrderPaymentMethodEnum = "SEPAY";

export interface UserSummary {
  id: string;
  name: string;
  phone: string;
  email: string | null;
}

export interface ProductSummary {
  id: string;
  name: string;
}

export interface OrderDetailResponse {
  id: string;
  product: ProductSummary | null;
  productSize: string | null;
  quantity: number;
  price: number;
  subtotal: number;
}

export interface OrderShippingAddressResponse {
  name: string;
  phone: string;
  city: string;
  district: string;
  ward: string;
  address: string;
}

export interface TransactionResponse {
  id: string;
  transactionCode: string;
  referenceCode: string;
  gatewayName: string;
  content: string;
  amount: number;
  transactionDate: string;
}

export interface OrderResponse {
  id: string;
  orderCode: string;
  user: UserSummary;
  orderDate: string;
  shippingDate: string;
  paymentMethod: OrderPaymentMethodEnum;
  status: OrderStatusEnum;
  total: number;
  note: string | null;
  cancellationReason: string | null;
  createdAt: string;
  updatedAt: string;
  orderDetails: OrderDetailResponse[];
  orderShippingAddress: OrderShippingAddressResponse;
  transactions: TransactionResponse[];
}

export interface PageResponse<T> {
  content: T[];
  page: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  };
}

export interface OrderSearchParams {
  page?: number;
  size?: number;
  status?: OrderStatusEnum | null;
  search?: string;
}

const API_URL = import.meta.env.VITE_API_URL;

function getHeaders(contentType = false): HeadersInit {
  const token = localStorage.getItem("token");
  return {
    ...(contentType ? { "Content-Type": "application/json" } : {}),
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
}

async function readErrorMessage(response: Response): Promise<string> {
  const data = await response.json().catch(() => null);
  return data?.message || data?.detail || "Thao tác thất bại";
}

export const orderApi = {
  getList: async (
    params: OrderSearchParams,
  ): Promise<PageResponse<OrderResponse>> => {
    const query = new URLSearchParams();
    query.set("page", String(params.page ?? 0));
    query.set("size", String(params.size ?? 10));
    if (params.status) query.set("status", params.status);
    if (params.search) query.set("search", params.search);

    const response = await fetch(`${API_URL}/api/orders?${query.toString()}`, {
      headers: getHeaders(),
    });

    if (!response.ok) throw new Error(await readErrorMessage(response));
    return response.json();
  },

  getById: async (id: string): Promise<OrderResponse> => {
    const response = await fetch(`${API_URL}/api/orders/${id}`, {
      headers: getHeaders(),
    });

    if (!response.ok) throw new Error(await readErrorMessage(response));
    return response.json();
  },

  updateStatus: async (
    id: string,
    status: OrderStatusEnum,
  ): Promise<OrderResponse> => {
    const response = await fetch(`${API_URL}/api/orders/${id}/status`, {
      method: "PATCH",
      headers: getHeaders(true),
      body: JSON.stringify({ status }),
    });

    if (!response.ok) throw new Error(await readErrorMessage(response));
    return response.json();
  },
};
