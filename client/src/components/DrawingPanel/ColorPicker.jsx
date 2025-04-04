import React, { useState } from 'react'
import { useSelector, useDispatch } from 'react-redux'
import { setColor, setBrushSize, setIsFillMode } from '../../store/drawSlice'
import ClearIcon from '../../assets/trash-bin.png'
import BrushIcon from '../../assets/paint-brush.png'
import BucketIcon from '../../assets/paint-bucket.png'
import { COLOR_OPTIONS, BRUSH_SIZES } from '../../utils/constants'
import './ColorPicker.css'

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
                {BRUSH_SIZES.map(b => (
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
          {COLOR_OPTIONS.map(c => (
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
