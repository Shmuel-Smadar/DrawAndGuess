import React, { useEffect, useState } from 'react'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'
import DrawingArea from './components/DrawingArea'
import NicknamePrompt from './components/NicknamePrompt'
import RoomPrompt from './components/RoomPrompt'
import RightSidebar from './components/RightSidebar'
import './App.css'

function App() {
  const [client, setClient] = useState(null)
  const [connected, setConnected] = useState(false)
  const [username, setUsername] = useState('')
  const [nicknameError, setNicknameError] = useState('')
  const [room, setRoom] = useState(null)
  const [rooms, setRooms] = useState([])


  useEffect(() => {
    const stompClient = new Client({
      brokerURL: 'ws://localhost:8080/draw-and-guess',
      webSocketFactory: () => new SockJS('http://localhost:8080/draw-and-guess'),
      debug: (str) => console.log(str),
      onConnect: () => {
        console.log('Connected to STOMP')
        stompClient.subscribe('/topic/rooms', (message) => {
          const data = JSON.parse(message.body)
          setRooms(data)
        })

        // Request current rooms
        stompClient.publish({
          destination: '/app/getRooms',
          body: '',
        })

        setConnected(true)
      },
      onDisconnect: () => {
        console.log('Disconnected from STOMP')
        setConnected(false)
      },
    })

    stompClient.activate()
    setClient(stompClient)

    return () => {
      stompClient.deactivate()
    }
  }, [])

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
