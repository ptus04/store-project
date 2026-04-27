import { memo, useEffect, useState } from "react";
import FeaturedCarousel from "@components/ui/FeaturedCarousel.tsx";
import ProductCard from "@components/ui/ProductCard.tsx";
import Loading from "@components/ui/Loading.tsx";

type Product = {
  id: string;
  name: string;
  price: number;
  discount: number;
  isNew: boolean;
  productImages: { file: string }[];
};

const HomePage = () => {
  const [newProducts, setNewProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch("/api/products?isNew=true")
      .then((res) => res.json())
      .then((data) => {
        setNewProducts(data);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, []);

  return (
    <div className="min-h-screen bg-gray-50">
      <FeaturedCarousel />

      <main className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
        <section className="mb-12">
          <div className="mb-8 flex items-center justify-between">
            <h2 className="text-3xl font-extrabold tracking-tight text-gray-900">
              Sản phẩm mới nhất
            </h2>
            <div className="mx-4 h-1 flex-1 rounded-full bg-red-100" />
          </div>

          {loading ? (
            <div className="flex h-64 items-center justify-center">
              <Loading isLoading={true} />
            </div>
          ) : (
            <div className="grid grid-cols-1 gap-x-6 gap-y-10 sm:grid-cols-2 lg:grid-cols-4 xl:gap-x-8">
              {newProducts.map((product) => (
                <ProductCard
                  key={product.id}
                  product={{
                    id: product.id,
                    title: product.name,
                    price: product.price,
                    discount: product.discount,
                    inStock: true,
                    images: [product.productImages?.[0]?.file || ""],
                  }}
                />
              ))}
            </div>
          )}
        </section>
      </main>
    </div>
  );
};

export default memo(HomePage);
