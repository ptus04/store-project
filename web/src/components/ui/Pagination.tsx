import { memo } from "react";

type PaginationProps = {
  pages: number[];
  currentPage: number;
  className?: string;
  handlePageChange: (page: number) => void;
};

const Pagination = ({
  pages,
  currentPage,
  className,
  handlePageChange,
}: PaginationProps) => {
  return (
    <div className={`my-4 flex justify-center gap-2 ${className}`}>
      <button
        className="cursor-pointer rounded-md border border-gray-300 px-3 py-1 disabled:opacity-50"
        type="button"
        onClick={() => handlePageChange(currentPage - 1)}
        disabled={currentPage === 1}
      >
        &lt;
      </button>
      {pages.map((page) =>
        page === -1 ? (
          <span key={page} className="px-2">
            ...
          </span>
        ) : (
          <button
            key={page}
            className={`cursor-pointer rounded-md border border-gray-300 px-3 py-1 ${currentPage === page ? "bg-red-500 text-white" : ""}`}
            type="button"
            onClick={() => handlePageChange(page)}
            disabled={page === currentPage}
          >
            {page}
          </button>
        ),
      )}
      <button
        className="cursor-pointer rounded-md border border-gray-300 px-3 py-1 disabled:opacity-50"
        type="button"
        onClick={() => handlePageChange(currentPage + 1)}
        disabled={currentPage === pages.length}
      >
        &gt;
      </button>
    </div>
  );
};

export default memo(Pagination) as typeof Pagination;
