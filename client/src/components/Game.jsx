import React, { useState, useEffect } from 'react'
import { useSelector } from 'react-redux'
import { motion } from 'framer-motion'
import { Home } from 'lucide-react'
import ThemeToggle from './common/ThemeToggle'
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
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 0.5 }}
      className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 dark:from-gray-900 dark:to-gray-800 p-2 lg:p-4"
    >
      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.2 }}
        className="flex justify-between items-center mb-6 max-w-7xl mx-auto"
      >
        <h1 className="game-title text-3xl">{GAME_TITLE}</h1>
        <div className="flex items-center gap-3">
          <ThemeToggle />
          <motion.button
            onClick={() => window.location.reload()}
            className="btn-outline flex items-center gap-2"
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
          >
            <Home className="w-4 h-4" />
            Leave Game
          </motion.button>
        </div>
      </motion.div>

      {/* Game Area */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.3 }}
        className="max-w-7xl mx-auto"
      >
        <div className="game-area flex flex-col lg:flex-row gap-1 lg:gap-6 items-start justify-center min-h-screen">
          {connected && client && (
            <motion.div
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.4 }}
              className="drawing-area flex flex-col items-center order-1 w-full lg:w-auto lg:order-1"
            >
              <Canvas client={client} />
              <motion.div
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.5 }}
                className="mt-1 lg:mt-4 w-full"
              >
                {isDrawer ? (
                  <ColorPicker client={client} />
                ) : (
                  <WordHint client={client} />
                )}
              </motion.div>
            </motion.div>
          )}
          <motion.div
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.4 }}
            className="order-3 lg:order-2 w-full lg:w-auto"
          >
            <RightSidebar
              client={client}
              height={windowSize.height * CANVAS_HEIGHT_RATIO}
              onDrawerChange={handleDrawerChange}
            />
          </motion.div>
        </div>
      </motion.div>

      {/* Word Selection Modal */}
      {isDrawer && showWordSelection && (
        <WordSelection
          words={wordOptions}
          onWordSelect={handleWordSelect}
        />
      )}
    </motion.div>
  )
}

export default Game