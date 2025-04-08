import React, { useCallback, useState, useEffect } from 'react'
import { useSelector, useDispatch } from 'react-redux'
import DrawingArea from './Drawing/DrawingArea'
import RightSidebar from './Sidebar/RightSidebar'
import WordSelection from './Prompt/WordSelection'
import useGameSubscriptions from '../hooks/useGameSubscriptions'
import { setIsDrawer, setShowWordSelection } from '../store/gameSlice'
import {  CANVAS_HEIGHT_RATIO, GAME_TITLE } from '../utils/constants'
import { APP_REQUEST_WORDS, APP_CHOOSE_WORD } from '../utils/subscriptionConstants'
import './Game.css'

function Game({ client, connected}) {
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
      });
    };
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
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

  useGameSubscriptions(client, connected, room, isDrawer, requestWordOptions)

  return (
    <div className="game">
      <h1>{GAME_TITLE}</h1>
      <div className="game-area">
      {connected && client && (
          <DrawingArea
            client={client}
          />
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