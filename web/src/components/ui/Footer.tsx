import { Link } from "react-router-dom";

const Footer = () => {
  return (
    <footer className="mt-8 px-3 py-8 text-center border-t">
      <p className="mb-4 text-2xl font-bold">THEO DÕI SLY NGAY</p>
      <ul className="flex justify-center space-x-6 mb-6">
        <li>
          <a className="text-sm hover:text-red-500 flex items-center gap-2" href="#">
            <i className="fa-brands fa-facebook"></i>
            <span> FACEBOOK</span>
          </a>
        </li>
        <li>
          <a className="text-sm hover:text-red-500 flex items-center gap-2" href="#">
            <i className="fa-brands fa-tiktok"></i>
            <span> TIKTOK</span>
          </a>
        </li>
        <li>
          <a className="text-sm hover:text-red-500 flex items-center gap-2" href="#">
            <i className="fa-brands fa-instagram"></i>
            <span> INSTAGRAM</span>
          </a>
        </li>
      </ul>

      <hr className="my-4 border-gray-200" />
      <ul className="flex flex-wrap justify-center gap-4 md:gap-8 mb-6">
        <li>
          <Link className="hover:text-red-500" to="/privacy-policy">
            CHÍNH SÁCH BẢO MẬT
          </Link>
        </li>
        <li>
          <Link className="hover:text-red-500" to="/refund-policy">
            CHÍNH SÁCH ĐỔI TRẢ
          </Link>
        </li>
        <li>
          <Link className="hover:text-red-500" to="/about-us">
            HỆ THỐNG CỬA HÀNG
          </Link>
        </li>
      </ul>

      <address className="mt-6 text-sm not-italic text-gray-600">
        <p className="font-bold text-gray-900 mb-1">Công ty TNHH Thương Mại và Dịch Vụ SLY</p>
        <p>123 Đường ABC, Phường XYZ, Quận 123, TP. HCM</p>
        <p>
          Điện thoại:{" "}
          <a className="hover:text-red-500" href="tel:+84868635209">
            +84 868635209
          </a>
        </p>
        <p>
          Email:{" "}
          <a className="hover:text-red-500" href="mailto:cs@sly.vn">
            cs@sly.vn
          </a>
        </p>
      </address>
      
      <p className="mt-4 text-xs text-gray-500">
        <span>Copyright {new Date().getFullYear()} &copy; </span>
        <Link className="hover:text-red-500" to="/">
          SLY
        </Link>
      </p>
    </footer>
  );
};

export default Footer;
