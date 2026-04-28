import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import Loading from "@components/ui/Loading";
import { useAuth } from "@/utils/AuthContext";
import { UserProfile } from "@/types";

interface PasswordForm {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

/**
 * ProfilePage component allows users to view and edit their profile information,
 * including name, email, gender, birth date, and avatar. It also handles password changes.
 */
const ProfilePage = () => {
  const { user, isLoading: authLoading, refreshUser, logout } = useAuth();
  const navigate = useNavigate();
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Form chỉnh sửa thông tin
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [gender, setGender] = useState("");
  const [birthDate, setBirthDate] = useState("");
  const [saveLoading, setSaveLoading] = useState(false);
  const [saveError, setSaveError] = useState<string | null>(null);
  const [saveSuccess, setSaveSuccess] = useState(false);

  // Avatar
  const [avatarPreview, setAvatarPreview] = useState<string | null>(null);
  const [avatarLoading, setAvatarLoading] = useState(false);
  const [avatarError, setAvatarError] = useState<string | null>(null);

  // Modal đổi mật khẩu
  const [showPwModal, setShowPwModal] = useState(false);
  const [pwForm, setPwForm] = useState<PasswordForm>({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });
  const [pwLoading, setPwLoading] = useState(false);
  const [pwError, setPwError] = useState<string | null>(null);
  const [pwSuccess, setPwSuccess] = useState(false);
  const [showCurrent, setShowCurrent] = useState(false);
  const [showNew, setShowNew] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);

  // Load profile from context
  useEffect(() => {
    if (!authLoading) {
      if (!user) {
        window.location.href = "http://localhost:8080/dang-nhap";
        return;
      }
      // Redirect customers to backend profile
      if (user.role === "CUSTOMER") {
        window.location.href = "http://localhost:8080/tai-khoan";
        return;
      }
      setName(user.name);
      setEmail(user.email ?? "");
      setGender(user.gender ?? "");
      setBirthDate(user.birthDate ?? "");
      setAvatarPreview(user.avatar);
      setLoading(false);
    }
  }, [user, authLoading]);

  // ─── Avatar change ───────────────────────────────────────────────
  const handleAvatarChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    // Preview tức thì
    setAvatarPreview(URL.createObjectURL(file));
    setAvatarError(null);
    setAvatarLoading(true);

    const formData = new FormData();
    formData.append("file", file);

    try {
      const res = await fetch("/api/users/avatar", {
        method: "POST",
        credentials: "include",
        body: formData,
      });
      if (!res.ok) {
        const data = await res.json();
        setAvatarError(data.message ?? "Upload thất bại");
        setAvatarPreview(user?.avatar ?? null); // Rollback preview
      } else {
        await refreshUser();
      }
    } catch {
      setAvatarError("Không thể kết nối máy chủ");
      setAvatarPreview(user?.avatar ?? null);
    } finally {
      setAvatarLoading(false);
    }
  };

  // ─── Save profile ─────────────────────────────────────────────────
  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaveLoading(true);
    setSaveError(null);
    setSaveSuccess(false);

    try {
      const res = await fetch("/api/users/profile", {
        method: "PUT",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          name,
          email: email || null,
          gender: gender || null,
          birthDate: birthDate || null,
        }),
      });
      if (!res.ok) {
        const data = await res.json();
        setSaveError(data.message ?? "Có lỗi xảy ra");
      } else {
        await refreshUser();
        setSaveSuccess(true);
        setTimeout(() => setSaveSuccess(false), 3000);
      }
    } catch {
      setSaveError("Không thể kết nối máy chủ");
    } finally {
      setSaveLoading(false);
    }
  };

  // ─── Change password ──────────────────────────────────────────────
  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    if (pwForm.newPassword !== pwForm.confirmPassword) {
      setPwError("Mật khẩu mới và xác nhận không khớp.");
      return;
    }
    setPwLoading(true);
    setPwError(null);
    setPwSuccess(false);

    try {
      const res = await fetch("/api/users/password", {
        method: "PUT",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(pwForm),
      });
      const data = await res.json();
      if (!res.ok) {
        setPwError(data.message ?? "Có lỗi xảy ra");
      } else {
        setPwSuccess(true);
        setTimeout(() => {
          setShowPwModal(false);
          setPwForm({ currentPassword: "", newPassword: "", confirmPassword: "" });
          setPwSuccess(false);
        }, 1500);
      }
    } catch {
      setPwError("Không thể kết nối máy chủ");
    } finally {
      setPwLoading(false);
    }
  };

  if (loading) return <Loading isLoading />;

  if (error) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gray-50">
        <p className="text-red-500 font-bold">{error}</p>
      </div>
    );
  }

  const initials = user.name.charAt(0).toUpperCase() || "?";

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Top bar */}
      <div className="border-b border-gray-100 bg-white px-8 py-4 flex items-center justify-between shadow-sm">
        <button
          onClick={() => navigate("/")}
          className="flex items-center gap-2 text-sm font-bold text-gray-500 hover:text-black transition-colors"
        >
          <i className="fa fa-arrow-left" /> Về trang chủ
        </button>
        <h1 className="text-lg font-black text-gray-900">Thông tin tài khoản</h1>
        <div className="flex items-center gap-3">
          <button
            onClick={logout}
            className="flex items-center gap-2 rounded-xl bg-red-50 px-4 py-2 text-sm font-bold text-red-600 transition-all hover:bg-red-100"
          >
            <i className="fa fa-sign-out" /> Đăng xuất
          </button>
        </div>
      </div>

      <div className="mx-auto max-w-3xl space-y-6 p-8">

        {/* ─── Card avatar + thông tin cơ bản ─────────────────────── */}
        <div className="overflow-hidden rounded-3xl bg-white shadow-sm">
          {/* Banner gradient */}
          <div className="h-28 bg-gradient-to-br from-gray-900 via-gray-800 to-red-900" />

          {/* Avatar + nút upload */}
          <div className="-mt-14 flex flex-col items-center px-8 pb-6">
            <div className="relative">
              <div className="h-28 w-28 overflow-hidden rounded-full border-4 border-white shadow-xl bg-gray-100">
                {avatarPreview ? (
                  <img
                    src={avatarPreview}
                    alt="Avatar"
                    className="h-full w-full object-cover"
                  />
                ) : (
                  <div className="flex h-full w-full items-center justify-center bg-red-500 text-4xl font-black text-white">
                    {initials}
                  </div>
                )}
                {/* Overlay loading */}
                {avatarLoading && (
                  <div className="absolute inset-0 flex items-center justify-center rounded-full bg-black/40">
                    <div className="h-6 w-6 animate-spin rounded-full border-3 border-white border-t-transparent" />
                  </div>
                )}
              </div>

              {/* Camera button */}
              <button
                id="btn-upload-avatar"
                onClick={() => fileInputRef.current?.click()}
                disabled={avatarLoading}
                className="absolute bottom-1 right-1 flex h-8 w-8 items-center justify-center rounded-full bg-black text-white shadow-md hover:bg-gray-800 disabled:opacity-50 transition-all"
                title="Đổi ảnh đại diện"
              >
                <i className="fa fa-camera text-xs" />
              </button>
              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                className="hidden"
                onChange={handleAvatarChange}
              />
            </div>

            {avatarError && (
              <p className="mt-2 text-xs font-semibold text-red-500">{avatarError}</p>
            )}

            <div className="mt-3 text-center">
              <p className="text-2xl font-black text-gray-900">{user.name}</p>
              <span className="mt-1 inline-block rounded-full bg-gray-100 px-3 py-0.5 text-xs font-bold uppercase tracking-wider text-gray-500">
                {user.role}
              </span>
            </div>

            <p className="mt-1 text-sm text-gray-400">
              <i className="fa fa-phone mr-1" />
              {user.phone}
            </p>
          </div>
        </div>

        {/* ─── Form chỉnh sửa thông tin ────────────────────────────── */}
        <div className="rounded-3xl bg-white p-8 shadow-sm">
          <h2 className="mb-6 text-xl font-black text-gray-900">Chỉnh sửa thông tin</h2>

          <form id="form-profile" onSubmit={handleSave} className="space-y-5">
            {/* Họ tên */}
            <div>
              <label className="mb-1.5 block text-xs font-bold uppercase tracking-wider text-gray-500">
                Họ tên <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
                maxLength={128}
                className="w-full rounded-2xl border-2 border-gray-100 bg-gray-50 px-4 py-3 font-semibold text-gray-900 outline-none transition-all focus:border-black focus:bg-white"
              />
            </div>

            {/* Email */}
            <div>
              <label className="mb-1.5 block text-xs font-bold uppercase tracking-wider text-gray-500">
                Email
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                maxLength={64}
                placeholder="Không bắt buộc"
                className="w-full rounded-2xl border-2 border-gray-100 bg-gray-50 px-4 py-3 font-semibold text-gray-900 outline-none transition-all focus:border-black focus:bg-white"
              />
            </div>

            {/* Giới tính + Ngày sinh (2 cột) */}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="mb-1.5 block text-xs font-bold uppercase tracking-wider text-gray-500">
                  Giới tính
                </label>
                <select
                  value={gender}
                  onChange={(e) => setGender(e.target.value)}
                  className="w-full rounded-2xl border-2 border-gray-100 bg-gray-50 px-4 py-3 font-semibold text-gray-900 outline-none transition-all focus:border-black focus:bg-white"
                >
                  <option value="">— Không chọn —</option>
                  <option value="MALE">Nam</option>
                  <option value="FEMALE">Nữ</option>
                </select>
              </div>

              <div>
                <label className="mb-1.5 block text-xs font-bold uppercase tracking-wider text-gray-500">
                  Ngày sinh
                </label>
                <input
                  type="date"
                  value={birthDate}
                  onChange={(e) => setBirthDate(e.target.value)}
                  className="w-full rounded-2xl border-2 border-gray-100 bg-gray-50 px-4 py-3 font-semibold text-gray-900 outline-none transition-all focus:border-black focus:bg-white"
                />
              </div>
            </div>

            {/* Số điện thoại (readonly) */}
            <div>
              <label className="mb-1.5 block text-xs font-bold uppercase tracking-wider text-gray-500">
                Số điện thoại
              </label>
              <div className="flex items-center gap-2 rounded-2xl border-2 border-gray-100 bg-gray-50 px-4 py-3">
                <i className="fa fa-lock text-xs text-gray-400" />
                <span className="font-semibold text-gray-400">{user.phone}</span>
              </div>
              <p className="mt-1 text-xs text-gray-400">Số điện thoại không thể thay đổi</p>
            </div>

            {saveError && (
              <div className="flex items-center gap-2 rounded-2xl bg-red-50 px-4 py-3">
                <i className="fa fa-exclamation-circle text-red-500" />
                <p className="text-sm font-semibold text-red-600">{saveError}</p>
              </div>
            )}
            {saveSuccess && (
              <div className="flex items-center gap-2 rounded-2xl bg-green-50 px-4 py-3">
                <i className="fa fa-check-circle text-green-500" />
                <p className="text-sm font-semibold text-green-600">Đã lưu thay đổi thành công!</p>
              </div>
            )}

            <div className="flex gap-3 pt-2">
              <button
                type="button"
                onClick={() => setShowPwModal(true)}
                className="flex items-center gap-2 rounded-2xl border-2 border-gray-200 px-5 py-3 text-sm font-bold text-gray-700 transition-all hover:border-gray-400"
              >
                <i className="fa fa-lock" /> Đổi mật khẩu
              </button>
              <button
                type="submit"
                disabled={saveLoading}
                className="flex flex-1 items-center justify-center gap-2 rounded-2xl bg-black py-3 text-sm font-bold text-white transition-all hover:bg-gray-800 disabled:opacity-60"
              >
                {saveLoading ? (
                  <>
                    <div className="h-4 w-4 animate-spin rounded-full border-2 border-white border-t-transparent" />
                    Đang lưu...
                  </>
                ) : (
                  <>
                    <i className="fa fa-save" /> Lưu thay đổi
                  </>
                )}
              </button>
            </div>
          </form>
        </div>
      </div>

      {/* ══════════════════════════════════════════
          MODAL: Đổi mật khẩu
      ══════════════════════════════════════════ */}
      {showPwModal && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm"
          onClick={(e) => e.target === e.currentTarget && setShowPwModal(false)}
        >
          <div className="w-full max-w-md rounded-3xl bg-white p-8 shadow-2xl">
            <div className="mb-6 flex items-center justify-between">
              <h2 className="text-2xl font-black text-gray-900">Đổi mật khẩu</h2>
              <button
                onClick={() => setShowPwModal(false)}
                className="flex h-8 w-8 items-center justify-center rounded-full text-gray-400 hover:bg-gray-100 hover:text-gray-700"
              >
                ✕
              </button>
            </div>

            <form id="form-change-password" onSubmit={handleChangePassword} className="space-y-4">
              {[
                { label: "Mật khẩu hiện tại", key: "currentPassword" as keyof PasswordForm, show: showCurrent, toggle: () => setShowCurrent(!showCurrent) },
                { label: "Mật khẩu mới", key: "newPassword" as keyof PasswordForm, show: showNew, toggle: () => setShowNew(!showNew), hint: "≥8 ký tự, hoa/thường/số/đặc biệt" },
                { label: "Xác nhận mật khẩu mới", key: "confirmPassword" as keyof PasswordForm, show: showConfirm, toggle: () => setShowConfirm(!showConfirm) },
              ].map(({ label, key, show, toggle, hint }) => (
                <div key={key}>
                  <label className="mb-1 block text-xs font-bold uppercase text-gray-500">
                    {label} <span className="text-red-500">*</span>
                  </label>
                  <div className="relative">
                    <input
                      type={show ? "text" : "password"}
                      value={pwForm[key]}
                      onChange={(e) => setPwForm({ ...pwForm, [key]: e.target.value })}
                      required
                      placeholder={hint}
                      className="w-full rounded-xl border-2 border-gray-100 px-4 py-2.5 pr-12 font-semibold text-gray-900 outline-none transition-all focus:border-black"
                    />
                    <button
                      type="button"
                      onClick={toggle}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-700"
                    >
                      <i className={`fa ${show ? "fa-eye-slash" : "fa-eye"}`} />
                    </button>
                  </div>
                </div>
              ))}

              {pwError && (
                <p className="rounded-xl bg-red-50 px-4 py-3 text-sm font-semibold text-red-600">
                  {pwError}
                </p>
              )}
              {pwSuccess && (
                <p className="rounded-xl bg-green-50 px-4 py-3 text-sm font-semibold text-green-600">
                  ✓ Đổi mật khẩu thành công!
                </p>
              )}

              <div className="flex gap-3 pt-2">
                <button
                  type="button"
                  onClick={() => setShowPwModal(false)}
                  className="flex-1 rounded-xl border-2 border-gray-200 py-2.5 font-bold text-gray-600 transition-all hover:border-gray-400"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  disabled={pwLoading}
                  className="flex-1 rounded-xl bg-black py-2.5 font-bold text-white transition-all hover:bg-gray-800 disabled:opacity-60"
                >
                  {pwLoading ? "Đang xử lý..." : "Xác nhận"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProfilePage;
