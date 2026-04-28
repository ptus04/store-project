import { memo } from "react";
import { Link } from "react-router-dom";
import { CarouselItem } from "@/types";

type FeaturedCarouselItemProps = {
  item: CarouselItem;
  isActive: boolean;
};

const FeaturedCarouselItem = (props: FeaturedCarouselItemProps) => (
  <Link
    className={`absolute h-full w-full cursor-pointer ${props.isActive ? "animate-(--animate-fade-in)" : "invisible animate-(--animate-fade-out)"}`}
    to={props.item.link}
  >
    <img
      className="h-full w-full object-cover"
      src={`/img/carousel/${props.item.image}`}
      alt={props.item.title}
      loading="lazy"
    />

    {props.item.title && (
      <div className="absolute right-0 bottom-0 left-0 bg-linear-to-t from-black to-75% pt-9 pr-4 pb-6 pl-4 text-white">
        <h3 className="text-2xl font-bold">{props.item.title}</h3>
        <p>{props.item.content}</p>
      </div>
    )}
  </Link>
);

export default memo(FeaturedCarouselItem);
