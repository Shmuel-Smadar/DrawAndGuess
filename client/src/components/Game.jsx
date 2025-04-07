import React, { useCallback } from 'react'
import { useSelector, useDispatch } from 'react-redux'
import DrawingArea from './Drawing/DrawingArea'
import RightSidebar from './Sidebar/RightSidebar'
import WordSelection from './Prompt/WordSelection'
import useGameSubscriptions from '../hooks/useGameSubscriptions'
import { setIsDrawer, setShowWordSelection } from '../store/gameSlice'
import { CANVAS_WIDTH_RATIO, CANVAS_HEIGHT_RATIO, GAME_TITLE } from '../utils/constants'
import { APP_REQUEST_WORDS, APP_CHOOSE_WORD } from '../utils/subscriptionConstants'

import './Game.css'

function Game({ client, connected, username, room }) {
  const dispatch = useDispatch()
  const isDrawer = useSelector(state => state.game.isDrawer)
  const showWordSelection = useSelector(state => state.game.showWordSelection)
  const wordOptions = useSelector(state => state.game.wordOptions)

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

  return (
    <div className="game">
      <h1>{GAME_TITLE}</h1>
      <div className="game-area">
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
          width={window.innerWidth * CANVAS_WIDTH_RATIO}
          height={window.innerHeight * CANVAS_HEIGHT_RATIO}
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
