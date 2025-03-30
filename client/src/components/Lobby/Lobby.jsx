import React, { useEffect, useState } from 'react'
import './Lobby.css'
import LeaderboardOverlay from '../Leaderboard/LeaderboardOverlay'
import RoomTable from './RoomTable'
import CreditsOverlay from './CreditsOverlay'

function Lobby({ client, connected, setRoom }) {
  const [newRoomName, setNewRoomName] = useState('')
  const [error, setError] = useState('')
  const [rooms, setRooms] = useState([])
  const [showLeaderboard, setShowLeaderboard] = useState(false)
  const [showCredits, setShowCredits] = useState(false)

  useEffect(() => {
    if (!client || !connected) return
    const subscription = client.subscribe('/topic/rooms', (message) => {
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
      destination: '/app/getRooms',
      body: ''
    })

    return () => subscription.unsubscribe()
  }, [client, connected])

  useEffect(() => {
    if (!client || !connected) return
    const sub = client.subscribe('/user/topic/roomCreated', (message) => {
      const room = JSON.parse(message.body)
      setRoom(room)
    })
    return () => sub.unsubscribe()
  }, [client, connected, setRoom])

  function handleJoinRoom(room) {
    if (client && connected) {
      client.publish({
        destination: '/app/joinRoom',
        body: room.roomId,
      })
      setRoom(room)
    }
  }

  function handleCreateRoom(e) {
    e.preventDefault()
    if (newRoomName.trim() === '') {
      setError('Room name cannot be empty.')
      return
    }
    if (client && connected) {
      client.publish({
        destination: '/app/createRoom',
        body: newRoomName.trim(),
      })
      client.publish({
        destination: '/app/getRooms',
        body: ''
      })
      setNewRoomName('')
      setError('')
    } else {
      setError('Unable to connect to the server.')
    }
  }

  return (
    <div className="lobby">
      <h2>Select a Room</h2>
      <RoomTable rooms={rooms} onJoinRoom={handleJoinRoom} />
      <form onSubmit={handleCreateRoom} className="new-room-form">
        <input
          type="text"
          value={newRoomName}
          onChange={(e) => setNewRoomName(e.target.value)}
          placeholder="New Room Name"
          maxLength={20}
          required
        />
        <button type="submit">Create New Room & Join</button>
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
