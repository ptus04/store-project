import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function Login() {
  const [showPassword, setShowPassword] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const [error, setError] = useState("");
  const API_URL = import.meta.env.VITE_API_URL;

  async function handleLogin(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setLoading(true);
    setError(""); // thêm state error vào component

    try {
      // const response = await fetch("/auth/api-login", {
      //   // <-- dòng bị thiếu
      //   method: "POST",
      //   headers: { "Content-Type": "application/x-www-form-urlencoded" },
      //   body: new URLSearchParams({ email, password }),
      //   credentials: "include",
      // });

      const response = await fetch(`${API_URL}/api/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: new URLSearchParams({ email, password }),
      });

      const data = await response.json();

      if (!response.ok) {
        setError(data.message || "Đăng nhập thất bại");
        return;
      }

      localStorage.setItem("token", data.token);
      localStorage.setItem(
        "user",
        JSON.stringify({
          name: data.name,
          role: data.role,
          avatar: data.avatar ?? null,
        }),
      );

      navigate("/");
    } catch {
      setError("Không thể kết nối đến máy chủ. Vui lòng thử lại.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="flex min-h-screen flex-grow items-center justify-center bg-gray-100 px-5 py-5">
      <div className="w-full max-w-md">
        <div className="border border-gray-300 bg-white p-12 transition-all duration-300">
          <div className="mb-12">
            <h1 className="mb-2 text-3xl font-bold text-gray-900">
              Welcome Back
            </h1>
            <p className="text-base text-gray-500">
              Please enter your details to access your dashboard.
            </p>
          </div>

          <form className="space-y-8" onSubmit={handleLogin}>
            <div className="relative">
              <label
                className="mb-2 block text-sm font-bold text-gray-900 uppercase"
                htmlFor="email"
              >
                Email Address
              </label>
              <input
                className="w-full border-x-0 border-t-0 border-b border-gray-300 bg-transparent px-0 py-3 placeholder:text-gray-400 focus:border-gray-900 focus:ring-0 disabled:opacity-50"
                id="email"
                name="email"
                placeholder="name@company.com"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                disabled={loading}
              />
            </div>

            <div className="relative">
              <div className="mb-2 flex items-end justify-between">
                <label
                  className="block text-sm font-bold text-gray-900 uppercase"
                  htmlFor="password"
                >
                  Password
                </label>
                <a
                  className="text-sm text-gray-500 uppercase transition-colors hover:text-gray-900"
                  href="#"
                >
                  Forgot Password?
                </a>
              </div>

              <div className="relative">
                <input
                  className="w-full border-0 border-b border-gray-300 bg-gray-100 px-3 py-4 pr-12 text-gray-950 placeholder:text-gray-400 focus:border-gray-950 focus:ring-0 focus:outline-none disabled:opacity-50"
                  id="password"
                  name="password"
                  placeholder="••••••••"
                  type={showPassword ? "text" : "password"}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  disabled={loading}
                />
                <button
                  className="absolute top-1/2 right-3 flex -translate-y-1/2 items-center justify-center text-gray-500 transition-colors hover:text-gray-950"
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  <span className="material-symbols-outlined text-[22px]">
                    {showPassword ? "visibility_off" : "visibility"}
                  </span>
                </button>
              </div>
            </div>

            <div className="pt-4">
              <button
                className="flex w-full items-center justify-center bg-gray-900 py-5 text-sm font-bold tracking-widest text-white uppercase transition-all duration-200 hover:bg-gray-700 active:scale-[0.98] disabled:cursor-not-allowed disabled:opacity-60"
                type="submit"
                disabled={loading}
              >
                {loading ? (
                  <>
                    <svg
                      className="mr-2 h-4 w-4 animate-spin"
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
                    Đang đăng nhập...
                  </>
                ) : (
                  "Sign In"
                )}
              </button>
            </div>
            {error && (
              <div className="mb-6 rounded border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                {error}
              </div>
            )}
          </form>
        </div>
      </div>
    </main>
  );
}
