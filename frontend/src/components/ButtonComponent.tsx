import React from 'react';

interface ButtonProps {
  onClick: () => void;
  text: string;
  httpMethod: string; // New prop for HTTP method
}

const ButtonComponent: React.FC<ButtonProps> = ({ onClick, text, httpMethod }) => {
  return (
    <div>
       <button
        className="flex items-center justify-center w-full bg-blue-500 text-white py-2 px-4 
          rounded font-semibold mt-2 hover:bg-blue-600 text-lg"
        onClick={onClick}
      >
        <div>{httpMethod}</div> {/* Render HTTP method on the left */}
        <div className="flex-grow text-center">{text}</div> {/* Render text in the middle */}
      </button>
    </div>
  );
};
export default ButtonComponent;
