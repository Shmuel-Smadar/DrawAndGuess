import React, { useEffect } from 'react'
import { useSelector, useDispatch } from 'react-redux'
import useStompClient from './utils/useStompClient'
import DrawingArea from './components/Drawing/DrawingArea'
import NicknamePrompt from './components/Prompt/NicknamePrompt'
import Lobby from './components/Lobby/Lobby'
import RightSidebar from './components/Sidebar/RightSidebar'
import WordSelection from './components/Prompt/WordSelection'
import { setUsername, setNicknameError } from './store/userSlice'
import { setRoom } from './store/roomSlice'
import { setIsDrawer, setShowWordSelection, setWordOptions } from './store/gameSlice'
import { USER_TOPIC_WORD_OPTIONS } from './utils/constants'
import './App.css'

function App() {
  const dispatch = useDispatch()
  const username = useSelector(state => state.user.username)
  const nicknameError = useSelector(state => state.user.nicknameError)
  const room = useSelector(state => state.room.room)
  const isDrawer = useSelector(state => state.game.isDrawer)
  const showWordSelection = useSelector(state => state.game.showWordSelection)
  const wordOptions = useSelector(state => state.game.wordOptions)
  const { client, connected } = useStompClient(process.env.REACT_APP_SOCKET_URL || 'http://localhost:8080/draw-and-guess');

  const handleDrawerChange = (drawerState) => {
    if (drawerState !== isDrawer) {
      dispatch(setIsDrawer(drawerState))
      if (drawerState) {
        requestWordOptions()
      }
    }
  }

  const requestWordOptions = () => {
    if (!client || !connected || !room) return
    client.publish({
      destination: `/app/room/${room.roomId}/requestWords`,
      body: ''
    })
  }

  useEffect(() => {
    if (!client || !connected) return
    const sub = client.subscribe(USER_TOPIC_WORD_OPTIONS, (msg) => {
      const data = JSON.parse(msg.body)
      dispatch(setWordOptions([data.word1, data.word2, data.word3]))
      dispatch(setShowWordSelection(true))
    })
    return () => sub.unsubscribe()
  }, [client, connected, dispatch])

  const handleWordSelect = (selectedWord) => {
    if (!client || !connected || !room) return
    client.publish({
      destination: `/app/room/${room.roomId}/chooseWord`,
      body: selectedWord
    })
    dispatch(setShowWordSelection(false))
  }

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
