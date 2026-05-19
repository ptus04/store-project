import { useEffect, useState } from "react";

interface UserProfile {
  id: string;
  name: string;
  phone: string;
  email: string | null;
  role: string;
  gender: "MALE" | "FEMALE" | null;
  birthDate: string | null;
  createdAt: string;
}

export default function Profile() {
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<"info" | "password">("info");

  // Edit Profile States
  const [isEditing, setIsEditing] = useState(false);
  const [name, setName] = useState("");
  const [phone, setPhone] = useState("");
  const [email, setEmail] = useState("");
  const [gender, setGender] = useState<"MALE" | "FEMALE" | "">("");
  const [birthDate, setBirthDate] = useState("");
  const [updateLoading, setUpdateLoading] = useState(false);
  const [updateError, setUpdateError] = useState("");
  const [updateSuccess, setUpdateSuccess] = useState("");

  // Change Password States
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [showOldPassword, setShowOldPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [passwordLoading, setPasswordLoading] = useState(false);
  const [passwordError, setPasswordError] = useState("");
  const [passwordSuccess, setPasswordSuccess] = useState("");

  const API_URL = import.meta.env.VITE_API_URL;
  const token = localStorage.getItem("token");

  useEffect(() => {
    async function fetchProfile() {
      setLoading(true);
      try {
        const response = await fetch(`${API_URL}/api/users/profile`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (!response.ok) {
          console.error("Không thể tải thông tin cá nhân");
          return;
        }
        const data: UserProfile = await response.json();
        setProfile(data);
        // Initialize edit fields
        setName(data.name || "");
        setPhone(data.phone || "");
        setEmail(data.email || "");
        setGender(data.gender || "");
        setBirthDate(data.birthDate || "");
      } catch (err) {
        console.error(err instanceof Error ? err.message : String(err));
      } finally {
        setLoading(false);
      }
    }
    fetchProfile();
  }, [API_URL, token]);

  async function handleUpdateProfile(e: React.FormEvent) {
    e.preventDefault();
    if (!name.trim() || !phone.trim()) return;

    setUpdateLoading(true);
    setUpdateError("");
    setUpdateSuccess("");

    try {
      const response = await fetch(`${API_URL}/api/users/profile`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          name,
          phone,
          email: email.trim() || null,
          gender: gender || null,
          birthDate: birthDate || null,
        }),
      });

      const data = await response.json();

      if (!response.ok) {
        setUpdateError(data.message || "Không thể cập nhật thông tin cá nhân");
        return;
      }

      setProfile(data);
      // Update local storage user details if changed
      const localUserRaw = localStorage.getItem("user");
      if (localUserRaw) {
        const localUser = JSON.parse(localUserRaw);
        localUser.name = data.name;
        localStorage.setItem("user", JSON.stringify(localUser));
      }

      setUpdateSuccess("Cập nhật thông tin cá nhân thành công!");
      setIsEditing(false);
    } catch (err) {
      setUpdateError(
        err instanceof Error
          ? err.message
          : "Lỗi khi cập nhật thông tin cá nhân",
      );
    } finally {
      setUpdateLoading(false);
    }
  }

  async function handleChangePassword(e: React.FormEvent) {
    e.preventDefault();
    if (!oldPassword || !newPassword || !confirmPassword) return;

    if (newPassword !== confirmPassword) {
      setPasswordError("Mật khẩu mới và xác nhận mật khẩu không trùng khớp");
      return;
    }

    if (newPassword.length < 6) {
      setPasswordError("Mật khẩu mới phải có ít nhất 6 ký tự");
      return;
    }

    setPasswordLoading(true);
    setPasswordError("");
    setPasswordSuccess("");

    try {
      const response = await fetch(`${API_URL}/api/users/change-password`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          oldPassword,
          newPassword,
        }),
      });

      const data = await response.json();

      if (!response.ok) {
        setPasswordError(data.message || "Không thể đổi mật khẩu");
        return;
      }

      setPasswordSuccess("Đổi mật khẩu thành công!");
      setOldPassword("");
      setNewPassword("");
      setConfirmPassword("");
    } catch (err) {
      setPasswordError(
        err instanceof Error ? err.message : "Lỗi khi đổi mật khẩu",
      );
    } finally {
      setPasswordLoading(false);
    }
  }

  return (
    <main className="p-gutter bg-background flex-1 overflow-y-auto">
      <div className="max-w-container-max mx-auto space-y-8 pb-12">
        {/* Page Header */}
        <div className="border-outline-variant flex flex-col justify-between gap-4 border-b pb-4 sm:flex-row sm:items-end">
          <div>
            <h2 className="text-headline-md font-headline-md text-primary tracking-tight">
              Tài Khoản & Cài Đặt
            </h2>
            <p className="text-body-md font-body-md text-secondary mt-1">
              Quản lý hồ sơ cá nhân và mật khẩu bảo mật của bạn.
            </p>
          </div>
        </div>

        {loading ? (
          <div className="bg-surface-container-lowest border-outline-variant flex min-h-[300px] items-center justify-center border p-8">
            <div className="flex flex-col items-center gap-3">
              <svg
                className="text-primary h-8 w-8 animate-spin"
                fill="none"
                viewBox="0 0 24 24"
              >
                <circle
                  className="opacity-25"
                  cx="12"
                  cy="12"
                  r="10"
                  stroke="currentColor"
                  strokeWidth="4"
                />
                <path
                  className="opacity-75"
                  fill="currentColor"
                  d="M4 12a8 8 0 018-8v8z"
                />
              </svg>
              <p className="text-secondary text-sm">
                Đang tải thông tin cá nhân...
              </p>
            </div>
          </div>
        ) : (
          <div className="grid grid-cols-1 gap-8 lg:grid-cols-4">
            {/* Left Sidebar / Tabs */}
            <div className="space-y-2 lg:col-span-1">
              <button
                onClick={() => setActiveTab("info")}
                className={`flex w-full items-center gap-3 border-l-4 px-4 py-3 text-left text-sm font-bold tracking-wider uppercase transition-all duration-200 ${
                  activeTab === "info"
                    ? "bg-primary text-on-primary border-primary"
                    : "bg-surface-container-lowest text-secondary border-transparent hover:bg-gray-100"
                }`}
              >
                <span className="material-symbols-outlined text-[20px]">
                  person
                </span>
                <span>Thông tin cá nhân</span>
              </button>

              <button
                onClick={() => setActiveTab("password")}
                className={`flex w-full items-center gap-3 border-l-4 px-4 py-3 text-left text-sm font-bold tracking-wider uppercase transition-all duration-200 ${
                  activeTab === "password"
                    ? "bg-primary text-on-primary border-primary"
                    : "bg-surface-container-lowest text-secondary border-transparent hover:bg-gray-100"
                }`}
              >
                <span className="material-symbols-outlined text-[20px]">
                  lock
                </span>
                <span>Đổi mật khẩu</span>
              </button>
            </div>

            {/* Right Form Panels */}
            <div className="lg:col-span-3">
              {activeTab === "info" && (
                <div className="bg-surface-container-lowest border-outline-variant space-y-6 border p-8">
                  <div className="border-outline-variant flex items-center justify-between border-b pb-4">
                    <h3 className="text-title-lg text-primary font-bold">
                      Thông tin hồ sơ
                    </h3>
                    {!isEditing && (
                      <button
                        onClick={() => setIsEditing(true)}
                        className="bg-primary text-on-primary flex cursor-pointer items-center gap-2 px-4 py-2 text-xs font-bold uppercase transition-all hover:bg-gray-800 active:scale-[0.98]"
                      >
                        <span className="material-symbols-outlined text-[16px]">
                          edit
                        </span>
                        <span>Cập nhật</span>
                      </button>
                    )}
                  </div>

                  {updateSuccess && (
                    <div className="rounded border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-700">
                      {updateSuccess}
                    </div>
                  )}

                  {updateError && (
                    <div className="rounded border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                      {updateError}
                    </div>
                  )}

                  {isEditing ? (
                    <form onSubmit={handleUpdateProfile} className="space-y-6">
                      <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
                        <div>
                          <label
                            className="text-secondary mb-2 block text-xs font-bold uppercase"
                            htmlFor="editName"
                          >
                            Họ và tên <span className="text-red-500">*</span>
                          </label>
                          <input
                            id="editName"
                            type="text"
                            required
                            className="w-full border-0 border-b border-gray-300 bg-gray-50 px-3 py-3 text-gray-900 focus:border-gray-950 focus:ring-0 focus:outline-none disabled:opacity-50"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            disabled={updateLoading}
                          />
                        </div>

                        <div>
                          <label
                            className="text-secondary mb-2 block text-xs font-bold uppercase"
                            htmlFor="editPhone"
                          >
                            Số điện thoại{" "}
                            <span className="text-red-500">*</span>
                          </label>
                          <input
                            id="editPhone"
                            type="tel"
                            required
                            className="w-full border-0 border-b border-gray-300 bg-gray-50 px-3 py-3 text-gray-900 focus:border-gray-950 focus:ring-0 focus:outline-none disabled:opacity-50"
                            value={phone}
                            onChange={(e) => setPhone(e.target.value)}
                            disabled={updateLoading}
                          />
                        </div>

                        <div>
                          <label
                            className="text-secondary mb-2 block text-xs font-bold uppercase"
                            htmlFor="editEmail"
                          >
                            Email
                          </label>
                          <input
                            id="editEmail"
                            type="email"
                            className="w-full border-0 border-b border-gray-300 bg-gray-50 px-3 py-3 text-gray-900 focus:border-gray-950 focus:ring-0 focus:outline-none disabled:opacity-50"
                            placeholder="Chưa cập nhật"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            disabled={updateLoading}
                          />
                        </div>

                        <div>
                          <label
                            className="text-secondary mb-2 block text-xs font-bold uppercase"
                            htmlFor="editGender"
                          >
                            Giới tính
                          </label>
                          <select
                            id="editGender"
                            className="w-full border-0 border-b border-gray-300 bg-gray-50 px-3 py-3 text-gray-900 focus:border-gray-950 focus:ring-0 focus:outline-none disabled:opacity-50"
                            value={gender}
                            onChange={(e) =>
                              setGender(
                                e.target.value as "MALE" | "FEMALE" | "",
                              )
                            }
                            disabled={updateLoading}
                          >
                            <option value="">Chọn giới tính</option>
                            <option value="MALE">Nam</option>
                            <option value="FEMALE">Nữ</option>
                          </select>
                        </div>

                        <div>
                          <label
                            className="text-secondary mb-2 block text-xs font-bold uppercase"
                            htmlFor="editBirthDate"
                          >
                            Ngày sinh
                          </label>
                          <input
                            id="editBirthDate"
                            type="date"
                            className="w-full border-0 border-b border-gray-300 bg-gray-50 px-3 py-3 text-gray-900 focus:border-gray-950 focus:ring-0 focus:outline-none disabled:opacity-50"
                            value={birthDate}
                            onChange={(e) => setBirthDate(e.target.value)}
                            disabled={updateLoading}
                          />
                        </div>
                      </div>

                      <div className="border-outline-variant flex justify-end gap-4 border-t pt-6">
                        <button
                          type="button"
                          onClick={() => {
                            setIsEditing(false);
                            setUpdateError("");
                            if (profile) {
                              setName(profile.name);
                              setPhone(profile.phone);
                              setEmail(profile.email || "");
                              setGender(profile.gender || "");
                              setBirthDate(profile.birthDate || "");
                            }
                          }}
                          className="text-secondary px-4 py-2 text-xs font-bold uppercase transition-colors hover:bg-gray-100"
                          disabled={updateLoading}
                        >
                          Hủy
                        </button>
                        <button
                          type="submit"
                          className="bg-gray-900 px-6 py-2 text-xs font-bold text-white uppercase transition-all hover:bg-gray-800 active:scale-[0.98] disabled:opacity-55"
                          disabled={updateLoading}
                        >
                          {updateLoading ? "Đang lưu..." : "Lưu thay đổi"}
                        </button>
                      </div>
                    </form>
                  ) : (
                    <div className="grid grid-cols-1 gap-8 text-sm md:grid-cols-2">
                      <div className="space-y-1">
                        <span className="text-secondary text-[11px] font-bold tracking-wider uppercase">
                          Họ và tên
                        </span>
                        <p className="text-primary text-base font-medium">
                          {profile?.name}
                        </p>
                      </div>

                      <div className="space-y-1">
                        <span className="text-secondary text-[11px] font-bold tracking-wider uppercase">
                          Số điện thoại
                        </span>
                        <p className="text-primary text-base font-medium">
                          {profile?.phone}
                        </p>
                      </div>

                      <div className="space-y-1">
                        <span className="text-secondary text-[11px] font-bold tracking-wider uppercase">
                          Email
                        </span>
                        <p className="text-primary text-base font-medium">
                          {profile?.email || (
                            <span className="text-gray-400 italic">
                              Chưa cập nhật
                            </span>
                          )}
                        </p>
                      </div>

                      <div className="space-y-1">
                        <span className="text-secondary text-[11px] font-bold tracking-wider uppercase">
                          Vai trò
                        </span>
                        <p className="text-primary text-base font-medium tracking-wider text-blue-600 uppercase">
                          {profile?.role === "ADMIN"
                            ? "Quản trị viên"
                            : "Nhân viên"}
                        </p>
                      </div>

                      <div className="space-y-1">
                        <span className="text-secondary text-[11px] font-bold tracking-wider uppercase">
                          Giới tính
                        </span>
                        <p className="text-primary text-base font-medium">
                          {profile?.gender === "MALE" ? (
                            "Nam"
                          ) : profile?.gender === "FEMALE" ? (
                            "Nữ"
                          ) : (
                            <span className="text-gray-400 italic">
                              Chưa chọn
                            </span>
                          )}
                        </p>
                      </div>

                      <div className="space-y-1">
                        <span className="text-secondary text-[11px] font-bold tracking-wider uppercase">
                          Ngày sinh
                        </span>
                        <p className="text-primary text-base font-medium">
                          {profile?.birthDate ? (
                            new Date(profile.birthDate).toLocaleDateString(
                              "vi-VN",
                            )
                          ) : (
                            <span className="text-gray-400 italic">
                              Chưa cập nhật
                            </span>
                          )}
                        </p>
                      </div>

                      <div className="space-y-1">
                        <span className="text-secondary text-[11px] font-bold tracking-wider uppercase">
                          Ngày tạo tài khoản
                        </span>
                        <p className="text-primary text-base font-medium">
                          {profile?.createdAt ? (
                            new Date(profile.createdAt).toLocaleDateString(
                              "vi-VN",
                            )
                          ) : (
                            <span className="text-gray-400 italic">
                              Chưa rõ
                            </span>
                          )}
                        </p>
                      </div>
                    </div>
                  )}
                </div>
              )}

              {activeTab === "password" && (
                <div className="bg-surface-container-lowest border-outline-variant space-y-6 border p-8">
                  <div className="border-outline-variant border-b pb-4">
                    <h3 className="text-title-lg text-primary font-bold">
                      Đổi mật khẩu bảo mật
                    </h3>
                  </div>

                  {passwordSuccess && (
                    <div className="rounded border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-700">
                      {passwordSuccess}
                    </div>
                  )}

                  {passwordError && (
                    <div className="rounded border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                      {passwordError}
                    </div>
                  )}

                  <form
                    onSubmit={handleChangePassword}
                    className="max-w-md space-y-6"
                  >
                    <div>
                      <label
                        className="text-secondary mb-2 block text-xs font-bold uppercase"
                        htmlFor="oldPass"
                      >
                        Mật khẩu cũ <span className="text-red-500">*</span>
                      </label>
                      <div className="relative">
                        <input
                          id="oldPass"
                          required
                          type={showOldPassword ? "text" : "password"}
                          className="w-full border-0 border-b border-gray-300 bg-gray-50 px-3 py-3 pr-12 text-gray-900 focus:border-gray-950 focus:ring-0 focus:outline-none disabled:opacity-50"
                          value={oldPassword}
                          onChange={(e) => setOldPassword(e.target.value)}
                          disabled={passwordLoading}
                        />
                        <button
                          type="button"
                          onClick={() => setShowOldPassword(!showOldPassword)}
                          className="absolute top-1/2 right-3 flex -translate-y-1/2 items-center text-gray-500 hover:text-gray-900"
                        >
                          <span className="material-symbols-outlined text-[20px]">
                            {showOldPassword ? "visibility_off" : "visibility"}
                          </span>
                        </button>
                      </div>
                    </div>

                    <div>
                      <label
                        className="text-secondary mb-2 block text-xs font-bold uppercase"
                        htmlFor="newPass"
                      >
                        Mật khẩu mới <span className="text-red-500">*</span>
                      </label>
                      <div className="relative">
                        <input
                          id="newPass"
                          required
                          minLength={6}
                          type={showNewPassword ? "text" : "password"}
                          className="w-full border-0 border-b border-gray-300 bg-gray-50 px-3 py-3 pr-12 text-gray-900 focus:border-gray-950 focus:ring-0 focus:outline-none disabled:opacity-50"
                          value={newPassword}
                          onChange={(e) => setNewPassword(e.target.value)}
                          disabled={passwordLoading}
                        />
                        <button
                          type="button"
                          onClick={() => setShowNewPassword(!showNewPassword)}
                          className="absolute top-1/2 right-3 flex -translate-y-1/2 items-center text-gray-500 hover:text-gray-900"
                        >
                          <span className="material-symbols-outlined text-[20px]">
                            {showNewPassword ? "visibility_off" : "visibility"}
                          </span>
                        </button>
                      </div>
                    </div>

                    <div>
                      <label
                        className="text-secondary mb-2 block text-xs font-bold uppercase"
                        htmlFor="confirmPass"
                      >
                        Xác nhận mật khẩu mới{" "}
                        <span className="text-red-500">*</span>
                      </label>
                      <input
                        id="confirmPass"
                        required
                        type="password"
                        className="w-full border-0 border-b border-gray-300 bg-gray-50 px-3 py-3 text-gray-900 focus:border-gray-950 focus:ring-0 focus:outline-none disabled:opacity-50"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        disabled={passwordLoading}
                      />
                    </div>

                    <div className="border-outline-variant flex justify-end border-t pt-6">
                      <button
                        type="submit"
                        className="bg-gray-900 px-6 py-2 text-xs font-bold text-white uppercase transition-all hover:bg-gray-800 active:scale-[0.98] disabled:opacity-55"
                        disabled={passwordLoading}
                      >
                        {passwordLoading ? "Đang đổi..." : "Cập nhật mật khẩu"}
                      </button>
                    </div>
                  </form>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </main>
  );
}
