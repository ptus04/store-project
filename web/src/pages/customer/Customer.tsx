import { useCallback, useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  customerApi,
  type UserGenderEnum,
  type UserResponse,
} from "@/api/customerApi";
import { isAdmin } from "@/utils/auth";

const PAGE_SIZE = 10;

function formatDate(iso: string | null | undefined): string {
  if (!iso) return "—";
  const d = new Date(iso);
  return d.toLocaleDateString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
}

function getInitials(name: string): string {
  return name
    .split(" ")
    .filter(Boolean)
    .slice(-2)
    .map((w) => w[0].toUpperCase())
    .join("");
}

function isDisabled(user: UserResponse): boolean {
  return !!user.disabledAt;
}

export default function Customer() {
  const navigate = useNavigate();

  const [customers, setCustomers] = useState<UserResponse[]>([]);
  const [totalElements, setTotalElements] = useState<number | null>(null);
  const [totalPages, setTotalPages] = useState(1);
  const [currentPage, setCurrentPage] = useState(0);

  const [genderFilter, setGenderFilter] = useState<UserGenderEnum | null>(null);
  const [searchInput, setSearchInput] = useState("");
  const [searchDebounced, setSearchDebounced] = useState("");

  const [loading, setLoading] = useState(false);
  const [togglingId, setTogglingId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const canToggle = isAdmin();

  // ── debounce search ──
  useEffect(() => {
    if (debounceRef.current) clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => {
      setSearchDebounced(searchInput.trim());
      setCurrentPage(0);
    }, 400);
    return () => {
      if (debounceRef.current) clearTimeout(debounceRef.current);
    };
  }, [searchInput]);

  // ── fetch ──
  const fetchCustomers = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await customerApi.getList({
        page: currentPage,
        size: PAGE_SIZE,
        gender: genderFilter,
        search: searchDebounced || undefined,
      });
      setCustomers(result.content ?? []);
      setTotalElements(result.page?.totalElements ?? 0);
      setTotalPages(
        Math.ceil((result.page?.totalElements ?? 0) / PAGE_SIZE) || 1,
      );
    } catch {
      setError("Không thể tải danh sách khách hàng. Vui lòng thử lại.");
    } finally {
      setLoading(false);
    }
  }, [currentPage, genderFilter, searchDebounced]);

  useEffect(() => {
    fetchCustomers();
  }, [fetchCustomers]);

  // ── handlers ──
  const handleGenderFilter = (g: UserGenderEnum | null) => {
    setGenderFilter(g);
    setCurrentPage(0);
  };

  const handleToggleStatus = async (customer: UserResponse) => {
    if (togglingId) return;
    const willDisable = !isDisabled(customer);
    const confirmed = window.confirm(
      willDisable
        ? `Vô hiệu hóa tài khoản của "${customer.name}"?`
        : `Kích hoạt lại tài khoản của "${customer.name}"?`,
    );
    if (!confirmed) return;

    setTogglingId(customer.id);
    try {
      const updated = await customerApi.toggleStatus(customer.id, willDisable);
      setCustomers((prev) =>
        prev.map((c) => (c.id === updated.id ? updated : c)),
      );
    } catch {
      alert("Thao tác thất bại. Vui lòng thử lại.");
    } finally {
      setTogglingId(null);
    }
  };

  // ── pagination ──
  const paginationPages = (): (number | "...")[] => {
    if (totalPages <= 5) return Array.from({ length: totalPages }, (_, i) => i);
    if (currentPage <= 2) return [0, 1, 2, "...", totalPages - 1];
    if (currentPage >= totalPages - 3)
      return [0, "...", totalPages - 3, totalPages - 2, totalPages - 1];
    return [
      0,
      "...",
      currentPage - 1,
      currentPage,
      currentPage + 1,
      "...",
      totalPages - 1,
    ];
  };

  // ── total label ──
  const totalLabel = () => {
    if (loading && totalElements === null) return "Đang tải...";
    if (totalElements === null || totalElements === 0)
      return "Không có khách hàng";
    return (
      <>
        Có <span className="text-primary font-bold">{totalElements}</span> khách
        hàng
      </>
    );
  };

  // ─── render ──────────────────────────────────────────────────────────────

  return (
    <main className="p-margin-page">
      {/* ── Page header ── */}
      <div className="mb-stack-lg flex items-end justify-between">
        <div>
          <h2 className="font-headline-lg text-headline-lg text-primary tracking-tight">
            Quản lý khách hàng
          </h2>
          <p className="font-body-base text-body-base text-secondary">
            Manage and view customer information
          </p>
        </div>
      </div>

      {/* ── Filter bar ── */}
      <div className="bg-surface border-outline-variant p-stack-md mb-gutter mt-5 flex items-center justify-between border">
        <div className="flex items-center space-x-6">
          {/* Gender filter */}
          <div className="flex items-center space-x-3">
            <span className="font-label-caps text-label-caps text-outline">
              Giới Tính:
            </span>
            <div className="border-outline-variant flex border p-1">
              {(
                [
                  { label: "Tất cả", value: null },
                  { label: "Nam", value: "MALE" as UserGenderEnum },
                  { label: "Nữ", value: "FEMALE" as UserGenderEnum },
                ] as const
              ).map((opt) => (
                <button
                  key={String(opt.value)}
                  onClick={() => handleGenderFilter(opt.value)}
                  className={`px-4 py-1 text-xs font-bold uppercase transition-colors ${
                    genderFilter === opt.value
                      ? "bg-primary text-on-primary"
                      : "text-secondary hover:bg-surface-container"
                  }`}
                >
                  {opt.label}
                </button>
              ))}
            </div>
          </div>

          {/* Search */}
          <div className="flex items-center space-x-3">
            <span className="font-label-caps text-label-caps text-outline">
              Tìm kiếm:
            </span>
            <div className="border-outline-variant flex items-center space-x-2 border px-3 py-1.5">
              <span className="material-symbols-outlined text-outline text-sm leading-none">
                search
              </span>
              <input
                type="text"
                placeholder="Tên, SĐT, Email..."
                value={searchInput}
                onChange={(e) => setSearchInput(e.target.value)}
                className="font-body-base text-body-base text-primary placeholder:text-outline w-48 bg-transparent text-xs outline-none"
              />
              {searchInput && (
                <button onClick={() => setSearchInput("")}>
                  <span className="material-symbols-outlined text-outline hover:text-primary text-sm leading-none transition-colors">
                    close
                  </span>
                </button>
              )}
            </div>
          </div>
        </div>

        <span className="font-body-base text-body-base text-secondary">
          {totalLabel()}
        </span>
      </div>

      {/* ── Table ── */}
      <div className="bg-surface border-outline-variant overflow-hidden border">
        {error && (
          <div className="text-error border-error/30 bg-error/5 border-b px-6 py-4 text-sm">
            {error}
          </div>
        )}

        <table className="w-full border-collapse text-left">
          <thead>
            <tr className="bg-surface-container-high border-outline-variant border-b">
              {[
                "Tên",
                "Số điện thoại",
                "Email",
                "Giới tính",
                "Ngày tạo",
                "Thao tác",
              ].map((h, i) => (
                <th
                  key={h}
                  className={`font-label-caps text-label-caps text-on-surface-variant px-6 py-4 tracking-widest uppercase ${
                    i === 5 ? "text-right" : ""
                  }`}
                >
                  {h}
                </th>
              ))}
            </tr>
          </thead>

          <tbody className="divide-outline-variant divide-y">
            {loading ? (
              Array.from({ length: 5 }).map((_, i) => (
                <tr key={i}>
                  {Array.from({ length: 6 }).map((__, j) => (
                    <td key={j} className="px-6 py-5">
                      <div className="bg-surface-container-high h-4 animate-pulse rounded" />
                    </td>
                  ))}
                </tr>
              ))
            ) : customers.length === 0 ? (
              <tr>
                <td
                  colSpan={6}
                  className="text-secondary px-6 py-16 text-center text-sm"
                >
                  <span className="material-symbols-outlined text-outline mb-2 block text-4xl">
                    person_search
                  </span>
                  Không tìm thấy khách hàng nào
                </td>
              </tr>
            ) : (
              customers.map((customer) => {
                const disabled = isDisabled(customer);
                const toggling = togglingId === customer.id;

                return (
                  <tr
                    key={customer.id}
                    className={`hover:bg-surface-container-low group transition-colors ${
                      disabled ? "opacity-60" : ""
                    }`}
                  >
                    <td className="px-6 py-5">
                      <div className="flex items-center space-x-4">
                        <div className="border-outline-variant bg-surface-container-highest text-outline flex h-10 w-10 shrink-0 items-center justify-center border text-xs font-bold">
                          {getInitials(customer.name)}
                        </div>
                        <div>
                          <p className="font-body-base text-body-base text-primary font-bold">
                            {customer.name}
                          </p>
                          <p className="text-secondary text-[11px]">
                            ID: #{customer.id.slice(0, 13).toUpperCase()}
                          </p>
                        </div>
                      </div>
                    </td>
                    <td className="font-body-base text-body-base px-6 py-5">
                      {customer.phone}
                    </td>
                    <td className="font-body-base text-body-base text-secondary px-6 py-5">
                      {customer.email ?? "—"}
                    </td>
                    <td className="px-6 py-5">
                      <span className="border-outline-variant border px-3 py-1 text-[11px] font-bold tracking-wider uppercase">
                        {customer.gender === "MALE"
                          ? "Nam"
                          : customer.gender === "FEMALE"
                            ? "Nữ"
                            : "—"}
                      </span>
                    </td>
                    <td className="font-body-base text-body-base text-secondary px-6 py-5">
                      {formatDate(customer.createdAt)}
                    </td>
                    <td className="px-6 py-5 text-right">
                      <div className="flex items-center justify-end space-x-1">
                        <button
                          title="Xem chi tiết"
                          onClick={() => navigate(`/customer/${customer.id}`)}
                          className="border-outline-variant hover:border-primary text-outline hover:text-primary flex h-9 w-9 items-center justify-center border transition-all"
                        >
                          <span className="material-symbols-outlined text-[18px]">
                            visibility
                          </span>
                        </button>
                        {canToggle && (
                          <button
                            title={disabled ? "Kích hoạt" : "Vô hiệu hóa"}
                            disabled={toggling}
                            onClick={() => handleToggleStatus(customer)}
                            className={`border-outline-variant flex h-9 w-9 items-center justify-center border transition-all ${
                              disabled
                                ? "hover:border-primary group/toggle"
                                : "hover:border-error group/toggle"
                            } ${toggling ? "cursor-wait opacity-50" : ""}`}
                          >
                            {toggling ? (
                              <span className="material-symbols-outlined text-outline animate-spin text-[16px]">
                                progress_activity
                              </span>
                            ) : (
                              <svg
                                width="26"
                                height="16"
                                viewBox="0 0 26 16"
                                fill="none"
                              >
                                <rect
                                  width="26"
                                  height="16"
                                  rx="8"
                                  fill={
                                    disabled
                                      ? "var(--color-outline,#8c8c8c)"
                                      : "var(--color-tertiary,#6dbfa7)"
                                  }
                                />
                                <circle
                                  cx={disabled ? 8 : 18}
                                  cy="8"
                                  r="5"
                                  fill="white"
                                />
                              </svg>
                            )}
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>

        {/* ── Pagination ── */}
        <div className="border-outline-variant bg-surface-container-low flex items-center justify-between border-t px-6 py-4">
          <div className="flex space-x-2">
            <button
              disabled={currentPage === 0}
              onClick={() => setCurrentPage((p) => p - 1)}
              className="border-outline-variant hover:border-primary flex h-8 w-8 items-center justify-center border transition-all disabled:cursor-not-allowed disabled:opacity-40"
            >
              <span className="material-symbols-outlined text-sm">
                chevron_left
              </span>
            </button>

            {paginationPages().map((p, i) =>
              p === "..." ? (
                <span
                  key={`dots-${i}`}
                  className="flex h-8 w-8 items-center justify-center text-xs"
                >
                  ...
                </span>
              ) : (
                <button
                  key={p}
                  onClick={() => setCurrentPage(p as number)}
                  className={`flex h-8 w-8 items-center justify-center text-xs font-bold transition-all ${
                    currentPage === p
                      ? "bg-primary text-on-primary"
                      : "border-outline-variant hover:border-primary border"
                  }`}
                >
                  {(p as number) + 1}
                </button>
              ),
            )}

            <button
              disabled={currentPage >= totalPages - 1}
              onClick={() => setCurrentPage((p) => p + 1)}
              className="border-outline-variant hover:border-primary flex h-8 w-8 items-center justify-center border transition-all disabled:cursor-not-allowed disabled:opacity-40"
            >
              <span className="material-symbols-outlined text-sm">
                chevron_right
              </span>
            </button>
          </div>

          <span className="font-label-caps text-label-caps text-outline text-xs">
            Trang {currentPage + 1} / {totalPages}
          </span>
        </div>
      </div>
    </main>
  );
}
