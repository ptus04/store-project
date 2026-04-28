import { Link } from "react-router-dom";

type NavbarProps = {
  isOpen: boolean;
  onClose: () => void;
};

const categories = {
  TOPS: ["TOPS", "TEE", "POLO"],
  OUTWEARS: ["OUTWEARS", "JACKETS", "HOODIES"],
  BOTTOMS: ["BOTTOMS", "SHORTS", "PANTS"],
  ACCESSORIES: ["ACCESSORIES", "WALLETS", "CAPS", "BACKPACKS"],
};

const Navbar = ({ isOpen, onClose }: NavbarProps) => {
  return (
    <nav
      className={`fixed top-0 left-0 z-30 h-dvh w-full duration-200 ${
        isOpen ? "bg-black/50 visible" : "invisible pointer-events-none"
      }`}
      onClick={(e) => e.target === e.currentTarget && onClose()}
    >
      <div
        className={`flex h-full w-full flex-col gap-4 overflow-auto bg-white/75 px-4 py-2 shadow-md backdrop-blur-md duration-200 md:w-2/5 lg:w-1/4 ${
          isOpen ? "translate-x-0" : "-translate-x-full"
        }`}
      >
        <div className="flex items-center justify-between">
          <form action="/danh-sach-san-pham" className="flex gap-2">
            <button
              className="cursor-pointer duration-200 hover:text-red-500"
              title="Tìm kiếm"
            >
              <i className="fa fa-search"></i>
            </button>
            <label>
              <span className="hidden">Từ khóa tìm kiếm</span>
              <input
                className="border-b px-3 py-1 outline-none"
                name="query"
                placeholder="Nhập từ khóa"
                required
                type="search"
              />
            </label>
          </form>
          <button
            className="aspect-square w-6 cursor-pointer duration-200 hover:bg-red-500 hover:text-white"
            onClick={onClose}
            title="Đóng menu"
            type="button"
          >
            <i className="fa fa-close"></i>
          </button>
        </div>

        <ul className="flex flex-col gap-2">
          {Object.entries(categories).map(([key, subcats]) => (
            <li key={key} className="group">
              <div className="flex items-center justify-between py-2 font-bold cursor-pointer hover:text-red-500">
                <span>{key}</span>
                <i className="fa fa-chevron-down text-xs"></i>
              </div>
              <ul className="hidden group-hover:block ps-4 flex flex-col gap-1">
                {subcats.map((sub) => (
                  <li key={sub}>
                    <Link
                      to={`/category/${sub.toLowerCase()}`}
                      className="text-sm hover:text-red-500"
                      onClick={onClose}
                    >
                      {sub}
                    </Link>
                  </li>
                ))}
              </ul>
            </li>
          ))}
        </ul>

        <div className="mt-auto pb-4">
          <ul className="text-sm flex flex-col gap-2 mb-4">
            <li>
              <Link
                className="hover:text-red-500"
                to="/refund-policy"
                onClick={onClose}
              >
                CHÍNH SÁCH ĐỔI TRẢ
              </Link>
            </li>
            <li>
              <Link
                className="hover:text-red-500"
                to="/privacy-policy"
                onClick={onClose}
              >
                CHÍNH SÁCH BẢO MẬT
              </Link>
            </li>
            <li>
              <Link
                className="hover:text-red-500"
                to="/about-us"
                onClick={onClose}
              >
                HỆ THỐNG CỬA HÀNG
              </Link>
            </li>
          </ul>
          <div className="flex items-center justify-center gap-4 text-xl mb-4">
            <a className="hover:text-red-500" href="#" title="Facebook">
              <i className="fa-brands fa-facebook"></i>
            </a>
            <a className="hover:text-red-500" href="#" title="TikTok">
              <i className="fa-brands fa-tiktok"></i>
            </a>
            <a className="hover:text-red-500" href="#" title="Instagram">
              <i className="fa-brands fa-instagram"></i>
            </a>
          </div>
          <p className="text-center text-xs">
            <span>Copyright {new Date().getFullYear()} &copy; </span>
            <Link className="hover:text-red-500" to="/">
              SLY
            </Link>
          </p>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
