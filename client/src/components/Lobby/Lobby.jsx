import React, { useEffect, useState } from 'react'
import './Lobby.css'
import LeaderboardOverlay from '../Leaderboard/LeaderboardOverlay'
import RoomTable from './RoomTable'
import CreditsOverlay from './CreditsOverlay'
import {
  MAX_ROOM_NAME_LENGTH,
  LOBBY_TITLE,
  NEW_ROOM_PLACEHOLDER,
  CREATE_ROOM_BUTTON_TEXT,
  ROOM_NAME_EMPTY_ERROR,
  SERVER_CONNECTION_ERROR
} from '../../utils/constants'
import {
  TOPIC_ROOMS,
  APP_GET_ROOMS,
  APP_CREATE_ROOM,
  APP_JOIN_ROOM,
  USER_TOPIC_ROOM_CREATED
} from '../../utils/subscriptionConstants'

function Lobby({ client, connected, setRoom }) {
  const [newRoomName, setNewRoomName] = useState('')
  const [error, setError] = useState('')
  const [rooms, setRooms] = useState([])
  const [showLeaderboard, setShowLeaderboard] = useState(false)
  const [showCredits, setShowCredits] = useState(false)

  useEffect(() => {
    if (!client || !connected) return
    const subscription = client.subscribe(TOPIC_ROOMS, (message) => {
      const data = JSON.parse(message.body)
      const sorted = data.sort((a, b) => {
        if (b.numberOfParticipants !== a.numberOfParticipants) {
          return b.numberOfParticipants - a.numberOfParticipants
        }
        return a.roomName.localeCompare(b.roomName)
      })
      setRooms(sorted)
    })

    client.publish({
      destination: APP_GET_ROOMS,
      body: ''
    })

    return () => subscription.unsubscribe()
  }, [client, connected])

  useEffect(() => {
    if (!client || !connected) return
    const sub = client.subscribe(USER_TOPIC_ROOM_CREATED, (message) => {
      const room = JSON.parse(message.body)
      setRoom(room)
    })
    return () => sub.unsubscribe()
  }, [client, connected, setRoom])

  function handleJoinRoom(room) {
    if (client && connected) {
      client.publish({
        destination: APP_JOIN_ROOM,
        body: room.roomId,
      })
      setRoom(room)
    }
  }

  function handleCreateRoom(e) {
    e.preventDefault()
    if (newRoomName.trim() === '') {
      setError(ROOM_NAME_EMPTY_ERROR)
      return
    }
    if (client && connected) {
      client.publish({
        destination: APP_CREATE_ROOM,
        body: newRoomName.trim(),
      })
      client.publish({
        destination: APP_GET_ROOMS,
        body: ''
      })
      setNewRoomName('')
      setError('')
    } else {
      setError(SERVER_CONNECTION_ERROR)
    }
  }

  return (
    <div className="lobby">
      <h2>{LOBBY_TITLE}</h2>
      <RoomTable rooms={rooms} onJoinRoom={handleJoinRoom} />
      <form onSubmit={handleCreateRoom} className="new-room-form">
        <input
          type="text"
          value={newRoomName}
          onChange={(e) => setNewRoomName(e.target.value)}
          placeholder={NEW_ROOM_PLACEHOLDER}
          maxLength={MAX_ROOM_NAME_LENGTH}
          required
        />
        <button type="submit">{CREATE_ROOM_BUTTON_TEXT}</button>
        {error && <div className="error">{error}</div>}
      </form>
      <div className="button-group">
        <button
          className="leaderboard-button"
          onClick={() => setShowLeaderboard(true)}
        >
          Leaderboard
        </button>
        <button
          className="credits-button"
          onClick={() => setShowCredits(true)}
        >
          Credits
        </button>
      </div>
      {showLeaderboard && (
        <LeaderboardOverlay onClose={() => setShowLeaderboard(false)} />
      )}
      {showCredits && (
        <CreditsOverlay onClose={() => setShowCredits(false)} />
      )}
    </div>
  )
}

export default Lobby
