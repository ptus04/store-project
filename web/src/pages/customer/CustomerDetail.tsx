import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { customerApi, type UserResponse } from "@/api/customerApi";
import { isAdmin } from "@/utils/auth";

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

export default function CustomerDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [customer, setCustomer] = useState<UserResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [togglingStatus, setTogglingStatus] = useState(false);
  const canToggle = isAdmin();

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    customerApi
      .getById(id)
      .then(setCustomer)
      .catch(() => setError("Không thể tải thông tin khách hàng."))
      .finally(() => setLoading(false));
  }, [id]);

  const handleToggleStatus = async () => {
    if (!customer || togglingStatus) return;
    const willDisable = !customer.disabledAt;
    const confirmed = window.confirm(
      willDisable
        ? `Vô hiệu hóa tài khoản của "${customer.name}"?`
        : `Kích hoạt lại tài khoản của "${customer.name}"?`,
    );
    if (!confirmed) return;

    setTogglingStatus(true);
    try {
      const updated = await customerApi.toggleStatus(customer.id, willDisable);
      setCustomer(updated);
    } catch {
      alert("Thao tác thất bại. Vui lòng thử lại.");
    } finally {
      setTogglingStatus(false);
    }
  };

  // ── Loading ──
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

  // ── Error ──
  if (error || !customer) {
    return (
      <main className="p-margin-page">
        <div className="py-16 text-center">
          <span className="material-symbols-outlined text-outline mb-3 block text-5xl">
            error_outline
          </span>
          <p className="text-secondary mb-6 text-sm">
            {error ?? "Không tìm thấy khách hàng."}
          </p>
          <button
            onClick={() => navigate("/customer")}
            className="bg-primary text-on-primary px-6 py-2 text-xs font-bold tracking-widest uppercase"
          >
            Quay lại danh sách
          </button>
        </div>
      </main>
    );
  }

  const disabled = !!customer.disabledAt;

  const fields: {
    icon: string;
    label: string;
    value: string;
    className?: string;
  }[] = [
    { icon: "phone", label: "Số điện thoại", value: customer.phone },
    { icon: "mail", label: "Email", value: customer.email ?? "—" },
    {
      icon: "wc",
      label: "Giới tính",
      value:
        customer.gender === "MALE"
          ? "Nam"
          : customer.gender === "FEMALE"
            ? "Nữ"
            : "—",
    },
    {
      icon: "cake",
      label: "Ngày sinh",
      value: customer.birthDate
        ? formatDate(customer.birthDate + "T00:00:00Z")
        : "—",
    },
    {
      icon: "verified",
      label: "Xác thực SĐT",
      value: customer.phoneVerifiedAt
        ? `✓ ${formatDate(customer.phoneVerifiedAt)}`
        : "Chưa xác thực",
      className: customer.phoneVerifiedAt ? "text-tertiary" : "text-outline",
    },
    {
      icon: "mark_email_read",
      label: "Xác thực Email",
      value: customer.emailVerifiedAt
        ? `✓ ${formatDate(customer.emailVerifiedAt)}`
        : "Chưa xác thực",
      className: customer.emailVerifiedAt ? "text-tertiary" : "text-outline",
    },
    {
      icon: "calendar_today",
      label: "Ngày tạo",
      value: formatDate(customer.createdAt),
    },
    {
      icon: "update",
      label: "Cập nhật lần cuối",
      value: formatDate(customer.updatedAt),
    },
    {
      icon: disabled ? "block" : "check_circle",
      label: "Trạng thái",
      value: disabled
        ? `Đã vô hiệu hóa (${formatDate(customer.disabledAt)})`
        : "Đang hoạt động",
      className: disabled ? "text-error" : "text-tertiary",
    },
  ];

  return (
    <main className="p-margin-page">
      {/* ── Breadcrumb / back ── */}
      <button
        onClick={() => navigate("/customer")}
        className="text-secondary hover:text-primary mb-stack-lg flex items-center space-x-1 text-xs font-bold tracking-widest uppercase transition-colors"
      >
        <span className="material-symbols-outlined text-[16px]">
          arrow_back
        </span>
        <span>Quay lại danh sách</span>
      </button>

      {/* ── Page header ── */}
      <div className="mb-gutter flex items-end justify-between">
        <div>
          <h2 className="font-headline-lg text-headline-lg text-primary tracking-tight">
            Chi tiết khách hàng
          </h2>
          <p className="font-body-base text-body-base text-secondary">
            Thông tin đầy đủ của tài khoản khách hàng
          </p>
        </div>

        {/* Toggle status button */}
        {canToggle && (
          <button
            disabled={togglingStatus}
            onClick={handleToggleStatus}
            className={`flex items-center space-x-2 border px-5 py-2 text-xs font-bold tracking-widest uppercase transition-all ${
              togglingStatus
                ? "cursor-wait opacity-50"
                : disabled
                  ? "border-primary text-primary hover:bg-primary hover:text-on-primary"
                  : "border-error text-error hover:bg-error hover:text-on-error"
            }`}
          >
            {togglingStatus ? (
              <span className="material-symbols-outlined animate-spin text-[16px]">
                progress_activity
              </span>
            ) : (
              <span className="material-symbols-outlined text-[16px]">
                {disabled ? "lock_open" : "block"}
              </span>
            )}
            <span>{disabled ? "Kích hoạt tài khoản" : "Vô hiệu hóa"}</span>
          </button>
        )}
      </div>

      <div className="gap-gutter grid grid-cols-1 lg:grid-cols-3">
        {/* ── Avatar card ── */}
        <div className="bg-surface border-outline-variant flex flex-col items-center justify-center space-y-4 border p-8 text-center">
          <div
            className={`border-outline-variant flex h-24 w-24 items-center justify-center border text-2xl font-bold ${
              disabled
                ? "bg-surface-container text-outline"
                : "bg-surface-container-highest text-primary"
            }`}
          >
            {getInitials(customer.name)}
          </div>
          <div>
            <p className="font-headline-sm text-primary text-lg font-bold">
              {customer.name}
            </p>
            <p className="text-secondary mt-1 text-xs">
              ID: #{customer.id.slice(0, 13).toUpperCase()}
            </p>
          </div>
          <span
            className={`border px-4 py-1 text-[11px] font-bold tracking-widest uppercase ${
              disabled
                ? "border-error/40 text-error bg-error/5"
                : "border-tertiary/40 text-tertiary bg-tertiary/5"
            }`}
          >
            {disabled ? "Vô hiệu hóa" : "Đang hoạt động"}
          </span>
        </div>

        {/* ── Info card ── */}
        <div className="bg-surface border-outline-variant border px-8 py-4 lg:col-span-2">
          <p className="font-label-caps text-label-caps text-outline mb-2 pt-2 tracking-widest uppercase">
            Thông tin tài khoản
          </p>
          <div className="divide-outline-variant divide-y">
            {fields.map((f) => (
              <FieldRow
                key={f.label}
                icon={f.icon}
                label={f.label}
                value={f.value}
                valueClassName={f.className}
              />
            ))}
          </div>
        </div>
      </div>
    </main>
  );
}
