import { useEffect, useState } from "react";
import {
  type PageResponse,
  productApi,
  type ProductResponse,
} from "@/api/productApi";
import ProductForm from "./ProductForm";
import { isAdmin as checkIsAdmin } from "@/utils/auth";
import { useLocation, useNavigate } from "react-router-dom";
import ConfirmModal from "@/components/ui/ConfirmModal";
// toast feature removed

const IMAGE_CONTAINER_URL = import.meta.env.VITE_IMAGE_CONTAINER_URL;
const SORT_OPTIONS = [
  { value: "newest", label: "Mới nhất" },
  { value: "price_asc", label: "Giá tăng dần" },
  { value: "price_desc", label: "Giá giảm dần" },
  { value: "discount_asc", label: "Giảm giá tăng dần" },
  { value: "discount_desc", label: "Giảm giá giảm dần" },
];

type PaginationItem =
  | { type: "page"; page: number }
  | { type: "dots"; key: string };

export default function ProductList() {
  const location = useLocation();
  const [tabDeleted, setTabDeleted] = useState(() => {
    const st = (location as unknown as { state?: { tabDeleted?: boolean } })
      ?.state;
    if (st?.tabDeleted != null) {
      return st.tabDeleted;
    }
    const params = new URLSearchParams(location.search);
    return params.get("deleted") === "true";
  });
  // Form (un-applied) filter inputs — API will be called only when user presses the "Lọc" button
  const [formQuery, setFormQuery] = useState("");
  const [formMinPrice, setFormMinPrice] = useState<number | null>(null);
  const [formMaxPrice, setFormMaxPrice] = useState<number | null>(null);
  const [formSortBy, setFormSortBy] = useState("newest");
  // categories for select (single)
  const [categories, setCategories] = useState<{ id: string; name: string }[]>(
    [],
  );
  const [formCategory, setFormCategory] = useState<string>("");

  // Applied filters used to fetch data
  const [appliedFilters, setAppliedFilters] = useState<{
    query?: string | null;
    minPrice?: number | null;
    maxPrice?: number | null;
    sortBy?: string;
    categoryName?: string | null;
  }>({
    query: null,
    minPrice: null,
    maxPrice: null,
    sortBy: "newest",
    categoryName: null,
  });

  const API_URL = import.meta.env.VITE_API_URL;
  const token = localStorage.getItem("token");

  useEffect(() => {
    // fetch categories for the multi-select
    (async function fetchCats() {
      try {
        const res = await fetch(`${API_URL}/api/categories`, {
          headers: token ? { Authorization: `Bearer ${token}` } : {},
        });
        if (!res.ok) return;
        const data = await res.json();
        setCategories(data || []);
      } catch (_) {
        // ignore
      }
    })();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
  const [page, setPage] = useState(0);
  const size = 10;
  const [data, setData] = useState<PageResponse<ProductResponse> | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editing, setEditing] = useState<ProductResponse | null>(null);
  const isAdmin = checkIsAdmin();
  const navigate = useNavigate();
  // toast removed

  const [deleteToConfirm, setDeleteToConfirm] = useState<{
    open: boolean;
    id?: string;
    name?: string;
    action?: "delete" | "restore";
  }>({ open: false });

  const paginationPages = (): PaginationItem[] => {
    let dotsCount = 0;
    const toItems = (arr: (number | "...")[]): PaginationItem[] =>
      arr.map((v) =>
        v === "..."
          ? { type: "dots", key: `dots-${dotsCount++}` }
          : { type: "page", page: v },
      );

    const totalPages = data?.page?.totalPages ?? 1;
    const currentPage = data?.page?.number ?? 0;

    if (totalPages <= 5)
      return toItems(Array.from({ length: totalPages }, (_, i) => i));
    if (currentPage <= 2) return toItems([0, 1, 2, "...", totalPages - 1]);
    if (currentPage >= totalPages - 3)
      return toItems([
        0,
        "...",
        totalPages - 3,
        totalPages - 2,
        totalPages - 1,
      ]);
    return toItems([
      0,
      "...",
      currentPage - 1,
      currentPage,
      currentPage + 1,
      "...",
      totalPages - 1,
    ]);
  };

  const totalLabel = () => {
    if (loading && !data) return "Đang tải...";
    if (!data || data.page.totalElements === 0) return "Không có sản phẩm";
    return (
      <>
        Có{" "}
        <span className="text-primary font-bold">
          {data.page.totalElements}
        </span>{" "}
        sản phẩm
      </>
    );
  };

  // Fetch products when page, tabDeleted or applied filter set changes
  useEffect(() => {
    fetchProducts();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, tabDeleted, appliedFilters]);

  // Open edit modal when navigated with state.editId
  useEffect(() => {
    const st = (
      location as unknown as {
        state?: { editId?: string; tabDeleted?: boolean };
      }
    )?.state;
    if (st?.editId) {
      (async () => {
        try {
          const p = await productApi.getById(st.editId as string);
          setEditing(p);
          setIsFormOpen(true);
          // clear location state so it doesn't reopen; preserve current search (query params)
          navigate(location.pathname + location.search, { replace: true });
        } catch (_) {
          // ignore
        }
      })();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const SKELETON_ROW_KEYS = ["sk-r-0", "sk-r-1", "sk-r-2", "sk-r-3"];
  const SKELETON_COL_KEYS = ["sk-c-0", "sk-c-1", "sk-c-2", "sk-c-3", "sk-c-4"];

  async function fetchProducts() {
    setLoading(true);
    setError("");
    try {
      const res = await productApi.getList({
        page,
        size,
        sortBy: appliedFilters.sortBy ?? "newest",
        query: appliedFilters.query ?? null,
        minPrice: appliedFilters.minPrice,
        maxPrice: appliedFilters.maxPrice,
        categoryName: appliedFilters.categoryName ?? undefined,
        onlyDeleted: tabDeleted,
      });
      setData(res);
    } catch (_) {
      const msg = _ instanceof Error ? _.message : String(_);
      setError(msg || "Lỗi khi tải sản phẩm");
    } finally {
      setLoading(false);
    }
  }

  function openCreate() {
    setEditing(null);
    setIsFormOpen(true);
  }

  function openEdit(p: ProductResponse) {
    setEditing(p);
    setIsFormOpen(true);
  }

  // deletion is handled inline via setDeleteToConfirm when user clicks the delete button

  return (
    <main className="p-gutter bg-background flex-1 overflow-y-auto">
      <div className="max-w-container-max mx-auto space-y-4 pb-8">
        <div className="mb-3 flex flex-wrap items-center justify-between gap-3">
          <div>
            <h2 className="font-headline-md text-headline-md text-primary tracking-tight">
              Sản phẩm
            </h2>
            <p className="font-body-base text-body-sm text-secondary">
              Quản lý sản phẩm
            </p>
          </div>

          <div className="flex items-center gap-2">
            <div className="flex gap-2">
              <button
                className={`cursor-pointer px-3 py-1.5 text-[11px] font-bold tracking-widest uppercase ${!tabDeleted ? "bg-primary text-on-primary" : "text-secondary hover:bg-surface-container-high"}`}
                onClick={() => {
                  setTabDeleted(false);
                  setPage(0);
                  // remove deleted query param when switching to active tab
                  navigate(location.pathname, { replace: true });
                }}
              >
                Đang kinh doanh
              </button>
              <button
                className={`cursor-pointer px-3 py-1.5 text-[11px] font-bold tracking-widest uppercase ${tabDeleted ? "bg-primary text-on-primary" : "text-secondary hover:bg-surface-container-high"}`}
                onClick={() => {
                  setTabDeleted(true);
                  setPage(0);
                  // add deleted query param so it's preserved in history
                  navigate(location.pathname + "?deleted=true", {
                    replace: true,
                  });
                }}
              >
                Ngừng kinh doanh
              </button>
            </div>

            <div>
              {isAdmin && (
                <button
                  onClick={openCreate}
                  className="bg-primary text-on-primary cursor-pointer px-3 py-1.5 text-[11px] font-bold tracking-widest uppercase"
                >
                  Thêm mới
                </button>
              )}
            </div>
          </div>
        </div>

        <form
          className="flex flex-wrap items-end justify-between gap-4"
          onSubmit={(e) => {
            e.preventDefault();
            setPage(0);
            setAppliedFilters({
              query: formQuery || null,
              minPrice: formMinPrice,
              maxPrice: formMaxPrice,
              sortBy: formSortBy,
              categoryName: formCategory || null,
            });
          }}
        >
          <div className="flex flex-wrap items-end gap-3">
            <input
              placeholder="Tìm kiếm..."
              className="border px-2 py-2"
              value={formQuery}
              onChange={(e) => setFormQuery(e.target.value)}
            />

            <div className="flex flex-col gap-1">
              <label className="text-sm">Danh mục</label>
              <select
                value={formCategory}
                onChange={(e) => setFormCategory(e.target.value)}
                className="w-48 border px-2 py-2"
              >
                <option value="">Tất cả</option>
                {categories.map((c) => (
                  <option key={c.id} value={c.name}>
                    {c.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="flex flex-col gap-1">
              <label htmlFor="sortBy" className="text-sm">
                Sắp xếp
              </label>
              <select
                id="sortBy"
                name="sortBy"
                value={formSortBy}
                onChange={(e) => setFormSortBy(e.target.value)}
                className="border px-2 py-2"
              >
                {SORT_OPTIONS.map((o) => (
                  <option key={o.value} value={o.value}>
                    {o.label}
                  </option>
                ))}
              </select>
            </div>

            <div className="flex flex-col gap-1">
              <label className="text-sm">
                Giá từ <span className="text-secondary text-xs">(VNĐ)</span>
              </label>
              <input
                placeholder="Giá từ"
                className="w-28 border px-2 py-2"
                value={formMinPrice ?? ""}
                onChange={(e) =>
                  setFormMinPrice(
                    e.target.value ? Number(e.target.value) : null,
                  )
                }
                type="number"
              />
            </div>

            <div className="flex flex-col gap-1">
              <label className="text-sm">
                Đến <span className="text-secondary text-xs">(VNĐ)</span>
              </label>
              <input
                placeholder="đến"
                className="w-28 border px-2 py-2"
                value={formMaxPrice ?? ""}
                onChange={(e) =>
                  setFormMaxPrice(
                    e.target.value ? Number(e.target.value) : null,
                  )
                }
                type="number"
              />
            </div>
          </div>

          <div className="flex items-center gap-2">
            <button
              type="submit"
              className="cursor-pointer bg-gray-900 px-3 py-2 text-white"
            >
              Lọc
            </button>
            <button
              type="button"
              onClick={() => {
                // reset form and applied filters
                setFormQuery("");
                setFormMinPrice(null);
                setFormMaxPrice(null);
                setFormSortBy("newest");
                setFormCategory("");
                setAppliedFilters({
                  query: null,
                  minPrice: null,
                  maxPrice: null,
                  sortBy: "newest",
                  categoryName: null,
                });
                setPage(0);
              }}
              className="cursor-pointer border px-3 py-2"
            >
              Reset
            </button>
          </div>
        </form>

        <div className="bg-surface border-outline-variant overflow-hidden border">
          {error && (
            <div className="text-error border-error/30 bg-error/5 border-b px-6 py-4 text-sm">
              {error}
            </div>
          )}

          <table className="w-full border-collapse text-left">
            <thead>
              <tr className="bg-surface-container-high border-outline-variant border-b">
                {["Tên", "Giá", "Tồn kho", "Danh mục", "Thao tác"].map(
                  (h, i) => (
                    <th
                      key={h}
                      className={`font-label-caps text-on-surface-variant px-6 py-2 text-[11px] tracking-[0.14em] uppercase ${i === 4 ? "text-right" : ""}`}
                    >
                      {h}
                    </th>
                  ),
                )}
              </tr>
            </thead>

            <tbody className="divide-outline-variant divide-y">
              {loading ? (
                SKELETON_ROW_KEYS.map((rowKey) => (
                  <tr key={rowKey}>
                    {SKELETON_COL_KEYS.map((colKey) => (
                      <td key={colKey} className="px-6 py-5">
                        <div className="bg-surface-container-high h-4 animate-pulse rounded" />
                      </td>
                    ))}
                  </tr>
                ))
              ) : data?.content?.length === 0 ? (
                <tr>
                  <td
                    colSpan={5}
                    className="text-secondary px-6 py-16 text-center text-sm"
                  >
                    <span className="material-symbols-outlined text-outline mb-2 block text-4xl">
                      inventory_2
                    </span>
                    <p>Không có sản phẩm</p>
                  </td>
                </tr>
              ) : (
                data?.content?.map((p: ProductResponse) => (
                  <tr
                    key={p.id}
                    className="hover:bg-surface-container-low group transition-colors"
                  >
                    <td className="px-6 py-5">
                      <div className="flex items-center space-x-4">
                        <div className="bg-surface-container-highest border-outline-variant flex h-12 w-12 items-center justify-center overflow-hidden rounded-md border text-xs">
                          {p.productImages && p.productImages.length > 0 ? (
                            <img
                              src={`${IMAGE_CONTAINER_URL}/${(p.productImages ?? [])[0].file}`}
                              alt={p.name}
                              className="h-full w-full object-cover"
                            />
                          ) : (
                            <div className="text-secondary">—</div>
                          )}
                        </div>
                        <div>
                          <p className="font-body-base text-body-base text-primary font-bold">
                            {p.name}
                          </p>
                          <p className="text-secondary text-[11px]">
                            {p.id.toUpperCase()}
                          </p>
                        </div>
                      </div>
                    </td>
                    <td className="font-body-base text-body-base text-secondary px-6 py-5">
                      {(() => {
                        const hasDiscount =
                          (p.discount ?? 0) > 0 &&
                          p.priceDiscount != null &&
                          p.priceDiscount !== p.price;
                        if (hasDiscount) {
                          return (
                            <div className="flex flex-col">
                              <div className="flex items-center gap-2">
                                <span className="text-secondary text-[13px] line-through">
                                  {p.price?.toLocaleString?.() ?? p.price} ₫
                                </span>
                                <span className="text-error text-xs font-bold">
                                  -{p.discount}%
                                </span>
                              </div>
                              <span className="font-body-base text-body-base text-primary font-bold">
                                {p.priceDiscount?.toLocaleString?.() ??
                                  p.priceDiscount}{" "}
                                ₫
                              </span>
                            </div>
                          );
                        }
                        return (
                          <div className="font-body-base text-body-base text-secondary">
                            {p.price?.toLocaleString?.() ?? p.price} ₫
                          </div>
                        );
                      })()}
                    </td>
                    <td className="font-body-base text-body-base text-secondary px-6 py-5">
                      {(function () {
                        // If product has sizes, sum their inStock. Otherwise use product.inStock
                        if (p.productSizes && p.productSizes.length > 0) {
                          return p.productSizes.reduce(
                            (s, it) => s + (it.inStock ?? 0),
                            0,
                          );
                        }
                        return p.inStock ?? 0;
                      })()}
                    </td>
                    <td className="text-secondary px-6 py-5">
                      {(p.categories ?? []).map((c) => c.name).join(", ")}
                    </td>
                    <td className="px-6 py-5 text-right">
                      <div className="flex items-center justify-end space-x-1">
                        <button
                          title="Xem chi tiết"
                          onClick={() =>
                            navigate(`/product/${p.id}`, {
                              state: { tabDeleted },
                            })
                          }
                          className="border-outline-variant hover:border-primary text-outline hover:text-primary flex h-9 w-9 cursor-pointer items-center justify-center border transition-all"
                        >
                          <span className="material-symbols-outlined text-[18px]">
                            visibility
                          </span>
                        </button>
                        {isAdmin && (
                          <>
                            <button
                              title="Sửa"
                              onClick={() => openEdit(p)}
                              className="border-outline-variant hover:border-primary text-outline hover:text-primary flex h-9 w-9 cursor-pointer items-center justify-center border transition-all"
                            >
                              <span className="material-symbols-outlined text-[18px]">
                                edit
                              </span>
                            </button>
                            {tabDeleted ? (
                              <button
                                title="Khôi phục"
                                onClick={() =>
                                  setDeleteToConfirm({
                                    open: true,
                                    id: p.id,
                                    name: p.name,
                                    action: "restore",
                                  })
                                }
                                className="border-outline-variant hover:border-tertiary text-outline hover:text-tertiary flex h-9 w-9 cursor-pointer items-center justify-center border transition-all"
                              >
                                <span className="material-symbols-outlined text-[18px]">
                                  restore
                                </span>
                              </button>
                            ) : (
                              <button
                                title="Xóa"
                                onClick={() =>
                                  setDeleteToConfirm({
                                    open: true,
                                    id: p.id,
                                    name: p.name,
                                    action: "delete",
                                  })
                                }
                                className="border-outline-variant hover:border-error text-outline hover:text-error flex h-9 w-9 cursor-pointer items-center justify-center border transition-all"
                              >
                                <span className="material-symbols-outlined text-[18px]">
                                  delete
                                </span>
                              </button>
                            )}
                          </>
                        )}
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>

          {data && (
            <div className="border-outline-variant bg-surface-container-low flex items-center justify-between border-t px-6 py-3">
              <span className="font-body-base text-body-base text-secondary">
                {totalLabel()}
              </span>

              <div className="flex space-x-2">
                <button
                  disabled={page <= 0}
                  onClick={() => setPage((p) => Math.max(0, p - 1))}
                  className="border-outline-variant hover:border-primary flex h-8 w-8 cursor-pointer items-center justify-center border transition-all disabled:cursor-not-allowed disabled:opacity-40"
                >
                  <span className="material-symbols-outlined text-sm">
                    chevron_left
                  </span>
                </button>

                {paginationPages().map((item) =>
                  item.type === "dots" ? (
                    <span
                      key={item.key}
                      className="flex h-8 w-8 items-center justify-center text-xs"
                    >
                      ...
                    </span>
                  ) : (
                    <button
                      key={item.page}
                      onClick={() => setPage(item.page)}
                      className={`flex h-8 w-8 cursor-pointer items-center justify-center text-xs font-bold transition-all ${
                        page === item.page
                          ? "bg-primary text-on-primary"
                          : "border-outline-variant hover:border-primary border"
                      }`}
                    >
                      {item.page + 1}
                    </button>
                  ),
                )}

                <button
                  disabled={page >= data.page.totalPages - 1}
                  onClick={() =>
                    setPage((p) => Math.min(data.page.totalPages - 1, p + 1))
                  }
                  className="border-outline-variant hover:border-primary flex h-8 w-8 cursor-pointer items-center justify-center border transition-all disabled:cursor-not-allowed disabled:opacity-40"
                >
                  <span className="material-symbols-outlined text-sm">
                    chevron_right
                  </span>
                </button>
              </div>

              <span className="font-label-caps text-label-caps text-outline text-xs">
                Trang {data.page.number + 1} / {data.page.totalPages}
              </span>
            </div>
          )}
        </div>
      </div>

      <ProductForm
        isOpen={isFormOpen}
        onClose={() => setIsFormOpen(false)}
        onSaved={() => fetchProducts()}
        editing={editing}
      />
      <ConfirmModal
        isOpen={deleteToConfirm.open}
        onClose={() => setDeleteToConfirm({ open: false })}
        onConfirm={async () => {
          if (!deleteToConfirm.id) return;
          const action = deleteToConfirm.action ?? "delete";
          try {
            if (action === "restore") {
              // build payload similar to ProductDetail restore: include images, sizes, categories and isRestore flag
              const p = data?.content?.find((x) => x.id === deleteToConfirm.id);
              const payload: any = {
                name: p?.name,
                productImages: (p?.productImages ?? []).map((pi) => ({
                  id: pi.id,
                  file: pi.file,
                })),
                productSizes: (p?.productSizes ?? []).map((s) => ({
                  id: s.id,
                  name: s.name,
                  inStock: s.inStock,
                })),
                categoryIds: (p?.categories ?? []).map((c) => c.id),
                isRestore: true,
              };
              await productApi.updateProduct(deleteToConfirm.id, payload);
              // restored
            } else {
              await productApi.deleteProduct(deleteToConfirm.id);
              // deleted
            }
            // refresh current tab content, keep tabDeleted state as-is
            fetchProducts();
          } catch (e) {
            const msg = e instanceof Error ? e.message : String(e);
            console.error(
              msg ||
                (deleteToConfirm.action === "restore"
                  ? "Lỗi khi khôi phục"
                  : "Lỗi khi xóa"),
            );
          } finally {
            setDeleteToConfirm({ open: false });
          }
        }}
        title={
          deleteToConfirm.action === "restore"
            ? "Khôi phục sản phẩm"
            : "Xóa sản phẩm"
        }
        message={
          deleteToConfirm.name
            ? deleteToConfirm.action === "restore"
              ? `Bạn có chắc muốn khôi phục sản phẩm "${deleteToConfirm.name}"?`
              : `Bạn có chắc muốn xóa sản phẩm "${deleteToConfirm.name}"?`
            : deleteToConfirm.action === "restore"
              ? "Bạn có chắc muốn khôi phục sản phẩm này?"
              : "Bạn có chắc muốn xóa sản phẩm này?"
        }
        confirmText={deleteToConfirm.action === "restore" ? "Khôi phục" : "Xóa"}
        cancelText="Hủy"
      />
    </main>
  );
}
