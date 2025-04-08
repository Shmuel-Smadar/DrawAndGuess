import React from 'react'
import { useSelector } from 'react-redux'
import useCanvas from '../../hooks/useCanvas'
import BucketIcon from '../../assets/paint-bucket.png'
import { BUCKET_ICON_CURSOR_OFFSET, DEFAULT_CURSOR } from '../../utils/constants'
import './Canvas.css'

function Canvas({ client }) {
    const roomId = useSelector(state => state.room.room?.roomId)
    const userID = useSelector(state => state.user.sessionId)
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

  const isFillMode = useSelector(state => state.draw.isFillMode)

  return (
    <canvas
      ref={canvasRef}
      className="myCanvas"
      style={{
        cursor: isFillMode
          ? `url(${BucketIcon}) ${BUCKET_ICON_CURSOR_OFFSET}, auto`
          : DEFAULT_CURSOR
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