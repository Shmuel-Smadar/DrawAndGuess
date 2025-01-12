import React from 'react';
import './WordSelection.css';

const WordSelection = ({ words, onWordSelect }) => {
  return (
    <div className="word-selection-overlay">
      <div className="word-selection-container">
        <h2 className="word-selection-title">Choose a Word to Draw</h2>
        <div className="word-selection-buttons">
          {words.map((word, index) => (
            <button
              key={index}
              className="word-button"
              onClick={() => onWordSelect(word)}
            >
              {word}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
};

export default WordSelection;
