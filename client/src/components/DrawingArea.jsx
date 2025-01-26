import React from 'react'
import { useSelector } from 'react-redux'
import Canvas from './Canvas'
import ColorPicker from './ColorPicker'
import WordHint from './WordHint'

function DrawingArea({ client, userID, roomId }) {
  const isDrawer = useSelector(state => state.game.isDrawer)

  return (
    <div className="drawing-area">
      <Canvas
        client={client}
        userID={userID}
        roomId={roomId}
      />
      {isDrawer ? (
        <ColorPicker
          client={client}
          userID={userID}
          roomId={roomId}
          isDrawer={isDrawer}
        />
      ) : (
        <WordHint
          client={client}
          roomId={roomId}
          isDrawer={isDrawer}
        />
      )}
    </div>
  )
}

export default DrawingArea
