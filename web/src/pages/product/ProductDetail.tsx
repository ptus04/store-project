import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { productApi, type ProductResponse } from "@/api/productApi";
import { formatDate } from "@/utils/customerUtils";
import { isAdmin } from "@/utils/auth";
import ConfirmModal from "@/components/ui/ConfirmModal";
import ProductForm from "./ProductForm";

function FieldRow({
  icon,
  label,
  value,
  valueClassName,
}: {
  icon: string;
  label: string;
  value: string;
  valueClassName?: string;
}) {
  return (
    <div className="flex items-center space-x-4 py-4">
      <span className="material-symbols-outlined text-outline w-5 shrink-0 text-[20px]">
        {icon}
      </span>
      <span className="text-secondary w-44 shrink-0 text-sm">{label}</span>
      <span
        className={`font-body-base text-body-base text-sm font-medium ${valueClassName ?? "text-primary"}`}
      >
        {value}
      </span>
    </div>
  );
}

function ProductImageCard({
  title,
  id,
  deleted,
  imageUrls,
}: {
  title: string;
  id: string;
  deleted: boolean;
  imageUrls?: string[] | null;
}) {
  const badgeClass = deleted
    ? "border-error/40 text-error bg-error/5"
    : "border-tertiary/40 text-tertiary bg-tertiary/5";
  const statusLabel = deleted ? "Đã xóa" : "Còn kinh doanh";
  const [selected, setSelected] = useState(0);
  const imgs = imageUrls ?? [];
  const main = imgs.length > 0 ? imgs[selected] : null;

  return (
    <div className="bg-surface border-outline-variant flex flex-col items-center justify-center space-y-4 border p-8 text-center">
      <div
        className={`border-outline-variant bg-surface-container-highest flex h-52 w-52 items-center justify-center overflow-hidden border text-2xl font-bold`}
      >
        {main ? (
          <img src={main} alt={title} className="h-full w-full object-cover" />
        ) : (
          <span className="text-secondary">No image</span>
        )}
      </div>
      {imgs.length > 1 && (
        <div className="max-w-full overflow-x-auto overflow-y-hidden pb-1">
          <div className="flex w-max flex-nowrap gap-2">
            {imgs.map((u, i) => (
              <button
                key={`${u}-${i}`}
                onClick={() => setSelected(i)}
                className={`h-12 w-12 flex-none cursor-pointer overflow-hidden rounded border ${i === selected ? "border-primary" : "border-outline-variant"}`}
              >
                <img
                  src={u}
                  alt={`${title}-${i}`}
                  className="h-full w-full object-cover"
                />
              </button>
            ))}
          </div>
        </div>
      )}
      <div>
        <p className="font-headline-sm text-primary text-lg font-bold">
          {title}
        </p>
        <p className="text-secondary mt-1 text-xs">{id.toUpperCase()}</p>
      </div>
      <span
        className={`border px-4 py-1 text-[11px] font-bold tracking-widest uppercase ${badgeClass}`}
      >
        {statusLabel}
      </span>
    </div>
  );
}

export default function ProductDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const location = useLocation();
  const IMAGE_CONTAINER_URL = import.meta.env.VITE_IMAGE_CONTAINER_URL;
  const canEdit = isAdmin();

  const [product, setProduct] = useState<ProductResponse | null>(null);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState<ProductResponse | null>(
    null,
  );
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [deleting, setDeleting] = useState(false);
  const [restoring, setRestoring] = useState(false);
  const [deleteConfirmOpen, setDeleteConfirmOpen] = useState(false);
  const [restoreConfirmOpen, setRestoreConfirmOpen] = useState(false);
  // toast removed

  const tabDeleted = Boolean(
    (location.state as { tabDeleted?: boolean } | null | undefined)?.tabDeleted,
  );
  const backToProducts = tabDeleted ? "/products?deleted=true" : "/products";

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    productApi
      .getById(id)
      .then((p) => {
        setProduct(p);
      })
      .catch((e) => setError(e?.message || "Không thể tải sản phẩm"))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) {
    return (
      <main className="p-margin-page">
        <div className="flex items-center justify-center space-x-3 py-16">
          <span className="material-symbols-outlined text-outline animate-spin text-2xl">
            progress_activity
          </span>
          <span className="text-secondary text-sm">Đang tải...</span>
        </div>
      </main>
    );
  }

  if (error || !product) {
    return (
      <main className="p-margin-page">
        <div className="py-16 text-center">
          <span className="material-symbols-outlined text-outline mb-3 block text-5xl">
            error_outline
          </span>
          <p className="text-secondary mb-6 text-sm">
            {error ?? "Không tìm thấy sản phẩm."}
          </p>
          <button
            onClick={() => navigate(backToProducts)}
            className="bg-primary text-on-primary cursor-pointer px-6 py-2 text-xs font-bold tracking-widest uppercase"
          >
            Quay lại
          </button>
        </div>
      </main>
    );
  }

  const deleted = !!product.deletedAt;

  const fields = [
    { icon: "paid", label: "Giá", value: `${product.price} ₫` },
    {
      icon: "inventory_2",
      label: "Tồn kho",
      value: String(
        product.productSizes && product.productSizes.length > 0
          ? product.productSizes.reduce((s, it) => s + (it.inStock ?? 0), 0)
          : (product.inStock ?? 0),
      ),
    },
    {
      icon: "category",
      label: "Danh mục",
      value: (product.categories ?? []).map((c) => c.name).join(", ") || "—",
    },
    {
      icon: "straighten",
      label: "Kích cỡ",
      value:
        (product.productSizes ?? []).length === 0
          ? "Không có kích cỡ"
          : (product.productSizes ?? [])
              .map((s) => `${s.name} (${s.inStock ?? 0})`)
              .join(", "),
    },
    {
      icon: "calendar_today",
      label: "Ngày tạo",
      value: formatDate(product.createdAt ?? null),
    },
    {
      icon: "update",
      label: "Cập nhật lần cuối",
      value: formatDate(product.updatedAt ?? null),
    },
    {
      icon: deleted ? "delete_forever" : "check_circle",
      label: "Trạng thái",
      value: deleted
        ? `Đã xóa (${formatDate(product.deletedAt ?? null)})`
        : "Còn kinh doanh",
      valueClassName: deleted ? "text-error" : "text-tertiary",
    },
  ];

  return (
    <main className="p-margin-page">
      <button
        onClick={() => navigate(backToProducts)}
        className="text-secondary hover:text-primary mb-stack-lg flex cursor-pointer items-center space-x-1 text-xs font-bold tracking-widest uppercase transition-colors"
      >
        <span className="material-symbols-outlined text-[16px]">
          arrow_back
        </span>
        <span>Quay lại danh sách</span>
      </button>

      <div className="mb-gutter flex items-end justify-between">
        <div>
          <h2 className="font-headline-lg text-headline-lg text-primary tracking-tight">
            Chi tiết sản phẩm
          </h2>
          <p className="font-body-base text-body-base text-secondary">
            Thông tin đầy đủ về sản phẩm
          </p>
        </div>

        {canEdit && (
          <div className="flex items-center gap-3">
            <button
              onClick={() => {
                // open inline edit modal on detail page
                setEditingProduct(product);
                setIsFormOpen(true);
              }}
              className="flex cursor-pointer items-center gap-2 border px-4 py-2 text-xs font-bold tracking-widest uppercase"
              title="Chỉnh sửa sản phẩm"
            >
              <span className="material-symbols-outlined text-[18px]">
                edit
              </span>
              <span>Sửa</span>
            </button>

            {!deleted ? (
              <>
                <button
                  onClick={() => setDeleteConfirmOpen(true)}
                  className="bg-error/10 text-error flex cursor-pointer items-center gap-2 border px-4 py-2 text-xs font-bold tracking-widest uppercase"
                  disabled={deleting || restoring}
                  title={
                    deleting || restoring ? "Đang xử lý..." : "Xóa sản phẩm"
                  }
                >
                  {deleting ? (
                    <span className="material-symbols-outlined animate-spin text-[18px]">
                      progress_activity
                    </span>
                  ) : (
                    <span className="material-symbols-outlined text-[18px]">
                      delete
                    </span>
                  )}
                  <span>Xóa</span>
                </button>

                <ConfirmModal
                  isOpen={deleteConfirmOpen}
                  onClose={() => setDeleteConfirmOpen(false)}
                  onConfirm={async () => {
                    setDeleting(true);
                    try {
                      await productApi.deleteProduct(product!.id);
                      // deleted
                      navigate(backToProducts);
                    } catch (e: any) {
                      console.error(e?.message || "Lỗi khi xóa sản phẩm");
                    } finally {
                      setDeleting(false);
                      setDeleteConfirmOpen(false);
                    }
                  }}
                  title="Xóa sản phẩm"
                  message={`Bạn có chắc muốn xóa sản phẩm "${product!.name}"?`}
                  confirmText="Xóa"
                  cancelText="Hủy"
                  isProcessing={deleting}
                />
              </>
            ) : (
              <>
                <button
                  onClick={() => setRestoreConfirmOpen(true)}
                  className="bg-tertiary/10 text-tertiary flex cursor-pointer items-center gap-2 border px-4 py-2 text-xs font-bold tracking-widest uppercase"
                  disabled={deleting || restoring}
                  title={
                    deleting || restoring
                      ? "Đang xử lý..."
                      : "Khôi phục sản phẩm"
                  }
                >
                  {restoring ? (
                    <span className="material-symbols-outlined animate-spin text-[18px]">
                      progress_activity
                    </span>
                  ) : (
                    <span className="material-symbols-outlined text-[18px]">
                      restore
                    </span>
                  )}
                  <span>Khôi phục</span>
                </button>

                <ConfirmModal
                  isOpen={restoreConfirmOpen}
                  onClose={() => setRestoreConfirmOpen(false)}
                  onConfirm={async () => {
                    setRestoring(true);
                    try {
                      const payload: any = {
                        name: product!.name,
                        productImages: (product!.productImages ?? []).map(
                          (pi) => ({ id: pi.id, file: pi.file }),
                        ),
                        productSizes: (product!.productSizes ?? []).map(
                          (s) => ({
                            id: s.id,
                            name: s.name,
                            inStock: s.inStock,
                          }),
                        ),
                        categoryIds: (product!.categories ?? []).map(
                          (c) => c.id,
                        ),
                        isRestore: true,
                      };
                      await productApi.updateProduct(product!.id, payload);
                      // restored
                      const refreshed = await productApi.getById(product!.id);
                      setProduct(refreshed);
                    } catch (e: any) {
                      console.error(e?.message || "Lỗi khi khôi phục sản phẩm");
                    } finally {
                      setRestoring(false);
                      setRestoreConfirmOpen(false);
                    }
                  }}
                  title="Khôi phục sản phẩm"
                  message={`Bạn có chắc muốn khôi phục sản phẩm "${product!.name}"?`}
                  confirmText="Khôi phục"
                  cancelText="Hủy"
                  isProcessing={restoring}
                />
              </>
            )}
          </div>
        )}
      </div>

      <div className="gap-gutter grid grid-cols-1 lg:grid-cols-3">
        <ProductImageCard
          title={product.name}
          id={product.id}
          deleted={deleted}
          imageUrls={(product.productImages ?? []).map(
            (pi) => `${IMAGE_CONTAINER_URL}/${pi.file}`,
          )}
        />

        <div className="bg-surface border-outline-variant border px-8 py-4 lg:col-span-2">
          <p className="font-label-caps text-label-caps text-outline mb-2 pt-2 tracking-widest uppercase">
            Thông tin sản phẩm
          </p>
          <div className="divide-outline-variant divide-y">
            {fields.map((f) => (
              <FieldRow
                key={f.label}
                icon={f.icon}
                label={f.label}
                value={f.value}
                valueClassName={f.valueClassName}
              />
            ))}

            <div className="py-4">
              <div className="text-secondary text-sm">Mô tả</div>
              <div className="mt-2 whitespace-pre-line">
                {product.description ?? "—"}
              </div>
            </div>

            <div className="py-4">
              <div className="text-secondary text-sm">Hướng dẫn bảo quản</div>
              <div className="mt-2 whitespace-pre-line">
                {product.careInstructions ?? "—"}
              </div>
            </div>
          </div>
        </div>
      </div>
      {/* inline edit modal for this product detail */}
      <ProductForm
        isOpen={isFormOpen}
        onClose={() => setIsFormOpen(false)}
        onSaved={async () => {
          // refresh product detail after save
          if (!product) return;
          try {
            const refreshed = await productApi.getById(product.id);
            setProduct(refreshed);
          } catch (e) {
            console.error("Failed to refresh product after save", e);
          }
        }}
        editing={editingProduct}
      />
    </main>
  );
}
