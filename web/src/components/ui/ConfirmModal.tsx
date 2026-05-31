import React from "react";
import Modal from "./Modal";

interface ConfirmModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  title: string;
  message: React.ReactNode;
  confirmText?: string;
  cancelText?: string;
  isProcessing?: boolean;
  error?: string;
}

export default function ConfirmModal({
  isOpen,
  onClose,
  onConfirm,
  title,
  message,
  confirmText = "Xác nhận",
  cancelText = "Hủy",
  isProcessing = false,
  error = "",
}: ConfirmModalProps) {
  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={
        <span className="flex items-center gap-2 text-red-500">
          <span className="material-symbols-outlined">warning</span>
          {title}
        </span>
      }
      maxWidth="max-w-sm"
    >
      <div className="space-y-6">
        <div className="text-primary">{message}</div>

        {error && (
          <div className="rounded border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {error}
          </div>
        )}

        <div className="border-outline-variant flex justify-end gap-4 border-t pt-4">
          <button
            type="button"
            onClick={onClose}
            className="text-secondary hover:bg-surface-container cursor-pointer px-4 py-2 font-bold uppercase transition-colors disabled:cursor-not-allowed disabled:opacity-50"
            disabled={isProcessing}
          >
            {cancelText}
          </button>
          <button
            type="button"
            onClick={onConfirm}
            className="cursor-pointer bg-red-600 px-6 py-2 font-bold text-white uppercase transition-all duration-200 hover:bg-red-700 active:scale-[0.98] disabled:cursor-not-allowed disabled:opacity-60"
            disabled={isProcessing}
          >
            {isProcessing ? "Đang xử lý..." : confirmText}
          </button>
        </div>
      </div>
    </Modal>
  );
}

