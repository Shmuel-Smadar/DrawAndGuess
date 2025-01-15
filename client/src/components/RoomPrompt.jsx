import React, { useState } from 'react'
import './RoomPrompt.css'

const RoomPrompt = ({ client, connected, rooms, setRoom }) => {
  const [newRoomName, setNewRoomName] = useState('')
  const [error, setError] = useState('')

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
      setNewRoomName('')
      setError('')
    } else {
      setError('Unable to connect to the server.')
    }
  }

  return (
    <div className="room-prompt">
      <h2>Select a Room</h2>
      <div className="room-list">
        {rooms.map((room) => (
          <div className="room-item" key={room.roomId}>
            <div className="room-info">
              <span className="room-name">{room.roomName}</span>
              <span className="participants">0/10</span> {/* Placeholder */}
            </div>
            <button className="join-button" onClick={() => handleJoinRoom(room)}>
              Join
            </button>
          </div>
        ))}
      </div>
      <form onSubmit={handleCreateRoom}>
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
    </div>
  )
}

export default RoomPrompt
