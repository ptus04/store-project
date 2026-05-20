export type UserGenderEnum = "MALE" | "FEMALE";
export type UserRoleEnum = "CUSTOMER" | "EMPLOYEE" | "ADMIN";

export interface UserResponse {
  id: string;
  name: string;
  phone: string;
  email: string | null;
  role: UserRoleEnum;
  gender: UserGenderEnum | null;
  birthDate: string | null;
  phoneVerifiedAt: string | null;
  emailVerifiedAt: string | null;
  createdAt: string;
  updatedAt: string;
  disabledAt: string | null;
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

export interface CustomerSearchParams {
  page?: number;
  size?: number;
  gender?: UserGenderEnum | null;
  search?: string;
}

const API_URL = import.meta.env.VITE_API_URL;

function getHeaders(): HeadersInit {
  const token = localStorage.getItem("token");
  return token ? { Authorization: `Bearer ${token}` } : {};
}

export const customerApi = {
  getList: async (
    params: CustomerSearchParams,
  ): Promise<PageResponse<UserResponse>> => {
    const query = new URLSearchParams();
    query.set("page", String(params.page ?? 0));
    query.set("size", String(params.size ?? 10));
    if (params.gender) query.set("gender", params.gender);
    if (params.search) query.set("search", params.search);

    const response = await fetch(
      `${API_URL}/api/customers?${query.toString()}`,
      {
        headers: getHeaders(),
      },
    );

    if (!response.ok) throw new Error("Không thể tải danh sách khách hàng");
    return response.json();
  },

  getById: async (id: string): Promise<UserResponse> => {
    const response = await fetch(`${API_URL}/api/customers/${id}`, {
      headers: getHeaders(),
    });

    if (!response.ok) throw new Error("Không thể tải thông tin khách hàng");
    return response.json();
  },

  toggleStatus: async (
    id: string,
    disabled: boolean,
  ): Promise<UserResponse> => {
    const response = await fetch(
      `${API_URL}/api/customers/${id}/status?disabled=${disabled}`,
      {
        method: "PATCH",
        headers: getHeaders(),
      },
    );

    if (!response.ok) throw new Error("Không thể cập nhật trạng thái");
    return response.json();
  },
};
