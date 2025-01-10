import React, { useState } from 'react'
import ClearIcon from '../assets/trash-bin.png'
import BrushIcon from '../assets/paint-brush.png'
import './ColorPicker.css'
const colors = [
  { name: '', code: '#000000' },
  { name: '', code: '#FF0000' },
  { name: '', code: '#00FF00' },
  { name: '', code: '#0000FF' },
  { name: '', code: '#FFFF00' },
  { name: '', code: '#A52A2A' },
  { name: '', code: '#800080' },
  { name: '', code: '#FFA500' },
  { name: '', code: '#FFC0CB' },
  { name: '', code: '#808080' },
  { name: '', code: '#00FFFF' },
  { name: '', code: '#FF00FF' },
]

const brushSizes = [
  { name: 'S', size: 2 },
  { name: 'M', size: 5 },
  { name: 'L', size: 10 },
]

const ColorPicker = ({ client, setColor, userID, roomId, isDrawingAllowed, setBrushSize }) => {
  const [showSizeList, setShowSizeList] = useState(false)

  const handleClearCanvas = () => {
    if (!client || !client.connected || !roomId) return
    const message = { userID }
    client.publish({
      destination: `/app/room/${roomId}/clearCanvas`,
      body: JSON.stringify(message),
    })
  }

  return (
    <div className="color-picker-container">
      {isDrawingAllowed && (
        <div className="color-buttons">
          <button onClick={handleClearCanvas} className="clear-button" aria-label="Clear Canvas">
            <img src={ClearIcon} alt="Clear Canvas" className="button-icon" />
          </button>
          <div className="brush-size-dropdown">
            <button onClick={() => setShowSizeList(!showSizeList)} className="brush-size-button">
              <img src={BrushIcon} alt="Brush Size" className="button-icon" />
            </button>
            {showSizeList && (
              <div className="brush-size-list">
                {brushSizes.map((brush) => (
                  <button
                    key={brush.size}
                    onClick={() => {
                      setBrushSize(brush.size)
                      setShowSizeList(false)
                    }}
                    className="brush-size-option"
                  >
                    {brush.name}
                  </button>
                ))}
              </div>
            )}
          </div>
          {colors.map((colorObj) => (
            <button
              key={colorObj.code}
              onClick={() => setColor(colorObj.code)}
              style={{ backgroundColor: colorObj.code }}
            />
          ))}
        </div>
      )}
    </div>
  )
}

export default ColorPicker