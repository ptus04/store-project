const formatter = new Intl.NumberFormat("vi-VN", {
  style: "currency",
  currency: "VND",
});
const options: Intl.DateTimeFormatOptions = {
  year: "numeric",
  month: "2-digit",
  day: "2-digit",
};

export const formatAsCurrency = (value: number) => formatter.format(value);

export const formatAsDate = (date: string | Date) =>
  new Date(date).toLocaleDateString("vi-VN", options);
