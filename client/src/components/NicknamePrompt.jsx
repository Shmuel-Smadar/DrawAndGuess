import React, { useState } from 'react';
import './NicknamePrompt.css';

const NicknamePrompt = ({ setUsername }) => {
  const [nicknameInput, setNicknameInput] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (nicknameInput.trim() !== '') {
      setUsername(nicknameInput.trim());
    }
  };

  return (
    <div className="nickname-prompt">
      <form onSubmit={handleSubmit}>
        <h2>Enter Your Nickname</h2>
        <input
          type="text"
          value={nicknameInput}
          onChange={(e) => setNicknameInput(e.target.value)}
          placeholder="Nickname"
          maxLength={20}
        />
        <button type="submit">Join</button>
      </form>
    </div>
  );
};

export default NicknamePrompt;
