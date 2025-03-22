import React, { useState, useEffect, useRef } from 'react'
import { useDispatch } from 'react-redux'
import { setUsername, setNicknameError, setSessionId } from '../../store/userSlice'
import './NicknamePrompt.css'

const NicknamePrompt = ({ client, connected, error }) => {
  const [nicknameInput, setNicknameInput] = useState('')
  const dispatch = useDispatch()
  const currentNickname = useRef('')

  useEffect(() => {
    if (client && connected) {
      const subscription = client.subscribe('/user/topic/nickname', (message) => {
        const data = JSON.parse(message.body)
        if (data.success) {
          dispatch(setUsername(currentNickname.current))
          dispatch(setNicknameError(''))
          dispatch(setSessionId(data.sessionId))
        } else {
          dispatch(setNicknameError(data.message))
        }
      })
      return () => subscription.unsubscribe()
    }
  }, [client, connected, dispatch])

  const handleSubmit = (e) => {
    e.preventDefault()
    const trimmedNickname = nicknameInput.trim()
    if (trimmedNickname !== '' && client && connected) {
      currentNickname.current = trimmedNickname
      const registrationMessage = { nickname: trimmedNickname }
      client.publish({
        destination: '/app/registerNickname',
        body: JSON.stringify(registrationMessage)
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