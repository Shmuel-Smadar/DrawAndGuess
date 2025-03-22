import React, { useState } from 'react'
import { useSelector, useDispatch } from 'react-redux'
import { setColor, setBrushSize, setIsFillMode } from '../../store/drawSlice'
import ClearIcon from '../../assets/trash-bin.png'
import BrushIcon from '../../assets/paint-brush.png'
import BucketIcon from '../../assets/paint-bucket.png'
import './ColorPicker.css'

const colors = [
  { code: '#000000' },
  { code: '#FF0000' },
  { code: '#00FF00' },
  { code: '#0000FF' },
  { code: '#FFFF00' },
  { code: '#A52A2A' },
  { code: '#800080' },
  { code: '#FFA500' },
  { code: '#FFC0CB' },
  { code: '#808080' },
  { code: '#00FFFF' },
  { code: '#FF00FF' },
  { code: '#8950F7' }
]
const brushSizes = [
  { name: 'S', size: 2 },
  { name: 'M', size: 5 },
  { name: 'L', size: 10 }
]

function ColorPicker({ client, userID, roomId, isDrawer }) {
  const dispatch = useDispatch()
  const [showSizeList, setShowSizeList] = useState(false)
  const color = useSelector(state => state.draw.color)
  const isFillMode = useSelector(state => state.draw.isFillMode)

  function handleClearCanvas() {
    if (!client || !client.connected || !roomId) return
    const message = { userID }
    client.publish({
      destination: `/app/room/${roomId}/clearCanvas`,
      body: JSON.stringify(message)
    })
  }

  return (
    <div className="color-picker-container">
      {isDrawer && (
        <div className="color-buttons">
          <button onClick={handleClearCanvas} className="clear-button">
            <img src={ClearIcon} alt="" className="button-icon" />
          </button>
          <button
            onClick={() => dispatch(setIsFillMode(!isFillMode))}
            className={isFillMode ? 'fill-button active' : 'fill-button'}
          >
            <img src={BucketIcon} alt="" className="button-icon" />
          </button>
          <div className="brush-size-dropdown">
            <button
              onClick={() => setShowSizeList(!showSizeList)}
              className="brush-size-button"
            >
              <img src={BrushIcon} alt="" className="button-icon" />
            </button>
            {showSizeList && (
              <div className="brush-size-list">
                {brushSizes.map(b => (
                  <button
                    key={b.size}
                    onClick={() => {
                      dispatch(setBrushSize(b.size))
                      setShowSizeList(false)
                    }}
                  >
                    {b.name}
                  </button>
                ))}
              </div>
            )}
          </div>
          {colors.map(c => (
            <button
              key={c.code}
              style={{ backgroundColor: c.code }}
              onClick={() => dispatch(setColor(c.code))}
              className={color === c.code ? 'selected-color' : ''}
            />
          ))}
        </div>
      )}
    </div>
  )
}
export default ColorPicker
