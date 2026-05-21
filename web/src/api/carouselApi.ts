export interface CarouselResponse {
  id: string;
  title: string;
  content: string;
  link: string;
  landscapeImage: string;
  portraitImage: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CarouselFormData {
  title: string;
  content: string;
  link: string;
  landscapeImage: string;
  portraitImage: string;
}

interface StorageSasResponse {
  blobName: string;
  url: string;
  expiresAt: string;
  createdAt: string;
}

const API_URL = import.meta.env.VITE_API_URL as string;
const STORAGE_URL = (import.meta.env.VITE_STORAGE_URL as string) ?? "";

// Hỗ trợ cả blob name lẫn full URL (backward compat data cũ)
export function getImageUrl(path: string | null | undefined): string | null {
  if (!path) return null;
  if (path.startsWith("http")) return path; // data cũ lưu full URL
  if (STORAGE_URL) return `${STORAGE_URL}/${path}`; // data mới lưu blob name
  return null;
}
/**
 * Trả về URL hiển thị.
 * Backend lưu full URL (https://...) → trả thẳng.
 * Nếu chỉ là path tương đối → trả null để không hiện ảnh lỗi.
 */
// export function getImageUrl(path: string | null | undefined): string | null {
//   if (!path) return null;
//   if (path.startsWith("http")) return path;
//   return null;
// }

function authHeaders(json = false): HeadersInit {
  const token = localStorage.getItem("token");
  const headers: Record<string, string> = {};
  if (token) headers["Authorization"] = `Bearer ${token}`;
  if (json) headers["Content-Type"] = "application/json";
  return headers;
}

export const carouselApi = {
  getAll: async (): Promise<CarouselResponse[]> => {
    const res = await fetch(`${API_URL}/api/carousel`, {
      headers: authHeaders(),
    });
    if (!res.ok) throw new Error("Không thể tải danh sách tin nổi bật");
    return res.json();
  },

  create: async (data: CarouselFormData): Promise<CarouselResponse> => {
    const res = await fetch(`${API_URL}/api/carousel`, {
      method: "POST",
      headers: authHeaders(true),
      body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error("Không thể tạo tin nổi bật");
    return res.json();
  },

  update: async (
    id: string,
    data: CarouselFormData,
  ): Promise<CarouselResponse> => {
    const res = await fetch(`${API_URL}/api/carousel/${id}`, {
      method: "PUT",
      headers: authHeaders(true),
      body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error("Không thể cập nhật tin nổi bật");
    return res.json();
  },

  delete: async (id: string): Promise<void> => {
    const res = await fetch(`${API_URL}/api/carousel/${id}`, {
      method: "DELETE",
      headers: authHeaders(),
    });
    if (!res.ok) throw new Error("Không thể xóa tin nổi bật");
  },

  deleteBlob: async (
    containerName: string,
    blobName: string,
  ): Promise<void> => {
    await fetch(`${API_URL}/api/blobs/${containerName}/${blobName}`, {
      method: "DELETE",
      headers: authHeaders(),
    });
  },

  getSasUrl: async (containerName: string): Promise<StorageSasResponse> => {
    const res = await fetch(`${API_URL}/api/blobs/${containerName}/sas`, {
      headers: authHeaders(),
    });
    if (!res.ok) throw new Error("Không thể lấy SAS URL");
    return res.json();
  },

  /**
   * Upload file lên Azure Blob qua SAS URL.
   * Trả về public URL (bỏ phần query SAS token).
   */
  // uploadBlob: async (sasUrl: string, file: File): Promise<string> => {
  //   const res = await fetch(sasUrl, {
  //     method: "PUT",
  //     headers: {
  //       "x-ms-blob-type": "BlockBlob",
  //       "Content-Type": file.type,
  //     },
  //     body: file,
  //   });
  //   if (!res.ok) throw new Error("Không thể tải ảnh lên");
  //   // Trả về public URL — bỏ SAS token (phần ?sv=...&sig=...)
  //   return sasUrl.split("?")[0];
  // },
  // uploadBlob — trả về blob name thay vì full URL
  uploadBlob: async (sasUrl: string, file: File): Promise<string> => {
    const res = await fetch(sasUrl, {
      method: "PUT",
      headers: {
        "x-ms-blob-type": "BlockBlob",
        "Content-Type": file.type,
      },
      body: file,
    });
    if (!res.ok) throw new Error("Không thể tải ảnh lên");
    // Chỉ lấy blob name (UUID), ví dụ: "019e48c4-37f3-72dd-afae-ea30787a489f"
    const urlObj = new URL(sasUrl);
    return urlObj.pathname.split("/").pop() ?? "";
  },
};
