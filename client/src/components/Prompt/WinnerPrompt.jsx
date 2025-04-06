import React, { useState } from 'react'
import { WINNER_PROMPT_TITLE, WINNER_PROMPT_LABEL, WINNER_PROMPT_SAVE } from '../../utils/constants'
import './WinnerPrompt.css'

export default function WinnerPrompt({ username, client, connected, onClose }) {
  const [winnerMessage, setWinnerMessage] = useState('')

  const handleSubmit = (e) => {
    e.preventDefault()
    if (!username || !client || !connected) return
    client.publish({
      destination: '/app/winnerMessage',
      body: JSON.stringify({ user: username, message: winnerMessage })
    })
    onClose()
  }

  return (
    <div className="winner-prompt-overlay">
      <div className="winner-prompt-container">
        <h2>{WINNER_PROMPT_TITLE}</h2>
        <form onSubmit={handleSubmit}>
          <label htmlFor="winner-message">{WINNER_PROMPT_LABEL}</label>
          <input
            id="winner-message"
            type="text"
            value={winnerMessage}
            onChange={(e) => setWinnerMessage(e.target.value)}
            maxLength={50}
          />
          <button type="submit">{WINNER_PROMPT_SAVE}</button>
        </form>
      </div>
    </div>
  )
}
