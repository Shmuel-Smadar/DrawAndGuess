import { useEffect } from 'react'
import { useSelector } from 'react-redux'
import { floodFill } from './helpers'
import {
  VIRTUAL_WIDTH,
  VIRTUAL_HEIGHT,
  EVENT_TYPE_START,
  EVENT_TYPE_DRAW,
  EVENT_TYPE_STOP,
  EVENT_TYPE_FILL
} from '../../utils/constants'
import {
  TOPIC_ROOM_DRAWING,
  TOPIC_ROOM_CLEAR_CANVAS,
  
} from '../../utils/subscriptionConstants'

/* A hook that subscribes to canvas events and perform the
*  actions that are being sent from other users on the canvas.
*/
export const useCanvasSubscriptions = ({ client, roomId, canvasRef, lastPositions }) => {
  const isDrawer = useSelector((state) => state.game.isDrawer)

  useEffect(() => {
    if (!client || !client.connected) return

    const drawingSub = client.subscribe(TOPIC_ROOM_DRAWING(roomId), (msg) => {
      const data = JSON.parse(msg.body)
      if (isDrawer && data.eventType !== EVENT_TYPE_FILL) return
      const canvas = canvasRef.current
      if (!canvas) return
      const ctx = canvas.getContext('2d')
      const { width, height } = canvas
      const offsetX = (data.normX / VIRTUAL_WIDTH) * width
      const offsetY = (data.normY / VIRTUAL_HEIGHT) * height
      switch (data.eventType) {
        case EVENT_TYPE_START:
          ctx.lineWidth = data.brushSize
          ctx.beginPath()
          ctx.moveTo(offsetX, offsetY)
          ctx.strokeStyle = data.color
          ctx.lineTo(offsetX, offsetY)
          ctx.stroke()
          lastPositions.current[data.userID] = { x: offsetX, y: offsetY }
          break
        case EVENT_TYPE_DRAW:
          ctx.lineWidth = data.brushSize
          const lastPos = lastPositions.current[data.userID]
          ctx.beginPath()
          if (lastPos) {
            ctx.moveTo(lastPos.x, lastPos.y)
          } else {
            ctx.moveTo(offsetX, offsetY)
          }
          ctx.lineTo(offsetX, offsetY)
          ctx.stroke()
          lastPositions.current[data.userID] = { x: offsetX, y: offsetY }
          break
        case EVENT_TYPE_STOP:
          delete lastPositions.current[data.userID]
          break
        case EVENT_TYPE_FILL:
          floodFill(ctx, Math.floor(offsetX), Math.floor(offsetY), data.color)
          break
        default:
          break
      }
    })

    const clearSub = client.subscribe(TOPIC_ROOM_CLEAR_CANVAS(roomId), () => {
      const canvas = canvasRef.current
      if (canvas) {
        const ctx = canvas.getContext('2d')
        ctx.clearRect(0, 0, canvas.width, canvas.height)
      }
    })

    return () => {
      drawingSub.unsubscribe()
      clearSub.unsubscribe()
    }
  }, [client, roomId, canvasRef, lastPositions, isDrawer])
}
