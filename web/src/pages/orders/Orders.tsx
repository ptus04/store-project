import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import {
  orderApi,
  type OrderResponse,
  type OrderStatusEnum,
} from "@/api/orderApi";

const PAGE_SIZE = 10;

const STATUS_OPTIONS: OrderStatusEnum[] = [
  "UNPAID",
  "PAID",
  "PACKAGING",
  "SHIPPING",
  "COMPLETED",
  "CANCELLED",
  "REFUNDED",
];

const STATUS_LABELS: Record<OrderStatusEnum, string> = {
  UNPAID: "Chờ thanh toán",
  PAID: "Đã thanh toán",
  PACKAGING: "Đang đóng gói",
  SHIPPING: "Đang giao",
  COMPLETED: "Hoàn tất",
  CANCELLED: "Đã hủy",
  REFUNDED: "Đã hoàn tiền",
};

const NEXT_STATUS: Record<OrderStatusEnum, OrderStatusEnum[]> = {
  UNPAID: ["PAID", "CANCELLED"],
  PAID: ["PACKAGING", "REFUNDED"],
  PACKAGING: ["SHIPPING", "REFUNDED"],
  SHIPPING: ["COMPLETED"],
  COMPLETED: ["REFUNDED"],
  CANCELLED: [],
  REFUNDED: [],
};

function formatCurrency(value: number): string {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
    maximumFractionDigits: 0,
  }).format(value);
}

function formatDateTime(value: string): string {
  return new Intl.DateTimeFormat("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(new Date(value));
}

function getStatusClass(status: OrderStatusEnum): string {
  if (status === "COMPLETED") return "bg-green-50 text-green-700";
  if (status === "CANCELLED" || status === "REFUNDED")
    return "bg-red-50 text-red-700";
  if (status === "SHIPPING") return "bg-blue-50 text-blue-700";
  return "bg-yellow-50 text-yellow-700";
}

function StatusBadge({ status }: Readonly<{ status: OrderStatusEnum }>) {
  return (
    <span
      className={`inline-flex px-3 py-1 text-xs font-bold tracking-wide ${getStatusClass(status)}`}
    >
      {STATUS_LABELS[status]}
    </span>
  );
}

function getOrderItemLabel(order: OrderResponse): string {
  if (order.orderDetails.length === 0) return "Không có sản phẩm";
  const firstProduct = order.orderDetails[0]?.product?.name ?? "Sản phẩm";
  const remaining = order.orderDetails.length - 1;
  return remaining > 0 ? `${firstProduct} +${remaining}` : firstProduct;
}

export default function Orders() {
  const [orders, setOrders] = useState<OrderResponse[]>([]);
  const [selectedOrder, setSelectedOrder] = useState<OrderResponse | null>(
    null,
  );
  const [totalElements, setTotalElements] = useState<number | null>(null);
  const [totalPages, setTotalPages] = useState(1);
  const [currentPage, setCurrentPage] = useState(0);
  const [statusFilter, setStatusFilter] = useState<OrderStatusEnum | null>(
    null,
  );
  const [searchInput, setSearchInput] = useState("");
  const [searchDebounced, setSearchDebounced] = useState("");
  const [loading, setLoading] = useState(false);
  const [detailLoading, setDetailLoading] = useState(false);
  const [updatingStatus, setUpdatingStatus] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  useEffect(() => {
    if (debounceRef.current) clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => {
      setSearchDebounced(searchInput.trim());
      setCurrentPage(0);
    }, 400);

    return () => {
      if (debounceRef.current) clearTimeout(debounceRef.current);
    };
  }, [searchInput]);

  const fetchOrders = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await orderApi.getList({
        page: currentPage,
        size: PAGE_SIZE,
        status: statusFilter,
        search: searchDebounced || undefined,
      });
      setOrders(result.content ?? []);
      setTotalElements(result.page?.totalElements ?? 0);
      setTotalPages(result.page?.totalPages || 1);
    } catch (err) {
      const message =
        err instanceof Error ? err.message : "Không thể tải danh sách đơn hàng";
      setError(message);
    } finally {
      setLoading(false);
    }
  }, [currentPage, searchDebounced, statusFilter]);

  useEffect(() => {
    fetchOrders();
  }, [fetchOrders]);

  const availableNextStatuses = useMemo(() => {
    if (!selectedOrder) return [];
    return NEXT_STATUS[selectedOrder.status] ?? [];
  }, [selectedOrder]);

  async function handleSelectOrder(order: OrderResponse) {
    setSelectedOrder(order);
    setDetailLoading(true);
    try {
      const detail = await orderApi.getById(order.id);
      setSelectedOrder(detail);
    } catch (err) {
      const message =
        err instanceof Error ? err.message : "Không thể tải chi tiết đơn hàng";
      globalThis.alert(message);
    } finally {
      setDetailLoading(false);
    }
  }

  async function handleUpdateStatus(nextStatus: OrderStatusEnum) {
    if (!selectedOrder || updatingStatus) return;
    const confirmed = globalThis.confirm(
      `Chuyển đơn ${selectedOrder.orderCode} sang "${STATUS_LABELS[nextStatus]}"?`,
    );
    if (!confirmed) return;

    setUpdatingStatus(true);
    try {
      const updated = await orderApi.updateStatus(selectedOrder.id, nextStatus);
      setSelectedOrder(updated);
      setOrders((prev) =>
        prev.map((order) => (order.id === updated.id ? updated : order)),
      );
    } catch (err) {
      const message =
        err instanceof Error ? err.message : "Không thể cập nhật trạng thái";
      globalThis.alert(message);
    } finally {
      setUpdatingStatus(false);
    }
  }

  function handleStatusFilter(value: string) {
    setStatusFilter(value ? (value as OrderStatusEnum) : null);
    setCurrentPage(0);
  }

  const totalLabel =
    totalElements === null
      ? "Đang tải..."
      : `${totalElements.toLocaleString("vi-VN")} đơn hàng`;

  return (
    <main className="bg-background p-gutter flex-1 overflow-y-auto">
      <div className="max-w-container-max mx-auto space-y-6 pb-12">
        <div className="border-outline-variant flex flex-col justify-between gap-4 border-b pb-4 lg:flex-row lg:items-end">
          <div>
            <h2 className="text-headline-md font-headline-md text-primary">
              Đơn hàng
            </h2>
            <p className="text-secondary mt-1 text-sm">
              Quản lý danh sách, chi tiết và trạng thái đơn hàng
            </p>
          </div>

          <div className="flex flex-col gap-3 sm:flex-row">
            <label className="min-w-64">
              <span className="text-secondary mb-1 block text-xs font-bold tracking-wide uppercase">
                Tìm kiếm
              </span>
              <input
                value={searchInput}
                onChange={(event) => setSearchInput(event.target.value)}
                className="border-outline-variant bg-surface-container-lowest focus:border-primary h-10 w-full border px-3 text-sm outline-none"
                placeholder="Mã đơn, khách hàng, SĐT, sản phẩm"
                type="search"
              />
            </label>

            <label className="min-w-48">
              <span className="text-secondary mb-1 block text-xs font-bold tracking-wide uppercase">
                Trạng thái
              </span>
              <select
                value={statusFilter ?? ""}
                onChange={(event) => handleStatusFilter(event.target.value)}
                className="border-outline-variant bg-surface-container-lowest focus:border-primary h-10 w-full border px-3 text-sm outline-none"
              >
                <option value="">Tất cả</option>
                {STATUS_OPTIONS.map((status) => (
                  <option key={status} value={status}>
                    {STATUS_LABELS[status]}
                  </option>
                ))}
              </select>
            </label>
          </div>
        </div>

        {error && (
          <div className="border-error-container bg-error-container text-on-error-container border px-4 py-3 text-sm">
            {error}
          </div>
        )}

        <div className="grid gap-6 xl:grid-cols-[minmax(0,1fr)_24rem]">
          <section className="border-outline-variant bg-surface-container-lowest border">
            <div className="border-outline-variant flex items-center justify-between border-b px-4 py-3">
              <p className="text-primary text-sm font-semibold">{totalLabel}</p>
              {loading && (
                <span className="material-symbols-outlined text-outline animate-spin text-[20px]">
                  progress_activity
                </span>
              )}
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm">
                <thead className="border-outline-variant bg-surface-container text-secondary border-b text-xs tracking-wide uppercase">
                  <tr>
                    <th className="px-4 py-3">Mã đơn</th>
                    <th className="px-4 py-3">Khách hàng</th>
                    <th className="px-4 py-3">Sản phẩm</th>
                    <th className="px-4 py-3">Ngày đặt</th>
                    <th className="px-4 py-3">Trạng thái</th>
                    <th className="px-4 py-3 text-right">Tổng tiền</th>
                    <th className="px-4 py-3 text-right">Thao tác</th>
                  </tr>
                </thead>
                <tbody className="divide-outline-variant divide-y">
                  {orders.length === 0 && !loading ? (
                    <tr>
                      <td
                        colSpan={7}
                        className="text-secondary px-4 py-16 text-center"
                      >
                        Không tìm thấy đơn hàng nào
                      </td>
                    </tr>
                  ) : (
                    orders.map((order) => (
                      <tr
                        key={order.id}
                        className="hover:bg-surface-container-low transition-colors"
                      >
                        <td className="text-primary px-4 py-4 font-bold">
                          {order.orderCode}
                        </td>
                        <td className="px-4 py-4">
                          <p className="text-primary font-semibold">
                            {order.user.name}
                          </p>
                          <p className="text-secondary text-xs">
                            {order.user.phone}
                          </p>
                        </td>
                        <td className="text-secondary max-w-56 px-4 py-4">
                          <p className="truncate">{getOrderItemLabel(order)}</p>
                        </td>
                        <td className="text-secondary px-4 py-4">
                          {formatDateTime(order.orderDate)}
                        </td>
                        <td className="px-4 py-4">
                          <StatusBadge status={order.status} />
                        </td>
                        <td className="text-primary px-4 py-4 text-right font-bold">
                          {formatCurrency(order.total)}
                        </td>
                        <td className="px-4 py-4 text-right">
                          <button
                            type="button"
                            onClick={() => handleSelectOrder(order)}
                            className="border-outline-variant hover:border-primary inline-flex h-9 items-center gap-2 border px-3 text-xs font-bold tracking-wide uppercase transition-colors"
                          >
                            <span className="material-symbols-outlined text-[18px]">
                              visibility
                            </span>
                            Xem
                          </button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            <div className="border-outline-variant flex items-center justify-between border-t px-4 py-3">
              <button
                type="button"
                disabled={currentPage <= 0 || loading}
                onClick={() => setCurrentPage((page) => Math.max(page - 1, 0))}
                className="border-outline-variant disabled:text-outline flex h-9 items-center gap-2 border px-3 text-sm font-semibold disabled:cursor-not-allowed disabled:opacity-60"
              >
                <span className="material-symbols-outlined text-[18px]">
                  chevron_left
                </span>
                Trước
              </button>
              <span className="text-secondary text-sm">
                Trang {currentPage + 1} / {totalPages}
              </span>
              <button
                type="button"
                disabled={currentPage >= totalPages - 1 || loading}
                onClick={() =>
                  setCurrentPage((page) => Math.min(page + 1, totalPages - 1))
                }
                className="border-outline-variant disabled:text-outline flex h-9 items-center gap-2 border px-3 text-sm font-semibold disabled:cursor-not-allowed disabled:opacity-60"
              >
                Sau
                <span className="material-symbols-outlined text-[18px]">
                  chevron_right
                </span>
              </button>
            </div>
          </section>

          <aside className="border-outline-variant bg-surface-container-lowest h-fit border">
            {!selectedOrder ? (
              <div className="text-secondary p-6 text-center">
                <span className="material-symbols-outlined text-outline mb-3 block text-4xl">
                  receipt_long
                </span>
                Chọn một đơn hàng để xem chi tiết
              </div>
            ) : (
              <div>
                <div className="border-outline-variant flex items-start justify-between gap-3 border-b p-4">
                  <div>
                    <p className="text-secondary text-xs font-bold tracking-wide uppercase">
                      Chi tiết đơn
                    </p>
                    <h3 className="text-primary mt-1 font-bold">
                      {selectedOrder.orderCode}
                    </h3>
                  </div>
                  <StatusBadge status={selectedOrder.status} />
                </div>

                {detailLoading ? (
                  <div className="text-secondary flex items-center justify-center gap-2 p-8">
                    <span className="material-symbols-outlined animate-spin">
                      progress_activity
                    </span>
                    Đang tải
                  </div>
                ) : (
                  <div className="space-y-5 p-4">
                    <div>
                      <p className="text-secondary mb-2 text-xs font-bold tracking-wide uppercase">
                        Khách hàng
                      </p>
                      <p className="text-primary font-semibold">
                        {selectedOrder.user.name}
                      </p>
                      <p className="text-secondary text-sm">
                        {selectedOrder.user.phone}
                      </p>
                      <p className="text-secondary text-sm">
                        {selectedOrder.user.email ?? "Chưa có email"}
                      </p>
                    </div>

                    <div>
                      <p className="text-secondary mb-2 text-xs font-bold tracking-wide uppercase">
                        Giao hàng
                      </p>
                      <p className="text-primary text-sm">
                        {selectedOrder.orderShippingAddress.name} -{" "}
                        {selectedOrder.orderShippingAddress.phone}
                      </p>
                      <p className="text-secondary text-sm">
                        {selectedOrder.orderShippingAddress.address},{" "}
                        {selectedOrder.orderShippingAddress.ward},{" "}
                        {selectedOrder.orderShippingAddress.district},{" "}
                        {selectedOrder.orderShippingAddress.city}
                      </p>
                    </div>

                    <div>
                      <p className="text-secondary mb-2 text-xs font-bold tracking-wide uppercase">
                        Sản phẩm
                      </p>
                      <div className="divide-outline-variant border-outline-variant divide-y border-y">
                        {selectedOrder.orderDetails.map((detail) => (
                          <div key={detail.id} className="py-3">
                            <p className="text-primary font-semibold">
                              {detail.product?.name ?? "Sản phẩm"}
                            </p>
                            <div className="text-secondary mt-1 flex justify-between gap-3 text-sm">
                              <span>
                                Size {detail.productSize ?? "—"} · x
                                {detail.quantity}
                              </span>
                              <span className="text-primary font-bold">
                                {formatCurrency(detail.subtotal)}
                              </span>
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>

                    {selectedOrder.note && (
                      <div>
                        <p className="text-secondary mb-2 text-xs font-bold tracking-wide uppercase">
                          Ghi chú
                        </p>
                        <p className="text-primary text-sm whitespace-pre-line">
                          {selectedOrder.note}
                        </p>
                      </div>
                    )}

                    {selectedOrder.cancellationReason && (
                      <div className="border-error-container bg-error-container text-on-error-container border p-3 text-sm">
                        <p className="mb-1 font-bold">Lý do hủy/hoàn tiền</p>
                        <p>{selectedOrder.cancellationReason}</p>
                      </div>
                    )}

                    <div className="border-outline-variant border-t pt-4">
                      <div className="mb-4 flex justify-between text-lg font-bold">
                        <span>Tổng tiền</span>
                        <span>{formatCurrency(selectedOrder.total)}</span>
                      </div>

                      <p className="text-secondary mb-2 text-xs font-bold tracking-wide uppercase">
                        Cập nhật trạng thái
                      </p>
                      {availableNextStatuses.length === 0 ? (
                        <p className="text-secondary text-sm">
                          Không có thao tác tiếp theo cho trạng thái này.
                        </p>
                      ) : (
                        <div className="flex flex-wrap gap-2">
                          {availableNextStatuses.map((status) => (
                            <button
                              key={status}
                              type="button"
                              disabled={updatingStatus}
                              onClick={() => handleUpdateStatus(status)}
                              className="bg-primary text-on-primary disabled:bg-outline flex h-10 items-center gap-2 px-3 text-sm font-semibold transition-colors disabled:cursor-wait"
                            >
                              {updatingStatus && (
                                <span className="material-symbols-outlined animate-spin text-[18px]">
                                  progress_activity
                                </span>
                              )}
                              {STATUS_LABELS[status]}
                            </button>
                          ))}
                        </div>
                      )}
                    </div>
                  </div>
                )}
              </div>
            )}
          </aside>
        </div>
      </div>
    </main>
  );
}
