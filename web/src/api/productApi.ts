export interface ProductImagePutRequest {
  id?: string | null;
  file: string;
}

export interface ProductSizePutRequest {
  id?: string | null;
  name: string;
  inStock?: number | null;
}

export interface ProductCreateRequest {
  name: string;
  description?: string | null;
  careInstructions?: string | null;
  price: number;
  inStock: number;
  discount: number;
  productImages: ProductImagePutRequest[];
  productSizes?: ProductSizePutRequest[];
  categoryIds?: string[];
}

export interface ProductUpdateRequest {
  name: string;
  description?: string | null;
  careInstructions?: string | null;
  price?: number | null;
  inStock?: number | null;
  discount?: number | null;
  productImages: ProductImagePutRequest[];
  productSizes?: ProductSizePutRequest[];
  categoryIds?: string[];
  isRestore?: boolean | null;
}

export interface ProductImageResponse {
  id: string;
  file: string;
  createdAt: string;
}

export interface ProductSizeResponse {
  id: string;
  name: string;
  inStock: number | null;
}

export interface CategoryResponse {
  id: string;
  name: string;
}

export interface ProductResponse {
  id: string;
  name: string;
  description?: string | null;
  careInstructions?: string | null;
  price: number;
  inStock?: number | null;
  discount?: number | null;
  priceDiscount?: number | null;
  createdAt?: string | null;
  updatedAt?: string | null;
  deletedAt?: string | null;
  productImages?: ProductImageResponse[];
  productSizes?: ProductSizeResponse[];
  categories?: CategoryResponse[];
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

const API_URL = import.meta.env.VITE_API_URL;

function getHeaders(contentType = "application/json"): HeadersInit {
  const token = localStorage.getItem("token");
  const base: HeadersInit = token ? { Authorization: `Bearer ${token}` } : {};
  if (contentType) base["Content-Type"] = contentType;
  return base;
}

export const productApi = {
  getList: async (params: {
    page?: number;
    size?: number;
    sortBy?: string;
    categoryName?: string | null;
    query?: string | null;
    minPrice?: number | null;
    maxPrice?: number | null;
    onlyDeleted?: boolean;
  }): Promise<PageResponse<ProductResponse>> => {
    const query = new URLSearchParams();
    query.set("page", String(params.page ?? 0));
    query.set("size", String(params.size ?? 20));
    query.set("sortBy", params.sortBy ?? "newest");
    if (params.categoryName) query.set("categoryName", params.categoryName);
    if (params.query) query.set("query", params.query);
    if (params.minPrice != null) query.set("minPrice", String(params.minPrice));
    if (params.maxPrice != null) query.set("maxPrice", String(params.maxPrice));
    if (params.onlyDeleted != null)
      query.set("onlyDeleted", String(params.onlyDeleted));

    const res = await fetch(`${API_URL}/api/products?${query.toString()}`, {
      headers: getHeaders(),
    });
    if (!res.ok) throw new Error("Không thể tải danh sách sản phẩm");
    return res.json();
  },

  getById: async (id: string): Promise<ProductResponse> => {
    const res = await fetch(`${API_URL}/api/products/${id}`, {
      headers: getHeaders(),
    });
    if (!res.ok) throw new Error("Không thể tải sản phẩm");
    return res.json();
  },

  createProduct: async (
    payload: ProductCreateRequest,
  ): Promise<ProductResponse> => {
    const res = await fetch(`${API_URL}/api/products`, {
      method: "POST",
      headers: getHeaders(),
      body: JSON.stringify(payload),
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || "Không thể tạo sản phẩm");
    }
    return res.json();
  },

  updateProduct: async (
    id: string,
    payload: ProductUpdateRequest,
  ): Promise<ProductResponse> => {
    const res = await fetch(`${API_URL}/api/products/${id}`, {
      method: "PATCH",
      headers: getHeaders(),
      body: JSON.stringify(payload),
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || "Không thể cập nhật sản phẩm");
    }
    return res.json();
  },

  deleteProduct: async (id: string): Promise<void> => {
    const res = await fetch(`${API_URL}/api/products/${id}`, {
      method: "DELETE",
      headers: getHeaders(),
    });
    if (!res.ok) throw new Error("Không thể xóa sản phẩm");
  },

  // Storage SAS helper
  getUploadSas: async (containerName: string, blobName?: string) => {
    const url = blobName
      ? `${API_URL}/api/blobs/${containerName}/sas?blobName=${encodeURIComponent(blobName)}`
      : `${API_URL}/api/blobs/${containerName}/sas`;
    const res = await fetch(url, { headers: getHeaders() });
    if (!res.ok) throw new Error("Không lấy được SAS URL");
    return res.json();
  },

  uploadToSas: async (sasUrl: string, file: File) => {
    const res = await fetch(sasUrl, {
      method: "PUT",
      headers: {
        "x-ms-blob-type": "BlockBlob",
        "Content-Type": file.type || "application/octet-stream",
      },
      body: file,
    });
    if (!res.ok) throw new Error("Không upload được file");
    return true;
  },

  uploadToSasWithProgress: (
    sasUrl: string,
    file: File,
    onProgress: (percent: number) => void,
  ) => {
    return new Promise<void>((resolve, reject) => {
      const xhr = new XMLHttpRequest();
      xhr.open("PUT", sasUrl, true);
      xhr.setRequestHeader("x-ms-blob-type", "BlockBlob");
      xhr.setRequestHeader(
        "Content-Type",
        file.type || "application/octet-stream",
      );

      xhr.upload.onprogress = (e) => {
        if (e.lengthComputable) {
          const pct = Math.round((e.loaded / e.total) * 100);
          try {
            onProgress(pct);
          } catch (_) {
            // ignore
          }
        }
      };

      xhr.onload = () => {
        if (xhr.status >= 200 && xhr.status < 300) resolve();
        else reject(new Error(`Upload failed with status ${xhr.status}`));
      };
      xhr.onerror = () => reject(new Error("Network error during upload"));
      xhr.send(file);
    });
  },
};
