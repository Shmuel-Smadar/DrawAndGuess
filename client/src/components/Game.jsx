import React, { useCallback, useState, useEffect } from 'react'
import { useSelector, useDispatch } from 'react-redux'
import RightSidebar from './Sidebar/RightSidebar'
import WordSelection from './Prompt/WordSelection'
import Canvas from './Drawing/Canvas'
import ColorPicker from './DrawingPanel/ColorPicker'
import WordHint from './DrawingPanel/WordHint'
import { setIsDrawer, setShowWordSelection, setWordOptions } from '../store/gameSlice'
import { CANVAS_HEIGHT_RATIO, GAME_TITLE } from '../utils/constants'
import { APP_REQUEST_WORDS, APP_CHOOSE_WORD, USER_TOPIC_WORD_OPTIONS, TOPIC_ROOM_CHAT } from '../utils/subscriptionConstants'
import './Game.css'


/*
* The main component inisde a room that displayes
* - The canvas
* - The drawing panel for the drawer and the hint for non drawers
* - Tha chat in which non drawers can guess the word being drawn
* - The list of participants in the current room
*/
function Game({ client, connected }) {
  const dispatch = useDispatch()
  const room = useSelector(state => state.room.room)
  const isDrawer = useSelector(state => state.game.isDrawer)
  const showWordSelection = useSelector(state => state.game.showWordSelection)
  const wordOptions = useSelector(state => state.game.wordOptions)
  const [windowSize, setWindowSize] = useState({
    width: window.innerWidth,
    height: window.innerHeight
  })

  useEffect(() => {
    const handleResize = () => {
      setWindowSize({
        width: window.innerWidth,
        height: window.innerHeight
      })
    }
    window.addEventListener('resize', handleResize)
    return () => window.removeEventListener('resize', handleResize)
  }, [])

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

  useEffect(() => {
    if (!client || !connected) return
    const sub = client.subscribe(USER_TOPIC_WORD_OPTIONS, (msg) => {
      const data = JSON.parse(msg.body)
      dispatch(setWordOptions([data.word1, data.word2, data.word3]))
      dispatch(setShowWordSelection(true))
    })
    return () => sub.unsubscribe()
  }, [client, connected, dispatch])

  useEffect(() => {
    if (!client || !connected || !room) return
    const chatSub = client.subscribe(TOPIC_ROOM_CHAT(room.roomId), (msg) => {
      const message = JSON.parse(msg.body)
      if (message.senderSessionId === 'system' && message.messageType === 'NEW_GAME_STARTED' && isDrawer) {
        requestWordOptions()
      }
    })
    return () => chatSub.unsubscribe()
  }, [client, connected, room, isDrawer, requestWordOptions])

  return (
    <div className="game">
      <h1>{GAME_TITLE}</h1>
      <div className="game-area">
        {connected && client && (
          <div className="drawing-area">
            <Canvas client={client} />
            {isDrawer ? (
              <ColorPicker client={client} />
            ) : (
              <WordHint client={client} />
            )}
          </div>
        )}
        <RightSidebar
          client={client}
          height={windowSize.height * CANVAS_HEIGHT_RATIO}
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

export default Game