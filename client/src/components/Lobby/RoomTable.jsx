import React from 'react'
import './RoomTable.css'
import EmptyIcon from '../../assets/empty-pockets.png'

function EmptyRoomState({ onCreateRoom }) {
  return (
    <div className="empty-room-container">
      <img
        src={EmptyIcon}
        alt="No rooms available"
        className="empty-room-image"
      />
      <h2 className="empty-room-heading">No Rooms Found</h2>
      <p className="empty-room-text">
        Looks like there are no rooms available right now. Create a new room and invite your friends to join!
      </p>
    </div>
  )
}

function RoomTable({ rooms, onJoinRoom, onCreateRoom }) {
  return (
    <div className="scrollable-rooms">
      {rooms.length === 0 ? (
        <EmptyRoomState onCreateRoom={onCreateRoom} />
      ) : (
        <div className="room-list">
          {rooms.map((room) => (
            <div className="room-item" key={room.roomId}>
              <div className="room-info">
                <span className="room-name">{room.roomName}</span>
                <span className="participants">{room.numberOfParticipants}/10</span>
              </div>
              <button className="join-button" onClick={() => onJoinRoom(room)}>
                Join
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default RoomTable
