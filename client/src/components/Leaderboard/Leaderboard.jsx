import React, { useState, useEffect } from 'react';
import './Leaderboard.css';

function Leaderboard() {
  const [scores, setScores] = useState([]);

  useEffect(() => {
    const fetchScores = async () => {
      try {
        const res = await fetch(process.env.REACT_APP_LEADERBOARD_URL || 'http://localhost:8080/leaderboard');
        const data = await res.json();
        const formattedScores = data.map(item => {
          const parts = item.split(':');
          const username = parts[0];
          const score = parseInt(parts[1], 10);
          const message = parts[2] ? parts[2] : '';
          return { username, score, message };
        });
        setScores(formattedScores);
      } catch (err) {
        console.error(err);
      }
    };
    fetchScores();
    const interval = setInterval(fetchScores, 100);
    return () => clearInterval(interval);
  }, []);

  const getRankIcon = (index) => {
    if (index === 0) return '🏆';
    if (index === 1) return '🥈';
    if (index === 2) return '🥉';
    return null;
  };

  return (
    <div className="leaderboard-container">
      <h2 className="leaderboard-title">Leaderboard</h2>
      <div className="leaderboard-list">
        {scores.map((item, index) => (
          <div className="leaderboard-item" key={index}>
            <div className="leaderboard-rank-container">
              <span className="leaderboard-rank">{index + 1}.</span>
              {getRankIcon(index) && (
                <span className="leaderboard-medal">
                  {getRankIcon(index)}
                </span>
              )}
            </div>
            <span className="leaderboard-username">{item.username}</span>
            <span className="leaderboard-score">{item.score} points</span>
            {item.message && (
              <span className="leaderboard-message">"{item.message}"</span>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

export default Leaderboard;
