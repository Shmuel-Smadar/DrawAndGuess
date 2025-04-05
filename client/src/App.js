import React from 'react'
import { useSelector, useDispatch } from 'react-redux'
import useStompClient from './utils/useStompClient'
import NicknamePrompt from './components/Prompt/NicknamePrompt'
import Lobby from './components/Lobby/Lobby'
import Game from './components/Game'
import { setUsername, setNicknameError } from './store/userSlice'
import { setRoom } from './store/roomSlice'
import './App.css'

function App() {
  const dispatch = useDispatch()
  const username = useSelector(state => state.user.username)
  const nicknameError = useSelector(state => state.user.nicknameError)
  const room = useSelector(state => state.room.room)
  const { client, connected } = useStompClient(process.env.REACT_APP_SOCKET_URL || 'http://localhost:8080/draw-and-guess')

  if (!username) {
    return (
      <NicknamePrompt
        client={client}
        connected={connected}
        setUsername={(val) => dispatch(setUsername(val))}
        setNicknameError={(val) => dispatch(setNicknameError(val))}
        error={nicknameError}
      />
    )
  }

  if (!room) {
    return (
      <Lobby
        client={client}
        connected={connected}
        setRoom={(val) => dispatch(setRoom(val))}
      />
    )
  }

  return (
    <Game client={client} connected={connected} username={username} room={room} />
  )
}

export default App
