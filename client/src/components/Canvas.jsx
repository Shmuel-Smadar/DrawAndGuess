import React from 'react'
import useCanvas from '../hooks/useCanvas'
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

  return (
    <canvas
      ref={canvasRef}
      className="myCanvas"
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
