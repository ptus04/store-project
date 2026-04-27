import { memo, useCallback, useEffect, useState } from "react";
import FeaturedCarouselIndicator from "./CarouselIndicator.tsx";
import FeaturedCarouselItem from "./FeaturedCarouselItem.tsx";

const caches = {} as Record<string, Record<string, string>[]>;

const getOrientation = () => screen.orientation.type.split("-")[0];

const FeaturedCarousel = () => {
  const [items, setItems] = useState<Record<string, string>[]>([]);
  const [active, setActive] = useState(0);
  const [reset, setReset] = useState(false);

  const handlePrev = () => {
    setActive((active) => (active - 1 + items.length) % items.length);
    setReset(!reset);
  };

  const handleNext = () => {
    setActive((active) => (active + 1) % items.length);
    setReset(!reset);
  };

  const handleChangeItem = (i: number) => {
    setActive(i);
    setReset(!reset);
  };

  const handleOrientationChanged = useCallback(async () => {
    const orientation = getOrientation();
    if (caches[orientation]) {
      setItems(caches[orientation]);
      return;
    }

    const res = await fetch(
      `/api/products/carousel?orientation=${orientation}`,
    );
    const data = await res.json();

    interface CarouselItem {
      id: string;
      title: string;
      landscapeImage: string;
      portraitImage?: string;
      link: string;
      content: string;
    }

    const mappedData = data.map((item: CarouselItem) => ({
      ...item,
      image:
        orientation === "landscape"
          ? item.landscapeImage
          : item.portraitImage || item.landscapeImage,
    }));

    caches[orientation] = mappedData;
    setItems(mappedData);
  }, []);

  useEffect(() => {
    screen.orientation.addEventListener("change", handleOrientationChanged);
    screen.orientation.dispatchEvent(new Event("change"));

    return () =>
      screen.orientation.removeEventListener(
        "change",
        handleOrientationChanged,
      );
  }, [handleOrientationChanged]);

  useEffect(() => {
    const interval = setInterval(
      () => setActive((active) => (active + 1) % items.length),
      5000,
    );

    return () => clearInterval(interval);
  }, [items, reset]);

  return (
    <div className="relative h-dvh">
      <div className="h-dvh">
        {items?.map((item, i) => (
          <FeaturedCarouselItem
            key={item.id}
            item={item}
            isActive={active === i}
          />
        ))}
      </div>

      <div className="absolute bottom-4 left-4 flex gap-1">
        {items?.map((item, i) => (
          <FeaturedCarouselIndicator
            key={item.id}
            title={item.title}
            isActive={active === i}
            onClick={() => handleChangeItem(i)}
          />
        ))}
      </div>

      <div className="absolute right-4 bottom-4 flex">
        <button
          className="cursor-pointer items-center rounded-tl-2xl rounded-bl-2xl bg-black/50 px-4 py-2"
          type="button"
          onClick={handlePrev}
          title="Quay lại"
        >
          <span className="fa fa-chevron-left text-white"></span>
        </button>
        <button
          className="cursor-pointer items-center rounded-tr-2xl rounded-br-2xl bg-black/50 px-4 py-2"
          type="button"
          onClick={handleNext}
          title="Kế tiếp"
        >
          <span className="fa fa-chevron-right text-white"></span>
        </button>
      </div>
    </div>
  );
};

export default memo(FeaturedCarousel);
