import React from 'react';

interface ButtonProps {
  onClick: () => void;
  text: string;
}

const ButtonComponent: React.FC<ButtonProps> = ({ onClick, text }) => {
  return (
    <button
      className="w-full bg-blue-500 text-white py-2 px-4 rounded font-semibold mt-2 hover:bg-blue-600 text-lg"
      onClick={onClick}
    >
      {text}
    </button>
  );
};

export default ButtonComponent;
