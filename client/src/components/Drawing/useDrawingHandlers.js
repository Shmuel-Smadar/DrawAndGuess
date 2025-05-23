import { useCallback } from 'react'
import { setIsFillMode } from '../../store/gameSlice'
import { getEventCoordinates } from './helpers'
import {
  VIRTUAL_WIDTH,
  VIRTUAL_HEIGHT,
  EVENT_TYPE_START,
  EVENT_TYPE_DRAW,
  EVENT_TYPE_STOP,
  EVENT_TYPE_FILL
} from '../../utils/constants'
import { APP_ROOM_DRAW } from '../../utils/subscriptionConstants'


/* A hook that publishes local drawing actions (start, draw, stop, fill)
* to the STOMP conenction, so that other participants see them. It also applies
* immediate local updates on the canvas for real-time feedback. */
export const useDrawingHandlers = ({
  client,
  userID,
  roomId,
  canvasRef,
  isFillMode,
  isDrawer,
  color,
  brushSize,
  dispatch,
  setIsDrawing,
  lastPositions,
  isDrawing
}) => {
  const computeCoords = useCallback(
    (event) => {
      const { offsetX, offsetY } = getEventCoordinates(event, canvasRef)
      const { width, height } = canvasRef.current
      const normX = (offsetX / width) * VIRTUAL_WIDTH
      const normY = (offsetY / height) * VIRTUAL_HEIGHT
      return { offsetX, offsetY, normX, normY }
    },
    [canvasRef]
  )

  const startDrawing = useCallback(
    (event) => {
      if (!isDrawer || !client) return
      const ctx = canvasRef.current.getContext('2d')
      if (isFillMode) {
        const { normX, normY } = computeCoords(event)
        const msg = {
          normX,
          normY,
          color,
          userID: String(userID),
          eventType: EVENT_TYPE_FILL
        }

        client.publish({
          destination: APP_ROOM_DRAW(roomId),
          body: JSON.stringify(msg)
        })

        dispatch(setIsFillMode(false))
        return
      }

      const { offsetX, offsetY, normX, normY } = computeCoords(event)
      ctx.beginPath()
      ctx.moveTo(offsetX, offsetY)
      ctx.lineTo(offsetX, offsetY)
      ctx.strokeStyle = color
      ctx.stroke()
      setIsDrawing(true)
      lastPositions.current[userID] = { x: offsetX, y: offsetY }

      const message = {
        normX,
        normY,
        color,
        brushSize,
        userID: String(userID),
        eventType: EVENT_TYPE_START
      }
      client.publish({
        destination: APP_ROOM_DRAW(roomId),
        body: JSON.stringify(message)
      })
    },
    [
      isDrawer,
      client,
      isFillMode,
      color,
      brushSize,
      roomId,
      userID,
      canvasRef,
      dispatch,
      setIsDrawing,
      lastPositions,
      computeCoords
    ]
  )

  const draw = useCallback(
    (event) => {
      if (!isDrawing || !isDrawer || !client || isFillMode) return
      const { offsetX, offsetY, normX, normY } = computeCoords(event)
      const ctx = canvasRef.current.getContext('2d')
      ctx.lineTo(offsetX, offsetY)
      ctx.stroke()
      lastPositions.current[userID] = { x: offsetX, y: offsetY }
      const msg = {
        normX,
        normY,
        brushSize,
        userID: String(userID),
        eventType: EVENT_TYPE_DRAW
      }

      client.publish({
        destination: APP_ROOM_DRAW(roomId),
        body: JSON.stringify(msg)
      })
    },
    [
      isDrawing,
      isDrawer,
      client,
      isFillMode,
      brushSize,
      roomId,
      userID,
      canvasRef,
      lastPositions,
      computeCoords
    ]
  )

  const stopDrawing = useCallback(
    (event) => {
      if (!isDrawer || !client) return

      setIsDrawing(false)
      delete lastPositions.current[userID]

      const msg = { userID: String(userID), eventType: EVENT_TYPE_STOP }

      client.publish({
        destination: APP_ROOM_DRAW(roomId),
        body: JSON.stringify(msg)
      })
    },
    [isDrawer, client, roomId, userID, setIsDrawing, lastPositions]
  )

  return { startDrawing, draw, stopDrawing }
}
