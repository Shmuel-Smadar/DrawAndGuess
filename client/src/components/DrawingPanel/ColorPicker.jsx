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

  const buttonSizeStyle = {
    width: 'var(--picker-button-size)',
    minWidth: 'var(--picker-button-size)',
    aspectRatio: '1 / 1'
  }
  const iconClass = 'w-4 h-4 lg:w-5 lg:h-5'
  const visibleColors = isDesktop ? COLOR_OPTIONS : COLOR_OPTIONS.slice(0, 8)
  const firstRowColors = isDesktop ? visibleColors.slice(0, 6) : visibleColors.slice(0, 4)
  const secondRowColors = isDesktop ? visibleColors.slice(6) : visibleColors.slice(4)

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      className={`card p-1 lg:p-3 max-w-full mx-auto ${isDesktop ? 'relative w-full' : ''}`}
      style={isDesktop
        ? { "--picker-button-size": "40px" }
        : {
          "--picker-button-size": "clamp(34px, calc((100vw - 4.5rem) / 7), 44px)",
          width: "var(--drawing-surface-width, 100%)"
        }}
    >
      {/* First row: Tools + First 6 colors */}
      <div className="flex justify-center gap-1.5 lg:gap-3 mb-1.5 lg:mb-3 flex-nowrap">
        <motion.button
          onClick={handleClearCanvas}
          className="bg-red-100 hover:bg-red-200 dark:bg-red-900/30 dark:hover:bg-red-900/50 rounded-lg transition-colors duration-200 flex items-center justify-center"
          style={buttonSizeStyle}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          title="Clear Canvas"
        >
          <Trash2 className={`${iconClass} text-red-600 dark:text-red-400`} />
        </motion.button>

        <motion.button
          onClick={() => dispatch(setIsFillMode(!isFillMode))}
          className={`rounded-lg transition-all duration-200 flex items-center justify-center ${
            isFillMode
              ? 'bg-primary-500 text-white shadow-md'
              : 'bg-gray-100 hover:bg-gray-200 dark:bg-gray-700 dark:hover:bg-gray-600'
          }`}
          style={buttonSizeStyle}
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
            className="bg-gray-100 hover:bg-gray-200 dark:bg-gray-700 dark:hover:bg-gray-600 rounded-lg transition-colors duration-200 flex items-center justify-center"
            style={buttonSizeStyle}
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
        {firstRowColors.map(c => (
          <motion.button
            key={c.code}
            onClick={() => selectColor(c.code)}
            className={`rounded-lg border-2 transition-all duration-200 ${
              color === c.code
                ? 'border-primary-500 shadow-lg'
                : 'border-gray-300 dark:border-gray-600 hover:scale-105'
            }`}
            style={{ ...buttonSizeStyle, backgroundColor: c.code }}
            whileHover={{ scale: 1.1 }}
            whileTap={{ scale: 0.95 }}
            title={`Color: ${c.code}`}
          />
        ))}
      </div>

      <div className="flex justify-center gap-1.5 lg:gap-3 flex-nowrap">
        {secondRowColors.map(c => (
          <motion.button
            key={c.code}
            onClick={() => selectColor(c.code)}
            className={`rounded-lg border-2 transition-all duration-200 ${
              color === c.code
                ? 'border-primary-500 shadow-lg'
                : 'border-gray-300 dark:border-gray-600 hover:scale-105'
            }`}
            style={{ ...buttonSizeStyle, backgroundColor: c.code }}
            whileHover={{ scale: 1.1 }}
            whileTap={{ scale: 0.95 }}
            title={`Color: ${c.code}`}
          />
        ))}
        <div className="relative">
          <motion.button
            onClick={() => setShowColorList(!showColorList)}
            className="relative rounded-lg border-2 border-gray-300 dark:border-gray-600 bg-gray-100 hover:bg-gray-200 dark:bg-gray-700 dark:hover:bg-gray-600 flex items-center justify-center transition-all duration-200"
            style={buttonSizeStyle}
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            title="More colors"
          >
            <Palette className={`${iconClass} text-gray-700 dark:text-gray-200`} />
            <span
              className="absolute bottom-1 right-1 h-2.5 w-2.5 rounded-full border border-white dark:border-gray-900"
              style={{ backgroundColor: color }}
            />
          </motion.button>

          <AnimatePresence>
            {showColorList && (
              <motion.div
                initial={{ opacity: 0, scale: 0.92, y: 8 }}
                animate={{ opacity: 1, scale: 1, y: 0 }}
                exit={{ opacity: 0, scale: 0.92, y: 8 }}
                className="absolute bottom-full right-0 mb-3 w-max grid grid-cols-4 gap-3 rounded-xl border border-gray-200 bg-white p-3 shadow-2xl dark:border-gray-600 dark:bg-gray-800 z-20"
              >
                {COLOR_OPTIONS.map(c => (
                  <motion.button
                    key={c.code}
                    onClick={() => selectColor(c.code)}
                    className={`h-10 w-10 rounded-lg border-2 transition-all duration-200 ${
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
    </motion.div>
  )
}

export default ColorPicker
