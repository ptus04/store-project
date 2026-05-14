import { memo } from "react";

type FeaturedCarouselIndicatorProps = {
  title: string;
  isActive: boolean;
  onClick: () => void;
};

const FeaturedCarouselIndicator = ({
  title,
  isActive,
  onClick,
}: FeaturedCarouselIndicatorProps) => (
  <button
    className={`h-1 w-8 cursor-pointer rounded-full ${isActive ? "bg-white" : "bg-gray-500"}`}
    type="button"
    title={title}
    onClick={onClick}
  />
);

export default memo(FeaturedCarouselIndicator);
