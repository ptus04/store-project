import { memo, useState } from "react";

type ProductCarouselProps = {
  product: {
    title: string;
    images: string[];
    discount?: number;
  };
};

const ProductCarousel = ({ product }: ProductCarouselProps) => {
  const [previewIndex, setPreviewIndex] = useState(0);

  const handlePrevImage = () =>
    setPreviewIndex(
      (prev) => (prev - 1 + product.images.length) % product.images.length,
    );
  const handleNextImage = () =>
    setPreviewIndex((prev) => (prev + 1) % product.images.length);

  return (
    <div className="relative grow basis-4/10">
      <div className="relative mx-auto min-h-96">
        {product?.images.map((image, index) => (
          <img
            key={image}
            src={`/img/${image}`}
            className={`absolute left-1/2 h-full w-full -translate-x-1/2 object-contain ${index === previewIndex ? "animate-(--animate-fade-in)" : "invisible animate-(--animate-fade-out)"}`}
            alt={product?.title}
          />
        ))}
      </div>

      <button
        className="absolute top-1/2 left-1/10 cursor-pointer invert-50 hover:invert-0 lg:left-1/4"
        type="button"
        title="Trở về"
        onClick={handlePrevImage}
      >
        <span className="fa fa-chevron-left text-3xl"></span>
      </button>
      <button
        className="absolute top-1/2 right-1/10 cursor-pointer invert-50 hover:invert-0 lg:right-1/4"
        type="button"
        title="Kế tiếp"
        onClick={handleNextImage}
      >
        <span className="fa fa-chevron-right text-3xl"></span>
      </button>

      {product?.discount && (
        <p className="absolute top-1/10 left-1/10 rounded-full bg-red-500 px-3 py-1 text-white lg:left-1/4">
          {(product?.discount ?? 0) * 100}%
        </p>
      )}

      <div className="absolute bottom-0 left-1/2 hidden w-full -translate-x-1/2 justify-center gap-2 lg:flex">
        {product?.images.map((image, index) => (
          <button
            key={image}
            className={`cursor-pointer border-gray-300 bg-white duration-300 md:max-w-[10%] ${index === previewIndex ? "-translate-y-2 border" : "opacity-50"}`}
            onClick={() => setPreviewIndex(index)}
            type="button"
            aria-current={index === previewIndex ? "true" : "false"}
            aria-label={`Slide ${index + 1}`}
          >
            <img key={image} src={`/img/${image}`} alt={product?.title} />
          </button>
        ))}
      </div>
    </div>
  );
};

export default memo(ProductCarousel);
