import { memo } from "react";

type RadioSelectorProps = {
  groupName: string;
  options: string[];
  className?: string;
  onChange?: (selected: string, selectedIndex: number) => void;
  selectedIndex?: number;
  required?: boolean;
  disabled?: boolean;
};

const RadioSelector = (props: RadioSelectorProps) => (
  <div className={`group inline-flex flex-wrap gap-4 ${props.className ?? ""}`}>
    {props.options.map((option, index) => (
      <div key={option} className="flex items-center gap-1">
        <input
          className="group accent-red-500 focus:outline-red-500 nth-[n+2]:ms-4"
          type="radio"
          id={props.groupName + index}
          name={props.groupName}
          required={props.required}
          checked={index === props.selectedIndex}
          onChange={() => props.onChange?.(option, index)}
          disabled={index !== props.selectedIndex ? props.disabled : false}
        />
        <label htmlFor={props.groupName + index}>{option}</label>
      </div>
    ))}
  </div>
);
export default memo(RadioSelector);
