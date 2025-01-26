import React, { useState, useEffect, useRef } from 'react'
import './NicknamePrompt.css'

const NicknamePrompt = ({ client, connected, setUsername, setNicknameError, error }) => {
  const [nicknameInput, setNicknameInput] = useState('')
  const currentNickname = useRef('')

  useEffect(() => {
    if (client && connected) {
      const subscription = client.subscribe('/user/topic/nickname', (message) => {
        const data = JSON.parse(message.body)
        if (data.success) {
          setUsername(currentNickname.current)
          setNicknameError('')
        } else {
          setNicknameError(data.message)
        }
      })
      return () => subscription.unsubscribe()
    }
  }, [client, connected, setUsername, setNicknameError])

  const handleSubmit = (e) => {
    e.preventDefault()
    const trimmedNickname = nicknameInput.trim()
    if (trimmedNickname !== '' && client && connected) {
      currentNickname.current = trimmedNickname
      const registrationMessage = { nickname: trimmedNickname }
      client.publish({
        destination: '/app/registerNickname',
        body: JSON.stringify(registrationMessage),
      })
    }
  }

  return (
    <div className="nickname-prompt">
      <form onSubmit={handleSubmit}>
        <h2>Enter Your Nickname</h2>
        <input
          type="text"
          value={nicknameInput}
          onChange={(e) => setNicknameInput(e.target.value)}
          placeholder="Nickname"
          maxLength={20}
        />
        <button type="submit">Join</button>
        {error && <p className="error-message">{error}</p>}
      </form>
    </div>
  )
}

export default NicknamePrompt
