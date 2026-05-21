import { useEffect, useRef, useState } from "react";
import {
  carouselApi,
  type CarouselFormData,
  type CarouselResponse,
  getImageUrl,
} from "../../api/carouselApi";
import { isAdmin } from "../../utils/auth";

const PAGE_SIZE = 10;

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString("vi-VN");
}

const EMPTY_FORM: CarouselFormData = {
  title: "",
  content: "",
  link: "",
  landscapeImage: "",
  portraitImage: "",
};

export default function Carousel() {
  const admin = isAdmin();

  // ── Data ──────────────────────────────────────────────────────
  const [carousels, setCarousels] = useState<CarouselResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // ── Drawer ────────────────────────────────────────────────────
  const [selected, setSelected] = useState<CarouselResponse | null>(null);

  // ── Form modal ────────────────────────────────────────────────
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<CarouselResponse | null>(null);
  const [form, setForm] = useState<CarouselFormData>(EMPTY_FORM);
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);
  const formBodyRef = useRef<HTMLDivElement>(null);

  // ── Image upload ──────────────────────────────────────────────
  const [uploadingLandscape, setUploadingLandscape] = useState(false);
  const [uploadingPortrait, setUploadingPortrait] = useState(false);
  const landscapeRef = useRef<HTMLInputElement>(null);
  const portraitRef = useRef<HTMLInputElement>(null);

  // ── Delete confirm ────────────────────────────────────────────
  const [deleteId, setDeleteId] = useState<string | null>(null);
  const [deleting, setDeleting] = useState(false);

  // ── Pagination ────────────────────────────────────────────────
  const [page, setPage] = useState(0);
  const totalPages = Math.max(1, Math.ceil(carousels.length / PAGE_SIZE));
  const paged = carousels.slice(page * PAGE_SIZE, (page + 1) * PAGE_SIZE);

  type PaginationItem =
    | { type: "page"; page: number; key: string }
    | { type: "dots"; key: string };

  function paginationPages(): PaginationItem[] {
    const items: PaginationItem[] = [];
    const delta = 1;
    for (let i = 0; i < totalPages; i++) {
      if (
        i === 0 ||
        i === totalPages - 1 ||
        (i >= page - delta && i <= page + delta)
      ) {
        items.push({ type: "page", page: i, key: `page-${i}` });
      } else if (items[items.length - 1]?.type !== "dots") {
        items.push({ type: "dots", key: `dots-${i}` });
      }
    }
    return items;
  }

  // ── Load ──────────────────────────────────────────────────────
  useEffect(() => {
    load();
  }, []);

  async function load() {
    setLoading(true);
    setError(null);
    try {
      setCarousels(await carouselApi.getAll());
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "Lỗi không xác định");
    } finally {
      setLoading(false);
    }
  }

  // ── Form helpers ──────────────────────────────────────────────
  function showFormError(msg: string) {
    setFormError(msg);
    formBodyRef.current?.scrollTo({ top: 0, behavior: "smooth" });
  }

  function openAdd() {
    setEditing(null);
    setForm(EMPTY_FORM);
    setFormError(null);
    setFormOpen(true);
  }

  function openEdit(c: CarouselResponse) {
    setEditing(c);
    setForm({
      title: c.title,
      content: c.content,
      link: c.link,
      landscapeImage: c.landscapeImage,
      portraitImage: c.portraitImage ?? "",
    });
    setFormError(null);
    setFormOpen(true);
  }

  async function handleSubmit() {
    if (!form.title.trim()) {
      showFormError("Vui lòng nhập tiêu đề");
      return;
    }
    if (!form.content.trim()) {
      showFormError("Vui lòng nhập nội dung");
      return;
    }
    if (!form.link.trim()) {
      showFormError("Vui lòng nhập liên kết");
      return;
    }
    if (!form.landscapeImage.trim()) {
      showFormError("Vui lòng tải lên hoặc nhập URL hình landscape");
      return;
    }
    if (!form.portraitImage.trim()) {
      showFormError("Vui lòng tải lên hoặc nhập URL hình portrait");
      return;
    }

    setSubmitting(true);
    setFormError(null);
    try {
      if (editing) {
        const updated = await carouselApi.update(editing.id, form);
        setCarousels((prev) =>
          prev.map((c) => (c.id === updated.id ? updated : c)),
        );
        setSelected(updated);
      } else {
        const created = await carouselApi.create(form);
        setCarousels((prev) => [...prev, created]);
        setPage(0);
      }
      setFormOpen(false);
    } catch (e: unknown) {
      showFormError(e instanceof Error ? e.message : "Có lỗi xảy ra");
    } finally {
      setSubmitting(false);
    }
  }

  // ── Delete ────────────────────────────────────────────────────
  async function handleDelete() {
    if (!deleteId) return;
    setDeleting(true);
    try {
      await carouselApi.delete(deleteId);
      setCarousels((prev) => prev.filter((c) => c.id !== deleteId));
      if (selected?.id === deleteId) setSelected(null);
      setDeleteId(null);
    } finally {
      setDeleting(false);
    }
  }

  // ── Image upload ──────────────────────────────────────────────
  async function uploadImage(
    field: "landscapeImage" | "portraitImage",
    file: File,
  ) {
    const setUploading =
      field === "landscapeImage" ? setUploadingLandscape : setUploadingPortrait;
    setUploading(true);
    setFormError(null);
    try {
      const oldBlobName = (() => {
        const old = form[field];
        if (!old) return null;
        if (old.startsWith("http")) {
          return old.split("/carousel/")[1]?.split("?")[0] ?? null;
        }
        return old;
      })();

      const { url } = await carouselApi.getSasUrl("carousel");
      const blobName = await carouselApi.uploadBlob(url, file);
      setForm((prev) => ({ ...prev, [field]: blobName }));

      if (oldBlobName) {
        carouselApi.deleteBlob("carousel", oldBlobName).catch(() => {});
      }
    } catch (e: unknown) {
      showFormError(e instanceof Error ? e.message : "Không thể tải ảnh lên");
    } finally {
      setUploading(false);
    }
  }

  // ── Render ────────────────────────────────────────────────────
  return (
    <main className="flex min-h-screen flex-col">
      <section className="p-margin-page gap-gutter flex h-[calc(100vh-80px)] flex-1 overflow-hidden">
        {/* ── Table Area ── */}
        <div className="flex min-w-0 flex-1 flex-col">
          <div className="mb-stack-lg flex items-end justify-between">
            <div>
              <h3 className="font-headline-lg text-headline-lg text-primary tracking-tight">
                Featured News
              </h3>
              <p className="font-body-base text-body-base text-secondary">
                Quản lý danh sách các banner và tin tức xuất hiện tại đầu trang
                chủ.
              </p>
            </div>
            {admin && (
              <button
                onClick={openAdd}
                className="bg-primary text-on-primary font-label-caps text-label-caps flex items-center gap-2 px-6 py-3 transition-all hover:opacity-90 active:scale-[0.98]"
              >
                <span
                  className="material-symbols-outlined text-sm"
                  data-icon="add"
                >
                  add
                </span>
                THÊM TIN NỔI BẬT
              </button>
            )}
          </div>

          <div className="border-outline-variant mt-5 flex flex-1 flex-col overflow-hidden border bg-white">
            {loading ? (
              <div className="flex flex-1 items-center justify-center gap-2">
                <span
                  className="material-symbols-outlined text-secondary animate-spin"
                  data-icon="progress_activity"
                >
                  progress_activity
                </span>
                <span className="font-body-base text-body-base text-secondary">
                  Đang tải...
                </span>
              </div>
            ) : error ? (
              <div className="flex flex-1 flex-col items-center justify-center gap-3">
                <span
                  className="material-symbols-outlined text-error text-4xl"
                  data-icon="error"
                >
                  error
                </span>
                <span className="font-body-base text-body-base text-error">
                  {error}
                </span>
                <button
                  onClick={load}
                  className="border-outline-variant font-label-caps text-label-caps text-secondary hover:bg-surface border px-4 py-2 transition-colors"
                >
                  THỬ LẠI
                </button>
              </div>
            ) : (
              <>
                {/* Table */}
                <div className="no-scrollbar flex-1 overflow-x-auto">
                  <table className="w-full border-collapse text-left">
                    <thead>
                      <tr className="border-outline-variant bg-surface border-b">
                        <th className="font-label-caps text-label-caps text-secondary p-4">
                          STT
                        </th>
                        <th className="font-label-caps text-label-caps text-secondary p-4">
                          TIÊU ĐỀ
                        </th>
                        <th className="font-label-caps text-label-caps text-secondary p-4">
                          NỘI DUNG
                        </th>
                        <th className="font-label-caps text-label-caps text-secondary p-4">
                          HÌNH ẢNH
                        </th>
                        <th className="font-label-caps text-label-caps text-secondary p-4 text-right">
                          THAO TÁC
                        </th>
                      </tr>
                    </thead>
                    <tbody className="divide-outline-variant divide-y">
                      {paged.length === 0 ? (
                        <tr>
                          <td
                            colSpan={5}
                            className="font-body-base text-body-base text-secondary p-12 text-center"
                          >
                            Chưa có tin nổi bật nào
                          </td>
                        </tr>
                      ) : (
                        paged.map((c, idx) => (
                          <tr
                            key={c.id}
                            onClick={() =>
                              setSelected(selected?.id === c.id ? null : c)
                            }
                            className={`group hover:bg-surface-container-low cursor-pointer transition-colors ${
                              selected?.id === c.id
                                ? "bg-surface-container-low"
                                : ""
                            }`}
                          >
                            <td className="font-body-base text-body-base p-4 font-bold">
                              #
                              {String(page * PAGE_SIZE + idx + 1).padStart(
                                2,
                                "0",
                              )}
                            </td>
                            <td className="font-body-base text-body-base p-4 font-bold">
                              {c.title}
                            </td>
                            <td className="font-body-base text-body-base text-secondary max-w-[200px] truncate p-4">
                              {c.content}
                            </td>
                            <td className="p-4">
                              <div className="flex gap-2">
                                <div className="border-outline-variant bg-surface flex h-8 w-12 items-center justify-center overflow-hidden border">
                                  {getImageUrl(c.landscapeImage) ? (
                                    <img
                                      src={getImageUrl(c.landscapeImage)!}
                                      alt="Landscape"
                                      className="h-full w-full object-cover"
                                    />
                                  ) : (
                                    <span
                                      className="material-symbols-outlined text-outline-variant text-sm"
                                      data-icon="image"
                                    >
                                      image
                                    </span>
                                  )}
                                </div>
                                <div className="border-outline-variant bg-surface flex h-8 w-8 items-center justify-center overflow-hidden border">
                                  {getImageUrl(c.portraitImage) ? (
                                    <img
                                      src={getImageUrl(c.portraitImage)!}
                                      alt="Portrait"
                                      className="h-full w-full object-cover"
                                    />
                                  ) : (
                                    <span
                                      className="material-symbols-outlined text-outline-variant text-sm"
                                      data-icon="image"
                                    >
                                      image
                                    </span>
                                  )}
                                </div>
                              </div>
                            </td>
                            <td className="p-4 text-right">
                              <div className="flex justify-end gap-3 opacity-0 transition-opacity group-hover:opacity-100">
                                {/* Xem chi tiết — tất cả */}
                                <button
                                  onClick={(e) => {
                                    e.stopPropagation();
                                    setSelected(
                                      selected?.id === c.id ? null : c,
                                    );
                                  }}
                                  className="text-secondary hover:text-primary"
                                  title="Xem chi tiết"
                                >
                                  <span
                                    className="material-symbols-outlined text-[20px]"
                                    data-icon={
                                      selected?.id === c.id
                                        ? "visibility_off"
                                        : "visibility"
                                    }
                                  >
                                    {selected?.id === c.id
                                      ? "visibility_off"
                                      : "visibility"}
                                  </span>
                                </button>

                                {/* Edit và Delete — chỉ admin */}
                                {admin && (
                                  <>
                                    <button
                                      onClick={(e) => {
                                        e.stopPropagation();
                                        openEdit(c);
                                      }}
                                      className="text-secondary hover:text-primary"
                                    >
                                      <span
                                        className="material-symbols-outlined text-[20px]"
                                        data-icon="edit"
                                      >
                                        edit
                                      </span>
                                    </button>
                                    <button
                                      onClick={(e) => {
                                        e.stopPropagation();
                                        setDeleteId(c.id);
                                      }}
                                      className="text-secondary hover:text-error"
                                    >
                                      <span
                                        className="material-symbols-outlined text-[20px]"
                                        data-icon="delete"
                                      >
                                        delete
                                      </span>
                                    </button>
                                  </>
                                )}
                              </div>
                            </td>
                          </tr>
                        ))
                      )}
                    </tbody>
                  </table>
                </div>

                {/* Pagination */}
                <div className="border-outline-variant bg-surface-container-low flex items-center justify-between border-t px-6 py-4">
                  <div className="flex space-x-2">
                    <button
                      disabled={page === 0}
                      onClick={() => setPage((p) => p - 1)}
                      className="border-outline-variant hover:border-primary flex h-8 w-8 items-center justify-center border transition-all disabled:cursor-not-allowed disabled:opacity-40"
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
                          className={`flex h-8 w-8 items-center justify-center text-xs font-bold transition-all ${
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
                      disabled={page >= totalPages - 1}
                      onClick={() => setPage((p) => p + 1)}
                      className="border-outline-variant hover:border-primary flex h-8 w-8 items-center justify-center border transition-all disabled:cursor-not-allowed disabled:opacity-40"
                    >
                      <span className="material-symbols-outlined text-sm">
                        chevron_right
                      </span>
                    </button>
                  </div>

                  <span className="font-label-caps text-label-caps text-outline text-xs">
                    {carousels.length === 0
                      ? "KHÔNG CÓ DỮ LIỆU"
                      : `Trang ${page + 1} / ${totalPages}`}
                  </span>
                </div>
              </>
            )}
          </div>
        </div>

        {/* ── Detail Drawer ── */}
        {selected && (
          <div className="border-outline-variant flex w-[400px] flex-shrink-0 flex-col border bg-white">
            <div className="border-outline-variant bg-surface flex items-center justify-between border-b p-6">
              <h4 className="font-title-md text-title-md text-primary">
                Chi tiết tin nổi bật
              </h4>
              <button
                onClick={() => setSelected(null)}
                className="text-secondary hover:text-primary"
              >
                <span className="material-symbols-outlined" data-icon="close">
                  close
                </span>
              </button>
            </div>

            <div className="no-scrollbar flex-1 space-y-8 overflow-y-auto p-6">
              <div className="space-y-4">
                <label className="font-label-caps text-label-caps text-secondary block">
                  PREVIEW BANNER (LANDSCAPE)
                </label>
                <div className="bg-surface-container border-outline-variant relative aspect-[16/9] overflow-hidden border">
                  {getImageUrl(selected.landscapeImage) ? (
                    <img
                      src={getImageUrl(selected.landscapeImage)!}
                      alt="Featured"
                      className="h-full w-full object-cover"
                    />
                  ) : (
                    <div className="flex h-full items-center justify-center">
                      <span
                        className="material-symbols-outlined text-outline-variant text-4xl"
                        data-icon="image"
                      >
                        image
                      </span>
                    </div>
                  )}
                </div>
              </div>

              <div className="space-y-6">
                <div className="space-y-2">
                  <label className="font-label-caps text-label-caps text-secondary block">
                    TIÊU ĐỀ
                  </label>
                  <h5 className="font-title-md text-title-md text-primary leading-tight">
                    {selected.title}
                  </h5>
                </div>
                <div className="space-y-2">
                  <label className="font-label-caps text-label-caps text-secondary block">
                    NỘI DUNG
                  </label>
                  <p className="font-body-base text-body-base text-on-surface">
                    {selected.content}
                  </p>
                </div>
                <div className="space-y-2">
                  <label className="font-label-caps text-label-caps text-secondary block">
                    LIÊN KẾT (LINK)
                  </label>
                  <div className="border-outline-variant bg-surface flex items-center justify-between border p-3">
                    <span className="font-body-base text-body-base text-secondary truncate">
                      {selected.link}
                    </span>
                    <span
                      className="material-symbols-outlined text-secondary text-sm"
                      data-icon="open_in_new"
                    >
                      open_in_new
                    </span>
                  </div>
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <label className="font-label-caps text-label-caps text-secondary block">
                      NGÀY TẠO
                    </label>
                    <span className="font-body-base text-body-base text-primary">
                      {formatDate(selected.createdAt)}
                    </span>
                  </div>
                  <div className="space-y-2">
                    <label className="font-label-caps text-label-caps text-secondary block">
                      CẬP NHẬT
                    </label>
                    <span className="font-body-base text-body-base text-primary">
                      {formatDate(selected.updatedAt)}
                    </span>
                  </div>
                </div>
              </div>

              {getImageUrl(selected.portraitImage) && (
                <div className="border-outline-variant space-y-4 border-t pt-6">
                  <label className="font-label-caps text-label-caps text-secondary block">
                    PORTRAIT ASSET
                  </label>
                  <div className="bg-surface-container border-outline-variant aspect-[3/4] w-1/2 overflow-hidden border">
                    <img
                      src={getImageUrl(selected.portraitImage)!}
                      alt="Portrait"
                      className="h-full w-full object-cover"
                    />
                  </div>
                </div>
              )}
            </div>

            {/* Footer drawer — chỉ admin mới thấy nút CHỈNH SỬA và XÓA */}
            {admin && (
              <div className="border-outline-variant bg-surface flex gap-3 border-t p-6">
                <button
                  onClick={() => openEdit(selected)}
                  className="bg-primary text-on-primary font-label-caps text-label-caps flex-1 py-3 transition-colors hover:bg-black/90"
                >
                  CHỈNH SỬA
                </button>
                <button
                  onClick={() => setDeleteId(selected.id)}
                  className="border-error text-error hover:bg-error/5 border p-3 transition-colors"
                >
                  <span
                    className="material-symbols-outlined"
                    data-icon="delete"
                  >
                    delete
                  </span>
                </button>
              </div>
            )}
          </div>
        )}
      </section>

      {/* ── Form Modal — chỉ admin mới mở được ── */}
      {formOpen && admin && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
          <div className="flex max-h-[90vh] w-full max-w-lg flex-col overflow-hidden bg-white shadow-xl">
            <div className="border-outline-variant bg-surface flex items-center justify-between border-b p-6">
              <h4 className="font-title-md text-title-md text-primary">
                {editing ? "Chỉnh sửa tin nổi bật" : "Thêm tin nổi bật mới"}
              </h4>
              <button
                onClick={() => setFormOpen(false)}
                className="text-secondary hover:text-primary"
              >
                <span className="material-symbols-outlined" data-icon="close">
                  close
                </span>
              </button>
            </div>

            <div
              ref={formBodyRef}
              className="no-scrollbar flex-1 space-y-5 overflow-y-auto p-6"
            >
              {formError && (
                <div className="flex items-start gap-2 border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                  <span
                    className="material-symbols-outlined mt-0.5 text-base"
                    data-icon="error"
                  >
                    error
                  </span>
                  <span>{formError}</span>
                </div>
              )}

              {/* TIÊU ĐỀ */}
              <div className="space-y-1.5">
                <label className="font-label-caps text-label-caps text-secondary block">
                  TIÊU ĐỀ <span className="text-error">*</span>
                </label>
                <input
                  type="text"
                  maxLength={32}
                  value={form.title}
                  onChange={(e) =>
                    setForm((p) => ({ ...p, title: e.target.value }))
                  }
                  className="border-outline-variant font-body-base text-body-base focus:border-primary w-full border p-3 focus:outline-none"
                  placeholder="Tối đa 32 ký tự"
                />
                <span className="text-secondary text-xs">
                  {form.title.length}/32
                </span>
              </div>

              {/* NỘI DUNG */}
              <div className="space-y-1.5">
                <label className="font-label-caps text-label-caps text-secondary block">
                  NỘI DUNG <span className="text-error">*</span>
                </label>
                <textarea
                  maxLength={64}
                  rows={2}
                  value={form.content}
                  onChange={(e) =>
                    setForm((p) => ({ ...p, content: e.target.value }))
                  }
                  className="border-outline-variant font-body-base text-body-base focus:border-primary w-full resize-none border p-3 focus:outline-none"
                  placeholder="Tối đa 64 ký tự"
                />
                <span className="text-secondary text-xs">
                  {form.content.length}/64
                </span>
              </div>

              {/* LIÊN KẾT */}
              <div className="space-y-1.5">
                <label className="font-label-caps text-label-caps text-secondary block">
                  LIÊN KẾT <span className="text-error">*</span>
                </label>
                <input
                  type="text"
                  maxLength={128}
                  value={form.link}
                  onChange={(e) =>
                    setForm((p) => ({ ...p, link: e.target.value }))
                  }
                  className="border-outline-variant font-body-base text-body-base focus:border-primary w-full border p-3 focus:outline-none"
                  placeholder="/collection/summer-2024"
                />
              </div>

              {/* HÌNH LANDSCAPE */}
              <div className="space-y-1.5">
                <label className="font-label-caps text-label-caps text-secondary block">
                  HÌNH LANDSCAPE <span className="text-error">*</span>
                </label>
                {getImageUrl(form.landscapeImage) && (
                  <div className="border-outline-variant mb-2 aspect-[16/9] overflow-hidden border">
                    <img
                      src={getImageUrl(form.landscapeImage)!}
                      alt="Preview landscape"
                      className="h-full w-full object-cover"
                    />
                  </div>
                )}
                <div className="flex gap-2">
                  <input
                    type="text"
                    maxLength={256}
                    value={form.landscapeImage}
                    onChange={(e) =>
                      setForm((p) => ({
                        ...p,
                        landscapeImage: e.target.value,
                      }))
                    }
                    className="border-outline-variant font-body-base text-body-base focus:border-primary flex-1 border p-3 focus:outline-none"
                    placeholder="URL hoặc tải lên"
                  />
                  <button
                    type="button"
                    onClick={() => landscapeRef.current?.click()}
                    disabled={uploadingLandscape}
                    className="border-outline-variant hover:bg-surface border px-4 transition-colors disabled:opacity-50"
                    title="Tải ảnh lên"
                  >
                    <span
                      className="material-symbols-outlined text-sm"
                      data-icon={
                        uploadingLandscape ? "progress_activity" : "upload"
                      }
                    >
                      {uploadingLandscape ? "progress_activity" : "upload"}
                    </span>
                  </button>
                  <input
                    ref={landscapeRef}
                    type="file"
                    accept="image/*"
                    className="hidden"
                    onChange={(e) => {
                      const f = e.target.files?.[0];
                      if (f) uploadImage("landscapeImage", f);
                      e.target.value = "";
                    }}
                  />
                </div>
                {uploadingLandscape && (
                  <p className="text-secondary text-xs">Đang tải ảnh lên...</p>
                )}
                {!uploadingLandscape && form.landscapeImage && (
                  <p className="truncate text-xs text-green-600">
                    ✓ {form.landscapeImage}
                  </p>
                )}
              </div>

              {/* HÌNH PORTRAIT */}
              <div className="space-y-1.5">
                <label className="font-label-caps text-label-caps text-secondary block">
                  HÌNH PORTRAIT <span className="text-error">*</span>
                </label>
                {getImageUrl(form.portraitImage) && (
                  <div className="border-outline-variant mb-2 aspect-[3/4] w-1/3 overflow-hidden border">
                    <img
                      src={getImageUrl(form.portraitImage)!}
                      alt="Preview portrait"
                      className="h-full w-full object-cover"
                    />
                  </div>
                )}
                <div className="flex gap-2">
                  <input
                    type="text"
                    maxLength={256}
                    value={form.portraitImage}
                    onChange={(e) =>
                      setForm((p) => ({ ...p, portraitImage: e.target.value }))
                    }
                    className="border-outline-variant font-body-base text-body-base focus:border-primary flex-1 border p-3 focus:outline-none"
                    placeholder="URL hoặc tải lên"
                  />
                  <button
                    type="button"
                    onClick={() => portraitRef.current?.click()}
                    disabled={uploadingPortrait}
                    className="border-outline-variant hover:bg-surface border px-4 transition-colors disabled:opacity-50"
                    title="Tải ảnh lên"
                  >
                    <span
                      className="material-symbols-outlined text-sm"
                      data-icon={
                        uploadingPortrait ? "progress_activity" : "upload"
                      }
                    >
                      {uploadingPortrait ? "progress_activity" : "upload"}
                    </span>
                  </button>
                  <input
                    ref={portraitRef}
                    type="file"
                    accept="image/*"
                    className="hidden"
                    onChange={(e) => {
                      const f = e.target.files?.[0];
                      if (f) uploadImage("portraitImage", f);
                      e.target.value = "";
                    }}
                  />
                </div>
                {uploadingPortrait && (
                  <p className="text-secondary text-xs">Đang tải ảnh lên...</p>
                )}
                {!uploadingPortrait && form.portraitImage && (
                  <p className="truncate text-xs text-green-600">
                    ✓ {form.portraitImage}
                  </p>
                )}
              </div>
            </div>

            <div className="border-outline-variant bg-surface flex gap-3 border-t p-6">
              <button
                onClick={() => setFormOpen(false)}
                className="border-outline-variant text-secondary hover:bg-surface font-label-caps text-label-caps flex-1 border py-3 transition-colors"
              >
                HỦY
              </button>
              <button
                onClick={handleSubmit}
                disabled={submitting || uploadingLandscape || uploadingPortrait}
                className="bg-primary text-on-primary font-label-caps text-label-caps flex-1 py-3 transition-colors hover:bg-black/90 disabled:opacity-50"
              >
                {submitting ? "ĐANG LƯU..." : editing ? "CẬP NHẬT" : "THÊM MỚI"}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ── Delete Confirm Modal ── */}
      {deleteId && admin && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
          <div className="flex w-full max-w-sm flex-col gap-6 bg-white p-8 shadow-xl">
            <div className="flex flex-col gap-2">
              <h4 className="font-title-md text-title-md text-primary">
                Xác nhận xóa
              </h4>
              <p className="font-body-base text-body-base text-secondary">
                Tin nổi bật này sẽ bị xóa vĩnh viễn. Hành động này không thể
                hoàn tác.
              </p>
            </div>
            <div className="flex gap-3">
              <button
                onClick={() => setDeleteId(null)}
                className="border-outline-variant text-secondary hover:bg-surface font-label-caps text-label-caps flex-1 border py-3 transition-colors"
              >
                HỦY
              </button>
              <button
                onClick={handleDelete}
                disabled={deleting}
                className="bg-error font-label-caps text-label-caps flex-1 py-3 text-white transition-colors hover:opacity-90 disabled:opacity-50"
              >
                {deleting ? "ĐANG XÓA..." : "XÓA"}
              </button>
            </div>
          </div>
        </div>
      )}
    </main>
  );
}
