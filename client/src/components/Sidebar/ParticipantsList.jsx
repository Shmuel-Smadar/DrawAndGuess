import React, {useState, useEffect, useRef } from 'react'
import { useSelector } from 'react-redux'
import { TOPIC_ROOM_PARTICIPANTS, APP_GET_PARTICIPANTS } from '../../utils/subscriptionConstants'
import { PARTICIPANTS_TITLE, DRAWING_INDICATOR_TEXT } from '../../utils/constants'
import './ParticipantsList.css'


/*
 * Shows a list of participants in the current room, 
 * with scores and an indicator of the current drawer.
 */
const ParticipantsList = ({client, height, onDrawerChange }) => {
  const [userList, setUserList] = useState([])
  const participantsWindowRef = useRef(null)
  const roomId = useSelector(state => state.room.room?.roomId)
  const username = useSelector(state => state.user.username)

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
      destination: APP_GET_PARTICIPANTS(roomId),
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
