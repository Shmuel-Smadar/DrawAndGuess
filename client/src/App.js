import React, { useEffect, useState } from 'react'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'
import DrawingArea from './components/DrawingArea'
import NicknamePrompt from './components/NicknamePrompt';
import './App.css'

function App() {
  const [client, setClient] = useState(null)
  const [connected, setConnected] = useState(false)
  const [username, setUsername] = useState('');

  useEffect(() => {
    const stompClient = new Client({
      brokerURL: 'ws://localhost:8080/draw-and-guess',
      webSocketFactory: () => new SockJS('http://localhost:8080/draw-and-guess'),
      debug: (str) => console.log(str),
      onConnect: () => {
        console.log('Connected to STOMP')
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
    return <NicknamePrompt setUsername={setUsername} />;
  }


  return (
    <div className="app">
      <h1>What's Being Drawn?</h1>
      <div className="gameArea">
        {connected && client && (
          <DrawingArea
            client={client}
            userID={username}
            isDrawingAllowed={true}
          />
        )}
      </div>
    </div>
  )
}

export default App
