import React from 'react'
import './RoomTable.css'

function RoomTable({ rooms, onJoinRoom }) {
  return (
    <div className="scrollable-rooms">
      <div className="room-list">
        {rooms.map((room) => (
          <div className="room-item" key={room.roomId}>
            <div className="room-info">
              <span className="room-name">{room.roomName}</span>
              <span className="participants">
                {room.numberOfParticipants}/10
              </span>
            </div>
            <button
              className="join-button"
              onClick={() => onJoinRoom(room)}
            >
              Join
            </button>
          </div>
        ))}
      </div>
    </div>
  )
}

export default RoomTable
