import { useEffect, useState } from "react";
import { NavLink, useLocation, useSearchParams } from "react-router-dom";

type BreadCrumbsProps = {
  product?: {
    productId: string;
    productTitle: string;
  };
  order?: {
    orderId: string;
  };
};

const Breadcrumbs = (props: BreadCrumbsProps) => {
  const location = useLocation();
  const [params] = useSearchParams();
  const [category, setCategory] = useState(params.get("category"));
  const [query, setQuery] = useState(params.get("query"));

  useEffect(() => {
    setCategory(params.get("category"));
    setQuery(params.get("query"));
  }, [params]);

  return (
    <nav className="text-sm text-gray-500">
      <ol className="flex gap-1">
        <li>
          <NavLink
            className="after:text-gray-500 after:content-['_/_'] hover:text-red-500"
            to="/"
          >
            Trang chủ
          </NavLink>
        </li>

        {/\/products/.test(location.pathname) && (
          <>
            <li>
              <NavLink
                className="after:text-gray-500 after:content-['_/_'] hover:text-red-500"
                to="/products"
                end
              >
                Cửa hàng
              </NavLink>
            </li>

            {props.product && (
              <li>
                <NavLink to={`/products/${props.product.productId}`}>
                  {props.product.productTitle}
                </NavLink>
              </li>
            )}

            {category && (
              <li>
                <NavLink
                  className={`after:text-gray-500 after:content-['_/_'] hover:text-red-500`}
                  to={`/products/?category=${category}`}
                >
                  Phân loại: {category.toUpperCase()}
                </NavLink>
              </li>
            )}

            {query && (
              <li>
                <NavLink
                  to={`/products/?${category ? "category=" + category : ""}&query=${query}`}
                >
                  Tìm kiếm: {query}
                </NavLink>
              </li>
            )}
          </>
        )}

        {/\/about-us/.test(location.pathname) && (
          <li>
            <NavLink to="/about-us">Hệ thống cửa hàng</NavLink>
          </li>
        )}

        {/\/privacy-policy/.test(location.pathname) && (
          <li>
            <NavLink to="/privacy-policy">Chính sách bảo mật</NavLink>
          </li>
        )}

        {/\/refund-policy/.test(location.pathname) && (
          <li>
            <NavLink to="/refund-policy">Chính sách đổi trả</NavLink>
          </li>
        )}

        {/\/user/.test(location.pathname) && (
          <>
            <li className="after:text-gray-500 after:content-['_/_'] hover:text-red-500">
              <NavLink to="/user" end>
                Thông tin tài khoản
              </NavLink>
            </li>

            {/\/user\/order-history/.test(location.pathname) && (
              <li className="after:text-gray-500 after:content-['_/_'] hover:text-red-500">
                <NavLink to="/user/order-history" end>
                  Quản lý đơn hàng
                </NavLink>
              </li>
            )}

            {/\/user\/order-history\/.+/.test(location.pathname) &&
              props.order && (
                <li>
                  <NavLink to={`/user/order-history/${props.order.orderId}`}>
                    Thông tin đơn hàng: {props.order.orderId}
                  </NavLink>
                </li>
              )}
          </>
        )}

        {/\/cart/.test(location.pathname) && (
          <>
            <li className="after:text-gray-500 after:content-['_/_'] hover:text-red-500">
              <NavLink to="/cart">Giỏ hàng</NavLink>
            </li>

            {/\/cart\/checkout/.test(location.pathname) && (
              <li>
                <NavLink to="/cart">Thanh toán</NavLink>
              </li>
            )}
          </>
        )}
      </ol>
    </nav>
  );
};

export default Breadcrumbs;
