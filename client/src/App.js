import React from 'react'
import { useSelector, useDispatch } from 'react-redux'
import { ThemeProvider } from './contexts/ThemeContext'
import useStompClient from './utils/useStompClient'
import NicknamePrompt from './components/Prompt/NicknamePrompt'
import Lobby from './components/Lobby/Lobby'
import Game from './components/Game'
import { setUsername, setNicknameError } from './store/userSlice'
import { setRoom } from './store/roomSlice'
import { DEFAULT_SOCKET_URL } from './utils/constants'


/*
* The main function of the client.
* sets up the STOMP connection to the server, shows the nickname prompt for the user to choose one.
* it then displays the lobby for the user, and after the user joined a room, it starts the game
*/
function AppContent() {
  const dispatch = useDispatch()
  const username = useSelector(state => state.user.username)
  const nicknameError = useSelector(state => state.user.nicknameError)
  const room = useSelector(state => state.room.room)
  const { client, connected } = useStompClient(process.env.REACT_APP_SOCKET_URL || DEFAULT_SOCKET_URL)

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
    <Game
     client={client}
     connected={connected}
    />
  )
}

function App() {
  return (
    <ThemeProvider>
      <AppContent />
    </ThemeProvider>
  )
}

export default App
