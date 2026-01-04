import React, { useState } from 'react'
import { useSelector, useDispatch } from 'react-redux'
import { motion, AnimatePresence } from 'framer-motion'
import { Trash2, Brush, PaintBucket } from 'lucide-react'
import { setColor, setBrushSize, setIsFillMode } from '../../store/gameSlice'
import { COLOR_OPTIONS, BRUSH_SIZES } from '../../utils/constants'
import { APP_CLEAR_CANVAS } from '../../utils/subscriptionConstants'


/*
* A panel for the current drawer to select colors, brush sizes, fill mode,
* and clear the canvas. Non-drawers won't see this panel.
*/
function ColorPicker({client}) {
  const dispatch = useDispatch()
  const roomId = useSelector(state => state.room.room?.roomId)
  const userID = useSelector(state => state.user.sessionId)
  const isDrawer = useSelector(state => state.game.isDrawer)
  const [showSizeList, setShowSizeList] = useState(false)
  const color = useSelector(state => state.game.color)
  const isFillMode = useSelector(state => state.game.isFillMode)


  function handleClearCanvas() {
    if (!client || !client.connected || !roomId) return
    const message = { userID }
    client.publish({
      destination: APP_CLEAR_CANVAS(roomId),
      body: JSON.stringify(message)
    })
  }

  if (!isDrawer) {
    // Non drawers don't see the color picker panel
    return <div />
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      className="card p-2 lg:p-3 max-w-md lg:max-w-3xl mx-auto"
    >
      {/* First row: Tools + First 6 colors */}
      <div className="flex justify-center gap-1 lg:gap-2 mb-1 lg:mb-2 flex-wrap">
        <motion.button
          onClick={handleClearCanvas}
          className="w-8 h-8 lg:w-10 lg:h-10 bg-red-100 hover:bg-red-200 dark:bg-red-900/30 dark:hover:bg-red-900/50 rounded-lg transition-colors duration-200 flex items-center justify-center"
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          title="Clear Canvas"
        >
          <Trash2 className="w-4 h-4 lg:w-5 lg:h-5 text-red-600 dark:text-red-400" />
        </motion.button>

        <motion.button
          onClick={() => dispatch(setIsFillMode(!isFillMode))}
          className={`w-8 h-8 lg:w-10 lg:h-10 rounded-lg transition-all duration-200 flex items-center justify-center ${
            isFillMode
              ? 'bg-primary-500 text-white shadow-md'
              : 'bg-gray-100 hover:bg-gray-200 dark:bg-gray-700 dark:hover:bg-gray-600'
          }`}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          title={isFillMode ? "Fill Mode: ON" : "Fill Mode: OFF"}
        >
          <PaintBucket className="w-4 h-4 lg:w-5 lg:h-5" />
        </motion.button>

        {/* Brush Size Dropdown */}
        <div className="relative">
          <motion.button
            onClick={() => setShowSizeList(!showSizeList)}
            className="w-8 h-8 lg:w-10 lg:h-10 bg-gray-100 hover:bg-gray-200 dark:bg-gray-700 dark:hover:bg-gray-600 rounded-lg transition-colors duration-200 flex items-center justify-center"
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            title="Brush Size"
          >
            <Brush className="w-4 h-4 lg:w-5 lg:h-5 text-gray-600 dark:text-gray-400" />
          </motion.button>

          <AnimatePresence>
            {showSizeList && (
              <motion.div
                initial={{ opacity: 0, scale: 0.8, y: -10 }}
                animate={{ opacity: 1, scale: 1, y: 0 }}
                exit={{ opacity: 0, scale: 0.8, y: -10 }}
                className="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 bg-white dark:bg-gray-800 rounded-lg shadow-lg border border-gray-200 dark:border-gray-700 p-2 z-10"
              >
                <div className="flex gap-1">
                  {BRUSH_SIZES.map(b => (
                    <motion.button
                      key={b.size}
                      onClick={() => {
                        dispatch(setBrushSize(b.size))
                        setShowSizeList(false)
                      }}
                      className="px-2 lg:px-3 py-1 bg-gray-100 hover:bg-gray-200 dark:bg-gray-700 dark:hover:bg-gray-600 rounded text-xs lg:text-sm font-medium transition-colors duration-200"
                      whileHover={{ scale: 1.05 }}
                      whileTap={{ scale: 0.95 }}
                    >
                      {b.name}
                    </motion.button>
                  ))}
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </div>

        {/* First 6 colors on the same row */}
        {COLOR_OPTIONS.slice(0, 6).map(c => (
          <motion.button
            key={c.code}
            onClick={() => dispatch(setColor(c.code))}
            className={`w-8 h-8 lg:w-10 lg:h-10 rounded-lg border-2 transition-all duration-200 ${
              color === c.code
                ? 'border-primary-500 shadow-lg scale-110'
                : 'border-gray-300 dark:border-gray-600 hover:scale-105'
            }`}
            style={{ backgroundColor: c.code }}
            whileHover={{ scale: 1.1 }}
            whileTap={{ scale: 0.95 }}
            title={`Color: ${c.code}`}
          />
        ))}
      </div>

      {/* Second row: Remaining 7 colors */}
      <div className="flex justify-center gap-1 lg:gap-2">
        {COLOR_OPTIONS.slice(6).map(c => (
          <motion.button
            key={c.code}
            onClick={() => dispatch(setColor(c.code))}
            className={`w-8 h-8 lg:w-10 lg:h-10 rounded-lg border-2 transition-all duration-200 ${
              color === c.code
                ? 'border-primary-500 shadow-lg scale-110'
                : 'border-gray-300 dark:border-gray-600 hover:scale-105'
            }`}
            style={{ backgroundColor: c.code }}
            whileHover={{ scale: 1.1 }}
            whileTap={{ scale: 0.95 }}
            title={`Color: ${c.code}`}
          />
        ))}
      </div>
    </motion.div>
  )
}

export default ColorPicker