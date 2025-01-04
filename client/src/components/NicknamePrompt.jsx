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
          setNicknameError(data.message) //TODO: display the error
        }
      })
      return () => subscription.unsubscribe()
    }
  }, [client, connected, setUsername, setNicknameError])

  const handleSubmit = (e) => {
    e.preventDefault()
    if (nicknameInput.trim() !== '' && client && connected) {
      currentNickname.current = nicknameInput.trim()
      const registrationMessage = { nickname: nicknameInput.trim() }
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
      </form>
    </div>
  )
}

export default NicknamePrompt
