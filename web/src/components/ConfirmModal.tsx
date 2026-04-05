import { memo } from "react";
import Button from "./Button.tsx";

interface LogoutConfirmModalProps {
  title: string;
  content: string;
  open: boolean;
  cancelText?: string;
  confirmText?: string;
  onClose: () => void;
  onCancel?: () => void;
  onConfirm?: () => void;
}

const ConfirmModal = (props: LogoutConfirmModalProps) => {
  return (
    <>
      {props.open && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
          <div className="relative flex flex-col gap-4 rounded-md bg-white p-4 shadow-md md:min-w-lg">
            <button
              className="absolute top-4 right-4 aspect-square w-6 cursor-pointer text-gray-400 duration-200 hover:bg-red-500 hover:text-white"
              type="button"
              onClick={props.onClose}
              title="Đóng"
            >
              <i className="fa fa-close" />
            </button>

            <h3 className="border-b border-b-gray-300 pb-2 text-center text-xl font-bold">
              {props.title}
            </h3>

            <p className="text-center">{props.content}</p>

            <div className="flex justify-between gap-2">
              {props.onCancel && (
                <Button
                  type="button"
                  preset="secondary"
                  onClick={props.onCancel}
                >
                  {props.cancelText ?? "Hủy"}
                </Button>
              )}
              {props.onConfirm && (
                <Button
                  type="button"
                  preset="primary"
                  onClick={props.onConfirm}
                >
                  {props.confirmText ?? "Ok"}
                </Button>
              )}
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default memo(ConfirmModal);
