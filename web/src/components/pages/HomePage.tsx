import { useEffect, useState } from "react";
import FeaturedCarousel from "@components/ui/FeaturedCarousel";
import ProductCard from "@components/ui/ProductCard";
import Loading from "@components/ui/Loading";
import { Product } from "@/types";

const HomePage = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const res = await fetch("/api/products?isNew=true");
        if (!res.ok) throw new Error("Failed to fetch products");
        const data = await res.json();
        setProducts(data);
      } catch (error) {
        console.error("Error fetching products:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchProducts();
  }, []);

  return (
    <div className="container mx-auto px-3 pt-5">
      <FeaturedCarousel />
      <Loading isLoading={isLoading} />
      {!isLoading && (
        <div className="mt-8 flex flex-wrap justify-center gap-4">
          {products.map((product) => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      )}
    </div>
  );
};

export default HomePage;
