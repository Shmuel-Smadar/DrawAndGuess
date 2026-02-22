import React from 'react'
import { useSelector } from 'react-redux'
import useCanvas from './useCanvas'
import BucketIcon from '../../assets/paint-bucket.png'
import { BUCKET_ICON_CURSOR_OFFSET, DEFAULT_CURSOR } from '../../utils/constants'

function Canvas({ client }) {
  const roomId = useSelector(state => state.room.room?.roomId)
  const userID = useSelector(state => state.user.sessionId)
  //handle user actions on the canvas
  const {
    canvasRef,
    handleMouseDown,
    handleMouseMove,
    handleMouseUp,
    handleMouseLeave,
    handleTouchStart,
    handleTouchMove,
    handleTouchEnd
  } = useCanvas({ client, userID, roomId })

  const isFillMode = useSelector(state => state.game.isFillMode)

  return (
    <canvas
      ref={canvasRef}
      className="w-full max-w-full h-auto border border-gray-300 dark:border-gray-600 rounded-lg shadow-sm bg-white dark:bg-gray-800 block mx-auto mb-1 lg:mb-0"
      style={{
        cursor: isFillMode
          ? `url(${BucketIcon}) ${BUCKET_ICON_CURSOR_OFFSET}, auto`
          : DEFAULT_CURSOR,
        maxHeight: '75vh', // Allow up to 75% of viewport height on mobile
        overscrollBehavior: 'none', // Prevent scroll during drawing
        touchAction: 'none' // Prevent default touch behaviors
      }}
      onMouseDown={handleMouseDown}
      onMouseMove={handleMouseMove}
      onMouseUp={handleMouseUp}
      onMouseLeave={handleMouseLeave}
      onTouchStart={handleTouchStart}
      onTouchMove={handleTouchMove}
      onTouchEnd={handleTouchEnd}
    />
  )
}

export default Canvas