import React, { useCallback } from 'react'
import { useSelector, useDispatch } from 'react-redux'
import useStompClient from './utils/useStompClient'
import DrawingArea from './components/Drawing/DrawingArea'
import NicknamePrompt from './components/Prompt/NicknamePrompt'
import Lobby from './components/Lobby/Lobby'
import RightSidebar from './components/Sidebar/RightSidebar'
import WordSelection from './components/Prompt/WordSelection'
import useGameSubscriptions from './hooks/useGameSubscriptions'
import { setUsername, setNicknameError } from './store/userSlice'
import { setRoom } from './store/roomSlice'
import { setIsDrawer, setShowWordSelection } from './store/gameSlice'
import { APP_REQUEST_WORDS, APP_CHOOSE_WORD } from './utils/constants'
import './App.css'

function App() {
  const dispatch = useDispatch()
  const username = useSelector(state => state.user.username)
  const nicknameError = useSelector(state => state.user.nicknameError)
  const room = useSelector(state => state.room.room)
  const isDrawer = useSelector(state => state.game.isDrawer)
  const showWordSelection = useSelector(state => state.game.showWordSelection)
  const wordOptions = useSelector(state => state.game.wordOptions)
  const { client, connected } = useStompClient(process.env.REACT_APP_SOCKET_URL || 'http://localhost:8080/draw-and-guess')

  const requestWordOptions = useCallback(() => {
    if (!client || !connected || !room) return
    client.publish({
      destination: APP_REQUEST_WORDS(room.roomId),
      body: ''
    })
  }, [client, connected, room])

  const handleDrawerChange = (drawerState) => {
    if (drawerState !== isDrawer) {
      dispatch(setIsDrawer(drawerState))
      if (drawerState) {
        requestWordOptions()
      }
    }
  }

  const handleWordSelect = (selectedWord) => {
    if (!client || !connected || !room) return
    client.publish({
      destination: APP_CHOOSE_WORD(room.roomId),
      body: selectedWord
    })
    dispatch(setShowWordSelection(false))
  }

  useGameSubscriptions(client, connected, room, isDrawer, requestWordOptions)

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
    <div className="app">
      <h1>What's Being Drawn?</h1>
      <div className="gameArea">
        {connected && client && (
          <DrawingArea
            client={client}
            userID={username}
            roomId={room.roomId}
          />
        )}
        <RightSidebar
          client={client}
          roomId={room.roomId}
          username={username}
          canChat={!isDrawer}
          width={window.innerWidth * 0.9}
          height={window.innerHeight * 0.80}
          onDrawerChange={handleDrawerChange}
        />
      </div>
      {isDrawer && showWordSelection && (
        <WordSelection
          words={wordOptions}
          onWordSelect={handleWordSelect}
        />
      )}
    </div>
  )
}

export default App
