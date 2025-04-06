import React, { useState, useEffect, useRef } from 'react'
import { TOPIC_ROOM_PARTICIPANTS } from '../../utils/constants'
import { PARTICIPANTS_TITLE, DRAWING_INDICATOR_TEXT } from '../../utils/constants'
import './ParticipantsList.css'

const ParticipantsList = ({ client, height, roomId, username, onDrawerChange }) => {
  const [userList, setUserList] = useState([])
  const participantsWindowRef = useRef(null)

  useEffect(() => {
    if (!client || !roomId) return
    const destination = TOPIC_ROOM_PARTICIPANTS(roomId)
    const subscription = client.subscribe(destination, (message) => {
      const participants = JSON.parse(message.body)
      setUserList(participants)
      const me = participants.find((p) => p.username === username)
      onDrawerChange(me && me.isDrawer)
    })
    return () => {
      subscription.unsubscribe()
    }
  }, [client, roomId, username, onDrawerChange])

  useEffect(() => {
    if (!client || !roomId) return
    client.publish({
      destination: `/app/room/${roomId}/getParticipants`,
      body: ''
    })
  }, [client, roomId])

  return (
    <div className="participants-container" style={{ height: `${height}px` }}>
      <div className="participants-header">
        <h2>{PARTICIPANTS_TITLE}</h2>
      </div>
      <div className="participants-window" ref={participantsWindowRef}>
        {userList.map((user) => (
          <div key={user.sessionId} className="participant-item">
            <span className="participant-name">
              {user.username} ({user.score})
            </span>
            {user.isDrawer && (
              <span className="drawer-indicator">{DRAWING_INDICATOR_TEXT}</span>
            )}
          </div>
        ))}
      </div>
    </div>
  )
}

export default ParticipantsList
