import React, { useState, useEffect } from 'react';
import './LeaderboardOverlay.css';
import {
  DEFAULT_LEADERBOARD_URL,
  LEADERBOARD_REFRESH_INTERVAL,
  FIRST_PLACE_ICON,
  SECOND_PLACE_ICON,
  THIRD_PLACE_ICON
} from '../../utils/constants';

function MessageModal({ record, onClose }) {
  return (
    <div className="message-overlay">
      <div className="message-modal">
        <button className="close-button" onClick={onClose}>X</button>
        <h2 className="message-title">{record.username}'s Message</h2>
        <p className="message-content">{record.message}</p>
      </div>
    </div>
  );
}

function LeaderboardOverlay({ onClose }) {
  const [scores, setScores] = useState([]);
  const [selectedRecord, setSelectedRecord] = useState(null);

  useEffect(() => {
    const fetchScores = async () => {
      try {
        const res = await fetch(process.env.REACT_APP_LEADERBOARD_URL || DEFAULT_LEADERBOARD_URL);
        const data = await res.json();
        setScores(data);
      } catch (err) {
        console.error(err);
      }
    };
    fetchScores();
    const interval = setInterval(fetchScores, LEADERBOARD_REFRESH_INTERVAL);
    return () => clearInterval(interval);
  }, []);

  const getRankIcon = (index) => {
    if (index === 0) return FIRST_PLACE_ICON;
    if (index === 1) return SECOND_PLACE_ICON;
    if (index === 2) return THIRD_PLACE_ICON;
    return null;
  };

  return (
    <>
      <div className="leaderboard-overlay">
        <div className="leaderboard-modal">
          <button className="close-button" onClick={onClose}>X</button>
          <h2 className="leaderboard-title">Leaderboard</h2>
          <div className="leaderboard-container">
            <div className="leaderboard-list">
              {scores.map((item, index) => (
                <div
                  className="leaderboard-item"
                  key={index}
                  onClick={() => item.message && setSelectedRecord(item)}
                >
                  <div className="leaderboard-rank-container">
                    <span className="leaderboard-rank">{index + 1}.</span>
                    {getRankIcon(index) && (
                      <span className="leaderboard-medal">{getRankIcon(index)}</span>
                    )}
                  </div>
                  <span className="leaderboard-username">{item.username}</span>
                  <span className="leaderboard-score">{item.score} points</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
      {selectedRecord && <MessageModal record={selectedRecord} onClose={() => setSelectedRecord(null)} />}
    </>
  );
}

export default LeaderboardOverlay;
