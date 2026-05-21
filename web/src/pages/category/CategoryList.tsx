import { useEffect, useState } from "react";
import Modal from "@components/ui/Modal";
import ConfirmModal from "@components/ui/ConfirmModal";

interface Category {
  id: string;
  name: string;
  createdAt: string;
  updatedAt: string;
}

export default function CategoryList() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const rawUser = localStorage.getItem("user");
  const user = rawUser ? JSON.parse(rawUser) : null;
  const isAdmin = user?.role === "ADMIN";

  // For Create/Edit modal
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCategory, setEditingCategory] = useState<Category | null>(null);
  const [categoryName, setCategoryName] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState("");

  // For Delete modal
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [categoryToDelete, setCategoryToDelete] = useState<Category | null>(
    null,
  );
  const [isDeleting, setIsDeleting] = useState(false);
  const [deleteError, setDeleteError] = useState("");

  const API_URL = import.meta.env.VITE_API_URL;
  const token = localStorage.getItem("token");

  useEffect(() => {
    fetchCategories();
  }, []);

  async function fetchCategories() {
    setLoading(true);
    try {
      const response = await fetch(`${API_URL}/api/categories`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!response.ok) {
        throw new Error("Không thể tải danh sách danh mục");
      }
      const data: Category[] = await response.json();
      setCategories(data);
    } catch (err: any) {
      setError(err.message || "Failed to fetch categories");
    } finally {
      setLoading(false);
    }
  }

  function handleOpenModal(category?: Category) {
    setSubmitError("");
    if (category) {
      setEditingCategory(category);
      setCategoryName(category.name);
    } else {
      setEditingCategory(null);
      setCategoryName("");
    }
    setIsModalOpen(true);
  }

  function handleCloseModal() {
    setIsModalOpen(false);
    setEditingCategory(null);
    setCategoryName("");
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!categoryName.trim()) return;

    setSubmitError("");
    setIsSubmitting(true);
    try {
      const isEdit = !!editingCategory;
      const url = isEdit
        ? `${API_URL}/api/categories/${editingCategory.id}`
        : `${API_URL}/api/categories`;
      const method = isEdit ? "PATCH" : "POST";

      const response = await fetch(url, {
        method,
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ name: categoryName }),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(
          errorData.message || errorData.detail || "Lỗi khi lưu danh mục",
        );
      }

      await fetchCategories();
      handleCloseModal();
    } catch (err: any) {
      setSubmitError(err.message || "Lỗi khi lưu danh mục");
    } finally {
      setIsSubmitting(false);
    }
  }

  function promptDelete(category: Category) {
    setCategoryToDelete(category);
    setDeleteError("");
    setIsDeleteModalOpen(true);
  }

  function closeDeleteModal() {
    setIsDeleteModalOpen(false);
    setCategoryToDelete(null);
    setDeleteError("");
  }

  async function confirmDelete() {
    if (!categoryToDelete) return;
    setIsDeleting(true);
    setDeleteError("");

    try {
      const response = await fetch(
        `${API_URL}/api/categories/${categoryToDelete.id}`,
        {
          method: "DELETE",
          headers: { Authorization: `Bearer ${token}` },
        },
      );

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(
          errorData.message || errorData.detail || "Lỗi khi xóa danh mục",
        );
      }

      await fetchCategories();
      closeDeleteModal();
    } catch (err: any) {
      setDeleteError(err.message || "Lỗi khi xóa danh mục");
    } finally {
      setIsDeleting(false);
    }
  }

  return (
    <main className="p-gutter bg-background flex-1 overflow-y-auto">
      <div className="max-w-container-max mx-auto space-y-8 pb-12">
        {/* Page Header */}
        <div className="border-outline-variant flex flex-col justify-between gap-4 border-b pb-4 sm:flex-row sm:items-end">
          <div>
            <h2 className="text-headline-md font-headline-md text-primary tracking-tight">
              Danh mục
            </h2>
            <p className="text-body-md font-body-md text-secondary mt-1">
              Quản lý các danh mục sản phẩm
            </p>
          </div>
          {isAdmin && (
            <div className="flex flex-col items-end gap-1">
              <button
                onClick={() => handleOpenModal()}
                className="bg-primary text-on-primary flex cursor-pointer items-center gap-2 px-4 py-2 transition-all hover:bg-gray-800 active:scale-[0.98]"
              >
                <span className="material-symbols-outlined text-[20px]">
                  add
                </span>
                <span>Thêm mới</span>
              </button>
            </div>
          )}
        </div>

        {error && (
          <div className="mb-6 rounded border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {error}
          </div>
        )}

        {/* Table */}
        <div className="bg-surface-container-lowest border-outline-variant border">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="border-outline-variant text-secondary bg-surface-container border-b text-xs tracking-wider uppercase">
                <tr>
                  <th className="px-6 py-4 font-bold">Tên danh mục</th>
                  <th className="max-w-[200px] px-6 py-4 font-bold">
                    Ngày tạo
                  </th>
                  <th className="max-w-[200px] px-6 py-4 font-bold">
                    Ngày cập nhật
                  </th>
                  {isAdmin && (
                    <th className="w-32 px-6 py-4 text-right font-bold">
                      Thao tác
                    </th>
                  )}
                </tr>
              </thead>
              <tbody className="divide-outline-variant divide-y">
                {loading ? (
                  [...Array(3)].map((_, i) => (
                    <tr key={i} className="animate-pulse">
                      <td className="px-6 py-4">
                        <div className="h-4 w-3/4 rounded bg-gray-200 dark:bg-gray-700"></div>
                      </td>
                      <td className="px-6 py-4">
                        <div className="h-4 w-1/2 rounded bg-gray-200 dark:bg-gray-700"></div>
                      </td>
                      <td className="px-6 py-4">
                        <div className="h-4 w-1/2 rounded bg-gray-200 dark:bg-gray-700"></div>
                      </td>
                      {isAdmin && (
                        <td className="px-6 py-4">
                          <div className="ml-auto h-4 w-20 rounded bg-gray-200 dark:bg-gray-700"></div>
                        </td>
                      )}
                    </tr>
                  ))
                ) : categories.length === 0 ? (
                  <tr>
                    <td
                      colSpan={isAdmin ? 4 : 3}
                      className="text-secondary px-6 py-16 text-center"
                    >
                      <div className="flex flex-col items-center justify-center">
                        <span className="material-symbols-outlined mb-3 text-5xl text-gray-300 dark:text-gray-600">
                          category
                        </span>
                        <p className="text-base font-medium">
                          Chưa có danh mục nào.
                        </p>
                        <p className="text-sm">
                          Hãy thêm danh mục mới để quản lý sản phẩm.
                        </p>
                      </div>
                    </td>
                  </tr>
                ) : (
                  categories.map((category) => (
                    <tr
                      key={category.id}
                      className="hover:bg-surface-container-lowest transition-colors"
                    >
                      <td className="text-primary px-6 py-4 font-medium">
                        {category.name}
                      </td>
                      <td className="text-secondary px-6 py-4">
                        {new Date(category.createdAt).toLocaleString("vi-VN")}
                      </td>
                      <td className="text-secondary px-6 py-4">
                        {new Date(category.updatedAt).toLocaleString("vi-VN")}
                      </td>
                      {isAdmin && (
                        <td className="px-6 py-4 text-right">
                          <div className="flex justify-end gap-3">
                            <button
                              onClick={() => handleOpenModal(category)}
                              className="text-primary cursor-pointer transition-colors hover:text-blue-600"
                            >
                              Sửa
                            </button>
                            <button
                              onClick={() => promptDelete(category)}
                              className="cursor-pointer text-red-500 transition-colors hover:text-red-700"
                            >
                              Xóa
                            </button>
                          </div>
                        </td>
                      )}
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {/* Modal */}
      <Modal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        title={editingCategory ? "Sửa danh mục" : "Thêm danh mục"}
        maxWidth="max-w-md"
      >
        <form onSubmit={handleSubmit} className="space-y-6">
          {submitError && (
            <div className="rounded border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
              {submitError}
            </div>
          )}

          <div>
            <label
              className="text-primary mb-2 block text-sm font-bold uppercase"
              htmlFor="categoryName"
            >
              Tên danh mục <span className="text-red-500">*</span>
            </label>
            <input
              id="categoryName"
              type="text"
              required
              minLength={3}
              maxLength={64}
              className="w-full border-x-0 border-t-0 border-b border-gray-300 bg-transparent px-0 py-3 placeholder:text-gray-400 focus:border-gray-900 focus:ring-0 disabled:opacity-50"
              placeholder="Nhập tên danh mục..."
              value={categoryName}
              onChange={(e) => setCategoryName(e.target.value)}
              disabled={isSubmitting}
            />
          </div>

          <div className="border-outline-variant flex justify-end gap-4 border-t pt-4">
            <button
              type="button"
              onClick={handleCloseModal}
              className="text-secondary hover:bg-surface-container cursor-pointer px-4 py-2 font-bold uppercase transition-colors disabled:cursor-not-allowed disabled:opacity-50"
              disabled={isSubmitting}
            >
              Hủy
            </button>
            <button
              type="submit"
              className="cursor-pointer bg-gray-900 px-6 py-2 font-bold text-white uppercase transition-all duration-200 hover:bg-gray-700 active:scale-[0.98] disabled:cursor-not-allowed disabled:opacity-60"
              disabled={isSubmitting}
            >
              {isSubmitting ? "Đang lưu..." : "Lưu"}
            </button>
          </div>
        </form>
      </Modal>

      {/* Delete Confirmation Modal */}
      <ConfirmModal
        isOpen={isDeleteModalOpen}
        onClose={closeDeleteModal}
        onConfirm={confirmDelete}
        title="Xóa danh mục"
        message={
          <>
            Bạn có chắc chắn muốn xóa danh mục{" "}
            <strong>{categoryToDelete?.name}</strong>? Hành động này không thể
            hoàn tác.
          </>
        }
        confirmText="Xóa"
        isProcessing={isDeleting}
        error={deleteError}
      />
    </main>
  );
}
