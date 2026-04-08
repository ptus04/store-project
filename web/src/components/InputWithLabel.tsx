import React, {
  type HTMLInputAutoCompleteAttribute,
  type HTMLInputTypeAttribute,
  memo,
} from "react";

type InputWithLabelProps = {
  id: string;
  label: string;
  type: HTMLInputTypeAttribute;
  value?: string;
  onChange?: (event: React.ChangeEvent<HTMLInputElement>) => void;
  className?: string;
  autoComplete?: HTMLInputAutoCompleteAttribute;
  autoFocus?: boolean;
  required?: boolean;
  disabled?: boolean;
  readOnly?: boolean;
  pattern?: string;
  error?: string;
};

const InputWithLabel = (props: InputWithLabelProps) => (
  <div className={`flex flex-col gap-2 ${props.className ?? ""}`}>
    <label className="font-semibold" htmlFor={props.id}>
      {props.label}
    </label>
    <input
      className="peer rounded-md border border-gray-300 px-4 py-2 hover:border-red-500 focus:outline-red-500"
      id={props.id}
      name={props.id}
      type={props.type}
      value={props.value}
      autoComplete={props.autoComplete}
      autoFocus={props.autoFocus}
      required={props.required}
      pattern={props.pattern}
      onChange={props.onChange}
      disabled={props.disabled}
      readOnly={props.readOnly}
    />
    <span className="hidden text-red-500 peer-invalid:block">
      {props.error}
    </span>
  </div>
);

export default memo(InputWithLabel);
