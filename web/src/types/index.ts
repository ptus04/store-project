export interface ProductImage {
  file: string;
}

export interface Product {
  id: string;
  name: string;
  price: number;
  discount: number;
  inStock: number;
  productImages: ProductImage[];
}

export interface CarouselItem {
  id: string;
  title: string;
  content: string;
  image: string;
  link: string;
}

export interface UserProfile {
  id: string;
  name: string;
  phone: string;
  email: string | null;
  role: "ADMIN" | "STAFF" | "CUSTOMER";
  gender: "MALE" | "FEMALE" | null;
  birthDate: string | null;
  avatar: string | null;
}
