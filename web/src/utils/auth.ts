export function getCurrentUserRole(): string | null {
  try {
    const token = localStorage.getItem("token");
    if (!token) return null;
    // JWT có 3 phần: header.payload.signature — decode phần payload
    const payload = JSON.parse(atob(token.split(".")[1]));
    return payload.role ?? payload.authorities?.[0] ?? null;
  } catch {
    return null;
  }
}

export function isAdmin(): boolean {
  return (
    getCurrentUserRole() === "ROLE_ADMIN" || getCurrentUserRole() === "ADMIN"
  );
}
