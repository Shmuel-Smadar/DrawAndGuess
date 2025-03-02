import React, { useState, useEffect } from 'react'

function Leaderboard() {
  const [scores, setScores] = useState([])

  useEffect(() => {
    const fetchScores = async () => {
      try {
        const res = await fetch('http://localhost:8080/leaderboard')
        const data = await res.json()
        setScores(data)
      } catch (err) {
      }
    }
    fetchScores()
    const interval = setInterval(fetchScores, 100)
    return () => clearInterval(interval)
  }, [])

  return (
    <div className="leaderboard-container">
      <div className="leaderboard-list">
        {scores.map((item, index) => (
          <div className="leaderboard-item" key={index}>
            <span className="leaderboard-rank">{index + 1}.</span>
            <span className="leaderboard-username">{item.username}</span>
            <span className="leaderboard-score">{item.score} points</span>
          </div>
        ))}
      </div>
    </div>
  )
}


export default Leaderboard
