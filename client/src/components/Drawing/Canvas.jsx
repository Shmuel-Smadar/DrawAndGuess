import React from 'react'
import { useSelector } from 'react-redux'
import useCanvas from '../../hooks/useCanvas'
import BucketIcon from '../../assets/paint-bucket.png'
import './Canvas.css'

function Canvas({ client, userID, roomId }) {
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
          ? `url(${BucketIcon}) 16 16, auto`
          : 'crosshair'
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
