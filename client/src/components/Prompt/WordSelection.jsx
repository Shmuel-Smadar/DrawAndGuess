import React from 'react';
import { WORD_SELECTION_TITLE } from '../../utils/constants'
import './WordSelection.css';

//Component that lets the user choose one of 3 random words to draw
const WordSelection = ({ words, onWordSelect }) => {
  return (
    <div className="word-selection-overlay">
      <div className="word-selection-container">
        <h2 className="word-selection-title">{WORD_SELECTION_TITLE}</h2>
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
