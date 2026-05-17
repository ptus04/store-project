export default function Header() {
  return (
    <header className="px-gutter border-outline-variant dark:border-outline bg-surface dark:bg-surface-dim sticky top-0 z-40 flex h-16 items-center justify-between border-b">
      <div className="flex items-center gap-4">
        <h1 className="text-headline-md font-headline-md text-primary dark:text-on-primary-fixed hidden font-black md:block">
          SLY Dashboard
        </h1>

        {/* Mobile Menu Button */}
        <button type="button" className="text-primary p-2 md:hidden">
          <span className="material-symbols-outlined" data-icon="menu">
            menu
          </span>
        </button>

        <h1 className="text-body-lg font-body-lg text-primary font-bold md:hidden">
          SLY
        </h1>
      </div>

      <div className="flex items-center gap-6">
        {/* Search on Right */}
        <div className="border-primary group hidden items-center border-b pb-1 transition-all focus-within:border-b-2 sm:flex">
          <span
            className="material-symbols-outlined text-secondary mr-2 text-[20px]"
            data-icon="search"
          >
            search
          </span>

          <input
            className="text-body-md font-body-md text-primary placeholder-secondary w-48 border-none bg-transparent p-0 outline-none focus:ring-0"
            placeholder="Search..."
            type="text"
          />
        </div>

        {/* Trailing Icons */}
        <div className="flex items-center gap-4">
          <button
            type="button"
            className="text-secondary hover:text-primary transition-colors"
          >
            <span
              className="material-symbols-outlined"
              data-icon="notifications"
            >
              notifications
            </span>
          </button>

          <button
            type="button"
            className="text-secondary hover:text-primary transition-colors"
          >
            <span
              className="material-symbols-outlined"
              data-icon="help_outline"
            >
              help_outline
            </span>
          </button>
        </div>
      </div>
    </header>
  );
}
