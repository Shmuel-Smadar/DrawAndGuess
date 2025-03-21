import React from 'react';
import Leaderboard from './Leaderboard'; 
import './LeaderboardOverlay.css'; // We'll style here.

function LeaderboardOverlay({ onClose }) {
  return (
    <div className="leaderboard-overlay">
      <div className="leaderboard-modal">
        <button className="close-button" onClick={onClose}>
          X
        </button>
        <Leaderboard />
      </div>
    </div>
  );
}

export default LeaderboardOverlay;
