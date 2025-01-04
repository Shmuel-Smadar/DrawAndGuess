import React, { useState } from 'react'
import Canvas from './Canvas'
import ColorPicker from './ColorPicker'

const DrawingArea = ({ client, userID, isDrawingAllowed }) => {
  const [color, setColor] = useState('#000000')
  const [brushSize, setBrushSize] = useState(2)

  return (
    <div className="drawing-area">
      <Canvas
        client={client}
        color={color}
        userID={userID}
        isDrawingAllowed={isDrawingAllowed}
        brushSize={brushSize}
      />
      <ColorPicker
        client={client}
        setColor={setColor}
        userID={userID}
        isDrawingAllowed={isDrawingAllowed}
        setBrushSize={setBrushSize}
      />
    </div>
  )
}

export default DrawingArea