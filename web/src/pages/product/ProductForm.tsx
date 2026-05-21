import React, { useEffect, useRef, useState } from "react";
import Modal from "@components/ui/Modal";
import ConfirmModal from "@components/ui/ConfirmModal";
import {
  productApi,
  type ProductCreateRequest,
  type ProductImagePutRequest,
  type ProductResponse,
  type ProductSizePutRequest,
  type ProductUpdateRequest,
} from "@/api/productApi";

interface CategoryOption {
  id: string;
  name: string;
}

interface Props {
  isOpen: boolean;
  editing?: ProductResponse | null;

  onClose(): void;

  onSaved(): void;
}

export default function ProductForm({
  isOpen,
  onClose,
  onSaved,
  editing,
}: Props) {
  const controlClass =
    "border px-2 py-2 text-sm outline-none focus:border-primary";
  const sectionLabelClass = "mb-2 block text-sm font-semibold";
  const actionPrimaryClass = "cursor-pointer bg-gray-900 px-3 py-2 text-white";
  const actionSecondaryClass = "cursor-pointer border px-3 py-2";
  const [categories, setCategories] = useState<CategoryOption[]>([]);
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [care, setCare] = useState("");
  const [price, setPrice] = useState<number>(0);
  const [inStock, setInStock] = useState<number>(0);
  const [discount, setDiscount] = useState<number>(0);
  const [selectedCategories, setSelectedCategories] = useState<string[]>([]);
  const [images, setImages] = useState<ProductImagePutRequest[]>([]);
  const IMAGE_CONTAINER_URL = import.meta.env.VITE_IMAGE_CONTAINER_URL;
  type UploadFile = {
    id: string;
    file: File;
    progress: number;
    status: "pending" | "uploading" | "done" | "error";
    error?: string;
  };

  const [newFiles, setNewFiles] = useState<UploadFile[]>([]);
  const [sizes, setSizes] = useState<ProductSizePutRequest[]>([]);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const fileInputRef = useRef<HTMLInputElement | null>(null);

  useEffect(() => {
    (async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await fetch(
          `${import.meta.env.VITE_API_URL}/api/categories`,
          {
            headers: token ? { Authorization: `Bearer ${token}` } : {},
          },
        );
        if (!res.ok) throw new Error("Không lấy được danh mục");
        setCategories(await res.json());
      } catch {
        // ignore
      }
    })();
  }, []);

  useEffect(() => {
    if (editing) {
      setName(editing.name ?? "");
      setDescription(editing.description ?? "");
      setCare(editing.careInstructions ?? "");
      setPrice(editing.price ?? 0);
      setInStock(editing.inStock ?? 0);
      setDiscount(editing.discount ?? 0);
      setSelectedCategories((editing.categories ?? []).map((c) => c.id));
      setImages(
        (editing.productImages ?? []).map((img) => ({
          id: img.id,
          file: img.file,
        })),
      );
      setSizes(
        (editing.productSizes ?? []).map((s) => ({
          id: s.id,
          name: s.name,
          inStock: s.inStock,
        })),
      );
    } else {
      // reset
      setName("");
      setDescription("");
      setCare("");
      setPrice(0);
      setInStock(0);
      setDiscount(0);
      setSelectedCategories([]);
      setImages([]);
      setSizes([]);
      setNewFiles([]);
    }
  }, [editing, isOpen]);

  // image container base URL from env

  function toggleCategory(id: string) {
    setSelectedCategories((prev) =>
      prev.includes(id) ? prev.filter((p) => p !== id) : [...prev, id],
    );
  }

  function handleFilesChange(e: React.ChangeEvent<HTMLInputElement>) {
    const files = e.target.files;
    if (!files) return;
    const arr = Array.from(files).map((f) => ({
      id: `${Date.now()}-${Math.random().toString(36).slice(2, 9)}`,
      file: f,
      progress: 0,
      status: "pending" as const,
    }));
    setNewFiles((prev) => [...prev, ...arr]);
  }

  function promptRemoveExistingImage(index: number) {
    setImageToDelete({ open: true, index, isExisting: true });
  }

  function promptRemoveNewFile(index: number) {
    setImageToDelete({ open: true, index, isExisting: false });
  }

  function removeExistingImage(index: number) {
    // prevent removing last image (there must be at least 1 image)
    if (images.length + newFiles.length <= 1) {
      setError("Sản phẩm phải có ít nhất 01 hình ảnh");
      return;
    }
    setImages((prev) => prev.filter((_, i) => i !== index));
  }

  function removeNewFile(index: number) {
    // prevent removing last image (existing + newFiles must be at least 1)
    if (images.length + newFiles.length <= 1) {
      setError("Sản phẩm phải có ít nhất 01 hình ảnh");
      return;
    }
    setNewFiles((prev) => prev.filter((_, i) => i !== index));
  }

  function removeSize(index: number) {
    // store index in ref to avoid stale closure issues when confirming
    sizeToDeleteRef.current = index;
    setSizeToDelete({ open: true });
  }

  function addSize() {
    setSizes((s) => [...s, { name: "", inStock: 0 }]);
  }

  function updateSize(
    i: number,
    field: keyof ProductSizePutRequest,
    value: string | number | null,
  ) {
    setSizes((prev) =>
      prev.map((it, idx) => (idx === i ? { ...it, [field]: value } : it)),
    );
  }

  async function submitProduct(keepOpen = false) {
    setSubmitting(true);
    setError("");

    try {
      // upload new files first
      // Work on a snapshot of newFiles to avoid modifying while iterating
      const pending = [...newFiles];
      // local accumulator to build final images without relying on async state updates
      const finalImagesLocal: ProductImagePutRequest[] = [...images];

      for (const uf of pending) {
        // update status
        setNewFiles((prev) =>
          prev.map((p) =>
            p.id === uf.id ? { ...p, status: "uploading", progress: 0 } : p,
          ),
        );
        const sas = await productApi.getUploadSas("images", uf.file.name);
        try {
          await productApi.uploadToSasWithProgress(sas.url, uf.file, (pct) => {
            setNewFiles((prev) =>
              prev.map((p) => (p.id === uf.id ? { ...p, progress: pct } : p)),
            );
          });

          // mark as done and remove temporary file preview -> replace with server image entry
          setNewFiles((prev) => prev.filter((p) => p.id !== uf.id));

          const newImg: ProductImagePutRequest = {
            id: null,
            file: sas.blobName,
          };
          // keep local accumulator and update images state for immediate UI
          finalImagesLocal.push(newImg);
          setImages((prev) => [...prev, newImg]);
        } catch (err) {
          const e: any = err;
          setNewFiles((prev) =>
            prev.map((p) =>
              p.id === uf.id
                ? { ...p, status: "error", error: e?.message ?? String(e) }
                : p,
            ),
          );
          throw err;
        }
      }

      const finalImages = finalImagesLocal;

      if (finalImages.length === 0) {
        setError("Sản phẩm phải có ít nhất 01 hình ảnh");
        setSubmitting(false);
        return;
      }

      // For create vs update we need slightly different payload rules:
      // - When PATCH (update): do not send fields whose value is null/undefined
      //   BUT arrays must be sent even if empty (so server can clear them).
      // - When creating: send full payload expected by create endpoint.

      const createPayload: ProductCreateRequest = {
        name,
        description,
        careInstructions: care,
        price,
        inStock,
        discount,
        productImages: finalImages,
        productSizes: sizes,
        categoryIds: selectedCategories,
      };

      if (editing) {
        // Build patch payload: omit keys with null/undefined, but include arrays (even if empty)
        const raw: any = {
          name,
          description,
          careInstructions: care,
          price,
          inStock,
          discount,
          productImages: finalImages,
          productSizes: sizes, // include even if []
          categoryIds: selectedCategories, // include even if []
        };

        const patch: any = {};
        Object.entries(raw).forEach(([k, v]) => {
          if (Array.isArray(v)) {
            // include arrays even if empty
            patch[k] = v;
            return;
          }
          if (v !== null && v !== undefined) {
            patch[k] = v;
          }
        });

        await productApi.updateProduct(
          editing.id,
          patch as ProductUpdateRequest,
        );
        onSaved();
        onClose();
        return;
      }

      // create new product
      await productApi.createProduct(createPayload as ProductCreateRequest);
      onSaved();

      if (keepOpen) {
        // reset fields for adding another product
        setName("");
        setDescription("");
        setCare("");
        setPrice(0);
        setInStock(0);
        setDiscount(0);
        setSelectedCategories([]);
        setImages([]);
        setNewFiles([]);
        setSizes([]);
        setError("");
        // keep modal open
      } else {
        onClose();
      }
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : String(err);
      setError(msg || "Lỗi khi lưu sản phẩm");
    } finally {
      setSubmitting(false);
    }
  }

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    submitProduct(false);
  }

  // confirm modals state
  const [imageToDelete, setImageToDelete] = useState<{
    open: boolean;
    index: number;
    isExisting: boolean;
  }>({ open: false, index: -1, isExisting: true });
  const [sizeToDelete, setSizeToDelete] = useState<{ open: boolean }>({
    open: false,
  });
  const sizeToDeleteRef = useRef<number | null>(null);

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={editing ? "Sửa sản phẩm" : "Thêm sản phẩm"}
      maxWidth="max-w-3xl"
    >
      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        {error && (
          <div className="border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {error}
          </div>
        )}

        <div className="max-h-[65vh] overflow-auto pr-2">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className={sectionLabelClass}>Tên sản phẩm</label>
              <input
                required
                value={name}
                onChange={(e) => setName(e.target.value)}
                className={`w-full ${controlClass}`}
              />
            </div>
            <div>
              <label className={sectionLabelClass}>Giá</label>
              <input
                required
                type="number"
                value={price}
                onChange={(e) => setPrice(Number(e.target.value))}
                className={`w-full ${controlClass}`}
              />
            </div>
          </div>

          <div>
            <label className={sectionLabelClass}>Mô tả</label>
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              className={`w-full ${controlClass}`}
            />
          </div>

          <div>
            <label className={sectionLabelClass}>Hướng dẫn bảo quản</label>
            <textarea
              value={care}
              onChange={(e) => setCare(e.target.value)}
              placeholder="Ghi chú cách bảo quản, giặt ủi..."
              className={`w-full ${controlClass}`}
            />
          </div>

          <div className="grid grid-cols-3 gap-4">
            <div>
              <label className={sectionLabelClass}>Số lượng tổng</label>
              <input
                type="number"
                value={inStock}
                onChange={(e) => setInStock(Number(e.target.value))}
                className={`w-full ${controlClass}`}
              />
            </div>
            <div>
              <label className={sectionLabelClass}>Giảm giá (%)</label>
              <input
                type="number"
                value={discount}
                onChange={(e) => setDiscount(Number(e.target.value))}
                className={`w-full ${controlClass}`}
              />
            </div>
            <div>
              <label className="mb-2 block text-sm font-semibold">
                Danh mục
              </label>
              <div className="max-h-40 overflow-auto border px-3 py-2">
                {categories.map((c) => (
                  <label
                    key={c.id}
                    className="flex cursor-pointer items-center gap-2 py-1 text-sm"
                  >
                    <input
                      type="checkbox"
                      checked={selectedCategories.includes(c.id)}
                      onChange={() => toggleCategory(c.id)}
                    />
                    <span>{c.name}</span>
                  </label>
                ))}
              </div>
            </div>
          </div>

          <div>
            <label className="mb-2 block text-sm font-semibold">Hình ảnh</label>
            <div className="border px-3 py-2">
              <input
                ref={fileInputRef}
                type="file"
                multiple
                accept="image/*"
                onChange={handleFilesChange}
                className="hidden"
              />
              <div className="mt-2 flex items-start gap-3 overflow-x-auto py-2">
                {/* add-image button styled like an image tile */}
                <button
                  type="button"
                  onClick={() => fileInputRef.current?.click()}
                  className="border-outline-variant bg-surface-container-highest text-secondary hover:border-primary flex h-32 w-32 shrink-0 cursor-pointer flex-col items-center justify-center border"
                  title="Thêm ảnh"
                >
                  <span className="material-symbols-outlined text-3xl">
                    add
                  </span>
                  <span className="mt-1 text-xs">Thêm</span>
                </button>

                {images.map((img, i) => (
                  <div
                    key={i}
                    className="border-outline-variant relative h-32 w-32 shrink-0 overflow-hidden border"
                  >
                    <img
                      src={`${IMAGE_CONTAINER_URL}/${img.file}`}
                      alt={img.file}
                      className="h-full w-full object-cover"
                    />
                    <button
                      type="button"
                      onClick={() => promptRemoveExistingImage(i)}
                      className={`absolute top-1 left-1 cursor-pointer rounded bg-white/60 px-1 text-xs text-red-500 ${images.length + newFiles.length <= 1 ? "pointer-events-none opacity-50" : ""}`}
                      disabled={images.length + newFiles.length <= 1}
                      title={
                        images.length + newFiles.length <= 1
                          ? "Phải có ít nhất 01 hình ảnh"
                          : "Xóa hình ảnh"
                      }
                    >
                      Xóa
                    </button>
                  </div>
                ))}

                {newFiles.map((uf, i) => (
                  <div
                    key={uf.id}
                    className="border-outline-variant relative h-32 w-32 shrink-0 overflow-hidden border"
                  >
                    <img
                      src={URL.createObjectURL(uf.file)}
                      alt={uf.file.name}
                      className="h-full w-full object-cover"
                    />
                    <div className="absolute right-0 bottom-0 left-0 bg-white/70 p-1">
                      <div className="bg-surface-container-high h-2 overflow-hidden rounded">
                        <div
                          className="bg-primary h-full"
                          style={{ width: `${uf.progress}%` }}
                        />
                      </div>
                      <div className="text-secondary mt-1 text-xs">
                        {uf.status === "uploading"
                          ? `Đang upload ${uf.progress}%`
                          : uf.status === "done"
                            ? "Uploaded"
                            : uf.status === "error"
                              ? `Lỗi: ${uf.error}`
                              : "Chưa upload"}
                      </div>
                    </div>
                    <button
                      type="button"
                      onClick={() => promptRemoveNewFile(i)}
                      className={`absolute top-1 left-1 cursor-pointer rounded bg-white/60 px-1 text-xs text-red-500 ${images.length + newFiles.length <= 1 ? "pointer-events-none opacity-50" : ""}`}
                      disabled={images.length + newFiles.length <= 1}
                      title={
                        images.length + newFiles.length <= 1
                          ? "Phải có ít nhất 01 hình ảnh"
                          : "Xóa hình ảnh"
                      }
                    >
                      Xóa
                    </button>
                  </div>
                ))}
              </div>
            </div>
          </div>

          <div>
            <label className="mb-2 block text-sm font-semibold">
              Kích cỡ & tồn kho
            </label>
            <div className="space-y-2">
              {sizes.map((s, i) => (
                <div key={i} className="flex items-center gap-2">
                  <input
                    value={s.name}
                    onChange={(e) => updateSize(i, "name", e.target.value)}
                    placeholder="Tên"
                    className="border px-2 py-2 text-sm"
                  />
                  <input
                    type="number"
                    value={s.inStock ?? 0}
                    onChange={(e) =>
                      updateSize(i, "inStock", Number(e.target.value))
                    }
                    className="w-24 border px-2 py-2 text-sm"
                  />
                  <button
                    type="button"
                    onClick={() => removeSize(i)}
                    className="cursor-pointer px-2 text-sm text-red-500"
                    title="Xóa kích cỡ"
                  >
                    Xóa
                  </button>
                </div>
              ))}
              <div>
                <button
                  type="button"
                  onClick={addSize}
                  className="cursor-pointer bg-gray-900 px-3 py-2 text-white"
                >
                  <span className="material-symbols-outlined">add</span>
                  <span>Thêm kích cỡ</span>
                </button>
              </div>
            </div>
          </div>
        </div>

        <div className="flex justify-end gap-2 border-t pt-4">
          <button
            type="button"
            onClick={onClose}
            className={actionSecondaryClass}
            disabled={submitting}
          >
            Hủy
          </button>
          {!editing && (
            <button
              type="button"
              onClick={() => submitProduct(true)}
              disabled={submitting}
              className={actionSecondaryClass}
            >
              {submitting ? "Đang lưu..." : "Lưu và thêm tiếp"}
            </button>
          )}
          <button
            type="submit"
            disabled={submitting}
            className={actionPrimaryClass}
          >
            {submitting ? "Đang lưu..." : "Lưu"}
          </button>
        </div>
      </form>
      <ConfirmModal
        isOpen={imageToDelete.open}
        onClose={() =>
          setImageToDelete({ open: false, index: -1, isExisting: true })
        }
        onConfirm={() => {
          if (imageToDelete.isExisting)
            removeExistingImage(imageToDelete.index);
          else removeNewFile(imageToDelete.index);
          setImageToDelete({ open: false, index: -1, isExisting: true });
        }}
        title="Xóa hình ảnh"
        message="Bạn có chắc muốn xóa hình ảnh này?"
        confirmText="Xóa"
        cancelText="Hủy"
      />

      <ConfirmModal
        isOpen={sizeToDelete.open}
        onClose={() => {
          setSizeToDelete({ open: false });
          sizeToDeleteRef.current = null;
        }}
        onConfirm={() => {
          const idx = sizeToDeleteRef.current;
          if (idx == null) {
            setSizeToDelete({ open: false });
            return;
          }
          setSizes((prev) => prev.filter((_, i) => i !== idx));
          setSizeToDelete({ open: false });
          sizeToDeleteRef.current = null;
        }}
        title="Xóa kích cỡ"
        message="Bạn có chắc muốn xóa kích cỡ này?"
        confirmText="Xóa"
        cancelText="Hủy"
      />
    </Modal>
  );
}
