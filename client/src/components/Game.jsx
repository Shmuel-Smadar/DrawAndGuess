import React, { useState, useEffect } from 'react'
import { useSelector, useDispatch } from 'react-redux'
import RightSidebar from './Sidebar/RightSidebar'
import WordSelection from './Prompt/WordSelection'
import Canvas from './Drawing/Canvas'
import ColorPicker from './DrawingPanel/ColorPicker'
import WordHint from './DrawingPanel/WordHint'
import { CANVAS_HEIGHT_RATIO, GAME_TITLE } from '../utils/constants'
import useGameSubscriptions from '../hooks/useGameSubscriptions'
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

  // Use the custom hook for subscription logic
  const { handleDrawerChange, handleWordSelect } = useGameSubscriptions({
    client,
    connected,
    room,
    isDrawer
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

  return (
    <div className="game">
      <h1>{GAME_TITLE}</h1>
      <div className="game-area">
        {connected && client && (
          <div className="drawing-area">
            <Canvas client={client} />
           {isDrawer ? (
  <div className="rotatable">
    <ColorPicker client={client} />
  </div>
) : (
  <div className="rotatable">
    <WordHint client={client} />
  </div>
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