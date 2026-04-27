import { memo, useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import ProductCarousel from "@components/ui/ProductCarousel.tsx";
import Button from "@components/ui/Button.tsx";
import Breadcrumbs from "@components/ui/BreadCrumbs.tsx";
import Loading from "@components/ui/Loading.tsx";
import { formatAsCurrency } from "@utils/formatters.ts";

// ─── Types ────────────────────────────────────────────────────────────────────

type ProductImage = { id: string; file: string; createdAt: string };
type ProductSize = { id: string; name: string; createdAt: string };

type Review = {
  id: string;
  author: string;
  avatar: string;
  rating: number;
  date: string;
  comment: string;
  verified: boolean;
};

type Product = {
  id: string;
  name: string;
  description: string;
  careInstructions: string;
  price: number;
  inStock: number;
  isNew: boolean;
  discount: number;
  productImages: ProductImage[];
  productSizes: ProductSize[];
};

// ─── Mock reviews (sẽ thay bằng API khi có bảng reviews) ────────────────────

const MOCK_REVIEWS: Review[] = [
  {
    id: "r1",
    author: "Nguyễn Thị Hoa",
    avatar: "H",
    rating: 5,
    date: "15/04/2025",
    comment:
      "Sản phẩm chất lượng tốt, vải mềm mịn, form đẹp đúng như mô tả. Giao hàng nhanh, đóng gói cẩn thận. Sẽ ủng hộ shop dài dài!",
    verified: true,
  },
  {
    id: "r2",
    author: "Trần Minh Tuấn",
    avatar: "T",
    rating: 4,
    date: "10/04/2025",
    comment:
      "Áo đẹp, chất vải thoáng mát. Màu sắc giống hình. Mình lấy size L vừa vặn. Trừ 1 sao vì giao hơi lâu.",
    verified: true,
  },
  {
    id: "r3",
    author: "Lê Thu Phương",
    avatar: "P",
    rating: 5,
    date: "05/04/2025",
    comment:
      "Mua lần 2 rồi, vẫn rất hài lòng. Chất lượng ổn định, giá cả hợp lý. Nhân viên tư vấn nhiệt tình.",
    verified: false,
  },
  {
    id: "r4",
    author: "Phạm Văn Đức",
    avatar: "Đ",
    rating: 4,
    date: "28/03/2025",
    comment:
      "Sản phẩm đúng mô tả. Chất vải thoáng, phù hợp thời tiết Việt Nam. Sẽ mua thêm.",
    verified: true,
  },
];

// ─── Star Rating ─────────────────────────────────────────────────────────────

const StarRating = ({ rating, max = 5 }: { rating: number; max?: number }) => (
  <div className="flex items-center gap-0.5">
    {Array.from({ length: max }).map((_, i) => (
      <span
        key={i}
        className={`fa-star text-sm ${i < Math.round(rating) ? "fa-solid text-amber-400" : "fa-regular text-gray-300"}`}
      />
    ))}
  </div>
);

// ─── Review Card ─────────────────────────────────────────────────────────────

const ReviewCard = memo(({ review }: { review: Review }) => (
  <div className="rounded-xl border border-gray-100 bg-white p-5 shadow-sm transition-shadow duration-200 hover:shadow-md">
    <div className="mb-3 flex items-start gap-3">
      <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-red-400 to-red-600 text-sm font-bold text-white">
        {review.avatar}
      </div>
      <div className="min-w-0 flex-1">
        <div className="flex flex-wrap items-center gap-2">
          <span className="font-semibold text-gray-800">{review.author}</span>
          {review.verified && (
            <span className="inline-flex items-center gap-1 rounded-full bg-green-50 px-2 py-0.5 text-xs font-medium text-green-600">
              <span className="fa-solid fa-circle-check text-[10px]" />
              Đã mua hàng
            </span>
          )}
        </div>
        <div className="mt-1 flex items-center gap-2">
          <StarRating rating={review.rating} />
          <span className="text-xs text-gray-400">{review.date}</span>
        </div>
      </div>
    </div>
    <p className="text-sm leading-relaxed text-gray-600">{review.comment}</p>
  </div>
));

// ─── Rating Summary ───────────────────────────────────────────────────────────

const RatingSummary = ({ reviews }: { reviews: Review[] }) => {
  const avg = reviews.reduce((sum, r) => sum + r.rating, 0) / reviews.length;
  const counts = [5, 4, 3, 2, 1].map((star) => ({
    star,
    count: reviews.filter((r) => r.rating === star).length,
  }));

  return (
    <div className="flex flex-col items-center gap-6 rounded-xl bg-gradient-to-br from-red-50 to-white p-6 sm:flex-row">
      {/* Big average */}
      <div className="flex flex-col items-center">
        <span className="text-6xl font-bold text-red-500">
          {avg.toFixed(1)}
        </span>
        <StarRating rating={avg} />
        <span className="mt-1 text-sm text-gray-500">
          {reviews.length} đánh giá
        </span>
      </div>

      {/* Bar breakdown */}
      <div className="flex-1 space-y-1.5 self-stretch">
        {counts.map(({ star, count }) => {
          const pct = reviews.length > 0 ? (count / reviews.length) * 100 : 0;
          return (
            <div
              key={star}
              className="flex items-center gap-2 text-xs text-gray-500"
            >
              <span className="w-3 text-right">{star}</span>
              <span className="fa-solid fa-star text-amber-400" />
              <div className="h-2 flex-1 overflow-hidden rounded-full bg-gray-100">
                <div
                  className="h-full rounded-full bg-amber-400 transition-all duration-500"
                  style={{ width: `${pct}%` }}
                />
              </div>
              <span className="w-4 text-right">{count}</span>
            </div>
          );
        })}
      </div>
    </div>
  );
};

// ─── Main Page ────────────────────────────────────────────────────────────────

const ProductDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const [product, setProduct] = useState<Product | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // size & quantity
  const [selectedSizeIndex, setSelectedSizeIndex] = useState<number | null>(
    null,
  );
  const [quantity, setQuantity] = useState(1);

  // tabs
  const [activeTab, setActiveTab] = useState<
    "description" | "instructions" | "reviews"
  >("description");

  // add-to-cart feedback
  const [addedToCart, setAddedToCart] = useState(false);

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    setError(null);

    fetch(`/api/products/${id}`)
      .then((res) => {
        if (!res.ok) throw new Error("Không tìm thấy sản phẩm");
        return res.json() as Promise<Product>;
      })
      .then((data) => {
        setProduct(data);
        setLoading(false);
      })
      .catch((err: Error) => {
        setError(err.message);
        setLoading(false);
      });
  }, [id]);

  const handleAddToCart = () => {
    // TODO: integrate with cart store / API
    setAddedToCart(true);
    setTimeout(() => setAddedToCart(false), 2000);
  };

  // ── Computed ──────────────────────────────────────────────────────────────
  if (loading) {
    return (
      <div className="flex min-h-[60vh] items-center justify-center">
        <Loading isLoading={true} />
      </div>
    );
  }

  if (error || !product) {
    return (
      <div className="flex min-h-[60vh] flex-col items-center justify-center gap-4 text-center">
        <span className="fa-solid fa-triangle-exclamation text-5xl text-red-400" />
        <p className="text-xl font-semibold text-gray-700">
          {error ?? "Sản phẩm không tồn tại"}
        </p>
        <Link to="/products">
          <Button preset="secondary">← Quay lại cửa hàng</Button>
        </Link>
      </div>
    );
  }

  const discountedPrice = product.price * (1 - product.discount);
  const discountPct = Math.round(product.discount * 100);
  const images = product.productImages.map((img) => img.file);
  const inStock = product.inStock > 0;
  const avgRating =
    MOCK_REVIEWS.reduce((s, r) => s + r.rating, 0) / MOCK_REVIEWS.length;

  return (
    <div className="mx-auto max-w-6xl px-4 py-6 sm:px-6 lg:px-8">
      {/* Breadcrumb */}
      <div className="mb-4">
        <Breadcrumbs
          product={{ productId: product.id, productTitle: product.name }}
        />
      </div>

      {/* ── Main card ─────────────────────────────────────────────────────── */}
      <div className="overflow-hidden rounded-2xl bg-white shadow-lg">
        <div className="flex flex-col lg:flex-row">
          {/* Left – Carousel */}
          <div className="relative lg:w-1/2">
            {images.length > 0 ? (
              <div className="min-h-96 bg-gray-50">
                <ProductCarousel
                  product={{
                    title: product.name,
                    images,
                    discount:
                      product.discount > 0 ? product.discount : undefined,
                  }}
                />
              </div>
            ) : (
              <div className="flex min-h-96 items-center justify-center bg-gray-100">
                <span className="fa-solid fa-image text-5xl text-gray-300" />
              </div>
            )}
          </div>

          {/* Right – Info */}
          <div className="flex flex-col gap-5 p-6 lg:w-1/2 lg:p-8">
            {/* Badges */}
            <div className="flex flex-wrap gap-2">
              {product.isNew && (
                <span className="rounded-full bg-blue-100 px-3 py-1 text-xs font-semibold text-blue-700">
                  ✨ Mới
                </span>
              )}
              {!inStock && (
                <span className="rounded-full bg-gray-200 px-3 py-1 text-xs font-semibold text-gray-600">
                  Hết hàng
                </span>
              )}
            </div>

            {/* Name */}
            <h1 className="text-2xl leading-snug font-bold text-gray-900 sm:text-3xl">
              {product.name}
            </h1>

            {/* Rating summary row */}
            <div className="flex items-center gap-3 border-b border-gray-100 pb-4">
              <StarRating rating={avgRating} />
              <span className="text-sm font-medium text-amber-500">
                {avgRating.toFixed(1)}
              </span>
              <button
                className="cursor-pointer text-sm text-gray-400 hover:text-red-500 hover:underline"
                onClick={() => setActiveTab("reviews")}
                type="button"
              >
                {MOCK_REVIEWS.length} đánh giá
              </button>
              <span className="ml-auto text-sm text-gray-400">
                Còn lại:{" "}
                <span className="font-semibold text-gray-700">
                  {product.inStock}
                </span>
              </span>
            </div>

            {/* Price */}
            <div className="flex flex-wrap items-end gap-3">
              {product.discount > 0 ? (
                <>
                  <span className="text-3xl font-bold text-red-500">
                    {formatAsCurrency(discountedPrice)}
                  </span>
                  <span className="text-base text-gray-400 line-through">
                    {formatAsCurrency(product.price)}
                  </span>
                  <span className="rounded-full bg-red-100 px-2.5 py-0.5 text-sm font-bold text-red-600">
                    -{discountPct}%
                  </span>
                </>
              ) : (
                <span className="text-3xl font-bold text-gray-900">
                  {formatAsCurrency(product.price)}
                </span>
              )}
            </div>

            {/* Size selector */}
            {product.productSizes.length > 0 && (
              <div>
                <p className="mb-2 text-sm font-semibold text-gray-700">
                  Kích cỡ:{" "}
                  {selectedSizeIndex !== null && (
                    <span className="font-bold text-red-500">
                      {product.productSizes[selectedSizeIndex].name}
                    </span>
                  )}
                </p>
                <div className="flex flex-wrap gap-2">
                  {product.productSizes.map((size, idx) => (
                    <button
                      key={size.id}
                      type="button"
                      onClick={() => setSelectedSizeIndex(idx)}
                      className={`h-10 w-12 cursor-pointer rounded-lg border-2 text-sm font-semibold transition-all duration-200 ${
                        selectedSizeIndex === idx
                          ? "border-red-500 bg-red-500 text-white shadow-md shadow-red-200"
                          : "border-gray-200 text-gray-700 hover:border-red-400 hover:text-red-500"
                      }`}
                    >
                      {size.name}
                    </button>
                  ))}
                </div>
              </div>
            )}

            {/* Quantity */}
            <div>
              <p className="mb-2 text-sm font-semibold text-gray-700">
                Số lượng:
              </p>
              <div className="flex h-10 w-fit items-stretch overflow-hidden rounded-lg border border-gray-200">
                <button
                  type="button"
                  className="flex w-10 cursor-pointer items-center justify-center text-gray-500 transition-colors hover:bg-red-50 hover:text-red-500 disabled:cursor-not-allowed disabled:opacity-40"
                  onClick={() => setQuantity((q) => Math.max(1, q - 1))}
                  disabled={quantity <= 1}
                  aria-label="Giảm số lượng"
                >
                  <span className="fa-solid fa-minus text-xs" />
                </button>
                <span className="flex min-w-[2.5rem] items-center justify-center border-x border-gray-200 text-sm font-semibold text-gray-800">
                  {quantity}
                </span>
                <button
                  type="button"
                  className="flex w-10 cursor-pointer items-center justify-center text-gray-500 transition-colors hover:bg-red-50 hover:text-red-500 disabled:cursor-not-allowed disabled:opacity-40"
                  onClick={() =>
                    setQuantity((q) => Math.min(product.inStock, q + 1))
                  }
                  disabled={quantity >= product.inStock}
                  aria-label="Tăng số lượng"
                >
                  <span className="fa-solid fa-plus text-xs" />
                </button>
              </div>
            </div>

            {/* CTA */}
            <div className="flex flex-wrap gap-3 pt-2">
              <button
                type="button"
                disabled={
                  !inStock ||
                  (product.productSizes.length > 0 &&
                    selectedSizeIndex === null)
                }
                onClick={handleAddToCart}
                className={`flex flex-1 cursor-pointer items-center justify-center gap-2 rounded-md px-4 py-2 font-semibold duration-200 ${
                  !inStock ||
                  (product.productSizes.length > 0 &&
                    selectedSizeIndex === null)
                    ? "cursor-not-allowed bg-red-500 text-white opacity-50"
                    : "bg-red-500 text-white hover:bg-red-400 active:bg-red-600"
                }`}
              >
                {addedToCart ? (
                  <>
                    <span className="fa-solid fa-circle-check" />
                    Đã thêm vào giỏ!
                  </>
                ) : (
                  <>
                    <span className="fa-solid fa-cart-plus" />
                    Thêm vào giỏ hàng
                  </>
                )}
              </button>
              <Button preset="secondary" className="flex-1" disabled={!inStock}>
                <span className="fa-solid fa-bolt" />
                Mua ngay
              </Button>
            </div>

            {/* Trust badges */}
            <div className="grid grid-cols-3 gap-2 rounded-xl bg-gray-50 p-4">
              {[
                { icon: "fa-truck-fast", label: "Giao hàng\nnhanh" },
                { icon: "fa-shield-halved", label: "Bảo hành\n12 tháng" },
                { icon: "fa-rotate-left", label: "Đổi trả\n30 ngày" },
              ].map((b) => (
                <div
                  key={b.label}
                  className="flex flex-col items-center gap-1 text-center"
                >
                  <span className={`fa-solid ${b.icon} text-lg text-red-400`} />
                  <span className="text-xs whitespace-pre-line text-gray-500">
                    {b.label}
                  </span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* ── Tabs: Description / Instructions / Reviews ─────────────────── */}
        <div className="border-t border-gray-100">
          {/* Tab headers */}
          <div className="flex overflow-x-auto border-b border-gray-100">
            {(
              [
                {
                  key: "description",
                  label: "Mô tả sản phẩm",
                  icon: "fa-align-left",
                },
                {
                  key: "instructions",
                  label: "Hướng dẫn",
                  icon: "fa-circle-info",
                },
                {
                  key: "reviews",
                  label: `Đánh giá (${MOCK_REVIEWS.length})`,
                  icon: "fa-star",
                },
              ] as const
            ).map((tab) => (
              <button
                key={tab.key}
                type="button"
                onClick={() => setActiveTab(tab.key)}
                className={`flex shrink-0 cursor-pointer items-center gap-2 border-b-2 px-6 py-4 text-sm font-semibold whitespace-nowrap transition-colors duration-200 ${
                  activeTab === tab.key
                    ? "border-red-500 text-red-500"
                    : "border-transparent text-gray-500 hover:text-gray-800"
                }`}
              >
                <span className={`fa-solid ${tab.icon}`} />
                {tab.label}
              </button>
            ))}
          </div>

          {/* Tab content */}
          <div className="p-6 lg:p-8">
            {activeTab === "description" && (
              <div className="prose prose-sm max-w-none text-gray-600">
                {product.description ? (
                  <p className="leading-relaxed whitespace-pre-line">
                    {product.description}
                  </p>
                ) : (
                  <p className="text-gray-400 italic">
                    Chưa có mô tả sản phẩm.
                  </p>
                )}
              </div>
            )}

            {activeTab === "instructions" && (
              <div className="prose prose-sm max-w-none text-gray-600">
                {product.careInstructions ? (
                  <p className="leading-relaxed whitespace-pre-line">
                    {product.careInstructions}
                  </p>
                ) : (
                  <p className="text-gray-400 italic">
                    Chưa có hướng dẫn sử dụng.
                  </p>
                )}
              </div>
            )}

            {activeTab === "reviews" && (
              <div className="space-y-6">
                <RatingSummary reviews={MOCK_REVIEWS} />
                <div className="grid gap-4 sm:grid-cols-2">
                  {MOCK_REVIEWS.map((review) => (
                    <ReviewCard key={review.id} review={review} />
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default memo(ProductDetailPage);
