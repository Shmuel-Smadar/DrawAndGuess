import React, { useEffect, useState } from 'react'
import useStompClient from './utils/useStompClient'
import DrawingArea from './components/DrawingArea'
import NicknamePrompt from './components/NicknamePrompt'
import RoomPrompt from './components/RoomPrompt'
import RightSidebar from './components/RightSidebar'
import './App.css'

function App() {
  const [username, setUsername] = useState('')
  const [nicknameError, setNicknameError] = useState('')
  const [room, setRoom] = useState(null)
  const [rooms, setRooms] = useState([])
  const { client, connected } = useStompClient('http://localhost:8080/draw-and-guess')

  useEffect(() => {
    if (!client || !connected) return
    const subscription = client.subscribe('/topic/rooms', (message) => {
      const data = JSON.parse(message.body)
      setRooms(data)
    })
    client.publish({
      destination: '/app/getRooms',
      body: '',
    })
    return () => {
      subscription.unsubscribe()
    }
  }, [client, connected])

  if (!username) {
    return (
      <NicknamePrompt
        client={client}
        connected={connected}
        setUsername={setUsername}
        setNicknameError={setNicknameError}
        error={nicknameError}
      />
    )
  }

  if (!room) {
    return <RoomPrompt client={client} connected={connected} rooms={rooms} setRoom={setRoom} />
  }

  return (
    <div className="app">
      <h1>What's Being Drawn?</h1>
      <div className="gameArea">
        {connected && client && (
          <DrawingArea
            client={client}
            userID={username}
            roomId={room.roomId}
            isDrawingAllowed={true}
          />
        )}
        <RightSidebar
          client={client}
          roomId={room.roomId}
          username={username}
          canChat={true}
          width={window.innerWidth * 0.9}
          height={window.innerHeight * 0.83}
        />
      </div>
    </div>
  )
}

export default App
