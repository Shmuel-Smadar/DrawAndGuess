import React from 'react';
import Leaderboard from './Leaderboard'; 
import './LeaderboardOverlay.css'; // We'll style here.

function LeaderboardOverlay({ onClose }) {
  return (
    <div className="leaderboard-overlay">
      <div className="leaderboard-modal">
        {/* Close Button */}
        <button className="close-button" onClick={onClose}>
          X
        </button>
        {/* The actual leaderboard */}
        <Leaderboard />
      </div>
    </div>
  );
}

export default LeaderboardOverlay;
