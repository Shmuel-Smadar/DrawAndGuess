import React from 'react'
import './RoomTable.css'
import EmptyIcon from '../../assets/empty-pockets.png'
import { EMPTY_ROOM_HEADING, EMPTY_ROOM_TEXT, JOIN_ROOM_BUTTON_TEXT } from '../../utils/constants'


// Shows an icon and a message stating that there are no rooms available
function EmptyRoomState() {
  return (
    <div className="empty-room-container">
      <img
        src={EmptyIcon}
        alt="No rooms available"
        className="empty-room-image"
      />
      <h2 className="empty-room-heading">{EMPTY_ROOM_HEADING}</h2>
      <p className="empty-room-text">
        {EMPTY_ROOM_TEXT}
      </p>
    </div>
  )
}

// Displays a scrollable list of rooms and calls EmptyRoomState in case of no rooms
function RoomTable({ rooms, onJoinRoom }) {
  return (
    <div className="scrollable-rooms">
      {rooms.length === 0 ? (
        <EmptyRoomState />
      ) : (
        <div className="room-list">
          {rooms.map((room) => (
            <div className="room-item" key={room.roomId}>
              <div className="room-info">
                <span className="room-name">{room.roomName}</span>
                <span className="participants">{room.numberOfParticipants}/10</span>
              </div>
              <button className="join-button" onClick={() => onJoinRoom(room)}>
                {JOIN_ROOM_BUTTON_TEXT}
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default RoomTable
