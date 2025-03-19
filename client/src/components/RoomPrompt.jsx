import React, { useEffect, useState } from 'react'
import './RoomPrompt.css'
import LeaderboardOverlay from './LeaderboardOverlay'

const RoomPrompt = ({ client, connected, setRoom }) => {
  const [newRoomName, setNewRoomName] = useState('')
  const [error, setError] = useState('')
  const [rooms, setRooms] = useState([])
  const [showLeaderboard, setShowLeaderboard] = useState(false);

  useEffect(() => {
    if (!client || !connected) return
    const subscription = client.subscribe('/topic/rooms', (message) => {
      const data = JSON.parse(message.body)
      setRooms(data)
    })
    client.publish({
      destination: '/app/getRooms',
      body: ''
    })
    return () => subscription.unsubscribe()
  }, [client, connected])

  const handleJoinRoom = (room) => {
    if (client && connected) {
      client.publish({
        destination: '/app/joinRoom',
        body: room.roomId,
      })
    }
    setRoom(room)
  }

  const handleCreateRoom = (e) => {
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
    <div className="room-prompt">
      <h2>Select a Room</h2>

      <div className="scrollable-rooms">
        <div className="room-list">
          {rooms.map((room) => (
            <div className="room-item" key={room.roomId}>
              <div className="room-info">
                <span className="room-name">{room.roomName}</span>
                <span className="participants">{room.numberOfParticipants}/10</span>
              </div>
              <button
                className="join-button"
                onClick={() => handleJoinRoom(room)}
              >
                Join
              </button>
            </div>
          ))}
        </div>
      </div>

      <form onSubmit={handleCreateRoom} className="new-room-form">
        <input
          type="text"
          value={newRoomName}
          onChange={(e) => setNewRoomName(e.target.value)}
          placeholder="New Room Name"
          maxLength={20}
          required
        />
        <button type="submit">Create Room</button>
        {error && <div className="error">{error}</div>}
      </form>

      <button
        className="leaderboard-button"
        onClick={() => setShowLeaderboard(true)}
      >
        Show Leaderboard
      </button>

      {showLeaderboard && (
        <LeaderboardOverlay onClose={() => setShowLeaderboard(false)} />
      )}
    </div>
  )
}

export default RoomPrompt
