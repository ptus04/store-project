import FeaturedCarousel from "@components/ui/FeaturedCarousel";
import ProductCard from "@components/ui/ProductCard";
import Loading from "@components/ui/Loading";

const HomePage = () => {
  return (
    <div className="container mx-auto p-4">
      <FeaturedCarousel />
      <div className="mt-8 grid grid-cols-1 gap-4 md:grid-cols-3 lg:grid-cols-4">
        {/* Example usage of ProductCard */}
        <ProductCard product={null as any} />
      </div>
      <Loading isLoading={false} />
    </div>
  );
};

export default HomePage;
