import * as React from "react";

type SelectWithLabelProps = {
  id: string;
  label: string;
  value: string;
  onChange?: (event: React.ChangeEvent<HTMLSelectElement>) => void;
  className?: string;
  autoFocus?: boolean;
  required?: boolean;
  disabled?: boolean;
  error?: string;
  children?: React.ReactNode;
};

const SelectWithLabel = (props: SelectWithLabelProps) => (
  <div className={`flex flex-col gap-2 ${props.className ?? ""}`}>
    <label className="font-semibold" htmlFor={props.id}>
      {props.label}
    </label>
    <select
      className="peer w-full rounded-md border-1 border-gray-300 px-4 py-2 hover:border-red-500 focus:outline-red-500"
      id={props.id}
      name={props.id}
      value={props.value}
      autoFocus={props.autoFocus}
      required={props.required}
      onChange={props.onChange}
      disabled={props.disabled}
    >
      {props.children}
    </select>
    <span className="hidden text-red-500 peer-invalid:block">
      {props.error}
    </span>
  </div>
);

export default React.memo(SelectWithLabel);
