import React, { useState } from 'react'
import Canvas from './Canvas'
import ColorPicker from './ColorPicker'
import WordHint from './WordHint'

const DrawingArea = ({ client, userID, roomId, isDrawingAllowed }) => {
  const [color, setColor] = useState('#000000')
  const [brushSize, setBrushSize] = useState(2)
  const [isFillMode, setIsFillMode] = useState(false)

  return (
    <div className="drawing-area">
      <Canvas
        client={client}
        color={color}
        userID={userID}
        roomId={roomId}
        isDrawingAllowed={isDrawingAllowed}
        brushSize={brushSize}
        isFillMode={isFillMode}
        onFillToggle={() => setIsFillMode(!isFillMode)}
      />
      {isDrawingAllowed ? (
        <ColorPicker
          client={client}
          setColor={setColor}
          userID={userID}
          roomId={roomId}
          isDrawingAllowed={isDrawingAllowed}
          setBrushSize={setBrushSize}
          onFillToggle={() => setIsFillMode(!isFillMode)}
          isFillMode={isFillMode}
        />
      ) : (
        <WordHint client={client} roomId={roomId} isDrawer={isDrawingAllowed} />
      )}
    </div>
  );
};

export default DrawingArea;
