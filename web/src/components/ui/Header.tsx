import { Link } from "react-router-dom";
import { useAuth } from "@/utils/AuthContext";

type HeaderProps = {
  onMenuClick: () => void;
};

const Header = ({ onMenuClick }: HeaderProps) => {
  const { user, isLoading } = useAuth();

  return (
    <header className="sticky top-0 z-10 flex w-full items-center justify-between bg-white px-4 py-2 shadow-md">
      <div className="flex shrink grow basis-0 flex-row items-center gap-3">
        <button
          className="cursor-pointer duration-200 hover:text-red-500"
          onClick={onMenuClick}
          title="Mở menu"
        >
          <i className="fa fa-bars"></i>
        </button>
        <form action="/danh-sach-san-pham" className="hidden md:flex">
          <label>
            <span className="hidden">Từ khóa</span>
            <input
              className="border-b px-3 py-1 outline-none"
              name="query"
              placeholder="Nhập từ khóa"
              required
              type="search"
            />
          </label>
          <button
            className="cursor-pointer px-1 py-1 duration-100 hover:text-red-500"
            title="Tìm kiếm"
            type="submit"
          >
            <i className="fa fa-search"></i>
          </button>
        </form>
      </div>

      <h1 className="mb-0 basis-auto text-center">
        <Link to="/">
          <img
            alt="LOGO SLY"
            className="h-12"
            src="/img/SLYLOGO_black-800x298.png"
          />
          <span className="invisible">Sly clothing</span>
        </Link>
      </h1>

      <div className="flex shrink grow basis-0 flex-row items-center justify-end gap-3">
        <div className="group relative">
          <Link
            className="relative flex items-center gap-1 hover:text-red-500"
            to="/gio-hang"
            title="Giỏ hàng"
          >
            <i className="fa fa-bag-shopping"></i>
            <span className="hidden md:inline"> Giỏ hàng</span>
          </Link>
        </div>

        {!isLoading && user ? (
          <a
            className="flex items-center gap-1 hover:text-red-500 font-bold"
            href={user.role === "CUSTOMER" ? "http://localhost:8080/tai-khoan" : "/profile"}
            title="Tài khoản"
          >
            <i className="fa fa-user-circle"></i>
            <span className="hidden md:inline">{user.name}</span>
          </a>
        ) : (
          <a
            className="flex items-center gap-1 hover:text-red-500"
            href="http://localhost:8080/dang-nhap"
            title="Tài khoản"
          >
            <i className="fa fa-user"></i>
            <span className="hidden md:inline">Tài khoản</span>
          </a>
        )}
      </div>
    </header>
  );
};

export default Header;
