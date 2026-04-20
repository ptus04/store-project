import * as React from "react";

const Loading = ({ isLoading }: { isLoading: boolean }) => {
  return (
    <>
      {isLoading && (
        <div className="flex h-96 items-center justify-center gap-2 text-2xl text-gray-500">
          <i className="fa fa-spinner fa-spin"></i>
          <span>Đang tải...</span>
        </div>
      )}
    </>
  );
};

export default React.memo(Loading);
