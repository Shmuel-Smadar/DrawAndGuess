import React from 'react'
import { useSelector } from 'react-redux'
import Canvas from './Canvas'
import ColorPicker from '../DrawingPanel/ColorPicker'
import WordHint from '../DrawingPanel/WordHint'
import './DrawingArea.css'

function DrawingArea({ client}) {
  const isDrawer = useSelector(state => state.game.isDrawer)

  return (
    <div className="drawing-area">
      <Canvas
        client={client}
      />
      {isDrawer ? (
        <ColorPicker
          client={client}
        />
      ) : (
        <WordHint
          client={client}
        />
      )}
    </div>
  )
}

export default DrawingArea