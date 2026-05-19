import React from "react";

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: React.ReactNode;
  children: React.ReactNode;
  maxWidth?: string; // e.g. "max-w-md", "max-w-sm"
}

export default function Modal({
  isOpen,
  onClose,
  title,
  children,
  maxWidth = "max-w-md",
}: ModalProps) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 px-4 backdrop-blur-sm transition-all">
      <div
        className={`bg-surface-container-lowest border-outline-variant w-full ${maxWidth} animate-in fade-in zoom-in-95 border p-6 shadow-xl duration-200`}
      >
        <div className="border-outline-variant mb-6 flex items-center justify-between border-b pb-4">
          <h3 className="text-primary text-xl font-bold">{title}</h3>
          <button
            onClick={onClose}
            className="text-secondary hover:text-primary cursor-pointer transition-colors"
          >
            <span className="material-symbols-outlined">close</span>
          </button>
        </div>

        {children}
      </div>
    </div>
  );
}
