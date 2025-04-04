import React, { useState, useEffect } from 'react'
import { topicRoomWordHint, appGetCurrentHint } from '../../utils/constants'
import './WordHint.css'

const WordHint = ({ client, roomId, isDrawer }) => {
  const [currentHint, setCurrentHint] = useState('')

  useEffect(() => {
    if (!client || !client.connected || isDrawer) return
    const subscription = client.subscribe(topicRoomWordHint(roomId), (message) => {
      const hint = message.body
      setCurrentHint(hint)
    })
    return () => {
      subscription.unsubscribe()
    }
  }, [client, roomId, isDrawer])

  useEffect(() => {
    if (!client || !roomId) return
    client.publish({
      destination: appGetCurrentHint(roomId),
      body: ''
    })
  }, [client, roomId])

  if (isDrawer) return null

  return (
    <div className="word-hint-container">
      <h3>Word Hint:</h3>
      <div className="word-hint">
        {currentHint.split('').map((char, index) => (
          <span key={index} className="word-letter">
            {(char === ' ' || char === '_') ? '\u00A0' : char}
          </span>
        ))}
      </div>
    </div>
  )
}

export default WordHint
