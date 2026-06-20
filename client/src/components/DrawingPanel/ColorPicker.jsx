import React, { useState } from 'react'
import { useSelector, useDispatch } from 'react-redux'
import { motion, AnimatePresence } from 'framer-motion'
import { Trash2, Brush, PaintBucket, Palette } from 'lucide-react'
import { setColor, setBrushSize, setIsFillMode } from '../../store/gameSlice'
import { COLOR_OPTIONS, BRUSH_SIZES } from '../../utils/constants'
import { APP_CLEAR_CANVAS } from '../../utils/subscriptionConstants'


/*
* A panel for the current drawer to select colors, brush sizes, fill mode,
* and clear the canvas. Non-drawers won't see this panel.
*/
function ColorPicker({client, variant = 'mobile'}) {
  const dispatch = useDispatch()
  const roomId = useSelector(state => state.room.room?.roomId)
  const userID = useSelector(state => state.user.sessionId)
  const isDrawer = useSelector(state => state.game.isDrawer)
  const [showSizeList, setShowSizeList] = useState(false)
  const [showColorList, setShowColorList] = useState(false)
  const color = useSelector(state => state.game.color)
  const isFillMode = useSelector(state => state.game.isFillMode)
  const isDesktop = variant === 'desktop'


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

  const selectColor = (nextColor) => {
    dispatch(setColor(nextColor))
    setShowColorList(false)
  }

  const buttonClass = isDesktop
    ? 'h-10 w-[var(--picker-button-size)] min-w-[var(--picker-button-size)]'
    : 'w-8 h-8 lg:w-10 lg:h-10'
  const iconClass = 'w-4 h-4 lg:w-5 lg:h-5'
  const visibleColors = isDesktop ? COLOR_OPTIONS.slice(0, 6) : COLOR_OPTIONS.slice(0, 6)
  const remainingColors = COLOR_OPTIONS.slice(6)

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      className={`card p-1 lg:p-3 max-w-full mx-auto ${isDesktop ? 'relative w-full' : ''}`}
      style={isDesktop
        ? { "--picker-button-size": "clamp(32px, calc((100% - 8.5rem) / 11), 40px)" }
        : { width: "var(--drawing-surface-width, 100%)" }}
    >
      {/* First row: Tools + First 6 colors */}
      <div className={`flex justify-center gap-0.5 lg:gap-2 ${isDesktop ? 'flex-nowrap' : 'mb-1 lg:mb-2 flex-wrap'}`}>
        <motion.button
          onClick={handleClearCanvas}
          className={`${buttonClass} bg-red-100 hover:bg-red-200 dark:bg-red-900/30 dark:hover:bg-red-900/50 rounded-lg transition-colors duration-200 flex items-center justify-center`}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          title="Clear Canvas"
        >
          <Trash2 className={`${iconClass} text-red-600 dark:text-red-400`} />
        </motion.button>

        <motion.button
          onClick={() => dispatch(setIsFillMode(!isFillMode))}
          className={`${buttonClass} rounded-lg transition-all duration-200 flex items-center justify-center ${
            isFillMode
              ? 'bg-primary-500 text-white shadow-md'
              : 'bg-gray-100 hover:bg-gray-200 dark:bg-gray-700 dark:hover:bg-gray-600'
          }`}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          title={isFillMode ? "Fill Mode: ON" : "Fill Mode: OFF"}
        >
          <PaintBucket className={iconClass} />
        </motion.button>

        {/* Brush Size Dropdown */}
        <div className="relative">
          <motion.button
            onClick={() => setShowSizeList(!showSizeList)}
            className={`${buttonClass} bg-gray-100 hover:bg-gray-200 dark:bg-gray-700 dark:hover:bg-gray-600 rounded-lg transition-colors duration-200 flex items-center justify-center`}
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            title="Brush Size"
          >
            <Brush className={`${iconClass} text-gray-600 dark:text-gray-400`} />
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
        {visibleColors.map(c => (
          <motion.button
            key={c.code}
            onClick={() => selectColor(c.code)}
            className={`${buttonClass} rounded-lg border-2 transition-all duration-200 ${
              color === c.code
                ? 'border-primary-500 shadow-lg'
                : 'border-gray-300 dark:border-gray-600 hover:scale-105'
            }`}
            style={{ backgroundColor: c.code }}
            whileHover={{ scale: 1.1 }}
            whileTap={{ scale: 0.95 }}
            title={`Color: ${c.code}`}
          />
        ))}

        <div className="relative">
          <motion.button
            onClick={() => setShowColorList(!showColorList)}
            className={`${buttonClass} rounded-lg border-2 border-gray-300 dark:border-gray-600 flex items-center justify-center transition-all duration-200`}
            style={{ backgroundColor: color }}
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            title="More colors"
          >
            <Palette className={`${iconClass} text-white drop-shadow-[0_1px_1px_rgba(0,0,0,0.8)]`} />
          </motion.button>

          <AnimatePresence>
            {showColorList && (
              <motion.div
                initial={{ opacity: 0, scale: 0.92, y: 8 }}
                animate={{ opacity: 1, scale: 1, y: 0 }}
                exit={{ opacity: 0, scale: 0.92, y: 8 }}
                className="absolute bottom-full right-0 mb-2 grid grid-cols-4 gap-2 bg-white dark:bg-gray-800 rounded-lg shadow-lg border border-gray-200 dark:border-gray-700 p-2 z-20"
              >
                {COLOR_OPTIONS.map(c => (
                  <motion.button
                    key={c.code}
                    onClick={() => selectColor(c.code)}
                    className={`w-8 h-8 rounded-lg border-2 transition-all duration-200 ${
                      color === c.code
                        ? 'border-primary-500 shadow-lg'
                        : 'border-gray-300 dark:border-gray-600'
                    }`}
                    style={{ backgroundColor: c.code }}
                    whileHover={{ scale: 1.08 }}
                    whileTap={{ scale: 0.95 }}
                    title={`Color: ${c.code}`}
                  />
                ))}
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </div>

      {!isDesktop && (
        <div className="flex justify-center gap-0.5 lg:gap-2 flex-wrap mt-1">
          {remainingColors.map(c => (
            <motion.button
              key={c.code}
              onClick={() => selectColor(c.code)}
              className={`w-8 h-8 lg:w-10 lg:h-10 rounded-lg border-2 transition-all duration-200 ${
                color === c.code
                  ? 'border-primary-500 shadow-lg'
                  : 'border-gray-300 dark:border-gray-600 hover:scale-105'
              }`}
              style={{ backgroundColor: c.code }}
              whileHover={{ scale: 1.1 }}
              whileTap={{ scale: 0.95 }}
              title={`Color: ${c.code}`}
            />
          ))}
        </div>
      )}
    </motion.div>
  )
}

export default ColorPicker
