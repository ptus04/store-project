import type { FormEvent } from "react";
import { useEffect, useMemo, useState } from "react";

interface Employee {
  id: string;
  name: string;
  phone: string;
  email: string;
  role: string;
  gender: string;
  birthDate: string;
  phoneVerifiedAt: string | null;
  emailVerifiedAt: string | null;
  createdAt: string;
  updatedAt: string;
  disabledAt: string | null;
}

type RoleFilter = "ALL" | "ADMIN" | "EMPLOYEE";
type FormMode = "create" | "edit";
type AccountRole = "ADMIN" | "EMPLOYEE";

type AccountFormState = {
  name: string;
  phone: string;
  email: string;
  password: string;
  role: AccountRole;
  gender: string;
  birthDate: string;
};

const EMPTY_FORM: AccountFormState = {
  name: "",
  phone: "",
  email: "",
  password: "",
  role: "EMPLOYEE",
  gender: "",
  birthDate: "",
};

const ROLE_LABELS: Record<string, string> = {
  ADMIN: "Quản trị viên",
  EMPLOYEE: "Nhân viên",
};

const ROLE_BADGE_COLORS: Record<string, string> = {
  ADMIN: "bg-red-100 text-red-800 border border-red-300",
  EMPLOYEE: "bg-blue-100 text-blue-800 border border-blue-300",
};

function parseJwtPayload(token: string | null): Record<string, unknown> {
  if (!token) return {};

  try {
    const parts = token.split(".");
    if (parts.length < 2) return {};

    const payloadBase64 = parts[1].replace(/-/g, "+").replace(/_/g, "/");
    const padded = payloadBase64.padEnd(
      payloadBase64.length + ((4 - (payloadBase64.length % 4)) % 4),
      "=",
    );

    return JSON.parse(window.atob(padded)) as Record<string, unknown>;
  } catch {
    return {};
  }
}

function readCurrentUser(token: string | null) {
  const payload = parseJwtPayload(token);
  const storedUser = localStorage.getItem("user");
  const parsedStoredUser = (() => {
    if (!storedUser) return {};

    try {
      return JSON.parse(storedUser) as Record<string, unknown>;
    } catch {
      return {};
    }
  })();

  const storedRole =
    typeof parsedStoredUser.role === "string" ? parsedStoredUser.role : "";
  const payloadRole = typeof payload.role === "string" ? payload.role : "";
  const normalizedRole = (storedRole || payloadRole || "").toUpperCase();

  const payloadPhone = typeof payload.phone === "string" ? payload.phone : "";
  const payloadSub = typeof payload.sub === "string" ? payload.sub : "";
  const currentUserPhone =
    payloadPhone || (/^\d{9,15}$/.test(payloadSub) ? payloadSub : "");

  const payloadId =
    typeof payload.userId === "string"
      ? payload.userId
      : typeof payload.id === "string"
        ? payload.id
        : /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i.test(
              payloadSub,
            )
          ? payloadSub
          : "";

  return {
    role: normalizedRole,
    id: payloadId,
    phone: currentUserPhone,
  };
}

export default function Employees() {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [roleFilter, setRoleFilter] = useState<RoleFilter>("ALL");
  const [searchTerm, setSearchTerm] = useState<string>("");
  const [statusSort, setStatusSort] = useState<
    "NONE" | "ACTIVE_FIRST" | "DISABLED_FIRST"
  >("NONE");
  const [selectedEmployee, setSelectedEmployee] = useState<Employee | null>(
    null,
  );
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formMode, setFormMode] = useState<FormMode>("create");
  const [formState, setFormState] = useState<AccountFormState>(EMPTY_FORM);
  const [updatingAccountIds, setUpdatingAccountIds] = useState<Set<string>>(
    new Set(),
  );

  const API_URL = import.meta.env.VITE_API_URL;
  const token = localStorage.getItem("token");
  const currentUser = useMemo(() => readCurrentUser(token), [token]);
  const isCurrentUserAdmin = currentUser.role === "ADMIN";

  useEffect(() => {
    fetchEmployees();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [roleFilter]);

  async function fetchEmployees() {
    try {
      setLoading(true);
      const url = new URL(`${API_URL}/api/employees`);
      if (roleFilter !== "ALL") {
        url.searchParams.append("role", roleFilter);
      }

      const response = await fetch(url.toString(), {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!response.ok) {
        setError("Không thể tải danh sách tài khoản");
        setEmployees([]);
        return;
      }

      const data: Employee[] = await response.json();
      setEmployees(data);
      setError("");
    } catch (err) {
      console.error("Error fetching employees:", err);
      setError("Không thể tải danh sách tài khoản");
    } finally {
      setLoading(false);
    }
  }

  const formatDate = (dateString: string) => {
    if (!dateString) return "—";
    return new Date(dateString).toLocaleDateString("vi-VN");
  };

  const formatDateTime = (dateString: string) => {
    if (!dateString) return "—";
    return new Date(dateString).toLocaleString("vi-VN");
  };

  const formatPhone = (phone: string) => {
    if (!phone) return "—";
    return phone.replace(/(\d{3})(\d{3})(\d{4})/, "$1 $2 $3");
  };

  const getRoleLabel = (role: string) => {
    return ROLE_LABELS[role] || role;
  };

  const getRoleBadgeColor = (role: string) => {
    return (
      ROLE_BADGE_COLORS[role] ||
      "bg-gray-100 text-gray-800 border border-gray-300"
    );
  };

  const activeCount = useMemo(
    () => employees.filter((employee) => !employee.disabledAt).length,
    [employees],
  );
  const disabledCount = employees.length - activeCount;

  // Filter employees by search term (name, phone, email)
  const filteredEmployees = useMemo(() => {
    const q = searchTerm.trim().toLowerCase();
    if (!q) return employees;

    const normalizedQ = q.replace(/\s+/g, "");

    return employees.filter((employee) => {
      const name = (employee.name || "").toLowerCase();
      const phone = (employee.phone || "").toLowerCase().replace(/\s+/g, "");
      const email = (employee.email || "").toLowerCase();

      const matchesName = name.includes(q);
      const matchesPhone = phone.includes(normalizedQ);
      const matchesEmail = email.includes(q);

      return matchesName || matchesPhone || matchesEmail;
    });
  }, [employees, searchTerm]);

  const hasNoResults =
    !loading &&
    filteredEmployees.length === 0 &&
    employees.length > 0 &&
    !error;
  const hasFooter = !loading && employees.length > 0;

  function handleRoleFilterChange(e: React.ChangeEvent<HTMLSelectElement>) {
    setRoleFilter(e.target.value as RoleFilter);
  }

  function handleStatusSortChange(e: React.ChangeEvent<HTMLSelectElement>) {
    setStatusSort(e.target.value as "NONE" | "ACTIVE_FIRST" | "DISABLED_FIRST");
  }

  const sortedEmployees = useMemo(() => {
    if (statusSort === "NONE") return filteredEmployees;

    return [...filteredEmployees].sort((a, b) => {
      const aDisabled = Boolean(a.disabledAt);
      const bDisabled = Boolean(b.disabledAt);

      if (aDisabled === bDisabled) {
        // Keep stable order - fallback to name
        return a.name.localeCompare(b.name);
      }

      if (statusSort === "ACTIVE_FIRST") {
        return aDisabled ? 1 : -1;
      }

      // DISABLED_FIRST
      return aDisabled ? -1 : 1;
    });
  }, [filteredEmployees, statusSort]);

  const hasTable = !loading && sortedEmployees.length > 0;

  function openCreateModal(role: AccountRole) {
    setFormMode("create");
    setFormState({ ...EMPTY_FORM, role });
    setSelectedEmployee(null);
    setIsModalOpen(true);
  }

  function openEditModal(employee: Employee) {
    setFormMode("edit");
    setSelectedEmployee(employee);
    setFormState({
      name: employee.name,
      phone: employee.phone,
      email: employee.email ?? "",
      password: "",
      role: employee.role === "ADMIN" ? "ADMIN" : "EMPLOYEE",
      gender: employee.gender ?? "",
      birthDate: employee.birthDate ?? "",
    });
    setIsModalOpen(true);
  }

  function openDetailModal(employee: Employee) {
    setSelectedEmployee(employee);
  }

  function closeModal() {
    setIsModalOpen(false);
    setSelectedEmployee(null);
    setFormState(EMPTY_FORM);
  }

  function closeDetail() {
    setSelectedEmployee(null);
  }

  function handleFormSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (formMode === "create") {
      const newEmployee: Employee = {
        id: `temp-${Date.now()}`,
        name: formState.name,
        phone: formState.phone,
        email: formState.email,
        role: formState.role,
        gender: formState.gender,
        birthDate: formState.birthDate,
        phoneVerifiedAt: null,
        emailVerifiedAt: null,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
        disabledAt: null,
      };

      setEmployees((current) => [newEmployee, ...current]);
      closeModal();
      return;
    }

    if (selectedEmployee) {
      setEmployees((current) =>
        current.map((employee) =>
          employee.id === selectedEmployee.id
            ? {
                ...employee,
                name: formState.name,
                phone: formState.phone,
                email: formState.email,
                role: formState.role,
                gender: formState.gender,
                birthDate: formState.birthDate,
                updatedAt: new Date().toISOString(),
              }
            : employee,
        ),
      );
      closeModal();
    }
  }

  async function toggleDisableAccount(targetEmployee: Employee) {
    const id = targetEmployee.id;
    if (updatingAccountIds.has(id)) return;

    const isSelfAccount =
      (currentUser.id && targetEmployee.id === currentUser.id) ||
      (currentUser.phone && targetEmployee.phone === currentUser.phone);

    // Admin mới có quyền vô hiệu hóa và không được tự khóa chính mình
    const canToggle = isCurrentUserAdmin && !isSelfAccount;

    if (!canToggle) return;

    const isCurrentlyDisabled = targetEmployee.disabledAt !== null;
    const disabledAt = isCurrentlyDisabled ? null : new Date().toISOString();

    try {
      setUpdatingAccountIds((current) => new Set(current).add(id));

      const response = await fetch(`${API_URL}/api/employees/${id}/status`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ disabledAt }),
      });

      if (!response.ok) {
        setError("Không thể cập nhật trạng thái tài khoản");
        return;
      }

      const updatedEmployee: Employee = await response.json();
      setEmployees((current) =>
        current.map((employee) =>
          employee.id === updatedEmployee.id ? updatedEmployee : employee,
        ),
      );
      setError("");
    } catch (err) {
      console.error("Error updating account status:", err);
      setError("Không thể cập nhật trạng thái tài khoản");
    } finally {
      setUpdatingAccountIds((current) => {
        const next = new Set(current);
        next.delete(id);
        return next;
      });
    }
  }

  return (
    <div className="from-surface via-surface to-surface-container flex-1 overflow-auto bg-linear-to-br">
      <div className="space-y-6 p-6">
        {/* Header Section */}
        <div className="border-primary/20 from-primary/10 to-primary/5 rounded-2xl border bg-linear-to-r p-8 shadow-lg transition-shadow hover:shadow-xl">
          <div className="flex flex-col gap-6 md:flex-row md:items-center md:justify-between">
            <div className="space-y-2">
              <div className="flex items-center gap-3">
                <div className="bg-primary/20 rounded-full p-3">
                  <span className="material-symbols-outlined text-primary text-2xl">
                    manage_accounts
                  </span>
                </div>
                <div>
                  <h1 className="font-title-lg text-title-lg text-primary tracking-wider uppercase">
                    Quản lý tài khoản
                  </h1>
                  <p className="text-secondary text-sm">
                    Xem chi tiết, tạo, cập nhật và vô hiệu hóa tài khoản nhân
                    viên / quản trị viên
                  </p>
                </div>
              </div>
            </div>

            <div className="flex w-full flex-col gap-3">
              <div className="flex w-full flex-wrap items-center gap-3">
                <div className="group relative">
                  <select
                    value={roleFilter}
                    onChange={handleRoleFilterChange}
                    className="border-outline/20 focus:border-primary focus:ring-primary/20 appearance-none rounded-lg border bg-white px-4 py-3 pr-10 shadow-md transition-all outline-none"
                  >
                    <option value="ALL">Tất cả tài khoản</option>
                    <option value="ADMIN">Quản trị viên</option>
                    <option value="EMPLOYEE">Nhân viên</option>
                  </select>
                  <span className="material-symbols-outlined text-secondary pointer-events-none absolute top-1/2 right-3 -translate-y-1/2">
                    expand_more
                  </span>
                </div>

                {/* Status sort select */}
                <div className="group relative">
                  <select
                    value={statusSort}
                    onChange={handleStatusSortChange}
                    className="border-outline/20 focus:border-primary focus:ring-primary/20 appearance-none rounded-lg border bg-white px-4 py-3 pr-10 shadow-md transition-all outline-none"
                  >
                    <option value="NONE">Không sắp xếp</option>
                    <option value="ACTIVE_FIRST">Hoạt động trước</option>
                    <option value="DISABLED_FIRST">Đã vô hiệu trước</option>
                  </select>
                  <span className="material-symbols-outlined text-secondary pointer-events-none absolute top-1/2 right-3 -translate-y-1/2">
                    expand_more
                  </span>
                </div>

                {/* Search input: name / phone / email */}
                <div className="relative min-w-[260px] flex-1">
                  <input
                    type="text"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    placeholder="Tìm theo tên, SĐT hoặc email"
                    className="border-outline/20 focus:border-primary focus:ring-primary/20 w-full rounded-lg border bg-white px-4 py-3 pr-10 shadow-md transition-all outline-none"
                  />
                  <span className="material-symbols-outlined text-secondary pointer-events-none absolute top-1/2 right-3 -translate-y-1/2">
                    search
                  </span>
                </div>

                {isCurrentUserAdmin && (
                  <div className="ml-auto shrink-0">
                    <button
                      onClick={() => openCreateModal("EMPLOYEE")}
                      className="from-primary to-primary/80 text-on-primary hover:from-primary/90 hover:to-primary flex items-center gap-2 rounded-lg bg-linear-to-r px-4 py-2 font-semibold shadow-md transition-all hover:shadow-lg active:scale-95"
                      title="Tạo tài khoản"
                    >
                      <span className="material-symbols-outlined">
                        person_add
                      </span>
                      {/*Tạo tài khoản*/}
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>

          <div className="mt-6 grid grid-cols-1 gap-4 md:grid-cols-3">
            <div className="rounded-xl border border-white/70 bg-white/70 p-4 shadow-sm backdrop-blur-sm">
              <p className="text-secondary text-xs font-semibold tracking-widest uppercase">
                Tổng tài khoản
              </p>
              <p className="text-primary mt-2 text-2xl font-bold">
                {employees.length}
              </p>
            </div>
            <div className="rounded-xl border border-white/70 bg-white/70 p-4 shadow-sm backdrop-blur-sm">
              <p className="text-secondary text-xs font-semibold tracking-widest uppercase">
                Đang hoạt động
              </p>
              <p className="mt-2 text-2xl font-bold text-emerald-600">
                {activeCount}
              </p>
            </div>
            <div className="rounded-xl border border-white/70 bg-white/70 p-4 shadow-sm backdrop-blur-sm">
              <p className="text-secondary text-xs font-semibold tracking-widest uppercase">
                Đã vô hiệu hóa
              </p>
              <p className="mt-2 text-2xl font-bold text-rose-600">
                {disabledCount}
              </p>
            </div>
          </div>
        </div>

        {/* Content Section */}
        <div className="border-outline/10 overflow-hidden rounded-2xl border bg-white shadow-lg">
          {loading && (
            <div className="flex h-80 flex-col items-center justify-center gap-4">
              <div className="relative h-12 w-12">
                <div className="border-primary/20 absolute inset-0 rounded-full border-4"></div>
                <div className="border-primary absolute inset-0 animate-spin rounded-full border-4 border-t-transparent"></div>
              </div>
              <p className="text-secondary font-medium">Đang tải dữ liệu...</p>
            </div>
          )}

          {error && !loading && (
            <div className="m-6 flex items-start gap-3 rounded-lg border-2 border-red-300 bg-red-50 p-4">
              <span className="material-symbols-outlined shrink-0 text-red-600">
                error
              </span>
              <div>
                <p className="font-semibold text-red-900">Lỗi</p>
                <p className="text-sm text-red-800">{error}</p>
              </div>
            </div>
          )}

          {hasNoResults && (
            <div className="flex h-80 flex-col items-center justify-center gap-4">
              <div className="bg-primary/10 rounded-full p-6">
                <span className="material-symbols-outlined text-primary text-5xl">
                  person_search
                </span>
              </div>
              <div className="text-center">
                <p className="text-secondary text-lg font-medium">
                  Không tìm thấy kết quả
                </p>
                <p className="text-secondary/60 mt-1 text-sm">
                  Không có tài khoản nào phù hợp với từ khóa tìm kiếm
                </p>
              </div>
            </div>
          )}

          {hasTable && (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-primary/20 from-primary/5 to-primary/10 border-b-2 bg-linear-to-r">
                    <th className="text-secondary px-6 py-4 text-left text-sm font-bold tracking-wide uppercase">
                      Họ Tên
                    </th>
                    <th className="text-secondary px-6 py-4 text-left text-sm font-bold tracking-wide uppercase">
                      Số Điện Thoại
                    </th>
                    <th className="text-secondary px-6 py-4 text-left text-sm font-bold tracking-wide uppercase">
                      Email
                    </th>
                    <th className="text-secondary px-6 py-4 text-left text-sm font-bold tracking-wide uppercase">
                      Vai Trò
                    </th>
                    <th className="text-secondary px-6 py-4 text-left text-sm font-bold tracking-wide uppercase">
                      Trạng thái
                    </th>
                    <th className="text-secondary px-6 py-4 text-center text-sm font-bold tracking-wide uppercase">
                      Thao tác
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-outline/10 divide-y">
                  {sortedEmployees.map((employee) => {
                    const isDisabled = Boolean(employee.disabledAt);
                    const isUpdatingAccount = updatingAccountIds.has(
                      employee.id,
                    );
                    const isSelfAccount =
                      (currentUser.id && employee.id === currentUser.id) ||
                      (currentUser.phone &&
                        employee.phone === currentUser.phone);
                    const canToggleAccount =
                      isCurrentUserAdmin &&
                      !isSelfAccount &&
                      !isUpdatingAccount;
                    const toggleTitle = !isCurrentUserAdmin
                      ? "Nhân viên không có quyền vô hiệu hóa tài khoản"
                      : isUpdatingAccount
                        ? "Đang cập nhật trạng thái tài khoản"
                        : isSelfAccount
                          ? "Không thể tự vô hiệu hóa tài khoản của chính mình"
                          : isDisabled
                            ? "Kích hoạt tài khoản"
                            : "Vô hiệu hóa tài khoản";

                    return (
                      <tr
                        key={employee.id}
                        className={`group hover:bg-primary/5 transition-colors duration-300 ${
                          isDisabled ? "bg-slate-50/80 opacity-75" : ""
                        }`}
                      >
                        <td className="text-on-surface px-6 py-4 text-sm font-semibold">
                          <div className="flex items-center gap-3">
                            <div className="from-primary/30 to-primary/10 flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-linear-to-br">
                              <span className="text-primary text-xs font-bold">
                                {employee.name.charAt(0).toUpperCase()}
                              </span>
                            </div>
                            <div>
                              <div className="flex items-center gap-2">
                                <span>{employee.name}</span>
                              </div>
                              <p className="text-secondary mt-1 text-xs">
                                {employee.id}
                              </p>
                            </div>
                          </div>
                        </td>
                        <td className="text-on-surface-variant px-6 py-4 text-sm">
                          {formatPhone(employee.phone)}
                        </td>
                        <td className="text-on-surface-variant px-6 py-4 text-sm">
                          {employee.email || "—"}
                        </td>
                        <td className="px-6 py-4 text-sm">
                          <div className="flex items-center gap-2">
                            <span
                              className={`inline-block rounded-full px-3 py-1 text-xs font-bold whitespace-nowrap ${getRoleBadgeColor(
                                employee.role,
                              )}`}
                            >
                              {getRoleLabel(employee.role)}
                            </span>
                          </div>
                        </td>
                        <td className="px-6 py-4 text-sm">
                          <div className="flex flex-col gap-2">
                            <div className="flex items-center gap-2">
                              {isCurrentUserAdmin ? (
                                <button
                                  type="button"
                                  onClick={() =>
                                    void toggleDisableAccount(employee)
                                  }
                                  disabled={!canToggleAccount}
                                  className={`focus:ring-primary/30 relative inline-flex h-6 w-11 items-center rounded-full border transition-all focus:ring-2 focus:outline-none ${
                                    isDisabled
                                      ? "border-rose-300 bg-rose-500"
                                      : "border-emerald-300 bg-emerald-500"
                                  } ${
                                    canToggleAccount
                                      ? "cursor-pointer"
                                      : "cursor-not-allowed opacity-50"
                                  }`}
                                  aria-pressed={isDisabled}
                                  title={toggleTitle}
                                >
                                  <span
                                    className={`inline-block h-5 w-5 transform rounded-full bg-white shadow-sm transition-transform ${
                                      isDisabled
                                        ? "translate-x-1"
                                        : "translate-x-5"
                                    }`}
                                  />
                                </button>
                              ) : (
                                <span
                                  className={`inline-flex rounded-full px-2 py-1 text-xs font-semibold ${
                                    isDisabled
                                      ? "bg-rose-100 text-rose-700"
                                      : "bg-emerald-100 text-emerald-700"
                                  }`}
                                  title={toggleTitle}
                                >
                                  {isDisabled ? "Đã vô hiệu" : "Đang hoạt động"}
                                </span>
                              )}
                              {isCurrentUserAdmin && (
                                <span
                                  className={`text-xs font-semibold ${isDisabled ? "text-rose-700" : "text-emerald-700"}`}
                                >
                                  {isDisabled
                                    ? "Đã vô hiệu hóa"
                                    : "Đang hoạt động"}
                                </span>
                              )}
                            </div>
                            <span className="text-secondary text-xs">
                              Cập nhật: {formatDate(employee.updatedAt)}
                            </span>
                          </div>
                        </td>
                        <td className="px-6 py-4 text-center">
                          <div className="flex justify-center gap-2 opacity-100 transition-opacity md:opacity-0 md:group-hover:opacity-100">
                            <button
                              onClick={() => openDetailModal(employee)}
                              className="rounded-lg bg-slate-100 p-2 text-slate-700 transition-all hover:scale-110 hover:bg-slate-200 active:scale-95"
                              title="Xem chi tiết"
                            >
                              <span className="material-symbols-outlined text-lg">
                                visibility
                              </span>
                            </button>

                            {/* CHỈ ADMIN MỚI THẤY NÚT SỬA TÀI KHOẢN TRÊN DÒNG */}
                            {isCurrentUserAdmin && (
                              <button
                                onClick={() => openEditModal(employee)}
                                className="bg-primary/10 text-primary hover:bg-primary/20 rounded-lg p-2 transition-all hover:scale-110 active:scale-95"
                                title="Cập nhật tài khoản"
                              >
                                <span className="material-symbols-outlined text-lg">
                                  edit
                                </span>
                              </button>
                            )}
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}

          {hasFooter && (
            <div className="border-primary/20 from-primary/5 to-primary/10 flex items-center justify-between border-t-2 bg-linear-to-r px-6 py-4">
              <div className="flex items-center gap-2">
                <span className="material-symbols-outlined text-primary">
                  info
                </span>
                <p className="text-secondary text-sm">
                  Tổng cộng:{" "}
                  <span className="text-primary font-bold">
                    {employees.length}
                  </span>{" "}
                  tài khoản
                </p>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Detail Modal */}
      {selectedEmployee && !isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4 backdrop-blur-sm">
          <div className="w-full max-w-2xl rounded-2xl bg-white p-6 shadow-2xl">
            <div className="mb-6 flex items-start justify-between gap-4">
              <div>
                <p className="text-secondary text-xs font-bold tracking-widest uppercase">
                  Xem chi tiết tài khoản
                </p>
                <h2 className="text-primary mt-1 text-2xl font-bold">
                  {selectedEmployee.name}
                </h2>
              </div>
              <button
                onClick={closeDetail}
                className="rounded-full bg-slate-100 p-2 text-slate-600 transition-colors hover:bg-slate-200"
              >
                <span className="material-symbols-outlined">close</span>
              </button>
            </div>

            <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
              <DetailItem
                label="Vai trò"
                value={getRoleLabel(selectedEmployee.role)}
              />
              <DetailItem
                label="Trạng thái"
                value={
                  selectedEmployee.disabledAt
                    ? "Đã vô hiệu hóa"
                    : "Đang hoạt động"
                }
              />
              <DetailItem
                label="Số điện thoại"
                value={formatPhone(selectedEmployee.phone)}
              />
              <DetailItem label="Email" value={selectedEmployee.email || "—"} />
              <DetailItem
                label="Giới tính"
                value={selectedEmployee.gender || "—"}
              />
              <DetailItem
                label="Ngày sinh"
                value={formatDate(selectedEmployee.birthDate)}
              />
              <DetailItem
                label="Xác thực SĐT"
                value={
                  selectedEmployee.phoneVerifiedAt
                    ? "Đã xác thực"
                    : "Chưa xác thực"
                }
              />
              <DetailItem
                label="Ngày tạo"
                value={formatDateTime(selectedEmployee.createdAt)}
              />
            </div>

            <div className="mt-6 flex justify-end gap-3">
              {/* CHỈ ADMIN MỚI THẤY NÚT CẬP NHẬT TRONG CHI TIẾT */}
              {isCurrentUserAdmin && (
                <button
                  onClick={() => {
                    const employeeToEdit = selectedEmployee;
                    closeDetail();
                    if (employeeToEdit) {
                      openEditModal(employeeToEdit);
                    }
                  }}
                  className="bg-primary text-on-primary hover:bg-primary/90 rounded-lg px-5 py-3 font-semibold transition-all"
                >
                  Cập nhật
                </button>
              )}
              <button
                onClick={closeDetail}
                className="border-outline/20 text-secondary rounded-lg border px-5 py-3 font-semibold transition-all hover:bg-slate-50"
              >
                Đóng
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Create/Edit Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4 backdrop-blur-sm">
          <form
            onSubmit={handleFormSubmit}
            className="w-full max-w-2xl rounded-2xl bg-white p-6 shadow-2xl"
          >
            <div className="mb-6 flex items-start justify-between gap-4">
              <div>
                <p className="text-secondary text-xs font-bold tracking-widest uppercase">
                  {formMode === "create"
                    ? "Tạo tài khoản mới"
                    : "Cập nhật tài khoản"}
                </p>
                <h2 className="text-primary mt-1 text-2xl font-bold">
                  {formMode === "create"
                    ? `Tạo ${formState.role === "ADMIN" ? "quản trị viên" : "nhân viên"}`
                    : selectedEmployee?.name || "Chỉnh sửa tài khoản"}
                </h2>
              </div>
              <button
                type="button"
                onClick={closeModal}
                className="rounded-full bg-slate-100 p-2 text-slate-600 transition-colors hover:bg-slate-200"
              >
                <span className="material-symbols-outlined">close</span>
              </button>
            </div>

            <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
              <InputField
                label="Họ tên"
                value={formState.name}
                onChange={(value) =>
                  setFormState((current) => ({ ...current, name: value }))
                }
              />
              <InputField
                label="Số điện thoại"
                value={formState.phone}
                onChange={(value) =>
                  setFormState((current) => ({ ...current, phone: value }))
                }
              />
              <InputField
                label="Email"
                value={formState.email}
                onChange={(value) =>
                  setFormState((current) => ({ ...current, email: value }))
                }
              />
              <InputField
                label="Ngày sinh"
                type="date"
                value={formState.birthDate}
                onChange={(value) =>
                  setFormState((current) => ({ ...current, birthDate: value }))
                }
              />
              <SelectField
                label="Giới tính"
                value={formState.gender}
                onChange={(value) =>
                  setFormState((current) => ({ ...current, gender: value }))
                }
                options={["", "MALE", "FEMALE"]}
                optionLabels={["Chọn giới tính", "Nam", "Nữ"]}
              />
              <SelectField
                label="Vai trò"
                value={formState.role}
                onChange={(value) =>
                  setFormState((current) => ({
                    ...current,
                    role: value as AccountRole,
                  }))
                }
                options={["EMPLOYEE", "ADMIN"]}
                optionLabels={["Nhân viên", "Quản trị viên"]}
              />
              {formMode === "create" ? (
                <InputField
                  label="Mật khẩu"
                  type="password"
                  value={formState.password}
                  onChange={(value) =>
                    setFormState((current) => ({ ...current, password: value }))
                  }
                  fullWidth
                />
              ) : (
                <div className="border-outline/20 text-secondary rounded-xl border border-dashed bg-slate-50 p-4 text-sm md:col-span-2">
                  Giao diện cập nhật tài khoản đã sẵn sàng. Khi kết nối API, nút
                  lưu sẽ cập nhật dữ liệu thật.
                </div>
              )}
            </div>

            <div className="mt-6 flex justify-end gap-3">
              <button
                type="button"
                onClick={closeModal}
                className="border-outline/20 text-secondary rounded-lg border px-5 py-3 font-semibold transition-all hover:bg-slate-50"
              >
                Hủy
              </button>
              <button
                type="submit"
                className="from-primary to-primary/80 text-on-primary hover:from-primary/90 hover:to-primary rounded-lg bg-linear-to-r px-5 py-3 font-semibold transition-all"
              >
                {formMode === "create" ? "Tạo tài khoản" : "Lưu thay đổi"}
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
}

function DetailItem({ label, value }: { label: string; value: string }) {
  return (
    <div className="border-outline/10 rounded-xl border bg-slate-50 p-4">
      <p className="text-secondary text-xs font-bold tracking-widest uppercase">
        {label}
      </p>
      <p className="text-on-surface mt-2 text-sm font-semibold">{value}</p>
    </div>
  );
}

function InputField({
  label,
  value,
  onChange,
  type = "text",
  fullWidth = false,
}: {
  label: string;
  value: string;
  onChange: (value: string) => void;
  type?: string;
  fullWidth?: boolean;
}) {
  return (
    <label className={fullWidth ? "md:col-span-2" : ""}>
      <span className="text-on-surface mb-2 block text-sm font-semibold">
        {label}
      </span>
      <input
        type={type}
        value={value}
        onChange={(event) => onChange(event.target.value)}
        className="border-outline/20 focus:border-primary focus:ring-primary/20 w-full rounded-lg border bg-white px-4 py-3 transition-all outline-none focus:ring-2"
      />
    </label>
  );
}

function SelectField({
  label,
  value,
  onChange,
  options,
  optionLabels,
}: {
  label: string;
  value: string;
  onChange: (value: string) => void;
  options: string[];
  optionLabels: string[];
}) {
  return (
    <label>
      <span className="text-on-surface mb-2 block text-sm font-semibold">
        {label}
      </span>
      <select
        value={value}
        onChange={(event) => onChange(event.target.value)}
        className="border-outline/20 focus:border-primary focus:ring-primary/20 w-full rounded-lg border bg-white px-4 py-3 transition-all outline-none focus:ring-2"
      >
        {options.map((option, index) => (
          <option key={option || index} value={option}>
            {optionLabels[index]}
          </option>
        ))}
      </select>
    </label>
  );
}
