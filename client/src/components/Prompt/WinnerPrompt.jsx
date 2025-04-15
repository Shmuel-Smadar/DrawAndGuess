import React, { useState } from 'react'
import { WINNER_PROMPT_TITLE, WINNER_PROMPT_LABEL, WINNER_PROMPT_SAVE, WINNER_MESSAGE_MAX_LENGTH } from '../../utils/constants'
import { APP_WINNER_MESSAGE } from '../../utils/subscriptionConstants'
import './WinnerPrompt.css'

// A component that lets the winner create a message and send to the server
export default function WinnerPrompt({ username, client, connected, onClose }) {
  const [winnerMessage, setWinnerMessage] = useState('')

  // A function that sends the message of the winner to the server.
  const handleSubmit = (e) => {
    e.preventDefault()
    if (!client || !connected) return
    client.publish({
      destination: APP_WINNER_MESSAGE,
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
            maxLength={WINNER_MESSAGE_MAX_LENGTH}
          />
          <button type="submit">{WINNER_PROMPT_SAVE}</button>
        </form>
      </div>
    </div>
  )
}
