import React, { useState } from 'react'

export default function WinnerPrompt({ username, onClose }) {
  const [winnerMessage, setWinnerMessage] = useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!username) return
    try {
      await fetch('http://localhost:8080/leaderboard/winner-message', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ user: username, message: winnerMessage })
      })
    } catch (err) {
      console.error(err)
    }
    onClose()
  }

  return (
    <div className="word-selection-overlay">
      <div className="word-selection-container">
        <h2>You Won!</h2>
        <form onSubmit={handleSubmit}>
          <label htmlFor="winner-message">Add a message for the leaderboard:</label>
          <input
            id="winner-message"
            type="text"
            value={winnerMessage}
            onChange={(e) => setWinnerMessage(e.target.value)}
            maxLength={50}
          />
          <button type="submit">Save</button>
          <button type="button" onClick={onClose}>Cancel</button>
        </form>
      </div>
    </div>
  )
}
